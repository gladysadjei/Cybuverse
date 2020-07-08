package com.gladys.cybuverse.Utils.APIClientTool;

//takes am address, and post data, and returns the html as a string

import com.gladys.cybuverse.Utils.GeneralUtils.collections.JSONData;

import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.println;

public class Main {

    public static void main(String[] args) throws Exception {

//        String USERNAME = "Otc Chingy";
//        String PASSWORD = "Adgjmptw111";
//        final String[] ACCESSTOKEN = {""};
//
//        Thread runner = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("running");
//            }
//        });
//
//        RestApiClient restApiClient = new RestApiClient("http://192.168.137.1:80/auth")
//                .addHeader(RestApiClient.HttpRequestHeaders.ACCEPT, RestApiClient.HttpRequestHeaders.JSON)
//                .addHeader(RestApiClient.HttpRequestHeaders.CONTENT_TYPE, RestApiClient.HttpRequestHeaders.JSON)
//                .addJSONData("username", USERNAME).addJSONData("password", PASSWORD)
//                .setOnRequestStartListener(() -> {
//                    runner.run();
//                })
//                .setOnRequestCompleteListener(result -> {
//                    runner.interrupt();
//                    if (result.getResponseCode() == HttpURLConnection.HTTP_OK){
//                        if (result.getResponse() != null){
//                            try {
//                                ACCESSTOKEN[0] = result.getResponseAsJson().getString("access_token");
//                            } catch (Collections.JSONData.JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                });
//
//        restApiClient.executeRequestMethod(RestApiClient.RequestMethod.POST);
//
//        restApiClient = new RestApiClient("http://192.168.137.1:80/login")
//                .addHeader("Authorization","JWT "+ACCESSTOKEN[0])
//                .setOnRequestCompleteListener(new RestApiClient.OnRequestCompleteListener() {
//                    @Override
//                    public void onComplete(RestApiClient.APIResponseObject result) {
//                        System.out.println(result);
//                        try {
//                            System.out.println(result.getResponseAsJson().getString("username"));
//                        } catch (Collections.JSONData.JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//
//        restApiClient.executeRequestMethod(RestApiClient.RequestMethod.GET);

        //do deleting directory

        String a = "[1, 45, \"bernard\", \"{age, name}\", 100, [21,15]]";
        JSONData jsonData = new JSONData();
        jsonData.add(1, 1);
        jsonData.add(12, a);
        jsonData.add(13, 3);

        println(jsonData.getArray(12));


    }

}