package gr.cityl.iliadis.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gr.cityl.iliadis.Manager.MySingleton;
import gr.cityl.iliadis.Manager.utils;
import gr.cityl.iliadis.Models.Catalog;
import gr.cityl.iliadis.Models.Country;
import gr.cityl.iliadis.Models.Customers;
import gr.cityl.iliadis.Models.FPA;
import gr.cityl.iliadis.Models.IliadisDatabase;
import gr.cityl.iliadis.Models.Products;
import gr.cityl.iliadis.Models.SecCustomers;
import gr.cityl.iliadis.R;

public class ReloadDbsActivity extends AppCompatActivity {

    Button product;
    Button customer;
    Button shop;
    Button catalog;
    Button fpa;
    Button country;
    IliadisDatabase iliadisDatabase;
    utils myutils;
    String ipserverpref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reload_dbs);

        getSupportActionBar().setTitle("Reload Db");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        iliadisDatabase = IliadisDatabase.getInstance(this);

        init();
        myutils = new utils();

        myutils.sharedpreferences = getSharedPreferences(myutils.MyPREFERENCES, Context.MODE_PRIVATE);
        ipserverpref = myutils.sharedpreferences.getString("ipserver", "");

        product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (utils.isConnectedToNetwork(getApplicationContext()))
                {
                    iliadisDatabase.daoAccess().deleteProducts(iliadisDatabase.daoAccess().getProductsList());
                    loadJsonProducts();
                }
                else {
                    Toast.makeText(ReloadDbsActivity.this,"No internet connection",Toast.LENGTH_LONG).show();
                }
            }
        });

        customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (utils.isConnectedToNetwork(getApplicationContext()))
                {
                    iliadisDatabase.daoAccess().deleteCustomer(iliadisDatabase.daoAccess().getCustomersList());
                    loadJsonCustomers();
                }
                else {
                    Toast.makeText(ReloadDbsActivity.this,"No internet connection",Toast.LENGTH_LONG).show();
                }
            }
        });

        shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (utils.isConnectedToNetwork(getApplicationContext()))
                {
                    iliadisDatabase.daoAccess().deleteShops(iliadisDatabase.daoAccess().getSecCustomersList());
                    loadJsonASecCustomers();
                }else {
                    Toast.makeText(ReloadDbsActivity.this,"No internet connection",Toast.LENGTH_LONG).show();
                }
            }
        });

        catalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (utils.isConnectedToNetwork(getApplicationContext()))
                {
                    iliadisDatabase.daoAccess().deleteCatalog(iliadisDatabase.daoAccess().getCatalogList());
                    loadJsonCatalog();
                } else {
                    Toast.makeText(ReloadDbsActivity.this,"No internet connection",Toast.LENGTH_LONG).show();
                }
            }
        });

        fpa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (utils.isConnectedToNetwork(getApplicationContext()))
                {
                    iliadisDatabase.daoAccess().deleteFpa(iliadisDatabase.daoAccess().getFpaList());
                    loadJsonFPA();
                }else {
                    Toast.makeText(ReloadDbsActivity.this,"No internet connection",Toast.LENGTH_LONG).show();
                }
            }
        });

        country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (utils.isConnectedToNetwork(getApplicationContext()))
                {
                    iliadisDatabase.daoAccess().deleteCountry(iliadisDatabase.daoAccess().getCountryList());
                    loadJsonCountry();
                }else {
                    Toast.makeText(ReloadDbsActivity.this,"No internet connection",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void init()
    {
        product = (Button)findViewById(R.id.button3);
        customer = (Button)findViewById(R.id.button4);
        shop = (Button)findViewById(R.id.button5);
        catalog = (Button)findViewById(R.id.button6);
        fpa = (Button)findViewById(R.id.button7);
        country = (Button)findViewById(R.id.button9);
    }

    private ArrayList<Products> loadJsonProducts() {
        final ArrayList<Products> products = new ArrayList<>();

        final ProgressDialog  pDialog = new ProgressDialog(ReloadDbsActivity.this);
        pDialog.setMessage(getString(R.string.downloadfileproduct));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.GET, ipserverpref+"/getproducts.asp", null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray responseArray) {
                        pDialog.dismiss();
                        try {
                            //Parse the JSON response array by iterating over it
                            for (int i = 1; i < responseArray.length(); i++) {
                                JSONObject response = responseArray.getJSONObject(i);
                                Products product = new Products();
                                product.setProdcode(response.getString("prodcode"));
                                product.setRealcode(response.getString("realcode"));
                                product.setProdescription(response.getString("prodescription"));
                                Log.d("Dimitra","desc: "+response.getString("prodescription"));
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

                                products.add(product);
                            }
                            iliadisDatabase.daoAccess().insertTaskProducts(products);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();

                        //Display error message whenever an error occurs
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);

        return products;
    }
    private ArrayList<Customers> loadJsonCustomers(){
        final ArrayList<Customers> customers = new ArrayList<>();

        final ProgressDialog  pDialog = new ProgressDialog(ReloadDbsActivity.this);
        pDialog.setMessage(getString(R.string.downloadfilecustomer));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.GET, ipserverpref+"/getcustomersx.asp", null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray responseArray) {
                        pDialog.dismiss();
                        try {
                            StringBuilder textViewData = new StringBuilder();
                            //Parse the JSON response array by iterating over it
                            for (int i = 0; i < responseArray.length(); i++) {
                                JSONObject response = responseArray.getJSONObject(i);
                                Customers customer = new Customers();
                                customer.setAddress(response.getString("address"));
                                customer.setProfession(response.getString("profession"));
                                customer.setTaxOffice(response.getString("taxOffice"));
                                customer.setCompanyName(response.getString("companyName"));
                                customer.setCity(response.getString("city"));
                                customer.setCountry(response.getString("country"));
                                customer.setRegion(response.getString("region"));
                                customer.setPhone(response.getString("phone"));
                                customer.setFax(response.getString("fax"));
                                customer.setAfm(response.getString("afm"));
                                Log.d("Dimitra","desc: "+response.getString("afm"));
                                customer.setEmail(response.getString("email"));
                                customer.setCatalogueid(response.getInt("catalogueid"));
                                customer.setPostalCode(response.getString("postalCode"));
                                customer.setCustid(response.getString("custid"));
                                customer.setPaymentid(response.getString("paymentid"));
                                customer.setCustvatid(response.getString("custvatid"));
                                customer.setCustomerid(response.getString("customerid"));

                                customers.add(customer);
                            }
                            iliadisDatabase.daoAccess().insertTaskCustomers(customers);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();

                        //Display error message whenever an error occurs
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
        return customers;
    }
    private ArrayList<Catalog> loadJsonCatalog() {
        final ArrayList<Catalog> catalogs = new ArrayList<>();

        final ProgressDialog  pDialog = new ProgressDialog(ReloadDbsActivity.this);
        pDialog.setMessage(getString(R.string.downloadfilecatalogue));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.GET, ipserverpref+"/getcatalogues.asp", null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray responseArray) {
                        pDialog.dismiss();
                        try {
                            StringBuilder textViewData = new StringBuilder();
                            //Parse the JSON response array by iterating over it
                            for (int i = 0; i < responseArray.length(); i++) {
                                JSONObject response = responseArray.getJSONObject(i);
                                Catalog catalog = new Catalog();
                                catalog.setCatid(response.getString("catid"));
                                catalog.setCustcatid(response.getString("custcatid"));
                                Log.d("Dimitra","desc: "+response.getString("custcatid"));
                                catalog.setDiscountqstart1(response.getInt("discountqstart1"));
                                catalog.setDiscountqstart2(response.getInt("discountqstart2"));
                                catalog.setDiscountqstart3(response.getInt("discountqstart3"));
                                catalog.setDiscountqstart4(response.getInt("discountqstart4"));
                                catalog.setDiscountqstart5(response.getInt("discountqstart5"));
                                catalog.setDiscountqend1(response.getInt("discountqend1"));
                                catalog.setDiscountqend2(response.getInt("discountqend2"));
                                catalog.setDiscountqend3(response.getInt("discountqend3"));
                                catalog.setDiscountqend4(response.getInt("discountqend4"));
                                catalog.setDiscountqend5(response.getInt("discountqend5"));
                                catalog.setDiscount1(response.getInt("discount1"));
                                catalog.setDiscount2(response.getInt("discount2"));
                                catalog.setDiscount3(response.getInt("discount3"));
                                catalog.setDiscount4(response.getInt("discount4"));
                                catalog.setDiscount5(response.getInt("discount5"));

                                catalogs.add(catalog);
                            }
                            iliadisDatabase.daoAccess().insertTaskCatalog(catalogs);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();

                        //Display error message whenever an error occurs
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
        return catalogs;
    }
    private ArrayList<FPA> loadJsonFPA() {
        final ArrayList<FPA> fpa = new ArrayList<>();

        final ProgressDialog  pDialog = new ProgressDialog(ReloadDbsActivity.this);
        pDialog.setMessage(getString(R.string.downloadfilevat));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.GET, ipserverpref+"/getvats.asp", null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray responseArray) {
                        pDialog.dismiss();
                        try {
                            StringBuilder textViewData = new StringBuilder();
                            //Parse the JSON response array by iterating over it
                            for (int i = 0; i < responseArray.length(); i++) {
                                JSONObject response = responseArray.getJSONObject(i);
                                FPA fpa1 = new FPA();
                                fpa1.setVatid(response.getString("vatid"));
                                fpa1.setVat(response.getDouble("vat"));
                                Log.d("Dimitra ","vat: "+response.getString("vat"));
                                if (response.has("custvatid")){
                                    fpa1.setCustvatid(response.getString("custvatid"));
                                }

                                fpa.add(fpa1);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();

                        //Display error message whenever an error occurs
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
        return fpa;
    }
    private ArrayList<Country> loadJsonCountry() {
        final ArrayList<Country> countries = new ArrayList<>();

        final ProgressDialog  pDialog = new ProgressDialog(ReloadDbsActivity.this);
        pDialog.setMessage(getString(R.string.downloadfilecountry));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.GET, ipserverpref+"/getcountries.asp", null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray responseArray) {
                        pDialog.dismiss();
                        try {
                            StringBuilder textViewData = new StringBuilder();
                            //Parse the JSON response array by iterating over it
                            for (int i = 0; i < responseArray.length(); i++) {
                                JSONObject response = responseArray.getJSONObject(i);
                                Country country = new Country();
                                country.setCountryid(response.getString("countryid"));
                                country.setCountry(response.getString("country"));
                                Log.d("Dimitra","country: "+response.getString("country"));

                                countries.add(country);
                            }
                            iliadisDatabase.daoAccess().insertTaskCountry(countries);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();

                        //Display error message whenever an error occurs
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
        return countries;
    }
    private ArrayList<SecCustomers> loadJsonASecCustomers() {
        final ArrayList<SecCustomers> secCustomers = new ArrayList<>();

        final ProgressDialog  pDialog = new ProgressDialog(ReloadDbsActivity.this);
        pDialog.setMessage(getString(R.string.downloadfileshops));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.GET, ipserverpref+"/getseccustomers.asp", null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray responseArray) {
                        pDialog.dismiss();
                        try {
                            StringBuilder textViewData = new StringBuilder();
                            //Parse the JSON response array by iterating over it
                            for (int i = 0; i < responseArray.length(); i++) {
                                JSONObject response = responseArray.getJSONObject(i);
                                SecCustomers secCustomer = new SecCustomers();
                                secCustomer.setCustid(response.getString("custid"));
                                secCustomer.setShopid(response.getString("shopid"));
                                secCustomer.setCompanyName(response.getString("companyName"));
                                secCustomer.setAddress(response.getString("address"));
                                secCustomer.setPhone(response.getString("phone"));

                                secCustomers.add(secCustomer);
                            }
                            iliadisDatabase.daoAccess().insertTaskShops(secCustomers);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();

                        //Display error message whenever an error occurs
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
        return secCustomers;
    }
}
