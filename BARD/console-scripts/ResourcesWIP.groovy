import org.codehaus.groovy.grails.plugins.GrailsPluginUtils
import liquibase.resource.FileSystemResourceAccessor

def mra = ctx.migrationResourceAccessor
//.getResourceAsStream('changlog.groovy').text
println(mra.baseDirectory)

File bardDomainModelPluginDir = GrailsPluginUtils.getPluginDirForName('bard-domain-model').getFile()
println(GrailsPluginUtils.getPluginDirForName('bard-domain-model'))


String changelogLocationPath = new File(bardDomainModelPluginDir, 'grails-app/migrations').path
ctx.setProperty('migrationResourceAccessor', new FileSystemResourceAccessor(changelogLocationPath))

println( ctx.migrationResourceAccessor)




