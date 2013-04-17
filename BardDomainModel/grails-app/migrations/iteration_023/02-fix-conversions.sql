-- cleanup unit_conversion
-- have a look at the problem
SELECT f.label from_unit,
    t.label to_unit,
    uc.*
FROM unit_conversion uc,
    element f,
    element t
WHERE f.element_id = uc.from_unit_id
  AND t.element_id = uc.to_unit_id
ORDER BY uc.from_unit_id
/


-- 1 get the form-to the right way round!
UPDATE unit_conversion
SET from_unit_id = to_unit_id,
    to_unit_id = from_unit_id
WHERE offset IS NULL
/

-- AND REVERT THE ONES THAT WERE CORRECT
UPDATE unit_conversion
SET from_unit_id = to_unit_id,
    to_unit_id = from_unit_id
WHERE offset IS NULL
  AND FROM_UNIT_ID IN (1051,1052,1053,1054,1055)
  AND TO_UNIT_ID IN (1051,1052,1053,1054,1055)
/

UPDATE unit_conversion
SET from_unit_id = to_unit_id,
    to_unit_id = from_unit_id
WHERE offset IS NULL
  AND FROM_UNIT_ID IN (1057,1058,1059)
  AND TO_UNIT_ID IN (1057,1058,1059)
/


-- 2 add the conversions for deg F and C

INSERT INTO unit_conversion
    (unit_conversion_id,
    from_unit_id,
    to_unit_id,
    multiplier,
    offset,
    formula,
    modified_by)
VALUES
    (unit_conversion_id_seq.nextval,
    1168,   -- fahrenheit
    1167,   -- celsius
    .555555555,
    -32,
    '(value + offset) * multiplier',
     'schatwin')
/

INSERT INTO unit_conversion
    (unit_conversion_id,
    from_unit_id,
    to_unit_id,
    multiplier,
    offset,
    formula,
    modified_by)
VALUES
    (unit_conversion_id_seq.nextval,
    1167,   -- celsius
    1168,   -- fahrenheit
    1.8,
    32,
    '(value * multiplier) + offset',
     'schatwin')
/

INSERT INTO unit_conversion
    (unit_conversion_id,
    from_unit_id,
    to_unit_id,
    multiplier,
    offset,
    formula,
    modified_by)
VALUES
    (unit_conversion_id_seq.nextval,
    1061,   -- kelvin
    1168,   -- fahrenheit
    1.8,
    -459.63,
    '(value * multiplier) + offset',
     'schatwin')
/

INSERT INTO unit_conversion
    (unit_conversion_id,
    from_unit_id,
    to_unit_id,
    multiplier,
    offset,
    formula,
    modified_by)
VALUES
    (unit_conversion_id_seq.nextval,
    1168,   -- fahrenheit
    1061,   -- kelvin
    .55555,
    459.63,
    '(value + offset) * multiplier',
     'schatwin')
/

-- 4 add the basic formulae ready for not-null columns
BEGIN
bard_context.set_username('schatwin');
end;
/

UPDATE unit_conversion
SET formula = 'value * multiplier'
WHERE multiplier != 1
  AND formula IS null
/

UPDATE unit_conversion
SET formula = 'value + offset'
WHERE offset != 0
  AND formula IS null
/

UPDATE unit_conversion
SET formula = 'value'
WHERE (offset = 0 OR multiplier = 1)
  AND formula IS null
/

COMMIT
/
