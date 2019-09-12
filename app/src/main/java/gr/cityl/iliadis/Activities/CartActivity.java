package gr.cityl.iliadis.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import gr.cityl.iliadis.Manager.utils;
import gr.cityl.iliadis.Models.Cart;
import gr.cityl.iliadis.Models.Customers;
import gr.cityl.iliadis.Models.IliadisDatabase;
import gr.cityl.iliadis.Models.Order;
import gr.cityl.iliadis.Models.ShopDatabase;
import gr.cityl.iliadis.R;

public class CartActivity extends AppCompatActivity {

    private TextInputLayout searchText;
    private RecyclerView recyclerView;
    private TextView subtotal,vat,grandtotal;
    private Button print;
    private ShopDatabase shopDatabase;
    MyAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public List<Cart> cartsList,carts;
    private IliadisDatabase iliadisDatabase;
    private String custid,custvatid,shopId;
    private int custcatid;
    private utils myutils;
    private String number;
    private String ipprintpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        myutils = new utils();
        myutils.sharedpreferences = getSharedPreferences(myutils.MyPREFERENCES, Context.MODE_PRIVATE);
        number = myutils.sharedpreferences.getString("numsale", "");
        ipprintpref = myutils.sharedpreferences.getString("ipprint", "");


        isReadStoragePermissionGranted();
        isWriteStoragePermissionGranted();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                //myutils.DialogBackbutton(getString(R.string.cancelorder),CartActivity.this);
                Intent intent = new Intent(CartActivity.this,ProductActivity.class);
                intent.putExtra("shopid",shopId);
                intent.putExtra("custvatid",custvatid);
                intent.putExtra("custid",custid);
                intent.putExtra("catalogueid",custcatid);
                intent.putExtra("orderid",shopDatabase.daoShop().getOrder(custid).getOrderid());
                startActivity(intent);
            }
        });

        searchText = (TextInputLayout)findViewById(R.id.textInputLayout6);
        recyclerView = (RecyclerView)findViewById(R.id.recyclercart);
        subtotal = (TextView)findViewById(R.id.textView27);
        vat = (TextView)findViewById(R.id.textView28);
        grandtotal = (TextView)findViewById(R.id.textView27);
        print = (Button)findViewById(R.id.button14);
        subtotal = (TextView)findViewById(R.id.textView27);
        vat = (TextView)findViewById(R.id.textView28);
        grandtotal = (TextView)findViewById(R.id.textView29);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        carts = new ArrayList<>();

        subtotal.setText("0");
        vat.setText("0");
        grandtotal.setText("0");

        custid = getIntent().getExtras().getString("custid");
        shopId= getIntent().getExtras().getString("shopid");
        if (shopId==null)
            shopId="0";
        custvatid = getIntent().getExtras().getString("custvatid");
        cartsList = (List<Cart>) getIntent().getExtras().getSerializable("cart");
        custcatid = getIntent().getExtras().getInt("catalogueid");
        carts.addAll(cartsList);

        shopDatabase = ShopDatabase.getInstance(CartActivity.this);
        iliadisDatabase = IliadisDatabase.getInstance(CartActivity.this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclercart);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), layoutManager.));

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(carts,CartActivity.this);
        recyclerView.setAdapter(mAdapter);

        if (cartsList.size()>0)
        {
            int prodSum = 0;
            double priceSum = 0.0;
            double sPriceSum = 0.0;
            double vTotal = 0.0;

            int j = Integer.parseInt(custvatid);

            int k = 0;

            if(j == 0){
                k = 24;
            }else if(j == 1){
                k = 0;
            }else if(j == 2){
                k = 17;
            }

            for(int i = 0; i < carts.size(); i++){
                //prodSum = prodSum + carts.get(i).getQuantity();
                priceSum = priceSum + (Double.parseDouble(carts.get(i).getPrice().replace(",",".")));
                sPriceSum = sPriceSum + (Double.parseDouble(carts.get(i).getPrice().replace(",",".")) * carts.get(i).getQuantity());
                vTotal = vTotal + (((k * Double.parseDouble(carts.get(i).getPrice().replace(",","."))) / 100) /** carts.get(i).getQuantity()*/);
            }
            subtotal.setText(new DecimalFormat("##.##").format(priceSum) + "€");
            vat.setText(new DecimalFormat("##.##").format(vTotal) + "€");
            double total = Double.parseDouble(new DecimalFormat("##.##").format((priceSum + vTotal)).replace(",","."));
            grandtotal.setText(""+total+ "€");
        }else {
            Toast.makeText(CartActivity.this,getString(R.string.noproducts),Toast.LENGTH_LONG).show();
        }

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBox();
            }
        });

        searchText.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mAdapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements Filterable {
        private List<Cart> cartList;
        private Context mycontext;
        NewFilter mfilter;

        @Override
        public Filter getFilter() {
            return mfilter;
        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView code,description,qty,price,optionbuttonmenu;
            public View view;
            public MyViewHolder(View v) {
                super(v);
                view = v;
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(List<Cart> cartList,Context context) {
            this.cartList = cartList;
            this.mycontext = context;
            mfilter = new NewFilter(MyAdapter.this);
        }

        public class NewFilter extends Filter {
            public MyAdapter mAdapter;

            public NewFilter(MyAdapter mAdapter) {
                super();
                this.mAdapter = mAdapter;
            }

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                carts.clear();
                final FilterResults results = new FilterResults();
                if(charSequence.length() == 0){
                    carts.addAll(cartsList);
                }else{
                    final String filterPattern =charSequence.toString().toLowerCase().trim();
                    for(Cart listcart : cartsList){
                        if(listcart.getProdcode().startsWith(filterPattern)|| listcart.getRealcode().startsWith(filterPattern)){
                            carts.add(listcart);
                        }
                    }
                }
                results.values = carts;
                results.count = carts.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                this.mAdapter.notifyDataSetChanged();
            }
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_cart, parent, false);
            MyViewHolder vh = new MyViewHolder(view);
            vh.code = (TextView) view.findViewById(R.id.textView30);
            vh.description = (TextView)view.findViewById(R.id.textView31);
            vh.qty = (TextView)view.findViewById(R.id.textView32);
            vh.price = (TextView)view.findViewById(R.id.textView33);
            vh.optionbuttonmenu =(TextView)view.findViewById(R.id.textView34);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.code.setText(cartList.get(position).getRealcode()+"-"+cartList.get(position).getProdcode());
            holder.description.setText(cartList.get(position).getDescription());
            holder.qty.setText("QTY: "+cartList.get(position).getQuantity());
            holder.price.setText(new DecimalFormat("##.##").format(Double.parseDouble(cartList.get(position).getPrice())));

            holder.optionbuttonmenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(CartActivity.this, holder.optionbuttonmenu);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.options_menu);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu1:
                                    //handle menu1 click
                                    Cart cart = cartList.get(holder.getAdapterPosition());
                                    shopDatabase.daoShop().deleteCart(cart);
                                    cartList.remove(holder.getAdapterPosition());
                                    notifyDataSetChanged();
                                    return true;
                                case R.id.menu2:
                                    //handle menu2 click
                                    Intent intent = new Intent(CartActivity.this,EditProductActivity.class);
                                    intent.putExtra("cart",(Serializable) cartList.get(position));
                                    intent.putExtra("custid",custid);
                                    intent.putExtra("custvatid",custvatid);
                                    intent.putExtra("orderid",cartList.get(position).getOrderid());
                                    intent.putExtra("catalogueid",custcatid);
                                    intent.putExtra("shopid",shopId);
                                    startActivity(intent);
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
            return cartList.size();
        }

    }

    public  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("cart activity","Permission is granted1");
                return true;
            } else {

                Log.v("cart activity","Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("cart activity","Permission is granted1");
            return true;
        }
    }

    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("cart activity","Permission is granted2");
                return true;
            } else {

                Log.v("cart activity","Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("cart activity","Permission is granted2");
            return true;
        }
    }

    public void dialogBox()
    {
        final String[] text = {""," "};

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_box, null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(true);
        final AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();

        Spinner spinner = view.findViewById(R.id.spinner2);
        final TextInputLayout comment = view.findViewById(R.id.textInputLayout7);
        Button ok = view.findViewById(R.id.button15);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line);
        adapter.add("Ελληνικά");
        adapter.add("English");
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                text[0] = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        comment.getEditText().setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                        keyCode == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                {
                    text[1] = comment.getEditText().getText().toString();
                    return true;
                }
                return false;
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (text[0].equals("Ελληνικά"))
                {
                    //create and print pdf in greek
                    try {
                        myutils.createPdfFileGr(cartsList,custid,custvatid,number,shopId,ipprintpref,iliadisDatabase,CartActivity.this);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss ");
                        String currentDateandTime = sdf.format(new Date());
                        Order order = new Order();
                        order.setOrderid(carts.get(0).getOrderid());
                        order.setCustid(custid);
                        order.setStatus(1);
                        order.setDateparsed(currentDateandTime);
                        order.setShopid(shopId);
                        order.setCommentorder(text[1]);
                        shopDatabase.daoShop().updateOrder(order);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                    //send csv file
                    Customers customer = iliadisDatabase.daoAccess().getCustomerByCustid(custid);
                    String result="";
                    result = myutils.createCsvFile(cartsList,number,custid,carts.get(0).getOrderid(),iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCustomerid(),custvatid,iliadisDatabase.daoAccess().getCustomerByCustid(custid).getPaymentid(),shopId,customer,iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCatalogueid(),text[1]);
                    if (result.equals("success")) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss ");
                        String currentDateandTime = sdf.format(new Date());
                        Order order = new Order();
                        order.setOrderid(carts.get(0).getOrderid());
                        order.setCustid(custid);
                        order.setStatus(2);
                        order.setDateparsed(currentDateandTime);
                        order.setShopid(shopId);
                        order.setCommentorder(text[1]);
                        shopDatabase.daoShop().updateOrder(order);
                        Toast.makeText(CartActivity.this, getString(R.string.sendfilecsv), Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(CartActivity.this, getString(R.string.nosendfilecsv), Toast.LENGTH_LONG).show();
                    }
                    Intent intent = new Intent(CartActivity.this,MainActivity.class);
                    startActivity(intent);
                }
                else if (text[0].equals("English"))
                {
                    //create and print pdf in english
                    try {
                        myutils.createPdfFileEn(cartsList,custid,custvatid,number,shopId,ipprintpref,iliadisDatabase,CartActivity.this);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss ");
                        String currentDateandTime = sdf.format(new Date());
                        Order order = new Order();
                        order.setOrderid(carts.get(0).getOrderid());
                        order.setCustid(custid);
                        order.setStatus(1);
                        order.setDateparsed(currentDateandTime);
                        order.setShopid(shopId);
                        order.setCommentorder(text[1]);
                        shopDatabase.daoShop().updateOrder(order);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                    //send csv file
                    Customers customer = iliadisDatabase.daoAccess().getCustomerByCustid(custid);
                    String result="";
                    result = myutils.createCsvFile(cartsList,number,custid,carts.get(0).getOrderid(),iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCustomerid(),custvatid,iliadisDatabase.daoAccess().getCustomerByCustid(custid).getPaymentid(),shopId,customer,iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCatalogueid(),text[1]);
                    if (result.equals("success")) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss ");
                        String currentDateandTime = sdf.format(new Date());
                        Order order = new Order();
                        order.setOrderid(carts.get(0).getOrderid());
                        order.setCustid(custid);
                        order.setStatus(2);
                        order.setDateparsed(currentDateandTime);
                        order.setShopid(shopId);
                        order.setCommentorder(text[1]);
                        shopDatabase.daoShop().updateOrder(order);
                        Toast.makeText(CartActivity.this, getString(R.string.sendfilecsv), Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(CartActivity.this, getString(R.string.nosendfilecsv), Toast.LENGTH_LONG).show();
                    }
                    Intent intent = new Intent(CartActivity.this,MainActivity.class);
                    startActivity(intent);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
