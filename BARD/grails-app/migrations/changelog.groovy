databaseChangeLog = {

    changeSet(author: "sbrudz (generated)", id: "1334581435395-1") {
        createTable(tableName: "ASSAY") {
            column(name: "ASSAY_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "PK_ASSAY", primaryKeyTablespace: "USERS")
            }

            column(name: "ASSAY_NAME", type: "VARCHAR2(128 BYTE)") {
                constraints(nullable: "false")
            }

            column(name: "ASSAY_STATUS_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValue: "1 ", name: "ASSAY_VERSION", type: "VARCHAR2(10 BYTE)") {
                constraints(nullable: "false")
            }

            column(name: "DESCRIPTION", type: "VARCHAR2(1000 BYTE)")

            column(name: "DESIGNED_BY", type: "VARCHAR2(100 BYTE)")

            column(defaultValueNumeric: "0", name: "VERSION", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueComputed: "sysdate", name: "DATE_CREATED", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "LAST_UPDATED", type: "TIMESTAMP(6)")

            column(name: "MODIFIED_BY", type: "VARCHAR2(40 BYTE)")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-2") {
        createTable(tableName: "ASSAY_STATUS") {
            column(name: "ASSAY_STATUS_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "PK_ASSAY_STATUS", primaryKeyTablespace: "USERS")
            }

            column(name: "STATUS", type: "VARCHAR2(20 BYTE)") {
                constraints(nullable: "false")
            }

            column(defaultValueNumeric: "0", name: "VERSION", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueComputed: "sysdate", name: "DATE_CREATED", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "LAST_UPDATED", type: "TIMESTAMP(6)")

            column(name: "MODIFIED_BY", type: "VARCHAR2(40 BYTE)")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-3") {
        createTable(tableName: "ELEMENT") {
            column(name: "ELEMENT_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "PK_ELEMENT", primaryKeyTablespace: "USERS")
            }

            column(name: "PARENT_ELEMENT_ID", type: "NUMBER(38,0)")

            column(name: "LABEL", type: "VARCHAR2(128 BYTE)") {
                constraints(nullable: "false")
            }

            column(name: "DESCRIPTION", type: "VARCHAR2(1000 BYTE)")

            column(name: "ABBREVIATION", type: "VARCHAR2(20 BYTE)")

            column(name: "ACRONYM", type: "VARCHAR2(20 BYTE)")

            column(name: "SYNONYMS", type: "VARCHAR2(1000 BYTE)")

            column(name: "ELEMENT_STATUS_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(name: "UNIT", type: "VARCHAR2(100 BYTE)")

            column(defaultValueNumeric: "0", name: "VERSION", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueComputed: "sysdate", name: "DATE_CREATED", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "LAST_UPDATED", type: "TIMESTAMP(6)")

            column(name: "MODIFIED_BY", type: "VARCHAR2(40 BYTE)")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-4") {
        createTable(tableName: "ELEMENT_STATUS") {
            column(name: "ELEMENT_STATUS_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "PK_ELEMENT_STATUS", primaryKeyTablespace: "USERS")
            }

            column(name: "ELEMENT_STATUS", type: "VARCHAR2(20 BYTE)") {
                constraints(nullable: "false")
            }

            column(name: "CAPABILITY", type: "VARCHAR2(256 BYTE)")

            column(defaultValueNumeric: "0", name: "VERSION", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueComputed: "sysdate", name: "DATE_CREATED", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "LAST_UPDATED", type: "TIMESTAMP(6)")

            column(name: "MODIFIED_BY", type: "VARCHAR2(40 BYTE)")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-5") {
        createTable(tableName: "EXPERIMENT") {
            column(name: "EXPERIMENT_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "PK_EXPERIMENT", primaryKeyTablespace: "USERS")
            }

            column(name: "EXPERIMENT_NAME", type: "VARCHAR2(256 BYTE)") {
                constraints(nullable: "false")
            }

            column(name: "ASSAY_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(name: "PROJECT_ID", type: "NUMBER(38,0)")

            column(name: "EXPERIMENT_STATUS_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(name: "RUN_DATE_FROM", type: "DATE")

            column(name: "RUN_DATE_TO", type: "DATE")

            column(name: "HOLD_UNTIL_DATE", type: "DATE")

            column(name: "DESCRIPTION", type: "VARCHAR2(1000 BYTE)")

            column(name: "SOURCE_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueNumeric: "0", name: "VERSION", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueComputed: "sysdate", name: "DATE_CREATED", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "LAST_UPDATED", type: "TIMESTAMP(6)")

            column(name: "MODIFIED_BY", type: "VARCHAR2(40 BYTE)")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-6") {
        createTable(tableName: "EXPERIMENT_STATUS") {
            column(name: "EXPERIMENT_STATUS_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "PK_EXPERIMENT_STATUS", primaryKeyTablespace: "USERS")
            }

            column(name: "STATUS", type: "VARCHAR2(20 BYTE)") {
                constraints(nullable: "false")
            }

            column(name: "CAPABILITY", type: "VARCHAR2(1000 BYTE)")

            column(defaultValueNumeric: "0", name: "VERSION", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueComputed: "sysdate", name: "DATE_CREATED", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "LAST_UPDATED", type: "TIMESTAMP(6)")

            column(name: "MODIFIED_BY", type: "VARCHAR2(40 BYTE)")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-7") {
        createTable(tableName: "EXTERNAL_ASSAY") {
            column(name: "EXTERNAL_SYSTEM_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(name: "ASSAY_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(name: "EXT_ASSAY_ID", type: "VARCHAR2(128 BYTE)") {
                constraints(nullable: "false")
            }

            column(defaultValueNumeric: "0", name: "VERSION", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueComputed: "sysdate", name: "DATE_CREATED", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "LAST_UPDATED", type: "TIMESTAMP(6)")

            column(name: "MODIFIED_BY", type: "VARCHAR2(40 BYTE)")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-8") {
        createTable(tableName: "EXTERNAL_SYSTEM") {
            column(name: "EXTERNAL_SYSTEM_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "PK_EXTERNAL_SYSTEM", primaryKeyTablespace: "USERS")
            }

            column(name: "SYSTEM_NAME", type: "VARCHAR2(128 BYTE)") {
                constraints(nullable: "false")
            }

            column(name: "OWNER", type: "VARCHAR2(128 BYTE)")

            column(name: "SYSTEM_URL", type: "VARCHAR2(1000 BYTE)")

            column(defaultValueNumeric: "0", name: "VERSION", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueComputed: "sysdate", name: "DATE_CREATED", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "LAST_UPDATED", type: "TIMESTAMP(6)")

            column(name: "MODIFIED_BY", type: "VARCHAR2(40 BYTE)")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-9") {
        createTable(tableName: "LABORATORY") {
            column(name: "LAB_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "PK_LABORATORY", primaryKeyTablespace: "USERS")
            }

            column(name: "LAB_NAME", type: "VARCHAR2(125 BYTE)") {
                constraints(nullable: "false")
            }

            column(name: "ABBREVIATION", type: "VARCHAR2(20 BYTE)")

            column(name: "DESCRIPTION", type: "VARCHAR2(1000 BYTE)")

            column(name: "LOCATION", type: "VARCHAR2(250 BYTE)")

            column(defaultValueNumeric: "0", name: "VERSION", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueComputed: "sysdate", name: "DATE_CREATED", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "LAST_UPDATED", type: "TIMESTAMP(6)")

            column(name: "MODIFIED_BY", type: "VARCHAR2(40 BYTE)")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-10") {
        createTable(tableName: "MEASURE") {
            column(name: "MEASURE_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "PK_MEASURE", primaryKeyTablespace: "USERS")
            }

            column(name: "ASSAY_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(name: "RESULT_TYPE_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(name: "ENTRY_UNIT", type: "VARCHAR2(100 BYTE)")

            column(name: "MEASURE_CONTEXT_ID", type: "NUMBER(38,0)")

            column(defaultValueNumeric: "0", name: "VERSION", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueComputed: "sysdate", name: "DATE_CREATED", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "LAST_UPDATED", type: "TIMESTAMP(6)")

            column(name: "MODIFIED_BY", type: "VARCHAR2(40 BYTE)")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-11") {
        createTable(tableName: "MEASURE_CONTEXT") {
            column(name: "MEASURE_CONTEXT_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "PK_MEASURE_CONTEXT", primaryKeyTablespace: "USERS")
            }

            column(name: "CONTEXT_NAME", type: "VARCHAR2(128 BYTE)") {
                constraints(nullable: "false")
            }

            column(defaultValueNumeric: "0", name: "VERSION", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueComputed: "sysdate", name: "DATE_CREATED", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "LAST_UPDATED", type: "TIMESTAMP(6)")

            column(name: "MODIFIED_BY", type: "VARCHAR2(40 BYTE)")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-12") {
        createTable(tableName: "MEASURE_CONTEXT_ITEM") {
            column(name: "MEASURE_CONTEXT_ITEM_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "PK_MEASURE_CONTEXT_ITEM", primaryKeyTablespace: "USERS")
            }

            column(name: "ASSAY_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(name: "MEASURE_CONTEXT_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(name: "GROUP_NO", type: "NUMBER(10,0)")

            column(name: "ATTRIBUTE_TYPE", type: "VARCHAR2(20 BYTE)") {
                constraints(nullable: "false")
            }

            column(name: "ATTRIBUTE_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(name: "QUALIFIER", type: "CHAR(2)")

            column(name: "VALUE_ID", type: "NUMBER(38,0)")

            column(name: "VALUE_DISPLAY", type: "VARCHAR2(256 BYTE)")

            column(name: "VALUE_NUM", type: "FLOAT(126)")

            column(name: "VALUE_MIN", type: "FLOAT(126)")

            column(name: "VALUE_MAX", type: "FLOAT(126)")

            column(defaultValueNumeric: "0", name: "VERSION", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueComputed: "sysdate", name: "DATE_CREATED", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "LAST_UPDATED", type: "TIMESTAMP(6)")

            column(name: "MODIFIED_BY", type: "VARCHAR2(40 BYTE)")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-13") {
        createTable(tableName: "ONTOLOGY") {
            column(name: "ONTOLOGY_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "PK_ONTOLOGY", primaryKeyTablespace: "USERS")
            }

            column(name: "ONTOLOGY_NAME", type: "VARCHAR2(256 BYTE)") {
                constraints(nullable: "false")
            }

            column(name: "ABBREVIATION", type: "VARCHAR2(20 BYTE)")

            column(name: "SYSTEM_URL", type: "VARCHAR2(1000 BYTE)")

            column(defaultValueNumeric: "0", name: "VERSION", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueComputed: "sysdate", name: "DATE_CREATED", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "LAST_UPDATED", type: "TIMESTAMP(6)")

            column(name: "MODIFIED_BY", type: "VARCHAR2(40 BYTE)")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-14") {
        createTable(tableName: "ONTOLOGY_ITEM") {
            column(name: "ONTOLOGY_ITEM_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "PK_ONTOLOGY_ITEM", primaryKeyTablespace: "USERS")
            }

            column(name: "ONTOLOGY_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(name: "ELEMENT_ID", type: "NUMBER(38,0)")

            column(name: "ITEM_REFERENCE", type: "CHAR(10)")

            column(name: "RESULT_TYPE_ID", type: "NUMBER(38,0)")

            column(defaultValueNumeric: "0", name: "VERSION", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueComputed: "sysdate", name: "DATE_CREATED", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "LAST_UPDATED", type: "TIMESTAMP(6)")

            column(name: "MODIFIED_BY", type: "VARCHAR2(40 BYTE)")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-15") {
        createTable(tableName: "PROJECT") {
            column(name: "PROJECT_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "PK_PROJECT", primaryKeyTablespace: "USERS")
            }

            column(name: "PROJECT_NAME", type: "VARCHAR2(256 BYTE)") {
                constraints(nullable: "false")
            }

            column(defaultValue: "Project", name: "GROUP_TYPE", type: "VARCHAR2(20 BYTE)") {
                constraints(nullable: "false")
            }

            column(name: "DESCRIPTION", type: "VARCHAR2(1000 BYTE)")

            column(defaultValueNumeric: "0", name: "VERSION", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueComputed: "sysdate", name: "DATE_CREATED", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "LAST_UPDATED", type: "TIMESTAMP(6)")

            column(name: "MODIFIED_BY", type: "VARCHAR2(40 BYTE)")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-16") {
        createTable(tableName: "PROJECT_ASSAY") {
            column(name: "ASSAY_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(name: "PROJECT_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(name: "STAGE", type: "VARCHAR2(20 BYTE)") {
                constraints(nullable: "false")
            }

            column(name: "SEQUENCE_NO", type: "NUMBER(10,0)")

            column(name: "PROMOTION_THRESHOLD", type: "FLOAT(126)")

            column(name: "PROMOTION_CRITERIA", type: "VARCHAR2(1000 BYTE)")

            column(defaultValueNumeric: "0", name: "VERSION", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueComputed: "sysdate", name: "DATE_CREATED", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "LAST_UPDATED", type: "TIMESTAMP(6)")

            column(name: "MODIFIED_BY", type: "VARCHAR2(40 BYTE)")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-17") {
        createTable(tableName: "PROTOCOL") {
            column(name: "PROTOCOL_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "PK_PROTOCOL", primaryKeyTablespace: "USERS")
            }

            column(name: "PROTOCOL_NAME", type: "VARCHAR2(500 BYTE)") {
                constraints(nullable: "false")
            }

            column(name: "PROTOCOL_DOCUMENT", type: "LONG RAW")

            column(name: "ASSAY_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueNumeric: "0", name: "VERSION", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueComputed: "sysdate", name: "DATE_CREATED", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "LAST_UPDATED", type: "TIMESTAMP(6)")

            column(name: "MODIFIED_BY", type: "VARCHAR2(40 BYTE)")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-18") {
        createTable(tableName: "QUALIFIER") {
            column(name: "QUALIFIER", type: "CHAR(2)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "PK_QUALIFIER", primaryKeyTablespace: "USERS")
            }

            column(name: "DESCRIPTION", type: "VARCHAR2(1000 BYTE)")

            column(defaultValueNumeric: "0", name: "VERSION", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueComputed: "sysdate", name: "DATE_CREATED", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "LAST_UPDATED", type: "TIMESTAMP(6)")

            column(name: "MODIFIED_BY", type: "VARCHAR2(40 BYTE)")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-19") {
        createTable(tableName: "RESULT") {
            column(name: "RESULT_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "PK_RESULT", primaryKeyTablespace: "USERS")
            }

            column(name: "VALUE_DISPLAY", type: "VARCHAR2(256 BYTE)")

            column(name: "VALUE_NUM", type: "FLOAT(126)")

            column(name: "VALUE_MIN", type: "FLOAT(126)")

            column(name: "VALUE_MAX", type: "FLOAT(126)")

            column(name: "QUALIFIER", type: "CHAR(2)")

            column(name: "RESULT_STATUS_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(name: "EXPERIMENT_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(name: "SUBSTANCE_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(name: "RESULT_CONTEXT_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(name: "ENTRY_UNIT", type: "VARCHAR2(100 BYTE)")

            column(name: "RESULT_TYPE_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueNumeric: "0", name: "VERSION", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueComputed: "sysdate", name: "DATE_CREATED", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "LAST_UPDATED", type: "TIMESTAMP(6)")

            column(name: "MODIFIED_BY", type: "VARCHAR2(40 BYTE)")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-20") {
        createTable(tableName: "RESULT_CONTEXT") {
            column(name: "RESULT_CONTEXT_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "PK_RESULT_CONTEXT", primaryKeyTablespace: "USERS")
            }

            column(name: "CONTEXT_NAME", type: "VARCHAR2(125 BYTE)")

            column(defaultValueNumeric: "0", name: "VERSION", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueComputed: "sysdate", name: "DATE_CREATED", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "LAST_UPDATED", type: "TIMESTAMP(6)")

            column(name: "MODIFIED_BY", type: "VARCHAR2(40 BYTE)")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-21") {
        createTable(tableName: "RESULT_CONTEXT_ITEM") {
            column(name: "RESULT_CONTEXT_ITEM_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "PK_RESULT_CONTEXT_ITEM", primaryKeyTablespace: "USERS")
            }

            column(name: "EXPERIMENT_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(name: "RESULT_CONTEXT_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(name: "GROUP_NO", type: "NUMBER(10,0)")

            column(name: "ATTRIBUTE_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(name: "VALUE_ID", type: "NUMBER(38,0)")

            column(name: "QUALIFIER", type: "CHAR(2)")

            column(name: "VALUE_DISPLAY", type: "VARCHAR2(256 BYTE)")

            column(name: "VALUE_NUM", type: "FLOAT(126)")

            column(name: "VALUE_MIN", type: "FLOAT(126)")

            column(name: "VALUE_MAX", type: "FLOAT(126)")

            column(defaultValueNumeric: "0", name: "VERSION", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueComputed: "sysdate", name: "DATE_CREATED", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "LAST_UPDATED", type: "TIMESTAMP(6)")

            column(name: "MODIFIED_BY", type: "VARCHAR2(40 BYTE)")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-22") {
        createTable(tableName: "RESULT_HIERARCHY") {
            column(name: "RESULT_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(name: "PARENT_RESULT_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(name: "HIERARCHY_TYPE", type: "VARCHAR2(10 BYTE)") {
                constraints(nullable: "false")
            }

            column(defaultValueNumeric: "0", name: "VERSION", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueComputed: "sysdate", name: "DATE_CREATED", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "LAST_UPDATED", type: "TIMESTAMP(6)")

            column(name: "MODIFIED_BY", type: "VARCHAR2(40 BYTE)")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-23") {
        createTable(tableName: "RESULT_STATUS") {
            column(name: "RESULT_STATUS_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "PK_RESULT_STATUS", primaryKeyTablespace: "USERS")
            }

            column(name: "STATUS", type: "VARCHAR2(20 BYTE)") {
                constraints(nullable: "false")
            }

            column(defaultValueNumeric: "0", name: "VERSION", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueComputed: "sysdate", name: "DATE_CREATED", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "LAST_UPDATED", type: "TIMESTAMP(6)")

            column(name: "MODIFIED_BY", type: "VARCHAR2(40 BYTE)")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-24") {
        createTable(tableName: "RESULT_TYPE") {
            column(name: "RESULT_TYPE_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "PK_RESULT_TYPE", primaryKeyTablespace: "USERS")
            }

            column(name: "PARENT_RESULT_TYPE_ID", type: "NUMBER(38,0)")

            column(name: "RESULT_TYPE_NAME", type: "VARCHAR2(128 BYTE)") {
                constraints(nullable: "false")
            }

            column(name: "DESCRIPTION", type: "VARCHAR2(1000 BYTE)")

            column(name: "RESULT_TYPE_STATUS_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(name: "BASE_UNIT", type: "VARCHAR2(100 BYTE)")

            column(defaultValueNumeric: "0", name: "VERSION", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueComputed: "sysdate", name: "DATE_CREATED", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "LAST_UPDATED", type: "TIMESTAMP(6)")

            column(name: "MODIFIED_BY", type: "VARCHAR2(40 BYTE)")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-25") {
        createTable(tableName: "STAGE") {
            column(name: "STAGE", type: "VARCHAR2(20 BYTE)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "PK_STAGE", primaryKeyTablespace: "USERS")
            }

            column(name: "DESCRIPTION", type: "VARCHAR2(1000 BYTE)")

            column(defaultValueNumeric: "0", name: "VERSION", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueComputed: "sysdate", name: "DATE_CREATED", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "LAST_UPDATED", type: "TIMESTAMP(6)")

            column(name: "MODIFIED_BY", type: "VARCHAR2(40 BYTE)")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-26") {
        createTable(tableName: "SUBSTANCE") {
            column(name: "SUBSTANCE_ID", type: "NUMBER(38,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "PK_SUBSTANCE", primaryKeyTablespace: "USERS")
            }

            column(name: "COMPOUND_ID", type: "NUMBER(10,0)")

            column(name: "SMILES", type: "VARCHAR2(4000 BYTE)")

            column(name: "MOLECULAR_WEIGHT", type: "NUMBER(10,3)")

            column(name: "SUBSTANCE_TYPE", type: "VARCHAR2(20 BYTE)") {
                constraints(nullable: "false")
            }

            column(defaultValueNumeric: "0", name: "VERSION", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueComputed: "sysdate", name: "DATE_CREATED", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "LAST_UPDATED", type: "TIMESTAMP(6)")

            column(name: "MODIFIED_BY", type: "VARCHAR2(40 BYTE)")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-27") {
        createTable(tableName: "UNIT") {
            column(name: "UNIT", type: "VARCHAR2(100 BYTE)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "PK_UNIT", primaryKeyTablespace: "USERS")
            }

            column(name: "DESCRIPTION", type: "VARCHAR2(1000 BYTE)")

            column(defaultValueNumeric: "0", name: "VERSION", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueComputed: "sysdate", name: "DATE_CREATED", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "LAST_UPDATED", type: "TIMESTAMP(6)")

            column(name: "MODIFIED_BY", type: "VARCHAR2(40 BYTE)")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-28") {
        createTable(tableName: "UNIT_CONVERSION") {
            column(name: "FROM_UNIT", type: "VARCHAR2(100 BYTE)") {
                constraints(nullable: "false")
            }

            column(name: "TO_UNIT", type: "VARCHAR2(100 BYTE)") {
                constraints(nullable: "false")
            }

            column(name: "MULTIPLIER", type: "FLOAT(126)")

            column(name: "OFFSET", type: "FLOAT(126)")

            column(name: "FORMULA", type: "VARCHAR2(256 BYTE)")

            column(defaultValueNumeric: "0", name: "VERSION", type: "NUMBER(38,0)") {
                constraints(nullable: "false")
            }

            column(defaultValueComputed: "sysdate", name: "DATE_CREATED", type: "TIMESTAMP(6)") {
                constraints(nullable: "false")
            }

            column(name: "LAST_UPDATED", type: "TIMESTAMP(6)")

            column(name: "MODIFIED_BY", type: "VARCHAR2(40 BYTE)")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-29") {
        addPrimaryKey(columnNames: "EXTERNAL_SYSTEM_ID, ASSAY_ID", constraintName: "PK_EXTERNAL_ASSAY", tableName: "EXTERNAL_ASSAY", tablespace: "USERS")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-30") {
        addPrimaryKey(columnNames: "ASSAY_ID, PROJECT_ID", constraintName: "PK_PROJECT_ASSAY", tableName: "PROJECT_ASSAY", tablespace: "USERS")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-31") {
        addPrimaryKey(columnNames: "RESULT_ID, PARENT_RESULT_ID", constraintName: "PK_RESULT_HIERARCHY", tableName: "RESULT_HIERARCHY", tablespace: "USERS")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-32") {
        addPrimaryKey(columnNames: "FROM_UNIT, TO_UNIT", constraintName: "PK_UNIT_CONVERSION", tableName: "UNIT_CONVERSION", tablespace: "USERS")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-33") {
        createIndex(indexName: "AK_ASSAY_STATUS", tableName: "ASSAY_STATUS", tablespace: "USERS", unique: "true") {
            column(name: "STATUS")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-34") {
        createIndex(indexName: "AK_MEASURE_CONTEXT_ITEM", tableName: "MEASURE_CONTEXT_ITEM", tablespace: "USERS", unique: "true") {
            column(name: "MEASURE_CONTEXT_ID")

            column(name: "GROUP_NO")

            column(name: "ATTRIBUTE_ID")

            column(name: "VALUE_DISPLAY")
        }
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-35") {
        addForeignKeyConstraint(baseColumnNames: "ASSAY_STATUS_ID", baseTableName: "ASSAY", baseTableSchemaName: "BARD_DEV", constraintName: "FK_ASSAY_ASSAY_STATUS_ID", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "ASSAY_STATUS_ID", referencedTableName: "ASSAY_STATUS", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-36") {
        addForeignKeyConstraint(baseColumnNames: "ELEMENT_STATUS_ID", baseTableName: "ELEMENT", baseTableSchemaName: "BARD_DEV", constraintName: "FK_ELEMENT_ELEMENT_STATUS", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "ELEMENT_STATUS_ID", referencedTableName: "ELEMENT_STATUS", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-37") {
        addForeignKeyConstraint(baseColumnNames: "PARENT_ELEMENT_ID", baseTableName: "ELEMENT", baseTableSchemaName: "BARD_DEV", constraintName: "FK_ELEMENT_PARENT_ELEMENT", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "ELEMENT_ID", referencedTableName: "ELEMENT", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-38") {
        addForeignKeyConstraint(baseColumnNames: "UNIT", baseTableName: "ELEMENT", baseTableSchemaName: "BARD_DEV", constraintName: "FK_ELEMENT_UNIT", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "UNIT", referencedTableName: "UNIT", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-39") {
        addForeignKeyConstraint(baseColumnNames: "ASSAY_ID", baseTableName: "EXPERIMENT", baseTableSchemaName: "BARD_DEV", constraintName: "FK_EXPERIMENT_ASSAY", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "ASSAY_ID", referencedTableName: "ASSAY", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-40") {
        addForeignKeyConstraint(baseColumnNames: "EXPERIMENT_STATUS_ID", baseTableName: "EXPERIMENT", baseTableSchemaName: "BARD_DEV", constraintName: "FK_EXPERIMENT_EXPRT_STATUS", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "EXPERIMENT_STATUS_ID", referencedTableName: "EXPERIMENT_STATUS", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-41") {
        addForeignKeyConstraint(baseColumnNames: "PROJECT_ID", baseTableName: "EXPERIMENT", baseTableSchemaName: "BARD_DEV", constraintName: "FK_PROJECT_EXPERIMENT", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "PROJECT_ID", referencedTableName: "PROJECT", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-42") {
        addForeignKeyConstraint(baseColumnNames: "SOURCE_ID", baseTableName: "EXPERIMENT", baseTableSchemaName: "BARD_DEV", constraintName: "FK_EXPERIMENT_SOURCE_LAB", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "LAB_ID", referencedTableName: "LABORATORY", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-43") {
        addForeignKeyConstraint(baseColumnNames: "ASSAY_ID", baseTableName: "EXTERNAL_ASSAY", baseTableSchemaName: "BARD_DEV", constraintName: "FK_EXT_ASSAY_ASSAY", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "ASSAY_ID", referencedTableName: "ASSAY", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-44") {
        addForeignKeyConstraint(baseColumnNames: "EXTERNAL_SYSTEM_ID", baseTableName: "EXTERNAL_ASSAY", baseTableSchemaName: "BARD_DEV", constraintName: "FK_EXT_ASSAY_EXT_SYSTEM", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "EXTERNAL_SYSTEM_ID", referencedTableName: "EXTERNAL_SYSTEM", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-45") {
        addForeignKeyConstraint(baseColumnNames: "ASSAY_ID", baseTableName: "MEASURE", baseTableSchemaName: "BARD_DEV", constraintName: "FK_MEASURE_ASSAY", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "ASSAY_ID", referencedTableName: "ASSAY", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-46") {
        addForeignKeyConstraint(baseColumnNames: "ENTRY_UNIT", baseTableName: "MEASURE", baseTableSchemaName: "BARD_DEV", constraintName: "FK_MEASURE_UNIT", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "UNIT", referencedTableName: "UNIT", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-47") {
        addForeignKeyConstraint(baseColumnNames: "MEASURE_CONTEXT_ID", baseTableName: "MEASURE", baseTableSchemaName: "BARD_DEV", constraintName: "FK_MEASURE_M_CONTEXT_ITEM", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "MEASURE_CONTEXT_ID", referencedTableName: "MEASURE_CONTEXT", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-48") {
        addForeignKeyConstraint(baseColumnNames: "RESULT_TYPE_ID", baseTableName: "MEASURE", baseTableSchemaName: "BARD_DEV", constraintName: "FK_MEASURE_RESULT_TYPE", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "RESULT_TYPE_ID", referencedTableName: "RESULT_TYPE", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-49") {
        addForeignKeyConstraint(baseColumnNames: "ASSAY_ID", baseTableName: "MEASURE_CONTEXT_ITEM", baseTableSchemaName: "BARD_DEV", constraintName: "FK_M_CONTEXT_ITEM_ASSAY", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "ASSAY_ID", referencedTableName: "ASSAY", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-50") {
        addForeignKeyConstraint(baseColumnNames: "ATTRIBUTE_ID", baseTableName: "MEASURE_CONTEXT_ITEM", baseTableSchemaName: "BARD_DEV", constraintName: "FK_M_CONTEXT_ITEM_ATTRIBUTE", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "ELEMENT_ID", referencedTableName: "ELEMENT", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-51") {
        addForeignKeyConstraint(baseColumnNames: "MEASURE_CONTEXT_ID", baseTableName: "MEASURE_CONTEXT_ITEM", baseTableSchemaName: "BARD_DEV", constraintName: "FK_M_CONTEXT_ITEM_M_CONTEXT", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "MEASURE_CONTEXT_ID", referencedTableName: "MEASURE_CONTEXT", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-52") {
        addForeignKeyConstraint(baseColumnNames: "QUALIFIER", baseTableName: "MEASURE_CONTEXT_ITEM", baseTableSchemaName: "BARD_DEV", constraintName: "FK_M_CONTEXT_ITEM_QUALIFIER", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "QUALIFIER", referencedTableName: "QUALIFIER", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-53") {
        addForeignKeyConstraint(baseColumnNames: "VALUE_ID", baseTableName: "MEASURE_CONTEXT_ITEM", baseTableSchemaName: "BARD_DEV", constraintName: "FK_M_CONTEXT_ITEM_VALUE", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "ELEMENT_ID", referencedTableName: "ELEMENT", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-54") {
        addForeignKeyConstraint(baseColumnNames: "ELEMENT_ID", baseTableName: "ONTOLOGY_ITEM", baseTableSchemaName: "BARD_DEV", constraintName: "FK_ONTOLOGY_ITEM_ELEMENT", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "ELEMENT_ID", referencedTableName: "ELEMENT", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-55") {
        addForeignKeyConstraint(baseColumnNames: "ONTOLOGY_ID", baseTableName: "ONTOLOGY_ITEM", baseTableSchemaName: "BARD_DEV", constraintName: "FK_ONTOLOGY_ITEM_ONTOLOGY", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "ONTOLOGY_ID", referencedTableName: "ONTOLOGY", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-56") {
        addForeignKeyConstraint(baseColumnNames: "RESULT_TYPE_ID", baseTableName: "ONTOLOGY_ITEM", baseTableSchemaName: "BARD_DEV", constraintName: "FK_ONTOLOGY_ITEM_RESULT_TYPE", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "RESULT_TYPE_ID", referencedTableName: "RESULT_TYPE", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-57") {
        addForeignKeyConstraint(baseColumnNames: "ASSAY_ID", baseTableName: "PROJECT_ASSAY", baseTableSchemaName: "BARD_DEV", constraintName: "FK_PROJECT_ASSAY_ASSAY", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "ASSAY_ID", referencedTableName: "ASSAY", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-58") {
        addForeignKeyConstraint(baseColumnNames: "PROJECT_ID", baseTableName: "PROJECT_ASSAY", baseTableSchemaName: "BARD_DEV", constraintName: "FK_PROJECT_ASSAY_PROJECT", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "PROJECT_ID", referencedTableName: "PROJECT", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-59") {
        addForeignKeyConstraint(baseColumnNames: "STAGE", baseTableName: "PROJECT_ASSAY", baseTableSchemaName: "BARD_DEV", constraintName: "FK_PROJECT_ASSAY_STAGE", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "STAGE", referencedTableName: "STAGE", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-60") {
        addForeignKeyConstraint(baseColumnNames: "ASSAY_ID", baseTableName: "PROTOCOL", baseTableSchemaName: "BARD_DEV", constraintName: "FK_PROTOCOL_ASSAY", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "ASSAY_ID", referencedTableName: "ASSAY", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-61") {
        addForeignKeyConstraint(baseColumnNames: "ENTRY_UNIT", baseTableName: "RESULT", baseTableSchemaName: "BARD_DEV", constraintName: "FK_RESULT_UNIT", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "UNIT", referencedTableName: "UNIT", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-62") {
        addForeignKeyConstraint(baseColumnNames: "EXPERIMENT_ID", baseTableName: "RESULT", baseTableSchemaName: "BARD_DEV", constraintName: "FK_RESULT_EXPERIMENT", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "EXPERIMENT_ID", referencedTableName: "EXPERIMENT", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-63") {
        addForeignKeyConstraint(baseColumnNames: "QUALIFIER", baseTableName: "RESULT", baseTableSchemaName: "BARD_DEV", constraintName: "FK_RESULT_QUALIFIER", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "QUALIFIER", referencedTableName: "QUALIFIER", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-64") {
        addForeignKeyConstraint(baseColumnNames: "RESULT_CONTEXT_ID", baseTableName: "RESULT", baseTableSchemaName: "BARD_DEV", constraintName: "FK_RESULT_RESULT_CONTEXT", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "RESULT_CONTEXT_ID", referencedTableName: "RESULT_CONTEXT", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-65") {
        addForeignKeyConstraint(baseColumnNames: "RESULT_STATUS_ID", baseTableName: "RESULT", baseTableSchemaName: "BARD_DEV", constraintName: "FK_RESULT_RESULT_STATUS", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "RESULT_STATUS_ID", referencedTableName: "RESULT_STATUS", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-66") {
        addForeignKeyConstraint(baseColumnNames: "RESULT_TYPE_ID", baseTableName: "RESULT", baseTableSchemaName: "BARD_DEV", constraintName: "FK_RESULT_RESULT_TYPE", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "RESULT_TYPE_ID", referencedTableName: "RESULT_TYPE", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-67") {
        addForeignKeyConstraint(baseColumnNames: "SUBSTANCE_ID", baseTableName: "RESULT", baseTableSchemaName: "BARD_DEV", constraintName: "FK_RESULT_SUBSTANCE", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "SUBSTANCE_ID", referencedTableName: "SUBSTANCE", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-68") {
        addForeignKeyConstraint(baseColumnNames: "ATTRIBUTE_ID", baseTableName: "RESULT_CONTEXT_ITEM", baseTableSchemaName: "BARD_DEV", constraintName: "FK_R_CONTEXT_ITEM_ATTRIBUTE", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "ELEMENT_ID", referencedTableName: "ELEMENT", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-69") {
        addForeignKeyConstraint(baseColumnNames: "EXPERIMENT_ID", baseTableName: "RESULT_CONTEXT_ITEM", baseTableSchemaName: "BARD_DEV", constraintName: "FK_R_CONTEXT_ITEM_EXPERIMENT", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "EXPERIMENT_ID", referencedTableName: "EXPERIMENT", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-70") {
        addForeignKeyConstraint(baseColumnNames: "QUALIFIER", baseTableName: "RESULT_CONTEXT_ITEM", baseTableSchemaName: "BARD_DEV", constraintName: "FK_R_CONTEXT_ITEM_QUALIFIER", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "QUALIFIER", referencedTableName: "QUALIFIER", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-71") {
        addForeignKeyConstraint(baseColumnNames: "RESULT_CONTEXT_ID", baseTableName: "RESULT_CONTEXT_ITEM", baseTableSchemaName: "BARD_DEV", constraintName: "FK_R_CONTEXT_ITEM_R_CONTEXT", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "RESULT_CONTEXT_ID", referencedTableName: "RESULT_CONTEXT", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-72") {
        addForeignKeyConstraint(baseColumnNames: "VALUE_ID", baseTableName: "RESULT_CONTEXT_ITEM", baseTableSchemaName: "BARD_DEV", constraintName: "FK_R_CONTEXT_ITEM_VALUE", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "ELEMENT_ID", referencedTableName: "ELEMENT", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-73") {
        addForeignKeyConstraint(baseColumnNames: "PARENT_RESULT_ID", baseTableName: "RESULT_HIERARCHY", baseTableSchemaName: "BARD_DEV", constraintName: "FK_RESULT_HIERARCHY_RESULT", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "RESULT_ID", referencedTableName: "RESULT", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-74") {
        addForeignKeyConstraint(baseColumnNames: "RESULT_ID", baseTableName: "RESULT_HIERARCHY", baseTableSchemaName: "BARD_DEV", constraintName: "FK_RESULT_HIERARCHY_RSLT_PRNT", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "RESULT_ID", referencedTableName: "RESULT", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-75") {
        addForeignKeyConstraint(baseColumnNames: "BASE_UNIT", baseTableName: "RESULT_TYPE", baseTableSchemaName: "BARD_DEV", constraintName: "FK_RESULT_TYPE_UNIT", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "UNIT", referencedTableName: "UNIT", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-76") {
        addForeignKeyConstraint(baseColumnNames: "PARENT_RESULT_TYPE_ID", baseTableName: "RESULT_TYPE", baseTableSchemaName: "BARD_DEV", constraintName: "FK_RESULT_TYPE_RSLT_TYP_PRNT", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "RESULT_TYPE_ID", referencedTableName: "RESULT_TYPE", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-77") {
        addForeignKeyConstraint(baseColumnNames: "RESULT_TYPE_STATUS_ID", baseTableName: "RESULT_TYPE", baseTableSchemaName: "BARD_DEV", constraintName: "FK_RESULT_TYPE_ELEMENT_STATUS", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "ELEMENT_STATUS_ID", referencedTableName: "ELEMENT_STATUS", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-78") {
        addForeignKeyConstraint(baseColumnNames: "FROM_UNIT", baseTableName: "UNIT_CONVERSION", baseTableSchemaName: "BARD_DEV", constraintName: "FK_UNIT_CONVERSION_FROM_UNIT", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "UNIT", referencedTableName: "UNIT", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-79") {
        addForeignKeyConstraint(baseColumnNames: "TO_UNIT", baseTableName: "UNIT_CONVERSION", baseTableSchemaName: "BARD_DEV", constraintName: "FK_UNIT_CONVERSION_TO_UNIT", deferrable: "false", initiallyDeferred: "false", onDelete: "RESTRICT", referencedColumnNames: "UNIT", referencedTableName: "UNIT", referencedTableSchemaName: "BARD_DEV", referencesUniqueColumn: "false")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-80") {
        createSequence(schemaName: "BARD_DEV", sequenceName: "ASSAY_ID_SEQ")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-81") {
        createSequence(schemaName: "BARD_DEV", sequenceName: "ASSAY_STATUS_ID_SEQ")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-82") {
        createSequence(schemaName: "BARD_DEV", sequenceName: "ELEMENT_ID_SEQ")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-83") {
        createSequence(schemaName: "BARD_DEV", sequenceName: "ELEMENT_STATUS_ID_SEQ")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-84") {
        createSequence(schemaName: "BARD_DEV", sequenceName: "EXPERIMENT_ID_SEQ")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-85") {
        createSequence(schemaName: "BARD_DEV", sequenceName: "EXPERIMENT_STATUS_ID_SEQ")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-86") {
        createSequence(schemaName: "BARD_DEV", sequenceName: "EXTERNAL_SYSTEM_ID_SEQ")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-87") {
        createSequence(schemaName: "BARD_DEV", sequenceName: "LABORATORY_ID_SEQ")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-88") {
        createSequence(schemaName: "BARD_DEV", sequenceName: "MEASURE_CONTEXT_ID_SEQ")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-89") {
        createSequence(schemaName: "BARD_DEV", sequenceName: "MEASURE_CONTEXT_ITEM_ID_SEQ")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-90") {
        createSequence(schemaName: "BARD_DEV", sequenceName: "MEASURE_ID_SEQ")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-91") {
        createSequence(schemaName: "BARD_DEV", sequenceName: "ONTOLOGY_ID_SEQ")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-92") {
        createSequence(schemaName: "BARD_DEV", sequenceName: "ONTOLOGY_ITEM_ID_SEQ")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-93") {
        createSequence(schemaName: "BARD_DEV", sequenceName: "PROJECT_ID_SEQ")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-94") {
        createSequence(schemaName: "BARD_DEV", sequenceName: "PROTOCOL_ID_SEQ")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-95") {
        createSequence(schemaName: "BARD_DEV", sequenceName: "RESULT_CONTEXT_ID_SEQ")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-96") {
        createSequence(schemaName: "BARD_DEV", sequenceName: "RESULT_CONTEXT_ITEM_ID_SEQ")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-97") {
        createSequence(schemaName: "BARD_DEV", sequenceName: "RESULT_ID_SEQ")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-98") {
        createSequence(schemaName: "BARD_DEV", sequenceName: "RESULT_STATUS_ID_SEQ")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-99") {
        createSequence(schemaName: "BARD_DEV", sequenceName: "RESULT_TYPE_ID_SEQ")
    }

    changeSet(author: "sbrudz (generated)", id: "1334581435395-100") {
        createSequence(schemaName: "BARD_DEV", sequenceName: "SUBSTANCE_ID_SEQ")
    }
}
