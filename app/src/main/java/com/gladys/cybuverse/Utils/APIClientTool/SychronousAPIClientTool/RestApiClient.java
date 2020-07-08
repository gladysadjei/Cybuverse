package com.gladys.cybuverse.Utils.APIClientTool.SychronousAPIClientTool;

import com.gladys.cybuverse.Utils.GeneralUtils.collections.JSONData;
import com.gladys.cybuverse.Utils.GeneralUtils.collections.KeyValuePair;
import com.gladys.cybuverse.Utils.GeneralUtils.collections.XMLObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.format;
import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.range;

public class RestApiClient {

    private ArrayList<KeyValue> HEADERS;
    private ArrayList<KeyValue> PARAMS;
    private ArrayList<String> RAW_DATA;
    private JSONData RAW_JSON_DATA;
    private XMLObject RAW_XML_DATA;

    private int CONNECTION_TIMEOUT = 15000;
    private int READ_TIMEOUT = 15000;
    private String baseUrlString;
    private int responseCode;
    private String responseMessage;
    private String responseData;

    private APIResponseObject apiResponseObject;

    private OnRequestCompleteListener requestCompleteListener;
    private OnRequestStartListener requestStartListener;


    public RestApiClient() {
        initialize();
    }

    public RestApiClient(String url) {
        setURL(url);
        initialize();
    }

    public RestApiClient(String url, OnExecuteRequestListener listener) {
        setOnExecuteRequestListener(listener);
        setURL(url);
        initialize();
    }

    public static synchronized String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append((line + "\n"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private void initialize() {
        PARAMS = new ArrayList<>();
        HEADERS = new ArrayList<>();
        RAW_JSON_DATA = new JSONData();
        RAW_XML_DATA = new XMLObject();
        RAW_DATA = new ArrayList<>();
    }

    public void setURL(String url) {
        this.baseUrlString = url.trim().replace(" ", "%20");
    }

    private RestApiClient setOnExecuteRequestListener(final OnExecuteRequestListener listener) {
        this.requestCompleteListener = new OnRequestCompleteListener() {
            @Override
            public void onComplete(APIResponseObject apiResponseObject) {
                listener.onComplete(apiResponseObject);
            }
        };

        this.requestStartListener = new OnRequestStartListener() {
            @Override
            public void onStart() {
                listener.onStart();
            }
        };

        return this;
    }

    public RestApiClient setOnRequestCompleteListener(OnRequestCompleteListener listener) {
        this.requestCompleteListener = listener;
        return this;
    }

    public RestApiClient setOnRequestStartListener(OnRequestStartListener listener) {
        this.requestStartListener = listener;
        return this;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public APIResponseObject getApiResponseObject() {
        return apiResponseObject;
    }

    public String getResponseData() {
        return responseData;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public RestApiClient addParam(String name, String value) {
        PARAMS.add(new KeyValue(name.trim(), value.trim()));
        return this;
    }

    public RestApiClient addHeader(String name, String value) {
        HEADERS.add(new KeyValue(name.trim(), value.trim()));
        return this;
    }

    public RestApiClient addJSONData(String name, String value) {
        RAW_JSON_DATA.add(name.trim(), value.trim());
        return this;
    }

    public RestApiClient addRAWData(String data) {
        RAW_DATA.add(data.trim());
        return this;
    }

    public JSONData getJSONData() {
        return RAW_JSON_DATA;
    }

    public void setJSONData(JSONData jsonData) {
        RAW_JSON_DATA = jsonData;
    }

    public XMLObject getXMLData() {
        return RAW_XML_DATA;
    }

    public void setXMLData(XMLObject xmlData) {
        RAW_XML_DATA = xmlData;
    }

    public RequestXMLDataAdder addXMLData() {
        return new RequestXMLDataAdder();
    }

    public int getConnectTimeout() {
        return CONNECTION_TIMEOUT;
    }

    public void setConnectTimeout(int timeout) {
        CONNECTION_TIMEOUT = timeout;
    }

    public int getReadTimeout() {
        return READ_TIMEOUT;
    }

    public void setReadTimeout(int timeout) {
        READ_TIMEOUT = timeout;
    }

    public boolean hasResponseData() {
        return apiResponseObject.getResponse() != null;
    }

    private boolean argumentsNotEmpty() {
        return (!RAW_XML_DATA.isEmpty() || !RAW_JSON_DATA.isEmpty() || !RAW_DATA.isEmpty());
    }

    protected APIResponseObject doInBackground(RequestMethod... params) {
        return null;
    }

    protected void onPostExecute() {
        if (requestCompleteListener != null) {
            requestCompleteListener.onComplete(getApiResponseObject());
        }
    }

    public synchronized void executeRequestMethod(RequestMethod method) throws Exception {
        URL baseURL = new URL(baseUrlString);

        //put parameters
        if (!PARAMS.isEmpty()) {

            StringBuilder combinedParams = new StringBuilder();

            combinedParams.append("?");

            for (KeyValue p : PARAMS) {
                String paramString = p.getKey() + "=" + p.getValue();  //|| URLEncoder.encode(p.getValue(), "UTF-8");
                if (combinedParams.length() > 1) {
                    combinedParams.append("&").append(paramString);
                } else {
                    combinedParams.append(paramString);
                }
            }
            combinedParams = new StringBuilder(combinedParams.toString().replace(" ", "%20"));
            baseURL = new URL(baseUrlString + combinedParams);
        }

        HttpURLConnection connection = (HttpURLConnection) baseURL.openConnection();

        switch (method) {
            case GET:
                connection.setRequestMethod(HttpRequestMethod.GET);
                break;
            case POST:
                connection.setRequestMethod(HttpRequestMethod.POST);
                break;
            case DELETE:
                connection.setRequestMethod(HttpRequestMethod.DELETE);
                break;
            case PUT:
                connection.setRequestMethod(HttpRequestMethod.PUT);
                break;
            case HEAD:
                connection.setRequestMethod(HttpRequestMethod.HEAD);
                break;
            case OPTIONS:
                connection.setRequestMethod(HttpRequestMethod.OPTIONS);
                break;
            default:
                connection.setRequestMethod(HttpRequestMethod.GET);
                break;
        }

        connection.setReadTimeout(getConnectTimeout());
        connection.setConnectTimeout(getReadTimeout());
        connection.setDoInput(true);
        connection.setDoOutput(true);

        if (!HEADERS.isEmpty()) {
            for (KeyValue r : HEADERS) {
                connection.addRequestProperty(r.getKey(), r.getValue());
            }
        }

        if (argumentsNotEmpty()) {

            JSONData DATA = new JSONData();
            JSONData raw_data = new JSONData();

            //String encodedData = URLEncoder.encode(RAW_JSON_DATA.toString(), "UTF-8");

            if (!RAW_JSON_DATA.isEmpty()) {
                DATA.add("json", RAW_JSON_DATA.toString());
            }

            if (!RAW_XML_DATA.isEmpty()) {
                DATA.add("xml", RAW_XML_DATA.toString());
            }

            if (!RAW_DATA.isEmpty()) {
                for (int i : range(RAW_DATA.size())) {
                    raw_data.add("data" + i, raw_data.get(i));
                }
                DATA.add("data", raw_data.toString());
            }

            OutputStream os = connection.getOutputStream();

            if (!DATA.isEmpty()) {
                if (DATA.size() == 1) {
                    switch (DATA.keys().get(0).toString()) {
                        case "json":
                            os.write(RAW_JSON_DATA.toString().getBytes());
                            break;
                        case "xml":
                            os.write(RAW_XML_DATA.toString().getBytes());
                            break;
                        default:
                            os.write(raw_data.toString().getBytes());
                            break;
                    }
                } else {
                    os.write(DATA.toString().getBytes());
                }
            }

            os.flush();
            os.close();
        }

        executeRequest(connection);
    }

    private synchronized void executeRequest(HttpURLConnection connection) throws IOException {
        String result = null;
        connection.connect();
        responseCode = connection.getResponseCode();
        responseMessage = connection.getResponseMessage();
        if (responseCode == 200) {
            result = convertStreamToString(connection.getInputStream());
        }
        connection.disconnect();
        responseData = result;

        apiResponseObject = new APIResponseObject(getResponseCode(), getResponseMessage(), getResponseData());

        onPostExecute();
    }

    public String toString() {
        return format("RestApiClient(<>)", baseUrlString);
    }

    public enum RequestMethod {
        GET, POST, _UPDATE, DELETE, PUT, _PATCH, _COPY, HEAD, _VIEW,
        OPTIONS, _LINK, _UNLINK, _PURGE, _LOCK, _UNLOCK, _PROPFIND
    }

    public interface OnExecuteRequestListener extends OnRequestCompleteListener, OnRequestStartListener {

    }

    public interface OnRequestCompleteListener {
        void onComplete(APIResponseObject result);
    }

    public interface OnRequestStartListener {
        void onStart();
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

        public boolean hasResponse() {
            return response != null;
        }

        public String getResponseMessage() {
            return responseMessage;
        }

        public JSONData getResponseAsJson() throws JSONData.JSONException {
            return new JSONData(response);
        }

        public String toString() {
            JSONData response = new JSONData();
            response.add("code", getResponseCode());
            response.add("message", getResponseMessage());
            response.add("result", getResponse());
            return response.toString();
        }
    }

    public static class HttpRequestMethod {
        public static final String GET = "GET";
        public static final String PUT = "PUT";
        public static final String POST = "POST";
        public static final String _LOCK = "LOCK";
        public static final String _COPY = "COPY";
        public static final String HEAD = "HEAD";
        public static final String _VIEW = "VIEW";
        public static final String _LINK = "LINK";
        public static final String _PATCH = "PATCH";
        public static final String _PURGE = "PURGE";
        public static final String _UPDATE = "UPDATE";
        public static final String DELETE = "DELETE";
        public static final String _UNLINK = "UNLINK";
        public static final String _UNLOCK = "UNLOCK";
        public static final String OPTIONS = "OPTIONS";
        public static final String _PROPFIND = "PROPFIND";
    }

    public static class HttpRequestHeaders {
        public static final String JSON = "application/json";
        public static final String ACCEPT = "Accept";
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String ENCODING_GZIP = "gzip";
        public static final String AUTHORIZATION = "Authorization";
        public static final String MIME_TEXT_PLAIN = "text/plain";
        public static final String ACCEPT_ENCODING = "Accept-Encoding";
        public static final String CONTENT_ENCODING = "Content-Encoding";
        public static final String MIME_FORM_ENCODED = "application/x-ww-form-urlencoded";
    }

    public static class HttpResponseCodes {
        public static final int OK = 200;
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int NOT_FOUND = 404;
        public static final int METHOD_NOT_ALLOWED = 405;
        public static final int INTERNAL_SERVER_ERROR = 500;
    }

    class KeyValue extends KeyValuePair<String, String> {
        public KeyValue(String key, String value) {
            super(key, value);
        }
    }

    public class RequestXMLDataAdder {

        public RequestXMLDataAdder() {
        }

        public void setXMLParent(String parent) {
            RAW_XML_DATA.setParentTAG(parent);
        }

        public RequestXMLDataAdder addXMLProperty(String propertyName, String propertyValue) {
            RAW_XML_DATA.addProperty(propertyName, propertyValue);
            return new RequestXMLDataAdder();
        }

        private RequestXMLDataAdder addXMLChild(XMLObject child) {
            RAW_XML_DATA.addChildren(child);
            return new RequestXMLDataAdder();
        }

        private RequestXMLDataAdder addXMLChildren(XMLObject... child) {
            RAW_XML_DATA.addChildren(child);
            return new RequestXMLDataAdder();
        }
    }

}
