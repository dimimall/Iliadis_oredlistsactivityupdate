package gr.cityl.iliadis.Services;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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

import gr.cityl.iliadis.Activities.MainActivity;
import gr.cityl.iliadis.Manager.MySingleton;
import gr.cityl.iliadis.Models.IliadisDatabase;
import gr.cityl.iliadis.Models.Products;

public class UpdateProductDbReceiver extends BroadcastReceiver {

    IliadisDatabase iliadisDatabase ;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

//        Intent contentIntent = new Intent(context, MainActivity.class);
//
//        PendingIntent contentPendingIntent = PendingIntent.getActivity
//                (context, 101, contentIntent, PendingIntent
//                        .FLAG_UPDATE_CURRENT);

        updateNewProductsDb(context);
    }

    public void updateNewProductsDb(final Context context) {
        iliadisDatabase = IliadisDatabase.getInstance(context);
        final List<Products> products = iliadisDatabase.daoAccess().getProductsList();
        final List<Products> products1 = new ArrayList<>();
        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.GET, "https://pod.iliadis.com.gr/getproductsupdate.asp", null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray responseArray) {
                        try {
                            //Parse the JSON response array by iterating over it
                            for (int i = 0; i < responseArray.length(); i++) {
                                JSONObject response = responseArray.getJSONObject(i);
                                Log.d("Receiver update",response.toString());
                                Products product = new Products();
                                product.setProdcode(response.getString("prodcode"));
                                product.setRealcode(response.getString("realcode"));
                                product.setProdescription(response.getString("prodescription"));
                                product.setProdescriptionEn(response.getString("prodescriptionen"));
                                product.setVatcode(response.getString("vatcode"));
                                product.setPriceid(response.getString("pricesid"));
                                product.setPrice(response.getString("price"));
                                product.setSpecialprice(response.getString("specialprice"));
                                product.setQuantityap(response.getString("quantityap"));
                                product.setQuantityav(response.getString("quantityav"));
                                product.setQuantitytotal(response.getString("quantitytotal"));
                                product.setReserved(response.getString("reserved"));
                                product.setQuantitywaiting(response.getString("quantitywaiting"));
                                product.setAdate(response.getString("adate"));
                                product.setMinimumstep(response.getString("minimumstep"));
                                product.setMinquantity(response.getString("minquantity"));
                                products1.add(product);
                            }
                            if (products.size() > 0)
                            {
                                updateDbProduct(products,products1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Display error message whenever an error occurs
                        Toast.makeText(context,
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(context).addToRequestQueue(jsArrayRequest);
    }

    public void updateDbProduct(List<Products> oldList, List<Products> newList)
    {
        List<Products> products = new ArrayList<>();

        for (int i=0; i<oldList.size(); i++)
        {
            for (int j=0; j < newList.size(); j++)
            {
                if (oldList.get(i).getProdcode().equals(newList.get(j).getProdcode()))
                {
                    products.add(newList.get(j));
                }
            }
        }
        iliadisDatabase.daoAccess().updateProductList(products);
    }
}
