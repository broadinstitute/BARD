def queryProjectApiService = ctx.queryProjectApiService
def json = queryProjectApiService.findProjects()
for(item in json){
   println(item)
}