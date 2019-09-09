package gr.cityl.iliadis.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import gr.cityl.iliadis.Manager.utils;
import gr.cityl.iliadis.Models.Customers;
import gr.cityl.iliadis.Models.IliadisDatabase;
import gr.cityl.iliadis.Models.ParamOrders;
import gr.cityl.iliadis.Models.SecCustomers;
import gr.cityl.iliadis.R;

public class NewOrderActivity extends AppCompatActivity {

    TextInputLayout afm_textinput;
    TextInputLayout code_textinput;
    TextInputLayout desc_textinput;
    Button send;
    IliadisDatabase iliadisDatabase;
    Customers customer;
    List<SecCustomers> secCustomers;
    utils myutils;
    List<String> companyNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        iliadisDatabase = IliadisDatabase.getInstance(this);
        secCustomers = new ArrayList<>();
        myutils = new utils();

        getSupportActionBar().setTitle(getString(R.string.customer));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

        myutils.sharedpreferences = getSharedPreferences(myutils.MyPREFERENCES, Context.MODE_PRIVATE);
        String value = myutils.sharedpreferences.getString("numsale", "");


        afm_textinput.getEditText().setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER))
                {
                    customer = iliadisDatabase.daoAccess().getCustomerByAfm(afm_textinput.getEditText().getText().toString());
                    if (customer != null)
                    {
                        code_textinput.getEditText().setText(customer.getCustid());
                        desc_textinput.getEditText().setText(customer.getCompanyName());
                    }
                    else {
                        afm_textinput.getEditText().setText("");
                        code_textinput.getEditText().setText("");
                        desc_textinput.getEditText().setText("");
                        myutils.createDialog("Δε υπάρχει αυτός ο πελάτης",NewOrderActivity.this);
                    }
                    try {
                        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    return true;
                }
                return false;
            }
        });

        code_textinput.getEditText().setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER))
                {
                    customer = iliadisDatabase.daoAccess().getCustomerByCustid(code_textinput.getEditText().getText().toString());
                    if (customer != null)
                    {
                        afm_textinput.getEditText().setText(customer.getAfm());
                        desc_textinput.getEditText().setText(customer.getCompanyName());
                    }
                    else{
                        code_textinput.getEditText().setText("");
                        afm_textinput.getEditText().setText("");
                        desc_textinput.getEditText().setText("");
                        myutils.createDialog("Δε υπάρχει αυτός ο πελάτης",NewOrderActivity.this);
                    }

                    try {
                        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    return true;
                }
                return false;
            }
        });

        desc_textinput.getEditText().setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER))
                {
                    companyNames = iliadisDatabase.daoAccess().getCustomerByName(desc_textinput.getEditText().getText().toString());
                    final Dialog dialog = new Dialog(NewOrderActivity.this);
                    ListView listView = new ListView(NewOrderActivity.this);
                    dialog.setContentView(listView);
                    dialog.setTitle("");
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(NewOrderActivity.this,android.R.layout.simple_list_item_1,companyNames);
                    listView.setAdapter(arrayAdapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            customer = iliadisDatabase.daoAccess().getCustomerName(adapterView.getItemAtPosition(i).toString());
                            if (customer != null)
                            {
                                afm_textinput.getEditText().setText(customer.getAfm());
                                code_textinput.getEditText().setText(customer.getCustid());
                                desc_textinput.getEditText().setText(customer.getCompanyName());
                            }
                            else {
                                desc_textinput.getEditText().setText("");
                                afm_textinput.getEditText().setText("");
                                code_textinput.getEditText().setText("");
                                myutils.createDialog("Δε υπάρχει αυτός ο πελάτης",NewOrderActivity.this);
                            }
                            dialog.cancel();
                        }
                    });

                    dialog.show();

                    try {
                        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    return true;
                }
                return false;
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (desc_textinput.getEditText().getText().toString() != null || code_textinput.getEditText().getText().toString() != null || afm_textinput.getEditText().getText().toString() != null)
                {
                    if (customer != null)
                    {
                        SharedPreferences.Editor editor = myutils.sharedpreferences.edit();
                        editor.putString("custid",customer.getCustid());
                        editor.putInt("catalogueid",customer.getCatalogueid());
                        editor.commit();

                        secCustomers = iliadisDatabase.daoAccess().getShopsByCust(customer.getCustid());
                        Intent intent = new Intent(NewOrderActivity.this,SecCustomerActivity.class);
                        intent.putExtra("custid",customer.getCustid());
                        intent.putExtra("custvatid", customer.getCustvatid());
                        intent.putExtra("name shop",customer.getCompanyName());
                        intent.putExtra("shops", (Serializable) secCustomers);
						intent.putExtra("catalogueid",customer.getCatalogueid());
                        startActivity(intent);
                    }
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
        afm_textinput = (TextInputLayout)findViewById(R.id.textInputLayout);
        code_textinput = (TextInputLayout)findViewById(R.id.textInputLayout2);
        desc_textinput = (TextInputLayout)findViewById(R.id.textInputLayout3);
        send = (Button)findViewById(R.id.button);
    }

}
