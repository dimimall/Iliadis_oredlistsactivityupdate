package gr.cityl.iliadis.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
    String selectshop;
    ShopDatabase shopDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sec_customer);

        getSupportActionBar().setTitle("Πελάτης");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        shopDatabase = ShopDatabase.getInstance(this);

        init();

        final String custid = getIntent().getStringExtra("custid");
        final List<SecCustomers> shops = (List<SecCustomers>) getIntent().getSerializableExtra("shops");
        final String custvatid = getIntent().getStringExtra("custvatid");
		final int custcatid = getIntent().getIntExtra("catalogueid",0);
        String title = getIntent().getStringExtra("name shop");
        final List<Order> orders = shopDatabase.daoShop().getListOrderStatus0(custid);

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
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss ");
                    String currentDateandTime = sdf.format(new Date());
                    Intent intent = new Intent(SecCustomerActivity.this, ProductActivity.class);
                    Order order1 = new Order();
                    if (shops.size() > 0) {
                        intent.putExtra("shopid", selectshop);
                        order1.setCustid(custid);
                        order1.setStatus(0);
                        order1.setDateparsed(currentDateandTime);
                        order1.setShopid(selectshop);
                        shopDatabase.daoShop().insertTask(order1);
                    }
                    else {
                        intent.putExtra("shopid", "0");
                        order1.setCustid(custid);
                        order1.setStatus(0);
                        order1.setDateparsed(currentDateandTime);
                        order1.setShopid("0");
                        shopDatabase.daoShop().insertTask(order1);
                    }
                    List<Order> orders = shopDatabase.daoShop().getListOrder(custid);
                    for (int i=0; i<orders.size(); i++)
                    {
                        Log.d("Dimitra","data "+orders.get(i).getOrderid()+" "+orders.get(i).getCustid()+" "+orders.get(i).getDateparsed()+" "+orders.get(i).getShopid());
                    }
                    intent.putExtra("custvatid",custvatid);
                    intent.putExtra("custid",custid);
					intent.putExtra("catalogueid",custcatid);
					intent.putExtra("orderid",orders.get(0).getOrderid());
                    startActivity(intent);
                }
                else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(SecCustomerActivity.this);
                    builder1.setMessage("Έχετε εκκρεμείς παραγγελείες");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Συνέχεια",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
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
                            "Νέα Παραγγελία",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
                                    String currentDateandTime = sdf.format(new Date());
                                    Intent intent = new Intent(SecCustomerActivity.this, ProductActivity.class);
                                    Order order1 = new Order();

                                    if (shops != null) {
                                        intent.putExtra("shopid", selectshop);
                                        order1.setCustid(custid);
                                        order1.setStatus(0);
                                        order1.setDateparsed(currentDateandTime);
                                        order1.setShopid(selectshop);
                                        shopDatabase.daoShop().insertTask(order1);
                                    }
                                    else {
                                        intent.putExtra("shopid", "0");
                                        order1 = new Order();
                                        order1.setCustid(custid);
                                        order1.setStatus(0);
                                        order1.setDateparsed(currentDateandTime);
                                        order1.setShopid("0");
                                        shopDatabase.daoShop().insertTask(order1);
                                    }
                                    List<Order> orders = shopDatabase.daoShop().getListOrder(custid);
                                    for (int i=0; i<orders.size(); i++)
                                    {
                                        Log.d("Dimitra","data "+orders.get(i).getOrderid()+" "+orders.get(i).getCustid()+" "+orders.get(i).getDateparsed()+" "+orders.get(i).getShopid());
                                    }
                                    intent.putExtra("custvatid",custvatid);
                                    intent.putExtra("custid",custid);
                                    intent.putExtra("catalogueid",custcatid);
                                    intent.putExtra("orderid",orders.get(orders.size()-1).getOrderid());
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
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}