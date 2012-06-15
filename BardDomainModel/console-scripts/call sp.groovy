import groovy.sql.Sql

def sql = new Sql(ctx.dataSource)
sql.call( '''
begin    
    MANAGE_ONTOLOGY.MAKE_TREES();
end;
''')