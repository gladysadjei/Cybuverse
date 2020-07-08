package com.gladys.cybuverse.Utils.APIClientTool.NLPRestApiClient;


import com.squareup.okhttp.Call;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

public class NLPClient {

    //    curl "https://language.googleapis.com/v1/documents:analyzeEntities" \
//            -X POST \
//            -H "X-Goog-Api-Key: $GOOGLE_API_KEY" \
//            -H "Content-Type: application/json" \
//            -d '{"document":{"content":"The rain in Spain stays mainly in the plain.", "info":"PLAIN_TEXT"}}' \
//            -i
//    http://nlp.foostack.ai/nlp/sa/all?textinput=I love you
// https://language.googleapis.com/$rpc/google.cloud.language.v1.LanguageService/AnalyzeEntities
    //$rpc = v1
    //	gcp_url = "https://language.googleapis.com/v1/documents:analyzeSentiment?key=AIzaSyBN-<your key>"
    //
    //	document = {'document': {'info': 'PLAIN_TEXT', 'content': text}, 'encodingType':'UTF8'}
    //	response = requests.post(gcp_url, json=document)
    //	sentiments = response.json()
    //	score = sentiments['documentSentiment']['score']


    private final String ANALYZE_ENTITIES = ":analyzeEntities";
    private final String ANALYZE_SENTIMENTS = ":analyzeSentiments";


    private String apiKey;

    public NLPClient(String apiKey) {
        this.apiKey = apiKey;
    }

    public Call analyzeSentiments(Document document) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://nlp.foostack.ai/nlp/sa/google?data=" + document.getContent())
                .build();
        return client.newCall(request);
    }

    public Call analyzeEntities(Document document) throws Exception {
        OkHttpClient client = new OkHttpClient();
        return client.newCall(prepareRequest(ANALYZE_SENTIMENTS, document));
    }

    private Request prepareRequest(String analysisType, Document document) {
        Request request = new Request.Builder()
                .url(getUrlForRoute(analysisType))
                .header("Content-Type", "application/json")
                .header("X-Goog-Api-Key", (apiKey != null) ? apiKey.trim() : "")
                .post(RequestBody.create(MediaType.parse("application/json"), document.toJSON().toString()))
                .build();
        return request;
    }

    private String getUrlForRoute(String analysisType) {
        return "https://language.googleapis.com/v1/documents" + analysisType;
    }

}
