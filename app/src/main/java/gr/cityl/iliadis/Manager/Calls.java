package gr.cityl.iliadis.Manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dimitra on 08/09/2019.
 */

public class Calls {

    public void makePostUsingVolley(final Context context, final String custid, final String salesid, final String orderid)
    {
        String tag_json_obj = "json_obj_req";
        String url = "https://pod.iliadis.com.gr/iamhere.asp";

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading...");
        pDialog.show();

        StringRequest req = new StringRequest(Request.Method.GET,url,
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
                params.put("custid",custid);
                params.put("salesid",salesid);
                params.put("orderid",orderid);
                return params;
            }

        };
        // Adding request to request queue
        MySingleton.getInstance(context).addToRequestQueue(req);
    }
}
