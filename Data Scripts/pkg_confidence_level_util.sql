-- for the confidence_Level algorithm.
-- use the table CONFIDENCE_LEVEL_FACTOR to adjust weightings, thresholds and precidence
-- not sure quite what precidence means!
-- this computes a score, then normalizes it to a 1-5 scale

CREATE OR REPLACE PACKAGE confidence_level_util
AUTHID current_user
AS
     --TYPE r_score IS RECORD (score NUMBER);

     TYPE t_score IS TABLE OF number
          index BY BINARY_INTEGER;

     TYPE t_factor IS TABLE OF confidence_level_factor%rowtype
          index BY BINARY_INTEGER;

    PROCEDURE calc_confidence_level (ani_experiment_id  IN NUMBER DEFAULT NULL);

    FUNCTION  get_confidence_score (ani_experiment_id IN NUMBER,
                                    avi_sql     IN VARCHAR2)
              RETURN NUMBER;

    FUNCTION overall_score (ati_scores IN t_score,
                            ati_factors IN  t_factor)
              RETURN NUMBER;

    PROCEDURE save_confidence_level( ani_experiment_id  IN  NUMBER,
                                     ani_confidence_level IN  NUMBER);

    PROCEDURE save_confidence_score (ani_score  IN  number,
                                     ani_experiment_id  IN  number,
                                     ani_factor_id IN NUMBER);

    FUNCTION normalize_score (ani_score IN number,
                           ari_factor IN confidence_level_factor%rowtype)
              RETURN NUMBER;

END confidence_level_util;
/

CREATE OR REPLACE PACKAGE BODY confidence_level_util

AS
-------------------------------------------------------------------------
-- private procedures for internal use in this package
    procedure log_error
        (an_errnum   in  number,
         av_errmsg  in varchar2,
         av_location    in varchar2,
         av_comment in varchar2 default null)
    as
    begin
        insert into error_log
           ( ERROR_LOG_ID,
             ERROR_DATE,
             procedure_name,
             ERR_NUM,
             ERR_MSG,
             ERR_COMMENT
           ) values (
             ERROR_LOG_ID_SEQ.NEXTVAL,
             sysdate,
             av_location,
             an_errnum,
             av_errmsg,
             av_comment
           );


    EXCEPTION
        -- no errors are allowed to disturb the force
        when others
        then
            null;
    end log_error;

-------------------------------------------------------------------------------------------------
-- public procedures and functions
    PROCEDURE calc_confidence_level (ani_experiment_id  IN NUMBER DEFAULT NULL)
    AS
        CURSOR cur_experiment
        IS
        SELECT experiment_id, assay_id
          FROM experiment
          WHERE (experiment_id = ani_experiment_id
             OR ani_experiment_id IS NULL);

        CURSOR cur_factor
        IS
        SELECT *
          FROM confidence_level_factor
         WHERE score_SQL IS NOT null
         ORDER BY precidence, confidence_level_factor_id;

        lt_factor t_factor;
        lt_score t_score;
        ln_weighted_score NUMBER;

    BEGIN
        -- beware of limits here - if this table gets really big say 1000+ rows, you need to limit the fetch
        -- dbms_Output.put_line('opening factor cursor');
        OPEN cur_factor;
        FETCH cur_factor BULK COLLECT INTO lt_factor;
        CLOSE cur_factor;
        -- dbms_Output.put_line('got ALL factors');

        IF lt_factor.last IS NULL
        THEN return;
        END IF;

        -- dbms_Output.put_line('start experiment loop ' || To_Char(ani_experiment_id));
        FOR lr_experiment IN cur_experiment
        LOOP
            FOR i IN lt_factor.first ..lt_factor.last
            LOOP
                lt_score(i) := get_confidence_score (lr_experiment.experiment_id, lt_factor(i).score_sql);
                lt_score(i) := normalize_score( lt_score(i), lt_factor(i));

                save_confidence_score (lt_score(i), lr_experiment.experiment_id, lt_factor(i).confidence_level_factor_id);
            END LOOP;

            ln_weighted_score := overall_score (lt_score, lt_factor);

            save_confidence_level (lr_experiment.experiment_id, ln_weighted_score);

       END LOOP;

    EXCEPTION
    WHEN OTHERS THEN
        IF cur_factor%ISOPEN THEN CLOSE cur_factor; END if;
        IF cur_experiment%ISOPEN THEN CLOSE cur_experiment; END if;
        RAISE;
    END calc_confidence_level;

    FUNCTION  get_confidence_score (ani_experiment_id IN NUMBER,
                                    avi_sql     IN VARCHAR2)
              RETURN NUMBER
    AS
        ln_score  NUMBER;
        TYPE t_cursor IS REF CURSOR;
        cur_score t_cursor;

    BEGIN
        -- Open a cursor with the SQL using the experiment_id as a parameter
        -- The SQL must be
        --    1. a single row return (e.g. max, sum, count)
        --    2. a single numeric column return
        --    3. use only one parameter - experiment_id
        -- dbms_Output.put_line(avi_sql);

        OPEN cur_score FOR avi_sql USING ani_experiment_id;
        FETCH cur_score INTO ln_score;
        CLOSE cur_score;

        -- make sure we return something if the cursor has an empty set response
        RETURN Nvl(ln_score, 0);

    EXCEPTION
    WHEN OTHERS THEN
        log_error (sqlcode, 'Score_SQL failed', 'get_confidence_score',
            'experiment_id= ' || To_Char(ani_experiment_id)
            || ', Score_SQL= '|| avi_sql
            || ', SQLERRM= ' || SQLERRM );

        RETURN 0;

    END get_confidence_score;

    FUNCTION overall_score (ati_scores IN t_score,
                            ati_factors IN  t_factor)
              RETURN NUMBER
    AS
        ln_weighted_score  NUMBER := 0;
        ln_factor_score    NUMBER;
    BEGIN
        -- first remove the threshold from the number
        -- then proportion to a 100 point scale using the max level (note some scores may go over 100 points!)
        --
        -- if the score is above the measure necessary for precidence, then
        --   go on a calculate the other scores - THIS NEEDS SOME MORE THOUGHT.  **********  <<--
        -- and finally multiply by the weighting

        -- then sum them and proportion the number into a 1..5 range
        -- none of the input scores is allowed to be null.
        FOR i IN ati_factors.first ..ati_factors.last
        LOOP
            -- in this first implementation lets ignore precidence
            --    when we do need precidence:
            --    take all the first precidence numbers and average them
            --    if the average is over 50%, then use the second factors
            --    and so on...   MAYBE !!
            ln_factor_score := ati_scores(i) * ati_factors(i).weighting ;
            ln_weighted_score := ln_weighted_score + ln_factor_score;
        END LOOP;

        -- create the average
        ln_weighted_score := ln_weighted_score / ati_factors.last;

        -- and bucket this into a 1 - 5 integer score
        ln_weighted_score := Round(ln_weighted_score / 100 , 0);
        IF ln_weighted_score > 5
        THEN
            ln_weighted_score := 5;
        END IF;

        RETURN ln_weighted_score;

    END overall_score;

    FUNCTION normalize_score (ani_score IN number,
                           ari_factor IN confidence_level_factor%rowtype)
              RETURN NUMBER
    AS
        ln_factor_score NUMBER;

    BEGIN
        ln_factor_score := ani_score - Nvl(ari_factor.min_threshold,0);
        IF ln_factor_score < 0
        THEN
            ln_factor_score := 0;
        END IF;

        IF ari_factor.max_level > 0
        THEN
            ln_factor_score := Round(ln_factor_score / ari_factor.max_level * 100, 0);
        ELSE
            ln_factor_score := 0;
        END IF;

        RETURN ln_factor_score;

    END normalize_score;

    PROCEDURE save_confidence_level( ani_experiment_id  IN  NUMBER,
                                     ani_confidence_level IN  NUMBER)
    AS

    BEGIN

        UPDATE experiment
        SET confidence_level = ani_confidence_level
        WHERE experiment_id = ani_experiment_id;

    END save_confidence_level;

    PROCEDURE save_confidence_score (ani_score  IN  number,
                                     ani_experiment_id  IN  number,
                                     ani_factor_id IN NUMBER)
    AS

    BEGIN

        UPDATE confidence_level_score
        SET score = ani_score,
            version = version + 1,
            last_updated = SYSDATE
        WHERE experiment_id = ani_experiment_id
          AND confidence_level_factor_id = ani_factor_id;

        IF SQL%ROWCOUNT = 0
        then
            INSERT INTO confidence_level_score
                (confidence_level_score_id,
                confidence_level_factor_id,
                experiment_id,
                score,
                modified_by
                )
            VALUES
                (confidence_level_score_id_seq.nextval,
                ani_factor_id,
                ani_experiment_id,
                ani_score,
                Nvl(p_pbs_context.get_username, Lower(USER))
                );

        END IF;

    END save_confidence_score;

END confidence_level_util;
/
