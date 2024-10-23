package com.example.jerry.ufo;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by charles on 17/11/16.
 */

public class APIRestClient {

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void setSession(String sessionToken) {
        client.addHeader("Cookie", sessionToken);
    }


    private static String getAbsoluteUrl(String relativeUrl) {
        return Model.getInstance().BASE_URL + ":" + Model.getInstance().PORT + "/" + relativeUrl;
    }
}
