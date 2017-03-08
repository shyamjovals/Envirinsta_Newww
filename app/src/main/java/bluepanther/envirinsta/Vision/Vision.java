package bluepanther.envirinsta.Vision;


import android.net.http.RequestQueue;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import bluepanther.envirinsta.Adapter.Cred_Update;
import bluepanther.envirinsta.Adapter.CurrentUser;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by shyam on 8/3/17.
 */

public class Vision {
    String filepath;
    PrintStream out;
    public MediaType JSON;
    OkHttpClient client;
    String mystring;
    public Vision(String str)
    {
        mystring=str;

               JSON = MediaType.parse("application/json; charset=utf-8");

        client = new OkHttpClient();
new MyTask().execute();


    }
    private class MyTask extends AsyncTask<String, Integer, String> {

        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressDialog = ProgressDialog.show(Sign_Up.this, "Message", "Creating Account...");

        }
       // String json="{\"requests\":[{\"images\": {\"source\": {\"gcsImageUri\": \"gs://envirinsta.appspot.com/Classes/Chennai/A/Images/shyam07-03-17 15:34:03\"}},\"features\": [{\"type\": \"SAFE_SEARCH_DETECTION\"}]}]}";
        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {
            // get the string from params, which is an array
            //  Accounts doc=new Accounts(date,fullname,image);

           // String json="{\"requests\":[{\"images\": {\"source\": {\"gcsImageUri\": \"gs://envirinsta.appspot.com/Classes/Chennai/A/Images/shyam07-03-17 15:34:03\"}},\"features\": [{\"type\": \"SAFE_SEARCH_DETECTION\"}]}]}";
String json ="{\"requests\":[{\"image\":{\"content\":\""+mystring+"\"},\"features\":[{\"type\":\"SAFE_SEARCH_DETECTION\"}]}]}";
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url("https://vision.googleapis.com/v1/images:annotate?key=AIzaSyADtvO9_3vK3TkkHW5imY4t25AXaE8Gkis")
                    .post(body)
                    .build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {

                System.out.println("Final response: "+response.body().string());
                JSONObject objJsonObject = new JSONObject(response.body().toString());
                System.out.println("Wathaaa"+objJsonObject.getString("safeSearchAnnotation"));
            } catch (Exception e) {
                System.out.println("Exceptionn is"+e);
                e.printStackTrace();
            }
//
//            System.out.println("Executing http");
//
//            HttpClient httpClient = new DefaultHttpClient();
//            // Creating HTTP Post
//            HttpPost httpPost = new HttpPost(
//                    "https://vision.googleapis.com/v1/images:annotate?key=AIzaSyADtvO9_3vK3TkkHW5imY4t25AXaE8Gkis");
//
//            // Building post parameters

//            HttpClient httpClient = new DefaultHttpClient();
//
//           // HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead
//
//            try {
//
//                HttpPost request = new HttpPost("https://vision.googleapis.com/v1/images:annotate?key=AIzaSyADtvO9_3vK3TkkHW5imY4t25AXaE8Gkis");
//                StringEntity paramss =new StringEntity("{\"requests\":[{\"images\":{\"content\":\""+mystring+"\"},\"features\":[{\"type\":\"SAFE_SEARCH_DETECTION\"}]}]}");
//                request.addHeader("content-type", "application/json");
//                request.setEntity(paramss);
//                System.out.println("COntent header length:"+request.getHeaders("content-length"));
//
//                HttpResponse response = httpClient.execute(request);
//                System.out.println("Final response: "+response.toString());
//                BufferedReader rd = new BufferedReader(new InputStreamReader(
//                        response.getEntity().getContent()));
//
//                String line = "";
//                while ((line = rd.readLine()) != null) {
//                    System.out.println(line);
//                    if (line.startsWith("Auth=")) {
//                        String key = line.substring(5);
//                        // do something with the key
//                    }
//
//                }
////
//                //handle response here...
//
//            }catch (Exception ex) {
//
//                //handle exception here
//                System.out.println("lol ex is"+ex);
//
//            }
            return "SUCCESS";
        }


        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Do things like hide the progress bar or change a TextView
        }
    }

}
