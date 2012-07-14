import wslite.json.JSONObject
import wslite.json.JSONArray

final String ELASTIC_COMPOUNDS_SEARCH = '''{
  "fields": ["name"],
  "query": {
    "query_string": {
      "default_field": "name",
      "query": "*"
    }
  },
  "size": 5
}
'''

    final String jsonAssayResponseString = '''{"hits":
        {"hits": [
            {"_index": "assays", "_type": "compound", "_id": 644, "_source": {"cids": [1234, 5678]}},
            {"_index": "assays", "_type": "assay", "_id": 644, "_source":
                {"aid": 644, "title": "Kinetic mechanism", "targets": [
                    {"resourcePath": "/bard/rest/v1/targets/accession/O75116", "taxId": 9606, "acc": "O75116"}]
                }
            }]
        }
    }'''
def restClient = ctx.getBean("restClient");
final String request = ELASTIC_COMPOUNDS_SEARCH.replaceAll("\\*","Broad*")
println restClient.url

JSONObject array = new JSONObject(jsonAssayResponseString)
println array.toString()


//final String searchURL = "http://bard-dev-vm:9200/assays/_search"
//println searchURL
//restClient.url = searchURL
//def response = restClient.post(){
  //  text array.toString()
//}
//def json = response.json
//if(json instanceof JSONObject){
  // JSONObject responseJSON = (JSONObject)json
 //def autoComplete = responseJSON.hits.hits.collect { it.fields }.collect { it.name }

//}