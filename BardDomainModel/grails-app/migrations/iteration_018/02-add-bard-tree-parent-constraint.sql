ALTER TABLE BARD_TREE ADD CONSTRAINT FK_BARD_TREE_PARENT
FOREIGN KEY (PARENT_NODE_ID)
REFERENCES BARD_TREE (NODE_ID)
ENABLE VALIDATE
;