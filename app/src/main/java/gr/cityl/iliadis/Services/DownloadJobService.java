package gr.cityl.iliadis.Services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gr.cityl.iliadis.Manager.MySingleton;
import gr.cityl.iliadis.Models.Country;
import gr.cityl.iliadis.Models.IliadisDatabase;
import gr.cityl.iliadis.Models.Products;

/**
 * Created by dimitra on 04/09/2019.
 */

public class DownloadJobService extends JobService{

    IliadisDatabase iliadisDatabase ;
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        updateNewProductsDb(this);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public void updateNewProductsDb(Context context) {
        iliadisDatabase = IliadisDatabase.getInstance(context);
        List<Products> products = iliadisDatabase.daoAccess().getProductsList();
        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.GET, "https://pod.iliadis.com.gr/getproductsupdate.asp", null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray responseArray) {
                        try {
                            //Parse the JSON response array by iterating over it
                            for (int i = 0; i < responseArray.length(); i++) {
                                JSONObject response = responseArray.getJSONObject(i);
                                Log.d("Dimitra",response.toString());

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Display error message whenever an error occurs
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }
}
