package com.example.nilesh.apicall;

import android.util.Log;

import com.example.nilesh.model.UserDetails;
import com.example.nilesh.util.XmlParser;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MakeAPICall {

	private static HttpClient connection;
	public static void makeGETRequest(final String url, final String parameter, final ApiListener apiListener)
	{
        Log.v("TAG", "makeGetRequest " + url);
        new Thread(new Runnable() {
            @Override
            public void run() {

                Log.v("TAG", "thread running");

                ArrayList<UserDetails> userDetailsList = new ArrayList<UserDetails>();

                connection = new DefaultHttpClient();
                try {
                    HttpGet request = new HttpGet(url+parameter.replace(" ", ""));
                    request.setHeader("Authorization", "Basic YWRtaW46YWRtaW4=");
                    HttpResponse httpResponse = connection.execute(request);

                    // receive response as inputStream
                    InputStream is = httpResponse.getEntity().getContent();
                    Log.v("TAG", "parsing response");
                    XmlParser xmlParser = new XmlParser();
                    userDetailsList = xmlParser.parse(is);

                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    apiListener.onLoadError();
                    return;
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    apiListener.onLoadError();
                    return;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    apiListener.onLoadError();
                    return;
                }

                apiListener.onLoadFinished(userDetailsList);
            }
        }).start();

	}
		
}
