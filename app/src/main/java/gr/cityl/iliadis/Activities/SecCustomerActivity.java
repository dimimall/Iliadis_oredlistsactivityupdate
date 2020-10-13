package gr.cityl.iliadis.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import gr.cityl.iliadis.Manager.Calls;
import gr.cityl.iliadis.Manager.utils;
import gr.cityl.iliadis.Models.Cart;
import gr.cityl.iliadis.Models.Order;
import gr.cityl.iliadis.Models.SecCustomers;
import gr.cityl.iliadis.Models.ShopDatabase;
import gr.cityl.iliadis.R;


/*ekptwsi= flatprice * (discount/100) ))
katharitimi = flatprice - discountprice
*/
public class SecCustomerActivity extends AppCompatActivity {

    TextView textView,noshops,textView2,textView3;
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    Button send;
    String selectshop="0";
    ShopDatabase shopDatabase;
    utils myutils;
    Calls calls;
    String number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sec_customer);

        myutils = new utils();
        calls = new Calls();

        getSupportActionBar().setTitle(getString(R.string.customer));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        shopDatabase = ShopDatabase.getInstance(this);

        init();

        myutils.sharedpreferences = getSharedPreferences(myutils.MyPREFERENCES, Context.MODE_PRIVATE);
        number = myutils.sharedpreferences.getString("numsale", "");
        //Log.d("Dimitra"," number "+number);

        final String custid = getIntent().getStringExtra("custid");
        final List<SecCustomers> shops = (List<SecCustomers>) getIntent().getSerializableExtra("shops");
        final String custvatid = getIntent().getStringExtra("custvatid");
		final int custcatid = getIntent().getIntExtra("catalogueid",0);
        final List<Order> orders = shopDatabase.daoShop().getListOrder(custid);

        String title = getIntent().getStringExtra("name shop");

        textView2.setText(title);

        arrayAdapter = new ArrayAdapter<String>(SecCustomerActivity.this,android.R.layout.simple_list_item_1);

        if (shops.size() > 0)
        {
            for (int i=0; i<shops.size(); i++)
            {
                arrayAdapter.add(shops.get(i).getCompanyName());
            }
            listView.setAdapter(arrayAdapter);
            listView.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);
            textView3.setVisibility(View.VISIBLE);
            noshops.setVisibility(View.GONE);
        }
        else {
            noshops.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            textView2.setVisibility(View.GONE);
            textView3.setVisibility(View.GONE);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectshop = shops.get(i).getShopid();

            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orders.size() <= 0){
                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
                    String currentDateandTime = sdf.format(new Date());
                    Intent intent = new Intent(SecCustomerActivity.this, ProductActivity.class);
                    Order order1 = new Order();
                    if (shops.size() > 0) {
                        myutils.sharedpreferences = getSharedPreferences(myutils.MyPREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = myutils.sharedpreferences.edit();
                        editor.putString("shopid",selectshop);
                        editor.commit();

                        intent.putExtra("shopid", selectshop);
                        order1.setCustid(custid);
                        order1.setStatus(0);
                        order1.setDateparsed(currentDateandTime);
                        order1.setShopid(selectshop);
                        order1.setCommentorder("");
                        shopDatabase.daoShop().insertTask(order1);
                    }
                    else {
                        myutils.sharedpreferences = getSharedPreferences(myutils.MyPREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = myutils.sharedpreferences.edit();
                        editor.putString("shopid","0");
                        editor.commit();

                        intent.putExtra("shopid", "0");
                        order1.setCustid(custid);
                        order1.setStatus(0);
                        order1.setDateparsed(currentDateandTime);
                        order1.setShopid("0");
                        order1.setCommentorder("");
                        shopDatabase.daoShop().insertTask(order1);
                    }
                    List<Order> orders = shopDatabase.daoShop().getListOrder(custid);
                    calls.makePostUsingVolley(SecCustomerActivity.this,custid,number,String.valueOf(orders.get(0).getOrderid()));
                    intent.putExtra("custvatid",custvatid);
                    intent.putExtra("custid",custid);
					intent.putExtra("catalogueid",custcatid);
					intent.putExtra("orderid",orders.get(0).getOrderid());
                    startActivity(intent);
                }
                else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(SecCustomerActivity.this);
                    builder1.setMessage(getString(R.string.havependingorder));
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            getString(R.string.next),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    calls.makePostUsingVolley(SecCustomerActivity.this,custid,number,String.valueOf(orders.get(0).getOrderid()));
                                    Intent intent = new Intent(SecCustomerActivity.this,OrderListsActivity.class);
                                    intent.putExtra("orders", (Serializable) orders);
                                    intent.putExtra("custvatid",custvatid);
                                    intent.putExtra("custid",custid);
                                    intent.putExtra("shopsid",selectshop);
                                    intent.putExtra("catalogueid",custcatid);
                                    startActivity(intent);
                                }
                            });

                    builder1.setNegativeButton(
                            getString(R.string.neworder),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
                                    String currentDateandTime = sdf.format(new Date());
                                    Intent intent = new Intent(SecCustomerActivity.this, ProductActivity.class);
                                    Order order1 = new Order();

                                    if (shops.size() <=0) {
                                        myutils.sharedpreferences = getSharedPreferences(myutils.MyPREFERENCES, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = myutils.sharedpreferences.edit();
                                        editor.putString("shopid",selectshop);
                                        editor.commit();

                                        intent.putExtra("shopid", selectshop);
                                        order1.setCustid(custid);
                                        order1.setStatus(0);
                                        order1.setDateparsed(currentDateandTime);
                                        order1.setShopid(selectshop);
                                        order1.setCommentorder("");
                                        shopDatabase.daoShop().insertTask(order1);
                                    }
                                    else if (shops.size() > 0){
                                        myutils.sharedpreferences = getSharedPreferences(myutils.MyPREFERENCES, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = myutils.sharedpreferences.edit();
                                        editor.putString("shopid","0");
                                        editor.commit();

                                        intent.putExtra("shopid", "0");
                                        order1 = new Order();
                                        order1.setCustid(custid);
                                        order1.setStatus(0);
                                        order1.setDateparsed(currentDateandTime);
                                        order1.setShopid("0");
                                        order1.setCommentorder("");
                                        shopDatabase.daoShop().insertTask(order1);
                                    }
                                    List<Order> orders = shopDatabase.daoShop().getListOrder(custid);
                                    calls.makePostUsingVolley(SecCustomerActivity.this,custid,number,String.valueOf(orders.get(0).getOrderid()));
                                    intent.putExtra("custvatid",custvatid);
                                    intent.putExtra("custid",custid);
                                    intent.putExtra("catalogueid",custcatid);
                                    intent.putExtra("orderid",orders.get(orders.size()-1).getOrderid());
                                    startActivity(intent);
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        new AlertDialog.Builder(this)
                .setTitle("Iliadis")
                .setMessage(getString(R.string.closeapp))
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        moveTaskToBack(true);
                    }
                }).create().show();
    }

    public void init()
    {
        textView = (TextView)findViewById(R.id.textView6);
        textView2 = (TextView)findViewById(R.id.textView17);
        textView3 = (TextView)findViewById(R.id.textView18);
        noshops = (TextView)findViewById(R.id.textView7);
        listView = (ListView) findViewById(R.id.listView);
        send = (Button)findViewById(R.id.button8);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                myutils.DialogBackbutton(getString(R.string.cancelorder),SecCustomerActivity.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
