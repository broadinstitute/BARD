package iteration_033

databaseChangeLog = {
    // Tables from core shoping-cart plugin
    changeSet(author: "ddurkin (generated)", id: "1376496330012-1", context: "standard") {
        createTable(tableName: "SC_QUANTITY") {
            column(name: "id", type: "number(19,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SC_QUANTITY_PK")
            }

            column(name: "version", type: "number(19,0)") {
                constraints(nullable: "false")
            }

            column(name: "shopping_cart_id", type: "number(19,0)") {
                constraints(nullable: "false")
            }

            column(name: "shopping_item_id", type: "number(19,0)") {
                constraints(nullable: "false")
            }

            column(name: "value", type: "number(10,0)") {
                constraints(nullable: "false")
            }
        }
        createSequence(sequenceName: "SC_QUANTITY_ID_SEQ")
    }
    changeSet(author: "ddurkin (generated)", id: "1376496330012-3", context: "standard") {
        createTable(tableName: "SC_SHOPPABLE") {
            column(name: "id", type: "number(19,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SC_SHOPPABLE_PK")
            }

            column(name: "version", type: "number(19,0)") {
                constraints(nullable: "false")
            }

            column(name: "shopping_item_id", type: "number(19,0)")

            column(name: "class", type: "varchar2(255 char)") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "varchar2(255 char)")
        }
        addColumn(tableName: "SC_SHOPPABLE") {
            column(name: "external_id", type: "number(19,0)")
        }
        addColumn(tableName: "SC_SHOPPABLE") {
            column(name: "num_assay_active", type: "number(10,0)")
        }
        addColumn(tableName: "SC_SHOPPABLE") {
            column(name: "num_assay_tested", type: "number(10,0)")
        }
        addColumn(tableName: "SC_SHOPPABLE") {
            column(name: "query_item_type", type: "varchar2(255 char)")
        }
        addColumn(tableName: "SC_SHOPPABLE") {
            column(name: "smiles", type: "varchar2(4000 char)")
        }
        createSequence(sequenceName: "SC_SHOPPABLE_ID_SEQ")
    }
    changeSet(author: "ddurkin (generated)", id: "1376496330012-4", context: "standard") {
        createTable(tableName: "SC_SHOPPING_CART") {
            column(name: "id", type: "number(19,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SC_SHOPPING_CART_PK")
            }

            column(name: "version", type: "number(19,0)") {
                constraints(nullable: "false")
            }

            column(name: "checked_out", type: "number(1,0)") {
                constraints(nullable: "false")
            }

            column(name: "lasturl", type: "varchar2(255 char)")

            column(name: "sessionid", type: "varchar2(255 char)") {
                constraints(nullable: "false")
            }
        }
        createSequence(sequenceName: "SC_SHOPPING_CART_ID_SEQ")
    }
    changeSet(author: "ddurkin (generated)", id: "1376496330012-2", context: "standard") {
        createTable(tableName: "SC_INTERFACE_TEST_PROD") {
            column(name: "id", type: "number(19,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SC_INTERFACE_TEST_PROD_PK")
            }

            column(name: "version", type: "number(19,0)") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "varchar2(255 char)") {
                constraints(nullable: "false")
            }

            column(name: "shopping_item_id", type: "number(19,0)") {
                constraints(nullable: "false")
            }
        }
        createSequence(sequenceName: "SC_INTERFACE_TEST_PROD_ID_SEQ")
    }
    changeSet(author: "ddurkin (generated)", id: "1376496330012-6", context: "standard") {
        createTable(tableName: "SC_SHOPPING_ITEM") {
            column(name: "id", type: "number(19,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SC_SHOPPING_ITEM_PK")
            }

            column(name: "version", type: "number(19,0)") {
                constraints(nullable: "false")
            }
        }
        createSequence(sequenceName: "SC_SHOPPING_ITEM_ID_SEQ")
    }
    changeSet(author: "ddurkin (generated)", id: "1376496330012-5", context: "standard") {
        createTable(tableName: "SC_SC_SHOPPING_ITEM") {
            column(name: "SC_SHOPPING_CART_ID", type: "number(19,0)")

            column(name: "SC_SHOPPING_ITEM_ID", type: "number(19,0)")
        }
    }
    changeSet(author: "ddurkin (generated)", id: "1376496330012-7", context: "standard") {
        addForeignKeyConstraint(baseColumnNames: "shopping_cart_id", baseTableName: "SC_QUANTITY", constraintName: "FKB368648BCC86F46F", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "SC_SHOPPING_CART", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "shopping_item_id", baseTableName: "SC_QUANTITY", constraintName: "FKB368648B29A10B8F", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "SC_SHOPPING_ITEM", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "shopping_item_id", baseTableName: "SC_INTERFACE_TEST_PROD", constraintName: "FKB22C606F29A10B8F", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "SC_SHOPPING_ITEM", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "shopping_item_id", baseTableName: "SC_SHOPPABLE", constraintName: "FK83826C9429A10B8F", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "SC_SHOPPING_ITEM", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "SC_SHOPPING_CART_ID", baseTableName: "SC_SC_SHOPPING_ITEM", constraintName: "SC_SC_SHOPPING_ITEM_SC_ID_FK", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "SC_SHOPPING_CART", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "SC_SHOPPING_ITEM_ID", baseTableName: "SC_SC_SHOPPING_ITEM", constraintName: "SC_SC_SHOPPING_ITEM_SI_ID_FK", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "SC_SHOPPING_ITEM", referencesUniqueColumn: "false")
    }

    // Extension tables to shopping cart for BARD
    changeSet(author: "ddurkin (generated)", id: "1377120073229-4") {
        createTable(tableName: "MOL_SS_HILL_CURVE_VALUE_HOLDER") {
            column(name: "id", type: "number(19,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "HILL_CURVE_VALUE_HOLDER_PK")
            }

            column(name: "version", type: "number(19,0)") {
                constraints(nullable: "false")
            }

            column(name: "coef", type: "double precision")

            column(name: "identifier", type: "varchar2(255 char)") {
                constraints(nullable: "false")
            }

            column(name: "qualifier", type: "varchar2(255 char)") {
                constraints(nullable: "false")
            }

            column(name: "s0", type: "double precision")

            column(name: "s_inf", type: "double precision")

            column(name: "slope", type: "double precision")

            column(name: "sub_column_index", type: "number(10,0)") {
                constraints(nullable: "false")
            }

            column(name: "x_axis_label", type: "varchar2(255 char)")

            column(name: "y_axis_label", type: "varchar2(255 char)")

            column(name: "MOL_SS_ACTIVITY_STORAGE_ID", type: "number(19,0)") {
                constraints(nullable: "false")
            }

            column(name: "MOL_SS_ACT_STORAGE_LIST_IDX", type: "number(10,0)")
        }
        createSequence(sequenceName: "HILL_CURVE_VALUE_HOLDER_ID_SEQ")
    }
    changeSet(author: "ddurkin (generated)", id: "1377120073229-10", context: "standard") {
        createTable(tableName: "MOL_SS_CELL") {
            column(name: "id", type: "number(19,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "MOL_SS_CELL_PK")
            }

            column(name: "version", type: "number(19,0)") {
                constraints(nullable: "false")
            }

            column(name: "activity", type: "varchar2(255 char)") {
                constraints(nullable: "false")
            }

            column(name: "int_internal_value", type: "number(10,0)") {
                constraints(nullable: "false")
            }

            column(name: "mol_spread_sheet_cell_type", type: "varchar2(255 char)") {
                constraints(nullable: "false")
            }

            column(name: "MOL_SS_DATA_ID", type: "number(19,0)") {
                constraints(nullable: "false")
            }

            column(name: "str_internal_value", type: "varchar2(255 char)")

            column(name: "supplemental_internal_value", type: "varchar2(255 char)")
        }
        createSequence(sequenceName: "MOL_SS_CELL_ID_SEQ")

    }
    changeSet(author: "ddurkin (generated)", id: "1377120073229-11", context: "standard") {
        createTable(tableName: "MOL_SS_COL_HEADER") {
            column(name: "id", type: "number(19,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "MOL_SS_COL_HEADER_PK")
            }

            column(name: "version", type: "number(19,0)") {
                constraints(nullable: "false")
            }
            column(name: "MOL_SS_COL_HEADER_IDX", type: "number(10,0)") {
                constraints(nullable: "false")
            }
        }
        createSequence(sequenceName: "MOL_SS_COL_HEADER_ID_SEQ")
    }
    changeSet(author: "ddurkin (generated)", id: "1377120073229-12", context: "standard") {
        createTable(tableName: "MOL_SS_COL_SUB_HEADER") {
            column(name: "ID", type: "number(19,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "MOL_SS_COL_SUB_HEADER_PK")
            }

            column(name: "VERSION", type: "number(19,0)") {
                constraints(nullable: "false")
            }

            column(name: "COLUMN_TITLE", type: "varchar2(255 char)") {
                constraints(nullable: "false")
            }

            column(name: "MAX_RESPONSE", type: "double precision") {
                constraints(nullable: "false")
            }

            column(name: "MIN_RESPONSE", type: "double precision") {
                constraints(nullable: "false")
            }

            column(name: "UNITS_IN_COLUMN", type: "varchar2(255 char)") {
                constraints(nullable: "false")
            }

            column(name: "UNITS_IN_COLUMN_ARE_UNIFORM", type: "number(1,0)") {
                constraints(nullable: "false")
            }
        }
        createSequence(sequenceName: "MOL_SS_COL_SUB_HEADER_ID_SEQ")
    }
    changeSet(author: "ddurkin (generated)", id: "1377120073229-19", context: "standard") {
        createTable(tableName: "MOL_SS_ACTIVITY_STORAGE") {
            column(name: "id", type: "number(19,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "MOL_SS_ACTIVITY_STORAGE_PK")
            }

            column(name: "version", type: "number(19,0)") {
                constraints(nullable: "false")
            }

            column(name: "activity_outcome", type: "varchar2(255 char)")

            column(name: "cid", type: "number(19,0)")

            column(name: "dictionary_description", type: "varchar2(255 char)")

            column(name: "dictionary_id", type: "number(10,0)") {
                constraints(nullable: "false")
            }

            column(name: "dictionary_label", type: "varchar2(255 char)")

            column(name: "eid", type: "number(19,0)")

            column(name: "MOL_SS_CELL", type: "number(19,0)") {
                constraints(nullable: "false")
            }

            column(name: "potency", type: "double precision")

            column(name: "response_unit", type: "varchar2(255 char)")

            column(name: "sid", type: "number(19,0)")
        }
        createSequence(sequenceName: "MOL_SS_ACTIVITY_STORAGE_ID_SEQ")
    }
    changeSet(author: "ddurkin (generated)", id: "create MOL_SS_DATA and related tables", context: "standard") {
        createTable(tableName: "MOL_SS_DATA") {
            column(name: "id", type: "number(19,0)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "MOL_SS_DATA_PK")
            }

            column(name: "version", type: "number(19,0)") {
                constraints(nullable: "false")
            }

            column(name: "MOL_SS_DERIVED_METHOD", type: "varchar2(255 char)")
        }
        createSequence(sequenceName: "MOL_SS_DATA_ID_SEQ")

        createTable(tableName: "MOL_SS_DATA_EXP_NAME_LIST") {
            column(name: "MOL_SS_DATA_ID", type: "number(19,0)") {
                constraints(nullable: "false")
            }
            column(name: "EXP_NAME_IDX", type: "number(10,0)") {
                constraints(nullable: "false")
            }
            column(name: "EXP_NAME", type: "varchar2(255 char)")
        }
        addForeignKeyConstraint(baseColumnNames: "MOL_SS_DATA_ID", baseTableName: "MOL_SS_DATA_EXP_NAME_LIST", constraintName: "MOL_SS_DATA_EXP_NAME_LIST_FK", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "MOL_SS_DATA")

        createTable(tableName: "MOL_SS_DATA_EXP_FULL_NAME_LIST") {
            column(name: "MOL_SS_DATA_ID", type: "number(19,0)") {
                constraints(nullable: "false")
            }
            column(name: "EXP_FULL_NAME_IDX", type: "number(10,0)") {
                constraints(nullable: "false")
            }
            column(name: "EXP_FULL_NAME", type: "varchar2(255 char)")
        }
        addForeignKeyConstraint(baseColumnNames: "MOL_SS_DATA_ID", baseTableName: "MOL_SS_DATA_EXP_FULL_NAME_LIST", constraintName: "MOL_SS_DATA_EXP_FULL_NAME_FK", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "MOL_SS_DATA")
    }
    changeSet(author: "ddurkin (generated)", id: "create MOL_SS_DATA_MOL_SS_CELL_MAP", context: "standard") {
        createTable(tableName: "MOL_SS_DATA_MOL_SS_CELL_MAP") {
            column(name: "MOL_SS_DATA_ID", type: "number(19,0)") {
                constraints(nullable: "false")
            }
            column(name: "CELL_POSITION_IDX", type: "varchar2(255 char)") {
                constraints(nullable: "false")
            }
            column(name: "MOL_SS_CELL_ID", type: "number(19,0)")
        }
        addForeignKeyConstraint(baseColumnNames: "MOL_SS_DATA_ID", baseTableName: "MOL_SS_DATA_MOL_SS_CELL_MAP", constraintName: "MOL_SS_DATA_MOL_SS_CELL_1_FK", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "MOL_SS_DATA")
        addForeignKeyConstraint(baseColumnNames: "MOL_SS_CELL_ID", baseTableName: "MOL_SS_DATA_MOL_SS_CELL_MAP", constraintName: "MOL_SS_DATA_MOL_SS_CELL_2_FK", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "MOL_SS_CELL")
    }
    changeSet(author: "ddurkin (generated)", id: "1377120073229-14", context: "standard") {
        createTable(tableName: "MOL_SS_DATA_COLUMN_POINTER_MAP") {
            column(name: "MOL_SS_DATA_ID", type: "number(19,0)") {
                constraints(nullable: "false")
            }
            column(name: "COLUMN_POINTER_IDX", type: "number(19,0)") {
                constraints(nullable: "false")
            }
            column(name: "COLUMN_POINTER", type: "number(10,0)")
        }
        addForeignKeyConstraint(baseColumnNames: "MOL_SS_DATA_ID", baseTableName: "MOL_SS_DATA_COLUMN_POINTER_MAP", constraintName: "MOL_SS_DATA_COL_PTR_MAP_FK", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "MOL_SS_DATA")
    }
    changeSet(author: "ddurkin (generated)", id: "1377120073229-17", context: "standard") {
        createTable(tableName: "MOL_SS_DATA_ROW_POINTER_MAP") {
            column(name: "MOL_SS_DATA_ID", type: "number(19,0)") {
                constraints(nullable: "false")
            }
            column(name: "COLUMN_ROW_IDX", type: "number(19,0)") {
                constraints(nullable: "false")
            }
            column(name: "COLUMN_ROW", type: "number(10,0)")
        }
        addForeignKeyConstraint(baseColumnNames: "MOL_SS_DATA_ID", baseTableName: "MOL_SS_DATA_ROW_POINTER_MAP", constraintName: "MOL_SS_DATA_ROW_PTR_MAP_FK", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "MOL_SS_DATA")
    }
    changeSet(author: "ddurkin (generated)", id: "1377120073229-9", context: "standard") {
        createTable(tableName: "MOL_SS_DATA_EXP_CAP_MAP") {
            column(name: "MOL_SS_DATA_ID", type: "number(19,0)") {
                constraints(nullable: "false")
            }
            column(name: "EXP_ID_IDX", type: "number(19,0)") {
                constraints(nullable: "false")
            }
            column(name: "CAP_ASSAY_ID", type: "number(19,0)")
        }
        addForeignKeyConstraint(baseColumnNames: "MOL_SS_DATA_ID", baseTableName: "MOL_SS_DATA_EXP_CAP_MAP", constraintName: "MOL_SS_DATA_EXP_CAP_MAP_FK", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "MOL_SS_DATA")
    }
    changeSet(author: "ddurkin (generated)", id: "1377120073229-15", context: "standard") {
        createTable(tableName: "MOL_SS_DATA_COL_ASSAY_MAP") {
            column(name: "MOL_SS_DATA_ID", type: "number(19,0)") {
                constraints(nullable: "false")
            }
            column(name: "COLUMN_ID_IDX", type: "number(10,0)") {
                constraints(nullable: "false")
            }
            column(name: "ASSAY", type: "varchar2(255 char)")
        }
        addForeignKeyConstraint(baseColumnNames: "MOL_SS_DATA_ID", baseTableName: "MOL_SS_DATA_COL_ASSAY_MAP", constraintName: "MOL_SS_DATA_COL_ASSAY_MAP_FK", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "MOL_SS_DATA")
    }
    changeSet(author: "ddurkin (generated)", id: "1377120073229-8", context: "standard") {
        createTable(tableName: "MOL_SS_DATA_COL_ASSAY_NAME_MAP") {
            column(name: "MOL_SS_DATA_ID", type: "number(19,0)") {
                constraints(nullable: "false")
            }
            column(name: "COLUMN_ID_IDX", type: "number(10,0)") {
                constraints(nullable: "false")
            }
            column(name: "ASSAY_NAME", type: "varchar2(255 char)")
        }
        addForeignKeyConstraint(baseColumnNames: "MOL_SS_DATA_ID", baseTableName: "MOL_SS_DATA_COL_ASSAY_NAME_MAP", constraintName: "MOL_SS_DATA_COL_ASSAY_NM_FK", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "MOL_SS_DATA")
    }
    changeSet(author: "ddurkin (generated)", id: "1377120073229-7", context: "standard") {
        createTable(tableName: "MOL_SS_DATA_COLS_NORM_MAP") {
            column(name: "MOL_SS_DATA_ID", type: "number(19,0)") {
                constraints(nullable: "false")
            }
            column(name: "COLUMN_ID_IDX", type: "number(10,0)") {
                constraints(nullable: "false")
            }
            column(name: "NORMALIZATION", type: "number(1,0)")
        }
        addForeignKeyConstraint(baseColumnNames: "MOL_SS_DATA_ID", baseTableName: "MOL_SS_DATA_COLS_NORM_MAP", constraintName: "MOL_SS_DATA_COLS_NORM_MAP_FK", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "MOL_SS_DATA")
    }
    changeSet(author: "ddurkin (generated)", id: "add foreign keys", context: "standard") {
        addForeignKeyConstraint(baseColumnNames: "MOL_SS_ACTIVITY_STORAGE_ID", baseTableName: "MOL_SS_HILL_CURVE_VALUE_HOLDER", constraintName: "SC_HL_CRV_VL_HLDR_SS_ACT_ST_FK", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "MOL_SS_ACTIVITY_STORAGE")
        addForeignKeyConstraint(baseColumnNames: "MOL_SS_DATA_ID", baseTableName: "MOL_SS_CELL", constraintName: "MOL_SS_CELL_MOL_SS_DATA_FK", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "MOL_SS_DATA")
    }
}