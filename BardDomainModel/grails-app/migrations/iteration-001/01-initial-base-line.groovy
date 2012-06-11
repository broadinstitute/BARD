databaseChangeLog = {

    changeSet(author: "jasiedu (generated)", id: "1339421436388-1") {
        createTable(tableName: "assay") {
            column(autoIncrement: "true", name: "Assay_ID", type: "bigint") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "assayPK")
            }

            column(name: "version", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "assay_name", type: "varchar(128)") {
                constraints(nullable: "false")
            }

            column(name: "assay_status", type: "varchar(20)") {
                constraints(nullable: "false")
            }

            column(name: "assay_version", type: "varchar(10)") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "description", type: "varchar(1000)")

            column(name: "designed_by", type: "varchar(100)")

            column(name: "last_updated", type: "timestamp")

            column(name: "modified_by", type: "varchar(40)")

            column(name: "ready_for_extraction", type: "varchar(20)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-2") {
        createTable(tableName: "assay_descriptor") {
            column(name: "node_id", type: "bigint") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "assay_descripPK")
            }

            column(name: "abbreviation", type: "varchar(20)")

            column(name: "acronym", type: "varchar(20)")

            column(name: "description", type: "varchar(1000)")

            column(name: "element_id", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "element_status", type: "varchar(20)") {
                constraints(nullable: "false")
            }

            column(name: "external_url", type: "varchar(1000)")

            column(name: "label", type: "varchar(128)") {
                constraints(nullable: "false")
            }

            column(name: "parent_node_id", type: "bigint")

            column(name: "synonyms", type: "varchar(1000)")

            column(name: "unit", type: "varchar(128)")
        }
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-3") {
        createTable(tableName: "biology_descriptor") {
            column(name: "node_id", type: "bigint") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "biology_descrPK")
            }

            column(name: "abbreviation", type: "varchar(20)")

            column(name: "acronym", type: "varchar(20)")

            column(name: "description", type: "varchar(1000)")

            column(name: "element_id", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "element_status", type: "varchar(20)") {
                constraints(nullable: "false")
            }

            column(name: "external_url", type: "varchar(1000)")

            column(name: "label", type: "varchar(128)") {
                constraints(nullable: "false")
            }

            column(name: "parent_node_id", type: "bigint")

            column(name: "synonyms", type: "varchar(1000)")

            column(name: "unit", type: "varchar(128)")
        }
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-4") {
        createTable(tableName: "element") {
            column(autoIncrement: "true", name: "Element_ID", type: "bigint") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "elementPK")
            }

            column(name: "version", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "abbreviation", type: "varchar(20)")

            column(name: "acronym", type: "varchar(20)")

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "description", type: "varchar(1000)")

            column(name: "element_status", type: "varchar(20)") {
                constraints(nullable: "false")
            }

            column(name: "external_url", type: "varchar(1000)")

            column(name: "label", type: "varchar(128)") {
                constraints(nullable: "false")
            }

            column(name: "last_updated", type: "timestamp")

            column(name: "modified_by", type: "varchar(40)")

            column(name: "ready_for_extraction", type: "varchar(20)") {
                constraints(nullable: "false")
            }

            column(name: "synonyms", type: "varchar(1000)")

            column(name: "unit", type: "varchar(128)")
        }
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-5") {
        createTable(tableName: "element_hierarchy") {
            column(autoIncrement: "true", name: "element_hierarchy_id", type: "bigint") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "element_hieraPK")
            }

            column(name: "version", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "child_element_id", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "last_updated", type: "timestamp")

            column(name: "modified_by", type: "varchar(40)")

            column(name: "parent_element_id", type: "bigint")

            column(name: "relationship_type", type: "varchar(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-6") {
        createTable(tableName: "experiment") {
            column(autoIncrement: "true", name: "Experiment_ID", type: "bigint") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "experimentPK")
            }

            column(name: "version", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "assay_id", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "description", type: "varchar(1000)")

            column(name: "experiment_name", type: "varchar(256)") {
                constraints(nullable: "false")
            }

            column(name: "experiment_status", type: "varchar(20)") {
                constraints(nullable: "false")
            }

            column(name: "hold_until_date", type: "timestamp")

            column(name: "last_updated", type: "timestamp")

            column(name: "modified_by", type: "varchar(40)")

            column(name: "project_id", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "ready_for_extraction", type: "varchar(20)") {
                constraints(nullable: "false")
            }

            column(name: "run_date_from", type: "timestamp")

            column(name: "run_date_to", type: "timestamp")
        }
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-7") {
        createTable(tableName: "external_assay") {
            column(name: "external_system_id", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "assay_id", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "version", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "ext_assay_id", type: "varchar(128)") {
                constraints(nullable: "false")
            }

            column(name: "last_updated", type: "timestamp")

            column(name: "modified_by", type: "varchar(40)")
        }
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-8") {
        createTable(tableName: "external_system") {
            column(autoIncrement: "true", name: "External_System_ID", type: "bigint") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "external_systPK")
            }

            column(name: "version", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "last_updated", type: "timestamp")

            column(name: "modified_by", type: "varchar(40)")

            column(name: "owner", type: "varchar(128)")

            column(name: "system_name", type: "varchar(128)") {
                constraints(nullable: "false")
            }

            column(name: "system_url", type: "varchar(1000)")
        }
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-9") {
        createTable(tableName: "instance_descriptor") {
            column(name: "node_id", type: "bigint") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "instance_descPK")
            }

            column(name: "abbreviation", type: "varchar(20)")

            column(name: "acronym", type: "varchar(20)")

            column(name: "description", type: "varchar(1000)")

            column(name: "element_id", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "element_status", type: "varchar(20)") {
                constraints(nullable: "false")
            }

            column(name: "external_url", type: "varchar(1000)")

            column(name: "label", type: "varchar(128)") {
                constraints(nullable: "false")
            }

            column(name: "parent_node_id", type: "bigint")

            column(name: "synonyms", type: "varchar(1000)")

            column(name: "unit", type: "varchar(128)")
        }
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-10") {
        createTable(tableName: "laboratory") {
            column(name: "node_id", type: "bigint") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "laboratoryPK")
            }

            column(name: "description", type: "varchar(1000)")

            column(name: "laboratory_id", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "laboratory", type: "varchar(128)") {
                constraints(nullable: "false")
            }

            column(name: "parent_node_id", type: "bigint")
        }
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-11") {
        createTable(tableName: "measure") {
            column(autoIncrement: "true", name: "Measure_ID", type: "bigint") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "measurePK")
            }

            column(name: "version", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "entry_unit", type: "varchar(128)")

            column(name: "last_updated", type: "timestamp")

            column(name: "measure_context_id", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "modified_by", type: "varchar(40)")

            column(name: "result_type_id", type: "bigint") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-12") {
        createTable(tableName: "measure_context") {
            column(autoIncrement: "true", name: "Measure_Context_ID", type: "bigint") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "measure_contePK")
            }

            column(name: "version", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "assay_id", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "context_name", type: "varchar(128)") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "last_updated", type: "timestamp")

            column(name: "modified_by", type: "varchar(40)")
        }
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-13") {
        createTable(tableName: "measure_context_item") {
            column(autoIncrement: "true", name: "measure_Context_Item_ID", type: "bigint") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "measure_conteIPK")
            }

            column(name: "version", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "assay_id", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "attribute_id", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "attribute_type", type: "varchar(255)") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "last_updated", type: "timestamp")

            column(name: "measure_context_id", type: "bigint")

            column(name: "modified_by", type: "varchar(40)")

            column(name: "GROUP_MEASURE_CONTEXT_ITEM_ID", type: "bigint")

            column(name: "qualifier", type: "char(2)")

            column(name: "value_display", type: "varchar(256)")

            column(name: "value_id", type: "bigint")

            column(name: "value_max", type: "float(19)")

            column(name: "value_min", type: "float(19)")

            column(name: "value_num", type: "float(19)")
        }
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-14") {
        createTable(tableName: "ontology") {
            column(autoIncrement: "true", name: "Ontology_ID", type: "bigint") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "ontologyPK")
            }

            column(name: "version", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "abbreviation", type: "varchar(20)")

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "last_updated", type: "timestamp")

            column(name: "modified_by", type: "varchar(40)")

            column(name: "ontology_name", type: "varchar(256)") {
                constraints(nullable: "false")
            }

            column(name: "system_url", type: "varchar(1000)")
        }
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-15") {
        createTable(tableName: "ontology_item") {
            column(autoIncrement: "true", name: "Ontology_Item_ID", type: "bigint") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "ontology_itemPK")
            }

            column(name: "version", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "element_id", type: "bigint")

            column(name: "item_reference", type: "char(10)")

            column(name: "last_updated", type: "timestamp")

            column(name: "modified_by", type: "varchar(40)")

            column(name: "ontology_id", type: "bigint") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-16") {
        createTable(tableName: "project") {
            column(autoIncrement: "true", name: "Project_ID", type: "bigint") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "projectPK")
            }

            column(name: "version", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "description", type: "varchar(1000)")

            column(name: "group_type", type: "varchar(255)") {
                constraints(nullable: "false")
            }

            column(name: "last_updated", type: "timestamp")

            column(name: "modified_by", type: "varchar(40)")

            column(name: "project_name", type: "varchar(256)") {
                constraints(nullable: "false")
            }

            column(name: "ready_for_extraction", type: "varchar(20)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-17") {
        createTable(tableName: "project_assay") {
            column(autoIncrement: "true", name: "project_assay_id", type: "bigint") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "project_assayPK")
            }

            column(name: "version", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "assay_id", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "last_updated", type: "timestamp")

            column(name: "modified_by", type: "varchar(40)")

            column(name: "project_id", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "promotion_criteria", type: "varchar(1000)")

            column(name: "promotion_threshold", type: "float(19)")

            column(name: "sequence_no", type: "integer")
        }
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-18") {
        createTable(tableName: "protocol") {
            column(autoIncrement: "true", name: "Protocol_ID", type: "bigint") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "protocolPK")
            }

            column(name: "version", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "assay_id", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "last_updated", type: "timestamp")

            column(name: "modified_by", type: "varchar(40)")

            column(name: "protocol_document", type: "longblob")

            column(name: "protocol_name", type: "varchar(500)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-19") {
        createTable(tableName: "qualifier") {
            column(name: "qualifier", type: "char(2)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "qualifierPK")
            }

            column(name: "version", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "description", type: "varchar(1000)")

            column(name: "last_updated", type: "timestamp")

            column(name: "modified_by", type: "varchar(40)")
        }
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-20") {
        createTable(tableName: "result") {
            column(autoIncrement: "true", name: "Result_ID", type: "bigint") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "resultPK")
            }

            column(name: "version", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "experiment_id", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "last_updated", type: "timestamp")

            column(name: "modified_by", type: "varchar(40)")

            column(name: "qualifier", type: "char(2)") {
                constraints(nullable: "false")
            }

            column(name: "result_status", type: "varchar(20)") {
                constraints(nullable: "false")
            }

            column(name: "result_type_id", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "substance_id", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "value_display", type: "varchar(256)")

            column(name: "value_max", type: "float(19)")

            column(name: "value_min", type: "float(19)")

            column(name: "value_num", type: "float(19)")
        }
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-21") {
        createTable(tableName: "result_context_item") {
            column(autoIncrement: "true", name: "Result_Context_Item_ID", type: "bigint") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "result_contexPK")
            }

            column(name: "version", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "attribute_id", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "experiment_id", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "last_updated", type: "timestamp")

            column(name: "modified_by", type: "varchar(40)")

            column(name: "group_result_context_id", type: "bigint")

            column(name: "qualifier", type: "char(2)") {
                constraints(nullable: "false")
            }

            column(name: "result_id", type: "bigint")

            column(name: "value_id", type: "bigint")

            column(name: "value_display", type: "varchar(256)")

            column(name: "value_max", type: "float(19)")

            column(name: "value_min", type: "float(19)")

            column(name: "value_num", type: "float(19)")
        }
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-22") {
        createTable(tableName: "result_hierarchy") {
            column(name: "result_id", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "parent_result_id", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "version", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "hierarchy_type", type: "varchar(255)") {
                constraints(nullable: "false")
            }

            column(name: "last_updated", type: "timestamp")

            column(name: "modified_by", type: "varchar(40)")
        }
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-23") {
        createTable(tableName: "result_type") {
            column(name: "node_id", type: "bigint") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "result_typePK")
            }

            column(name: "abbreviation", type: "varchar(20)")

            column(name: "base_unit", type: "varchar(128)")

            column(name: "description", type: "varchar(1000)")

            column(name: "result_type_id", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "parent_node_id", type: "bigint")

            column(name: "result_type_name", type: "varchar(128)") {
                constraints(nullable: "false")
            }

            column(name: "result_type_status", type: "varchar(255)") {
                constraints(nullable: "false")
            }

            column(name: "synonyms", type: "varchar(1000)")
        }
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-24") {
        createTable(tableName: "stage") {
            column(name: "node_id", type: "bigint") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "stagePK")
            }

            column(name: "description", type: "varchar(1000)")

            column(name: "stage_id", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "parent_node_id", type: "bigint")

            column(name: "stage", type: "varchar(128)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-25") {
        createTable(tableName: "substance") {
            column(autoIncrement: "true", name: "Substance_ID", type: "bigint") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "substancePK")
            }

            column(name: "version", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "compound_id", type: "integer")

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "last_updated", type: "timestamp")

            column(name: "modified_by", type: "varchar(40)")

            column(name: "molecular_weight", type: "decimal(19,3)")

            column(name: "smiles", type: "varchar(4000)")

            column(name: "substance_type", type: "varchar(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-26") {
        createTable(tableName: "tree_root") {
            column(name: "tree_root_id", type: "bigint") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "tree_rootPK")
            }

            column(name: "version", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "element_id", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "last_updated", type: "timestamp")

            column(name: "modified_by", type: "varchar(255)")

            column(name: "relationship_type", type: "varchar(20)")

            column(name: "tree_name", type: "varchar(30)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-27") {
        createTable(tableName: "unit") {
            column(name: "unit", type: "varchar(128)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "unitPK")
            }

            column(name: "description", type: "varchar(1000)")

            column(name: "unit_id", type: "bigint")

            column(name: "node_id", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "parent_node_id", type: "bigint") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-28") {
        createTable(tableName: "unit_conversion") {
            column(name: "from_unit", type: "varchar(128)") {
                constraints(nullable: "false")
            }

            column(name: "to_unit", type: "varchar(128)") {
                constraints(nullable: "false")
            }

            column(name: "version", type: "bigint") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "formula", type: "varchar(256)")

            column(name: "last_updated", type: "timestamp")

            column(name: "modified_by", type: "varchar(40)")

            column(name: "multiplier", type: "float(19)")

            column(name: "offset", type: "float(19)")
        }
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-29") {
        addPrimaryKey(columnNames: "external_system_id, assay_id", constraintName: "external_assaPK", tableName: "external_assay")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-30") {
        addPrimaryKey(columnNames: "result_id, parent_result_id", constraintName: "result_hierarPK", tableName: "result_hierarchy")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-31") {
        addPrimaryKey(columnNames: "from_unit, to_unit", constraintName: "unit_conversiPK", tableName: "unit_conversion")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-32") {
        addForeignKeyConstraint(baseColumnNames: "element_id", baseTableName: "assay_descriptor", constraintName: "FK635728F5FB1DEEB5", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Element_ID", referencedTableName: "element", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-33") {
        addForeignKeyConstraint(baseColumnNames: "parent_node_id", baseTableName: "assay_descriptor", constraintName: "FK635728F57FA5EC26", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "node_id", referencedTableName: "assay_descriptor", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-34") {
        addForeignKeyConstraint(baseColumnNames: "unit", baseTableName: "assay_descriptor", constraintName: "FK635728F583F30E6D", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "unit", referencedTableName: "unit", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-35") {
        addForeignKeyConstraint(baseColumnNames: "element_id", baseTableName: "biology_descriptor", constraintName: "FKA9710231FB1DEEB5", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Element_ID", referencedTableName: "element", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-36") {
        addForeignKeyConstraint(baseColumnNames: "parent_node_id", baseTableName: "biology_descriptor", constraintName: "FKA9710231701B08AA", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "node_id", referencedTableName: "biology_descriptor", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-37") {
        addForeignKeyConstraint(baseColumnNames: "unit", baseTableName: "biology_descriptor", constraintName: "FKA971023183F30E6D", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "unit", referencedTableName: "unit", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-38") {
        addForeignKeyConstraint(baseColumnNames: "unit", baseTableName: "element", constraintName: "FK9CE31EFC83F30E6D", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "unit", referencedTableName: "unit", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-39") {
        addForeignKeyConstraint(baseColumnNames: "child_element_id", baseTableName: "element_hierarchy", constraintName: "FKA08920B23D2B45B8", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Element_ID", referencedTableName: "element", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-40") {
        addForeignKeyConstraint(baseColumnNames: "parent_element_id", baseTableName: "element_hierarchy", constraintName: "FKA08920B217940D6A", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Element_ID", referencedTableName: "element", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-41") {
        addForeignKeyConstraint(baseColumnNames: "assay_id", baseTableName: "experiment", constraintName: "FKFAE9DBFD95147578", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Assay_ID", referencedTableName: "assay", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-42") {
        addForeignKeyConstraint(baseColumnNames: "project_id", baseTableName: "experiment", constraintName: "FKFAE9DBFD523DB1FC", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Project_ID", referencedTableName: "project", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-43") {
        addForeignKeyConstraint(baseColumnNames: "assay_id", baseTableName: "external_assay", constraintName: "FKF7DD6D0595147578", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Assay_ID", referencedTableName: "assay", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-44") {
        addForeignKeyConstraint(baseColumnNames: "external_system_id", baseTableName: "external_assay", constraintName: "FKF7DD6D05999C7EF3", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "External_System_ID", referencedTableName: "external_system", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-45") {
        addForeignKeyConstraint(baseColumnNames: "element_id", baseTableName: "instance_descriptor", constraintName: "FKB76D5559FB1DEEB5", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Element_ID", referencedTableName: "element", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-46") {
        addForeignKeyConstraint(baseColumnNames: "parent_node_id", baseTableName: "instance_descriptor", constraintName: "FKB76D5559DE87A66C", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "node_id", referencedTableName: "instance_descriptor", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-47") {
        addForeignKeyConstraint(baseColumnNames: "unit", baseTableName: "instance_descriptor", constraintName: "FKB76D555983F30E6D", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "unit", referencedTableName: "unit", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-48") {
        addForeignKeyConstraint(baseColumnNames: "laboratory_id", baseTableName: "laboratory", constraintName: "FKB9066FB36CCBCE9E", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Element_ID", referencedTableName: "element", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-49") {
        addForeignKeyConstraint(baseColumnNames: "parent_node_id", baseTableName: "laboratory", constraintName: "FKB9066FB37DD3D3B", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "node_id", referencedTableName: "laboratory", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-50") {
        addForeignKeyConstraint(baseColumnNames: "entry_unit", baseTableName: "measure", constraintName: "FK37EDA55EE98361A", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "unit", referencedTableName: "unit", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-51") {
        addForeignKeyConstraint(baseColumnNames: "measure_context_id", baseTableName: "measure", constraintName: "FK37EDA55E73967CFF", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Measure_Context_ID", referencedTableName: "measure_context", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-52") {
        addForeignKeyConstraint(baseColumnNames: "result_type_id", baseTableName: "measure", constraintName: "FK37EDA55E6711D2BA", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "node_id", referencedTableName: "result_type", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-53") {
        addForeignKeyConstraint(baseColumnNames: "assay_id", baseTableName: "measure_context", constraintName: "FK4CE043CE95147578", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Assay_ID", referencedTableName: "assay", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-54") {
        addForeignKeyConstraint(baseColumnNames: "assay_id", baseTableName: "measure_context_item", constraintName: "FK78C420E495147578", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Assay_ID", referencedTableName: "assay", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-55") {
        addForeignKeyConstraint(baseColumnNames: "attribute_id", baseTableName: "measure_context_item", constraintName: "FK78C420E49C323F15", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Element_ID", referencedTableName: "element", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-56") {
        addForeignKeyConstraint(baseColumnNames: "GROUP_MEASURE_CONTEXT_ITEM_ID", baseTableName: "measure_context_item", constraintName: "FK78C420E4DB107C1C", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "measure_Context_Item_ID", referencedTableName: "measure_context_item", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-57") {
        addForeignKeyConstraint(baseColumnNames: "measure_context_id", baseTableName: "measure_context_item", constraintName: "FK78C420E473967CFF", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Measure_Context_ID", referencedTableName: "measure_context", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-58") {
        addForeignKeyConstraint(baseColumnNames: "qualifier", baseTableName: "measure_context_item", constraintName: "FK78C420E4239027BB", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "qualifier", referencedTableName: "qualifier", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-59") {
        addForeignKeyConstraint(baseColumnNames: "value_id", baseTableName: "measure_context_item", constraintName: "FK78C420E484BC6C20", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Element_ID", referencedTableName: "element", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-60") {
        addForeignKeyConstraint(baseColumnNames: "element_id", baseTableName: "ontology_item", constraintName: "FKD0A7A403FB1DEEB5", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Element_ID", referencedTableName: "element", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-61") {
        addForeignKeyConstraint(baseColumnNames: "ontology_id", baseTableName: "ontology_item", constraintName: "FKD0A7A403A78BDC3F", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Ontology_ID", referencedTableName: "ontology", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-62") {
        addForeignKeyConstraint(baseColumnNames: "assay_id", baseTableName: "project_assay", constraintName: "FKC71121D395147578", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Assay_ID", referencedTableName: "assay", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-63") {
        addForeignKeyConstraint(baseColumnNames: "project_id", baseTableName: "project_assay", constraintName: "FKC71121D3523DB1FC", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Project_ID", referencedTableName: "project", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-64") {
        addForeignKeyConstraint(baseColumnNames: "assay_id", baseTableName: "protocol", constraintName: "FKC50A8E9895147578", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Assay_ID", referencedTableName: "assay", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-65") {
        addForeignKeyConstraint(baseColumnNames: "experiment_id", baseTableName: "result", constraintName: "FKC84DC81D36E14E78", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Experiment_ID", referencedTableName: "experiment", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-66") {
        addForeignKeyConstraint(baseColumnNames: "qualifier", baseTableName: "result", constraintName: "FKC84DC81D239027BB", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "qualifier", referencedTableName: "qualifier", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-67") {
        addForeignKeyConstraint(baseColumnNames: "result_type_id", baseTableName: "result", constraintName: "FKC84DC81DA046AF95", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Element_ID", referencedTableName: "element", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-68") {
        addForeignKeyConstraint(baseColumnNames: "substance_id", baseTableName: "result", constraintName: "FKC84DC81D5555CE1C", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Substance_ID", referencedTableName: "substance", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-69") {
        addForeignKeyConstraint(baseColumnNames: "attribute_id", baseTableName: "result_context_item", constraintName: "FK7DA4AC859C323F15", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Element_ID", referencedTableName: "element", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-70") {
        addForeignKeyConstraint(baseColumnNames: "experiment_id", baseTableName: "result_context_item", constraintName: "FK7DA4AC8536E14E78", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Experiment_ID", referencedTableName: "experiment", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-71") {
        addForeignKeyConstraint(baseColumnNames: "group_result_context_id", baseTableName: "result_context_item", constraintName: "FK7DA4AC85C92A32B4", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Result_Context_Item_ID", referencedTableName: "result_context_item", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-72") {
        addForeignKeyConstraint(baseColumnNames: "qualifier", baseTableName: "result_context_item", constraintName: "FK7DA4AC85239027BB", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "qualifier", referencedTableName: "qualifier", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-73") {
        addForeignKeyConstraint(baseColumnNames: "result_id", baseTableName: "result_context_item", constraintName: "FK7DA4AC8580BF978", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Result_ID", referencedTableName: "result", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-74") {
        addForeignKeyConstraint(baseColumnNames: "value_id", baseTableName: "result_context_item", constraintName: "FK7DA4AC8584BC6C20", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Element_ID", referencedTableName: "element", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-75") {
        addForeignKeyConstraint(baseColumnNames: "parent_result_id", baseTableName: "result_hierarchy", constraintName: "FK3AEF9493DFACB023", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Result_ID", referencedTableName: "result", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-76") {
        addForeignKeyConstraint(baseColumnNames: "result_id", baseTableName: "result_hierarchy", constraintName: "FK3AEF949380BF978", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Result_ID", referencedTableName: "result", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-77") {
        addForeignKeyConstraint(baseColumnNames: "base_unit", baseTableName: "result_type", constraintName: "FKDFFD0A1C17785B1B", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "unit", referencedTableName: "unit", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-78") {
        addForeignKeyConstraint(baseColumnNames: "parent_node_id", baseTableName: "result_type", constraintName: "FKDFFD0A1C2CC1427F", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "node_id", referencedTableName: "result_type", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-79") {
        addForeignKeyConstraint(baseColumnNames: "result_type_id", baseTableName: "result_type", constraintName: "FKDFFD0A1CA046AF95", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Element_ID", referencedTableName: "element", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-80") {
        addForeignKeyConstraint(baseColumnNames: "parent_node_id", baseTableName: "stage", constraintName: "FK68AC2FE80DD261C", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "node_id", referencedTableName: "stage", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-81") {
        addForeignKeyConstraint(baseColumnNames: "stage_id", baseTableName: "stage", constraintName: "FK68AC2FE26A4B373", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Element_ID", referencedTableName: "element", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-82") {
        addForeignKeyConstraint(baseColumnNames: "element_id", baseTableName: "tree_root", constraintName: "FK1E758E3FB1DEEB5", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Element_ID", referencedTableName: "element", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-83") {
        addForeignKeyConstraint(baseColumnNames: "unit_id", baseTableName: "unit", constraintName: "FK36D984C7B5C72D", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "Element_ID", referencedTableName: "element", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-84") {
        addForeignKeyConstraint(baseColumnNames: "from_unit", baseTableName: "unit_conversion", constraintName: "FK297866B1888BE9E2", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "unit", referencedTableName: "unit", referencesUniqueColumn: "false")
    }

    changeSet(author: "jasiedu (generated)", id: "1339421436388-85") {
        addForeignKeyConstraint(baseColumnNames: "to_unit", baseTableName: "unit_conversion", constraintName: "FK297866B13EEA8DB1", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "unit", referencedTableName: "unit", referencesUniqueColumn: "false")
    }
}
