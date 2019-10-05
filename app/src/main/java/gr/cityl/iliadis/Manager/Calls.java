package gr.cityl.iliadis.Manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dimitra on 08/09/2019.
 */

public class Calls {

    utils myutils = new utils();

    public void makePostUsingVolley(final Context context, final String custid, final String salesid, final String orderid)
    {
        String tag_json_obj = "json_obj_req";
        String url = "https://pod.iliadis.com.gr/iamhere.asp";

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading...");
        pDialog.show();

        StringRequest req = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONArray json;
                        ObjectOutput out = null;
                        try {
                            json = new JSONArray(response);
                            JSONObject object = json.getJSONObject(0);
                            String result = object.getString("result");
                            Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        pDialog.hide();
                        // Toast.makeText(getApplicationContext(),"hi", Toast.LENGTH_SHORT).show();
                        Log.d("", response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("", "Error: " + error.getMessage());
                Toast.makeText(context.getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                pDialog.hide();
                // hide the progress dialog
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("salesid",salesid);
                params.put("orderid",orderid);
                params.put("custid",custid);
                params.put("mac",myutils.getMacAddress(context));
                return params;
            }

        };
        // Adding request to request queue
        MySingleton.getInstance(context).addToRequestQueue(req);
    }

    public String sendCsvFiles(File file,File fileDir){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String success="";

        String url = "https://pod.iliadis.com.gr/UploadFile.php";
        File filecsv = new File(fileDir,file.getName());
        if(filecsv.exists()) {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);

                try {
                    FileBody bin = new FileBody(filecsv);
                    //MultipartEntityBuilder builder = MultipartEntityBuilder.create();

                    MultipartEntity reqEntity = new MultipartEntity();
                    reqEntity.addPart("order", bin);
                    httppost.setEntity(reqEntity);

                    System.out.println("Requesting : " + httppost.getRequestLine());
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    String responseBody = httpclient.execute(httppost, responseHandler);

                    System.out.println("responseBody : " + responseBody);

                    JSONArray array = new JSONArray(responseBody);
                    JSONObject object = array.getJSONObject(0);
                    success = object.getString("result");

                    Log.d("Dimitra", " file "+success);

                } catch (UnsupportedEncodingException e) {
                    Log.e("Error",e.getLocalizedMessage());
                } catch (ClientProtocolException e) {
                    Log.e("Error",e.getLocalizedMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    httpclient.getConnectionManager().shutdown();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return success;
    }
}
