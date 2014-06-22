package com.example.metster;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

public class RequestTask extends AsyncTask<String, String, String> {
		
	
	
	@Override
    protected String doInBackground(String... uri) {
		
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(uri[0]);
        HttpResponse response;
        String responseString = null;
       
        
        
        
        try {  	
        	List<NameValuePair> nameValuePairs;
        	nameValuePairs = new ArrayList<NameValuePair>(6);
            nameValuePairs.add(new BasicNameValuePair("appkey", uri[1]));
            nameValuePairs.add(new BasicNameValuePair("firstname", uri[2]));
            nameValuePairs.add(new BasicNameValuePair("lastname", uri[3]));
            nameValuePairs.add(new BasicNameValuePair("email", uri[4]));
            nameValuePairs.add(new BasicNameValuePair("password", uri[5]));
            nameValuePairs.add(new BasicNameValuePair("image", uri[6]));
            nameValuePairs.add(new BasicNameValuePair("gender", uri[7]));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpclient.execute(httppost);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            //TODO Handle problems..
        } catch (IOException e) {
            //TODO Handle problems..
        }
        return responseString;
    }
	
	
    @Override
    protected void onPostExecute(String result) {
    	
        super.onPostExecute(result);
    }
    
				    
}
