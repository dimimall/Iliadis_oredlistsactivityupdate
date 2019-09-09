package gr.cityl.iliadis.Activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import gr.cityl.iliadis.Manager.MySingleton;
import gr.cityl.iliadis.Manager.utils;
import gr.cityl.iliadis.Models.Catalog;
import gr.cityl.iliadis.Models.Country;
import gr.cityl.iliadis.Models.Customers;
import gr.cityl.iliadis.Models.FPA;
import gr.cityl.iliadis.Models.IliadisDatabase;
import gr.cityl.iliadis.Models.Order;
import gr.cityl.iliadis.Models.Products;
import gr.cityl.iliadis.Models.SecCustomers;
import gr.cityl.iliadis.Models.ShopDatabase;
import gr.cityl.iliadis.R;
import gr.cityl.iliadis.Services.UpdateProductDbReceiver;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView textView,neworder,title;
    LinearLayout buttonLayout;
    ImageView image;
    ProgressDialog pDialog;
    IliadisDatabase iliadisDatabase;
    ShopDatabase shopDatabase;
    utils myutils;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        iliadisDatabase = IliadisDatabase.getInstance(this);
        shopDatabase = ShopDatabase.getInstance(this);

        initial();

        myutils = new utils();


        Room.databaseBuilder(MainActivity.this, ShopDatabase.class, ShopDatabase.DB_NAME)
                .addMigrations(MIGRATION_2_3,MIGRATION_3_4).build();

//        Intent notifyIntent = new Intent(getApplicationContext(),UpdateProductDbReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),101,notifyIntent,0);
//        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,   SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES,AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (utils.isConnectedToNetwork(getApplicationContext()))
        {
            loadDatabase();
        }
        else {
            Toast.makeText(MainActivity.this,"Disconnected netowrk",Toast.LENGTH_LONG).show();
        }

        buttonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,NewOrderActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
//
        } else {
            //super.onBackPressed();
            new AlertDialog.Builder(this)
                    .setTitle("Iliadis")
                    .setMessage(getString(R.string.closeapp))
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            quit();
                        }
                    }).create().show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        myutils.sharedpreferences = getSharedPreferences(myutils.MyPREFERENCES, Context.MODE_PRIVATE);
        String custid = myutils.sharedpreferences.getString("custid", "");
        String shopid = myutils.sharedpreferences.getString("shopid","");

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(MainActivity.this,NewOrderActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {
            List<Order> orders = shopDatabase.daoShop().getListOrderStatus0();
            Customers customer = iliadisDatabase.daoAccess().getCustomerByCustid(custid);
            Intent intent = new Intent(MainActivity.this,OrderListsActivity.class);
            intent.putExtra("custid",customer.getCustid());
            intent.putExtra("custvatid", customer.getCustvatid());
            intent.putExtra("catalogueid",customer.getCatalogueid());
            intent.putExtra("orders", (Serializable) orders);
            intent.putExtra("shopsid",shopid);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {
            List<Order> orders = shopDatabase.daoShop().getListOrderStatus1();
            Customers customer = iliadisDatabase.daoAccess().getCustomerByCustid(custid);
            Intent intent = new Intent(MainActivity.this,ReprintListsActivity.class);
            intent.putExtra("custid",customer.getCustid());
            intent.putExtra("custvatid", customer.getCustvatid());
            intent.putExtra("catalogueid",customer.getCatalogueid());
            intent.putExtra("orders", (Serializable) orders);
            intent.putExtra("shopsid",shopid);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(MainActivity.this,ReloadDbsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(MainActivity.this,ProductActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_settings) {
            Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void quit() {
        Intent start = new Intent(Intent.ACTION_MAIN);
        start.addCategory(Intent.CATEGORY_HOME);
        //start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //start.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(start);
    }

    public void initial()
    {
        title = (TextView)toolbar.findViewById(R.id.title);
        neworder = (TextView)findViewById(R.id.text);
        textView = (TextView)findViewById(R.id.textView);
        image = (ImageView)findViewById(R.id.image);
        buttonLayout = (LinearLayout)findViewById(R.id.buttonlayout);
        image.setColorFilter(Color.argb(255, 255, 255, 255));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        title.setText(getString(R.string.home));
        neworder.setText(getString(R.string.neworder));
        super.onConfigurationChanged(newConfig);
    }

    private void loadJsonProducts() {
        final ArrayList<Products> products = new ArrayList<>();
        displayLoader(getString(R.string.downloadfileproduct));
        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.GET, "https://pod.iliadis.com.gr/getproducts.asp", null, new Response.Listener<JSONArray>() {
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
                                Log.d("Dimitra",product.getProdescription());

                            }
                            iliadisDatabase.daoAccess().insertTaskProducts(products);
                            loadJsonCustomers();

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

    }
    private ArrayList<Customers> loadJsonCustomers(){
        displayLoader(getString(R.string.downloadfilecustomer));
        final ArrayList<Customers> customers = new ArrayList<>();
        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.GET, "https://pod.iliadis.com.gr/getcustomersx.asp", null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray responseArray) {
                        pDialog.dismiss();
                        try {
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
                                customer.setEmail(response.getString("email"));
                                customer.setCatalogueid(response.getInt("catalogueid"));
                                customer.setPostalCode(response.getString("postalCode"));
                                customer.setCustid(response.getString("custid"));
                                customer.setPaymentid(response.getString("paymentid"));
                                customer.setCustvatid(response.getString("custvatid"));
                                customer.setCustomerid(response.getString("customerid"));
                                customers.add(customer);
                                Log.d("Dimitra",customer.getCompanyName());

                                //iliadisDatabase.daoAccess().insertTask(customer);
                            }
                            iliadisDatabase.daoAccess().insertTaskCustomers(customers);
                            loadJsonASecCustomers();
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

    private ArrayList<SecCustomers> loadJsonASecCustomers() {
        displayLoader(getString(R.string.downloadfileshops));

        final ArrayList<SecCustomers> secCustomers = new ArrayList<>();
        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.GET, "https://pod.iliadis.com.gr/getseccustomers.asp", null, new Response.Listener<JSONArray>() {
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

                                Log.d("Dimitra",secCustomer.getCompanyName());

                                //iliadisDatabase.daoAccess().insertTask(secCustomer);
                            }
                            iliadisDatabase.daoAccess().insertTaskShops(secCustomers);
                            loadJsonCatalog();
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

    private ArrayList<Catalog> loadJsonCatalog() {
        displayLoader(getString(R.string.downloadfilecatalogue));

        final ArrayList<Catalog> catalogs = new ArrayList<>();
        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.GET, "https://pod.iliadis.com.gr/getcatalogues.asp", null, new Response.Listener<JSONArray>() {
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
                                Log.d("Dimitra","catalog id "+catalog.getCatid());

                                //iliadisDatabase.daoAccess().insertTask(catalog);
                            }
                            iliadisDatabase.daoAccess().insertTaskCatalog(catalogs);
                            loadJsonFPA();
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
        displayLoader(getString(R.string.downloadfilevat));

        final ArrayList<FPA> fpa = new ArrayList<>();
        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.GET, "https://pod.iliadis.com.gr/getvats.asp", null, new Response.Listener<JSONArray>() {
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
                                if (response.has("custvatid")){
                                    fpa1.setCustvatid(response.getString("custvatid"));
                                }
                                fpa.add(fpa1);
                                Log.d("Dimitra","fpa id "+fpa1.getVatid());
                                //iliadisDatabase.daoAccess().insertTask(fpa1);
                            }
                            iliadisDatabase.daoAccess().insertTaskFpa(fpa);
                            loadJsonCountry();
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
        displayLoader(getString(R.string.downloadfilecountry));

        final ArrayList<Country> countries = new ArrayList<>();

        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.GET, "https://pod.iliadis.com.gr/getcountries.asp", null, new Response.Listener<JSONArray>() {
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
                                countries.add(country);
                                Log.d("Dimitra",country.getCountry());
                            }
                            iliadisDatabase.daoAccess().insertTaskCountry(countries);
                            //loadJsonCatalog();

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

    private void displayLoader(String message){
        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage(message);
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public void loadDatabase()
    {
        if (iliadisDatabase.daoAccess().getProductsList().size() <=0)
        {
            loadJsonProducts();
        }
    }

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE cart (cartid INTEGER primary key autoincrement NOT NULL,  "
                    +"orderid INTEGER, "+ "realcode TEXT, "+"prodcode TEXT, "+"price TEXT, "+"comment TEXT, "+"description TEXT, "+"quantity INTEGER, "+"vatcode TEXT, "+"priceid TEXT)");
            database.execSQL("CREATE TABLE order (orderid INTEGER primary key autoincrement NOT NULL, "+"custid TEXT, "+"status INTEGER, "+"dateparsed TEXT)");
        }
    };
    //Add column
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE cart "
                    + " ADD COLUMN discountid TEXT , COLUMN priceid TEXT");

            database.execSQL("ALTER TABLE order "
                    + " ADD COLUMN commentorder TEXT");
        }
    };
}
