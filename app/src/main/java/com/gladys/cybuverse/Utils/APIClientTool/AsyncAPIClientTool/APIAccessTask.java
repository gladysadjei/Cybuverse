package com.gladys.cybuverse.Utils.APIClientTool.AsyncAPIClientTool;

import android.content.Context;

import com.gladys.cybuverse.Utils.GeneralUtils.collections.JSONData;
import com.gladys.cybuverse.Utils.GeneralUtils.collections.KeyValuePair;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


//extends AsyncTask<String,Void,APIResponseObject>

public class APIAccessTask {
    private String requestUrl;
    private Context context;
    private HttpURLConnection urlConnection;
    private List<KeyValuePair<String, String>> headerData, paramData;
    private JSONData postData;
    private String method;
    private int responseCode = HttpURLConnection.HTTP_OK;
    private String responseMessage;
    private OnRequestCompleteListener delegate;


    public APIAccessTask(Context context, String requestUrl, String method) {
        this.context = context;
        this.method = method;
        try {
            this.requestUrl = requestUrl;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        initializeFields();
    }

    public APIAccessTask(Context context, String requestUrl, String method, OnRequestCompleteListener delegate) {
        this.context = context;
        this.delegate = delegate;
        this.method = method;
        try {
            this.requestUrl = requestUrl;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        initializeFields();
    }

    public Context getContext() {
        return context;
    }

    public APIAccessTask setRequestOnCompleteListener(OnRequestCompleteListener delegate) {
        this.delegate = delegate;
        return this;
    }

    public APIAccessTask addParam(String key, String value) {
        paramData.add(new KeyValuePair<>(key, value));
        return this;
    }

    public APIAccessTask addHeader(String key, String value) {
        headerData.add(new KeyValuePair<>(key, value));
        return this;
    }

    public APIAccessTask addPostData(String key, String value) {
        postData.add(key, value);
        return this;
    }

    private void initializeFields() {
        this.requestUrl = this.requestUrl.replace(" ", "%20");
        this.postData = new JSONData();
        this.paramData = new ArrayList<>();
        this.headerData = new ArrayList<>();
    }

    protected void onPreExecute() {
//        super.onpreExecute();
    }

    protected APIResponseObject doInBackground(String... params) {
        try {

//            prepareParamData();

            urlConnection = (HttpURLConnection) new URL(requestUrl).openConnection();
            if (!headerData.isEmpty()) {
                for (KeyValuePair<String, String> pair : headerData) {
                    urlConnection.setRequestProperty(pair.getKey(), pair.getValue());
                }
            }

            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setRequestMethod(method);
            urlConnection.connect();
            StringBuilder sb = new StringBuilder();

            if (!(method.equals("GET")) && !postData.isEmpty()) {
                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(postData.toString());
                writer.flush();
                writer.close();
                out.close();
            }

            urlConnection.connect();

            responseCode = urlConnection.getResponseCode();
            responseMessage = urlConnection.getResponseMessage();
            if (responseCode == HttpURLConnection.HTTP_OK) {

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append((line + "\n"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return new APIResponseObject(responseCode, responseMessage, sb.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(APIResponseObject result) {
        if (delegate != null) {
            delegate.onComplete(result);
        }
//        super.onPostExecute(result);
    }

    private String getParamsString(List<KeyValuePair<String, String>> paramData) {
        if (!paramData.isEmpty()) {

            StringBuilder combinedParams = new StringBuilder();

            combinedParams.append("?");

            for (KeyValuePair p : paramData) {
                String paramString = p.getKey() + "=" + p.getValue();
                if (combinedParams.length() > 1) {
                    combinedParams.append("&").append(paramString);
                } else {
                    combinedParams.append(paramString);
                }
            }
            combinedParams = new StringBuilder(combinedParams.toString().replace(" ", "%20"));
            return combinedParams.toString();
        }
        return "";
    }

    private void prepareParamData() {
        if (!paramData.isEmpty()) {
            StringBuilder combinedParams = new StringBuilder();

            combinedParams.append("?");

            for (KeyValuePair p : paramData) {
                String paramString = p.getKey() + "=" + p.getValue();
                if (combinedParams.length() > 1) {
                    combinedParams.append("&").append(paramString);
                } else {
                    combinedParams.append(paramString);
                }
            }
            combinedParams = new StringBuilder(combinedParams.toString().replace(" ", "%20"));
            this.requestUrl = requestUrl + combinedParams;
        }
    }

    public void execute() {
        onPreExecute();
        onPostExecute(doInBackground());
    }

    public interface OnRequestCompleteListener {
        void onComplete(APIResponseObject result);
    }

    public static class APIResponseObject {
        private int responseCode;
        private String response;
        private String responseMessage;

        APIResponseObject(int responseCode, String responseMessage, String response) {
            this.responseMessage = responseMessage;
            this.responseCode = responseCode;
            this.response = response;
        }

        public String getResponse() {
            return response;
        }

        public int getResponseCode() {
            return responseCode;
        }

        public String getResponseMessage() {
            return responseMessage;
        }
    }
}
