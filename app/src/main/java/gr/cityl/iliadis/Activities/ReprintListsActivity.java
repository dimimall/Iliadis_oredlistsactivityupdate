package gr.cityl.iliadis.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Printer;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.DocumentException;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gr.cityl.iliadis.Interfaces.AlertDialogCallback;
import gr.cityl.iliadis.Manager.utils;
import gr.cityl.iliadis.Models.Cart;
import gr.cityl.iliadis.Models.Customers;
import gr.cityl.iliadis.Models.IliadisDatabase;
import gr.cityl.iliadis.Models.Order;
import gr.cityl.iliadis.Models.ParamOrders;
import gr.cityl.iliadis.Models.ShopDatabase;
import gr.cityl.iliadis.R;

public class ReprintListsActivity extends AppCompatActivity{

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private IliadisDatabase iliadisDatabase;
    private ShopDatabase shopDatabase;
    private List<ParamOrders> paramOrders;
    private List<Order> orderList;
    private String shopid;
    private utils myutils;
    private String number,ipprintpref;
    private String shopId;
    public AlertDialogCallback alertDialogCallback;
    public ProgressDialog progressDialog;
    public Handler handler = new Handler();
    public int progressStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reprint_lists);

        getSupportActionBar().setTitle(getString(R.string.reprintagain));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myutils = new utils();

        myutils.sharedpreferences = getSharedPreferences(myutils.MyPREFERENCES, Context.MODE_PRIVATE);
        number = myutils.sharedpreferences.getString("numsale", "");
        ipprintpref = myutils.sharedpreferences.getString("ipprint", "");

        shopDatabase = ShopDatabase.getInstance(ReprintListsActivity.this);
        iliadisDatabase = IliadisDatabase.getInstance(ReprintListsActivity.this);

        paramOrders = new ArrayList<>();

        orderList = (List<Order>) getIntent().getExtras().getSerializable("orders");

        for (int i=0; i<orderList.size(); i++)
        {
            Customers customer = iliadisDatabase.daoAccess().getCustomerByCustid(orderList.get(i).getCustid());
            ParamOrders paramOrder = new ParamOrders(orderList.get(i).getCustid(),customer.getAfm(),customer.getCompanyName(),orderList.get(i).getDateparsed(),shopid,orderList.get(i).getOrderid());
            paramOrders.add(paramOrder);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerlist);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(paramOrders);
        recyclerView.setAdapter(mAdapter);

        alertDialogCallback = new AlertDialogCallback() {
            @Override
            public void alertDialogCallback(String[] ret) {

            }
        };
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private List<ParamOrders> paramOrdersList;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView afmtext,cnametext,datetext,optionbuttonmenu;
            public View view;
            public MyViewHolder(View v) {
                super(v);
                view = v;
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(List<ParamOrders> paramOrdersList) {
            this.paramOrdersList = paramOrdersList;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(final ViewGroup parent,
                                                                            int viewType) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.orderrecyclerview, parent, false);
            MyAdapter.MyViewHolder vh = new MyAdapter.MyViewHolder(view);
            vh.afmtext = (TextView) view.findViewById(R.id.textView35);
            vh.cnametext = (TextView)view.findViewById(R.id.textView36);
            vh.datetext = (TextView)view.findViewById(R.id.textView37);
            vh.optionbuttonmenu =(TextView)view.findViewById(R.id.textView38);

            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final MyAdapter.MyViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            holder.afmtext.setText(paramOrdersList.get(position).getCustid()+" "+paramOrdersList.get(position).getAfm());
            holder.cnametext.setText(paramOrdersList.get(position).getCompanyname());
            holder.datetext.setText(paramOrdersList.get(position).getDateorder());

            holder.optionbuttonmenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(getApplicationContext(), holder.optionbuttonmenu);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.print_menu);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu1:
                                    //handle menu1 click
                                    DialogBox(position,alertDialogCallback);
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    //displaying the popup
                    popup.show();
                }
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return paramOrdersList.size();
        }
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

    public void DialogBox(int position, final AlertDialogCallback callback)
    {
        this.alertDialogCallback = callback;
        final String[] text = {""," "," "};
        text[0]=String.valueOf(position);
        Log.d("Dimitra","message: "+text[0]);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                ReprintListsActivity.this);
        final LayoutInflater inflater = (LayoutInflater) ReprintListsActivity.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_box, null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(true);

        final AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();

        Spinner spinner = view.findViewById(R.id.spinner2);
        final TextInputLayout comment = view.findViewById(R.id.textInputLayout7);
        final Button ok = view.findViewById(R.id.button15);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(ReprintListsActivity.this,android.R.layout.simple_dropdown_item_1line);
        adapter.add("Ελληνικά");
        adapter.add("English");
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                text[1] = adapterView.getItemAtPosition(i).toString();
                Log.d("Dimitra","message: "+text[1]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        comment.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                text[2] = editable.toString();
                Log.d("Dimitra","message: "+text[2]);
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final List<Cart> cartsList = shopDatabase.daoShop().getCartList(paramOrders.get(Integer.parseInt(text[0])).getOrderid());
                myutils.sharedpreferences = getSharedPreferences(myutils.MyPREFERENCES, Context.MODE_PRIVATE);
                number = myutils.sharedpreferences.getString("numsale", "");
                final Customers customers = iliadisDatabase.daoAccess().getCustomerByCustid(shopDatabase.daoShop().getCastidOrder(paramOrders.get(Integer.parseInt(text[0])).getOrderid()));
                dialog.dismiss();
                if (text[1].equals("Ελληνικά"))
                {
                    //create and print pdf in greek
                    try {
                        myutils.createPdfFileGr(cartsList,shopDatabase.daoShop().getCastidOrder(paramOrders.get(Integer.parseInt(text[0])).getOrderid()),customers.getCustvatid(),number,shopDatabase.daoShop().getShopidOrder(paramOrders.get(Integer.parseInt(text[0])).getOrderid()),ipprintpref,iliadisDatabase,ReprintListsActivity.this);
                        for (int i=0; i<2; i++)
                        {
                            final ProgressDialog pd = new ProgressDialog(ReprintListsActivity.this);
                            // Set progress dialog style spinner
                            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            // Set the progress dialog title and message
                            pd.setTitle("Title of progress dialog.");
                            pd.setMessage("Loading.........");
                            pd.setIndeterminate(false);
                            // Finally, show the progress dialog
                            pd.show();
                            // Set the progress status zero on each button click
                            progressStatus = 0;

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    utils.printPdf(ipprintpref,ReprintListsActivity.this);
                                    while(progressStatus < 100){
                                        // Update the progress status
                                        progressStatus +=1;

                                        // Try to sleep the thread for 20 milliseconds
                                        try{
                                            Thread.sleep(20);
                                        }catch(InterruptedException e){
                                            e.printStackTrace();
                                        }

                                        // Update the progress bar
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                // Update the progress status
                                                pd.setProgress(progressStatus);
                                                // If task execution completed
                                                if(progressStatus == 100){
                                                    // Dismiss/hide the progress dialog
                                                    pd.dismiss();
                                                }
                                            }
                                        });
                                    }
                                }
                            }).start();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(ReprintListsActivity.this,MainActivity.class);
                    startActivity(intent);
                }
                else if (text[1].equals("English"))
                {
                    //create and print pdf in english
                    try {
                        myutils.createPdfFileEn(cartsList,shopDatabase.daoShop().getCastidOrder(paramOrders.get(Integer.parseInt(text[0])).getOrderid()),customers.getCustvatid(),number,shopDatabase.daoShop().getShopidOrder(paramOrders.get(Integer.parseInt(text[0])).getOrderid()),ipprintpref,iliadisDatabase,ReprintListsActivity.this);
                        for (int i=0; i<2; i++)
                        {
                            final ProgressDialog pd = new ProgressDialog(ReprintListsActivity.this);
                            // Set progress dialog style spinner
                            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            // Set the progress dialog title and message
                            pd.setMessage("Loading.........");
                            pd.setIndeterminate(false);
                            // Finally, show the progress dialog
                            pd.show();
                            // Set the progress status zero on each button click
                            progressStatus = 0;

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    utils.printPdf(ipprintpref,ReprintListsActivity.this);
                                    while(progressStatus < 100){
                                        // Update the progress status
                                        progressStatus +=1;
                                        // Try to sleep the thread for 20 milliseconds
                                        try{
                                            Thread.sleep(20);
                                        }catch(InterruptedException e){
                                            e.printStackTrace();
                                        }

                                        // Update the progress bar
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                // Update the progress status
                                                pd.setProgress(progressStatus);
                                                // If task execution completed
                                                if(progressStatus == 100){
                                                    // Dismiss/hide the progress dialog
                                                    pd.dismiss();
                                                }
                                            }
                                        });
                                    }
                                }
                            }).start();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(ReprintListsActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        });
        dialog.show();
    }

}
