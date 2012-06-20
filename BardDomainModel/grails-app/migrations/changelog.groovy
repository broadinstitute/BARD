databaseChangeLog = {
    include file: 'iteration-001/01-initial-base-line.groovy'
    include file: 'iteration-002/01-initial-changes.groovy'



    // do last
    include file: 'manage-stored-procedures.groovy'
    include file: 'reset-sequences.groovy'
    include file: 'drop-retired-tables.groovy'
}
