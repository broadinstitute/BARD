-- for the confidence_Level algorithm.
-- use the table CONFIDENCE_LEVEL_FACTOR to adjust weightings, thresholds and precidence
-- not sure quite what precidence means!
-- this computes a score, then normalizes it to a 1-5 scale

CREATE OR REPLACE PACKAGE confidence_level_util
AUTHID current_user
AS
     TYPE t_score IS TABLE OF NUMBER
          index BY BINARY_INTEGER;

     TYPE t_factor IS TABLE OF confidence_level_factor%rowtype
          index BY BINARY_INTEGER;

    PROCEDURE calc_confidence_level (ani_experiment_id  IN NUMBER DEFAULT NULL);

    FUNCTION  get_confidence_score (ani_experiment_id IN NUMBER,
                                    avi_sql     IN VARCHAR2)
              RETURN NUMBER;

    FUNCTION weight_score (ati_scores IN t_score,
                            ati_factors IN  t_factor)
              RETURN NUMBER;

    PROCEDURE save_confidence_level( ani_experiment_id  IN  NUMBER,
                                     ani_confidence_level IN  NUMBER);

    PROCEDURE save_confidence_score (ani_score  IN  number,
                                     ani_experiment_id  IN  number,
                                     ani_factor_id IN NUMBER);

END confidence_level_util;
/

CREATE OR REPLACE PACKAGE BODY confidence_level_util

AS

    PROCEDURE calc_confidence_level (ani_experiment_id  IN NUMBER DEFAULT NULL)
    AS
        CURSOR cur_experiment
        IS
        SELECT experiment_id, assay_id
          FROM experiment
          WHERE experiment_id = ani_experiment_id
             OR ani_experiment_id IS NULL;

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
        OPEN cur_factor;
        FETCH cur_factor BULK COLLECT INTO lt_factor;
        CLOSE cur_factor;

        IF lt_factor.last IS NULL
        THEN return; END IF;

        FOR lr_experiment IN cur_experiment
        LOOP
            FOR i IN lt_factor.first ..lt_factor.last
            LOOP
                lt_score(i) := get_confidence_score (lr_experiment.experiment_id, lt_factor(i).score_sql);
                save_confidence_score (lt_score(i), lr_experiment.experiment_id, lt_factor(i).confidence_level_factor_id);
            END LOOP;

            ln_weighted_score := weight_score (lt_score, lt_factor);

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

    BEGIN
        -- all scores must be between 0 and 100
        -- but we don't know how big the numbers might be
        -- maybe we should do a test before we start for non-bounded numbers to how big the DB gets?
        -- we should add a "standard" or "best level" score in the list of factors...
        RETURN ln_score;

    END get_confidence_score;

    FUNCTION weight_score (ati_scores IN t_score,
                           ati_factors IN  t_factor)
              RETURN NUMBER
    AS
        ln_weighted_score  NUMBER;

    BEGIN
        -- first remove the threshold from the number
        -- then multiply by the weighting
        -- and divide by the precidence - THIS NEEDS SOME MORE THOUGHT.  **********  <<--

        -- then sum them and proportion the number into a 1..5 range
        -- none of the input scores is allowed to be null.
         RETURN ln_weighted_score;

    END weight_score;

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
