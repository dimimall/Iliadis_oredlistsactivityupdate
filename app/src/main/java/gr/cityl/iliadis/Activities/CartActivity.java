package gr.cityl.iliadis.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.tv.TvContract;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.itextpdf.text.BadElementException;
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

import org.w3c.dom.Text;

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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import gr.cityl.iliadis.Interfaces.AlertDialogCallback;
import gr.cityl.iliadis.Manager.utils;
import gr.cityl.iliadis.Models.Cart;
import gr.cityl.iliadis.Models.Catalog;
import gr.cityl.iliadis.Models.Customers;
import gr.cityl.iliadis.Models.IliadisDatabase;
import gr.cityl.iliadis.Models.Order;
import gr.cityl.iliadis.Models.ShopDatabase;
import gr.cityl.iliadis.R;

public class CartActivity extends AppCompatActivity{

    private TextInputLayout searchText;
    private RecyclerView recyclerView;
    private TextView subtotal,vat,grandtotal,ordertext;
    private Button print;
    private ShopDatabase shopDatabase;
    MyAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public List<Cart> cartsList,carts;
    private IliadisDatabase iliadisDatabase;
    private String custid,custvatid,shopId;
    private int orderid;
    private int custcatid;
    private utils myutils;
    private String number;
    private String ipprintpref;
    private boolean lang;
    private AlertDialogCallback alertDialogCallback;
    int prodSum = 0;
    double priceSum = 0.0;
    double totalprice = 0.0;
    double sPriceSum = 0.0;
    double vTotal = 0.0;
    int progressStatus = 0;
    boolean finished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        myutils = new utils();
        myutils.sharedpreferences = getSharedPreferences(myutils.MyPREFERENCES, Context.MODE_PRIVATE);
        number = myutils.sharedpreferences.getString("numsale", "");
        ipprintpref = myutils.sharedpreferences.getString("ipprint", "");
        lang = myutils.sharedpreferences.getBoolean("language",false);

        isReadStoragePermissionGranted();
        isWriteStoragePermissionGranted();

        custid = getIntent().getExtras().getString("custid");
        shopId= getIntent().getExtras().getString("shopid");
        if (shopId==null)
            shopId="0";
        custvatid = getIntent().getExtras().getString("custvatid");
        custcatid = getIntent().getExtras().getInt("catalogueid");
        orderid = getIntent().getExtras().getInt("orderid");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                Intent intent = new Intent(CartActivity.this,ProductActivity.class);
                intent.putExtra("shopid",shopId);
                intent.putExtra("custvatid",custvatid);
                intent.putExtra("custid",custid);
                intent.putExtra("catalogueid",custcatid);
                intent.putExtra("orderid",orderid);
                startActivity(intent);
            }
        });
        ordertext = (TextView)toolbar.findViewById(R.id.order);
        ordertext.setText(getString(R.string.order)+" ("+orderid+")");

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

        shopDatabase = ShopDatabase.getInstance(CartActivity.this);
        iliadisDatabase = IliadisDatabase.getInstance(CartActivity.this);


        recyclerView = (RecyclerView) findViewById(R.id.recyclercart);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        if (cartsList == null){
            new GetCartItemAsyncTask().execute();
        }

        alertDialogCallback = new AlertDialogCallback() {
            @Override
            public void alertDialogCallback(String[] ret) {

            }
        };

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBox(alertDialogCallback);
            }
        });

        searchText.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //mAdapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                filter(editable.toString());
            }
        });
    }

    private void filter(String text) {
        ArrayList<Cart> filteredList = new ArrayList<>();

        for (Cart item : carts) {
            if(item.getRealcode().contains(text)|| item.getProdcode().contains(text)){
                filteredList.add(item);
            }
        }

        mAdapter.filterList(filteredList);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> /*implements Filterable*/ {
        private List<Cart> cartList;
        private Context mycontext;

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
        public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.code.setText(cartList.get(position).getRealcode()+"-"+cartList.get(position).getProdcode());
            holder.description.setText(localeChange(iliadisDatabase.daoAccess().getProductByRealCode(cartList.get(position).getRealcode()).getProdescriptionEn(),cartList.get(position).getDescription()));
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

        public void filterList(ArrayList<Cart> filteredList) {
            cartList = filteredList;
            notifyDataSetChanged();
        }

    }

    public String localeChange(String producten,String productgr)
    {
        String str="";

        if (lang == true ){
            str = producten;
        }
        else if (lang == false){

            str = productgr;
        }
        return str;
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

    public void dialogBox(final AlertDialogCallback callback)
    {
        this.alertDialogCallback=callback;
        final String[] text = {""," "," "};

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
        final Button ok = view.findViewById(R.id.button15);
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

        comment.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                text[1] = editable.toString();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                String printed = "";

                if (text[0].equals("Ελληνικά"))
                {
//                    //send csv file
//                    Customers customer = iliadisDatabase.daoAccess().getCustomerByCustid(custid);
//                    String result="";
//                    result = myutils.createCsvFile(cartsList,number,custid,carts.get(0).getOrderid(),iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCustomerid(),custvatid,iliadisDatabase.daoAccess().getCustomerByCustid(custid).getPaymentid(),shopId,customer,iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCatalogueid(),text[1]);
//                    if (result.equals("success")) {
//                        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
//                        String currentDateandTime = sdf.format(new Date());
//                        Order order = new Order();
//                        order.setOrderid(carts.get(0).getOrderid());
//                        order.setCustid(custid);
//                        order.setStatus(1);
//                        order.setDateparsed(currentDateandTime);
//                        order.setShopid(shopId);
//                        order.setCommentorder(text[1]);
//                        shopDatabase.daoShop().updateOrder(order);
//                        Toast.makeText(CartActivity.this, getString(R.string.sendfilecsv), Toast.LENGTH_LONG).show();
//                    }
//                    else
//                    {
//                        Toast.makeText(CartActivity.this, getString(R.string.nosendfilecsv), Toast.LENGTH_LONG).show();
//                    }

                    createPdfFileGr(cartsList,custid,custvatid,number,shopId,iliadisDatabase,CartActivity.this, text[1],prodSum,priceSum,totalprice,vTotal);
                }
                else if (text[0].equals("English"))
                {
//                    //send csv file
//                    Customers customer = iliadisDatabase.daoAccess().getCustomerByCustid(custid);
//                    String result="";
//                    result = myutils.createCsvFile(cartsList,number,custid,carts.get(0).getOrderid(),iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCustomerid(),custvatid,iliadisDatabase.daoAccess().getCustomerByCustid(custid).getPaymentid(),shopId,customer,iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCatalogueid(),text[1]);
//                    if (result.equals("success")) {
//                        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
//                        String currentDateandTime = sdf.format(new Date());
//                        Order order = new Order();
//                        order.setOrderid(carts.get(0).getOrderid());
//                        order.setCustid(custid);
//                        order.setStatus(1);
//                        order.setDateparsed(currentDateandTime);
//                        order.setShopid(shopId);
//                        order.setCommentorder(text[1]);
//                        shopDatabase.daoShop().updateOrder(order);
//                        Toast.makeText(CartActivity.this, getString(R.string.sendfilecsv), Toast.LENGTH_LONG).show();
//                    }
//                    else
//                    {
//                        Toast.makeText(CartActivity.this, getString(R.string.nosendfilecsv), Toast.LENGTH_LONG).show();
//                    }
                    createPdfFileEn(cartsList,custid,custvatid,number,shopId,iliadisDatabase,CartActivity.this,text[1],prodSum,priceSum,totalprice,vTotal);
                }
            }
        });
        dialog.show();
    }


    private class GetCartItemAsyncTask extends AsyncTask<Void,Integer,String>{

        List<Cart> cartList = new ArrayList<>();

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(CartActivity.this);
            dialog.setMessage("loading");
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            cartList = shopDatabase.daoShop().getCartList(orderid);
            cartsList = cartList;
            carts.addAll(cartsList);

            dialog.setMax(carts.size());

            if (cartsList.size()>0)
            {
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
                    publishProgress(i);
                    dialog.incrementProgressBy(i);
                    prodSum = prodSum + carts.get(i).getQuantity();
                    priceSum = priceSum + (Double.parseDouble(carts.get(i).getPrice().replace(",",".")));
                    sPriceSum = sPriceSum + (Double.parseDouble(carts.get(i).getPrice().replace(",",".")) * carts.get(i).getQuantity());
                    totalprice = totalprice + (Double.parseDouble(carts.get(i).getPriceid().replace(",",".")) * carts.get(i).getQuantity());
                    k = (int)iliadisDatabase.daoAccess().getVat(iliadisDatabase.daoAccess().getProductByProdCode(carts.get(i).getProdcode()).getVatcode(),custvatid);
                    vTotal = vTotal + (((k * Double.parseDouble(carts.get(i).getPrice().replace(",","."))) / 100) /** carts.get(i).getQuantity()*/);
                }

            }else {
                Toast.makeText(CartActivity.this,getString(R.string.noproducts),Toast.LENGTH_LONG).show();
            }

            return "Task Completed";
        }

        @Override
        public void onProgressUpdate(Integer... values){
            dialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String param) {
            super.onPostExecute(param);
            if (param.equals("Task Completed"))
            {
                dialog.dismiss();
                // specify an adapter (see also next example)
                mAdapter = new MyAdapter(carts,CartActivity.this);
                recyclerView.setAdapter(mAdapter);
                subtotal.setText(new DecimalFormat("##.##").format(priceSum) + "€");
                vat.setText(new DecimalFormat("##.##").format(vTotal) + "€");
                double total = Double.parseDouble(new DecimalFormat("##.##").format((priceSum + vTotal)).replace(",","."));
                grandtotal.setText(""+total+ "€");
            }
        }
    }

    public void createPdfFileGr(final List<Cart> carts, final String custid, final String custvatid, final String number, final String shopId, final IliadisDatabase iliadisDatabase, final Context context, final String mymessage, final int prodSum,
                                final double priceSum,
                                final double totalprice,
                                final double vTotal)
    {
        final ProgressDialog dialog1 = new ProgressDialog(CartActivity.this);
        dialog1.setMessage("Loading");
        dialog1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog1.setMax(carts.size());
        dialog1.setCancelable(false);
        dialog1.show();
        new Thread(new Runnable() {
            @Override
            public void run() {

                AssetManager assetManager = getApplicationContext().getAssets();
                InputStream is = null;
                try {
                    is = assetManager.open("pdf_image_gr.png");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                bitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bitmapdata = stream.toByteArray();
                Image image = null;

                File root =new File(Environment.getExternalStorageDirectory(),"Pdf file");
                if (!root.exists())
                {
                    root.mkdir();
                }
                File pdffile = new File(root,"order.pdf");

                Document doc = new Document();
                doc.setPageSize(PageSize.A4);
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(pdffile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    PdfWriter.getInstance(doc, fileOutputStream);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                //open the document
                doc.open();

                BaseFont bfTimes = null;
                try {
                    bfTimes = BaseFont.createFont("assets/arial.ttf",BaseFont.IDENTITY_H,BaseFont.EMBEDDED);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

                final Font urFontName = new Font(bfTimes, 8, Font.NORMAL);
                Font titlefont = new Font(bfTimes,18, Font.BOLD, BaseColor.WHITE);

                PdfPTable del_table = new PdfPTable(3);
                del_table.setWidthPercentage(100);
                PdfPCell cell = new PdfPCell();

                int indentation = 0;
                try {
                    image = Image.getInstance(stream.toByteArray());
                } catch (BadElementException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                float scaler = ((doc.getPageSize().getWidth() - doc.leftMargin()
                        - doc.rightMargin() - indentation) / image.getWidth()) * 100;
                image.scalePercent(scaler);
                image.setAlignment(Element.ALIGN_LEFT);

                //Logo header
                try {
                    doc.add(image);
                    doc.add( Chunk.NEWLINE );
                    doc.add( Chunk.NEWLINE );
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                
                //Stoixeia paraggeleias
                del_table.addCell(new Paragraph("ΚΩΔΙΚΟΣ ΠΑΡΑΓΓΕΛΙΑΣ",urFontName));
                del_table.addCell(new Paragraph("ΗΜΕΡΟΜΗΝΙΑ: ",urFontName));
                del_table.addCell(new Paragraph("ΠΩΛΗΤΗΣ: ",urFontName));
                del_table.addCell(new Paragraph(""+carts.get(0).getOrderid(),urFontName));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());
                del_table.addCell(new Paragraph(""+currentDateandTime,urFontName));
                del_table.addCell(new Paragraph(""+number,urFontName));
                try {
                    doc.add(del_table);
                    doc.add( Chunk.NEWLINE );
                    doc.add( Chunk.NEWLINE );
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                
                PdfPTable all_cust_table = new PdfPTable(3);
                all_cust_table.setWidthPercentage(100);
                all_cust_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

                //stoixeia pelati
                PdfPTable cust_table = new PdfPTable(1);
                cust_table.setWidthPercentage(100);
                cust_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                Paragraph c = new Paragraph("ΠΕΛΑΤΗΣ",urFontName);
                cust_table.addCell(c);
                Paragraph c1 = new Paragraph("ΚΩΔΙΚΟΣ ΠΕΛΑΤΗ: " + custid,urFontName);
                cust_table.addCell(c1);
                Paragraph c2 = new Paragraph("ΟΝΟΜΑ: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCompanyName(),urFontName);
                cust_table.addCell(c2);
                Paragraph c3 = new Paragraph("ΑΦΜ.: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getAfm(),urFontName);
                cust_table.addCell(c3);
                Paragraph c4 = new Paragraph("ΔΟΥ: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getTaxOffice(),urFontName);
                cust_table.addCell(c4);
                Paragraph c5 = new Paragraph("ΧΩΡΑ: " + iliadisDatabase.daoAccess().getCountry(iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCountry()).getCountry(),urFontName);
                cust_table.addCell(c5);

                all_cust_table.addCell(cust_table);
                all_cust_table.addCell("");

                //Dieuthinsi pelati
                PdfPTable addr_table = new PdfPTable(1);
                addr_table.setWidthPercentage(100);
                addr_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                if(Integer.parseInt(shopId) == 0) {
                    Paragraph f1 = new Paragraph("ΠΟΛΗ: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCity(),urFontName);
                    addr_table.addCell(f1);
                }else{
                    Paragraph f1 = new Paragraph("ΠΟΛΗ: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCity(),urFontName);
                    addr_table.addCell(f1);
                }
                if(Integer.parseInt(shopId) == 0) {
                    Paragraph f2 = new Paragraph("ΔΙΕΥΘΥΝΣΗ: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getAddress(),urFontName);
                    addr_table.addCell(f2);
                }else{
                    Paragraph f2 = new Paragraph("ΔΙΕΥΘΥΝΣΗ: " + iliadisDatabase.daoAccess().getShopsByShopid(shopId).getAddress(),urFontName);
                    addr_table.addCell(f2);
                }
                if(Integer.parseInt(shopId) == 0) {
                    Paragraph f3 = new Paragraph("Τ.Κ: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getPostalCode(),urFontName);
                    addr_table.addCell(f3);
                }else{
                    Paragraph f3 = new Paragraph("Τ.Κ: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getPostalCode(),urFontName);
                    addr_table.addCell(f3);
                }
                Paragraph f4 = new Paragraph("ΤΗΛΕΦΩΝΟ: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getPhone(),urFontName);
                addr_table.addCell(f4);
                Paragraph f5 = new Paragraph("EMAIL: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getEmail(),urFontName);
                addr_table.addCell(f5);

                all_cust_table.addCell(addr_table);
                try {
                    doc.add(all_cust_table);
                    doc.add( Chunk.NEWLINE );
                    doc.add( Chunk.NEWLINE );
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                
                PdfPTable prod_lb_table = new PdfPTable(new float[] { 150f, 600f, /*350f,*/ 150f, 150f, 150f });
                prod_lb_table.setWidthPercentage(100);

                Paragraph lb = new Paragraph("ΚΩΔΙΚΟΣ",urFontName);
                prod_lb_table.addCell(lb);
                Paragraph lb2 = new Paragraph("ΠΕΡΙΓΡΑΦΗ",urFontName);
                prod_lb_table.addCell(lb2);
//       
                Paragraph lb4 = new Paragraph("ΠΟΣΟΤΗΤΑ",urFontName);
                prod_lb_table.addCell(lb4);
                Paragraph lb5 = new Paragraph("ΤΙΜΗ",urFontName);
                prod_lb_table.addCell(lb5);
                Paragraph lb6 = new Paragraph("ΣΥΝΟΛΙΚΟ ΠΟΣΟ",urFontName);
                prod_lb_table.addCell(lb6);

                try {
                    doc.add(prod_lb_table);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

                final PdfPTable prod_table = new PdfPTable(new float[] { 150f, 600f, /*350f,*/ 150f, 150f, 150f });
                prod_table.setWidthPercentage(100);
                prod_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

                for(int i = 0; i < carts.size(); i++){
                    Log.d("Dimitra","passssss");
                    prod_table.addCell(new PdfPCell(new Phrase(carts.get(i).getRealcode(),urFontName)));
                    prod_table.addCell(new PdfPCell(new Phrase(carts.get(i).getDescription()+"\n"+carts.get(i).getComment(),urFontName)));
                    prod_table.addCell(new PdfPCell(new Phrase("" + carts.get(i).getQuantity(),urFontName)));
                    Catalog catalog = iliadisDatabase.daoAccess().getCatalogueDiscount(iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCatalogueid(),iliadisDatabase.daoAccess().getProductByProdCode(carts.get(i).getProdcode()).getPriceid());
                    prod_table.addCell(new PdfPCell(new Phrase("" + new DecimalFormat("##.####").format(myutils.getProductPrice(Double.parseDouble(iliadisDatabase.daoAccess().getProductByProdCode(carts.get(i).getProdcode()).getPrice().replace(",",".")),catalog.getDiscount1())),urFontName)));
                    prod_table.addCell(new PdfPCell(new Phrase(""+new DecimalFormat("##.####").format(Double.parseDouble(carts.get(i).getPrice())),urFontName)));
                    progressStatus += 1;
                    dialog1.setProgress(progressStatus);
                    if (progressStatus == carts.size()-1){
                        dialog1.dismiss();
                    }
                }

                try {
                    doc.add(prod_table);
                    doc.add( Chunk.NEWLINE );
                    doc.add( Chunk.NEWLINE );
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                
                PdfPTable aSum_table = new PdfPTable(3);
                aSum_table.setWidthPercentage(100);
                aSum_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

                PdfPTable sum_table = new PdfPTable(1);
                //sum_table.setWidthPercentage(100);
                sum_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

                PdfPCell s = new PdfPCell(new Phrase("ΣΥΝΟΛΟ ΠΟΣΟΤΗΤΑΣ: " + new DecimalFormat("##.####").format(prodSum),urFontName));
                s.setBorder(Rectangle.NO_BORDER);
                s.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sum_table.addCell(s);
                //sum_table.addCell("" + new DecimalFormat("##.##").format(prodSum));

                PdfPCell s1 = new PdfPCell(new Phrase("ΚΑΘΑΡΗ ΑΞΙΑ: " + new DecimalFormat("##.####").format(totalprice)  + "€",urFontName));
                s1.setBorder(Rectangle.NO_BORDER);
                s1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sum_table.addCell(s1);

                PdfPCell s3 = new PdfPCell(new Phrase("ΠΟΣΟ ΜΕΤΑ ΤΗΝ ΕΚΠΤΩΣΗ: " + new DecimalFormat("##.####").format(priceSum) + "€",urFontName));
                s3.setBorder(Rectangle.NO_BORDER);
                s3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sum_table.addCell(s3);

                PdfPCell s4 = new PdfPCell(new Phrase("ΦΠΑ.: " + new DecimalFormat("##.####").format(vTotal) + "€",urFontName));
                s4.setBorder(Rectangle.NO_BORDER);
                s4.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sum_table.addCell(s4);

                PdfPCell s5 = new PdfPCell(new Phrase("ΣΥΝΟΛΙΚΗ ΑΞΙΑ: " + new DecimalFormat("##.####").format((priceSum + vTotal)) + "€",urFontName));
                s5.setBorder(Rectangle.NO_BORDER);
                s5.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sum_table.addCell(s5);

                aSum_table.addCell("");
                aSum_table.addCell("");
                aSum_table.addCell(sum_table);
                try {
                    doc.add(aSum_table);
                    doc.add( Chunk.NEWLINE );
                    doc.add( Chunk.NEWLINE );
                    doc.add( Chunk.NEWLINE );
                    doc.add( Chunk.NEWLINE );
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                
                
                PdfPTable com_table = new PdfPTable(new float[] { 600f, 150f, 150f });
                com_table.setWidthPercentage(100);
                com_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                PdfPCell com = new PdfPCell(new Phrase("Παρατηρήσεις"+"\n"+mymessage,urFontName));
                com.setBorder(Rectangle.NO_BORDER);
                com_table.addCell(com);
                PdfPCell com1 = new PdfPCell(new Phrase("Ο Εκδότης",urFontName));
                com1.setBorder(Rectangle.NO_BORDER);
                com_table.addCell(com1);
                PdfPCell com2 = new PdfPCell(new Phrase("Ο παραλαβών",urFontName));
                com2.setBorder(Rectangle.NO_BORDER);
                com_table.addCell(com2);

                try {
                    doc.add(com_table);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                doc.close();

                if (!doc.isOpen()){

                    File root2 =new File(Environment.getExternalStorageDirectory(),"Pdf file");
                    File pdffile2 = new File(root2,"order.pdf");
                    if (pdffile2.exists()) {
                        for (int i=0; i<2; i++){
                            utils.printPdf(ipprintpref,CartActivity.this);
                        }

                        SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
                        String currentDateandTime2 = sdf2.format(new Date());
                        Order order = new Order();
                        order.setOrderid(carts.get(0).getOrderid());
                        order.setCustid(custid);
                        order.setStatus(2);
                        order.setDateparsed(currentDateandTime2);
                        order.setShopid(shopId);
                        order.setCommentorder(mymessage);
                        shopDatabase.daoShop().updateOrder(order);
                    }

                    //send csv file
                    Customers customer = iliadisDatabase.daoAccess().getCustomerByCustid(custid);
                    String result="";
                    result = myutils.createCsvFile(cartsList,number,custid,carts.get(0).getOrderid(),iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCustomerid(),custvatid,iliadisDatabase.daoAccess().getCustomerByCustid(custid).getPaymentid(),shopId,customer,iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCatalogueid(),mymessage);
                    if (result.equals("success")) {
                        SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
                        String currentDateandTime2 = sdf2.format(new Date());
                        Order order = new Order();
                        order.setOrderid(carts.get(0).getOrderid());
                        order.setCustid(custid);
                        order.setStatus(1);
                        order.setDateparsed(currentDateandTime);
                        order.setShopid(shopId);
                        order.setCommentorder(mymessage);
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
            }
        }).start();
    }

    public void createPdfFileEn(final List<Cart> carts, final String custid, final String custvatid, final String number, final String shopId, final IliadisDatabase iliadisDatabase, final Context context, final String mymessage, final int prodSum,
                                final double priceSum,
                                final double totalprice,
                                final double vTotal)
    {
        final ProgressDialog dialog1 = new ProgressDialog(CartActivity.this);
        dialog1.setMessage("Loading");
        dialog1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog1.setMax(carts.size());
        dialog1.setCancelable(false);
        dialog1.show();

        new Thread(new Runnable() {
            @Override
            public void run() {

                AssetManager assetManager = getApplicationContext().getAssets();
                InputStream is = null;
                try {
                    is = assetManager.open("pdf_image_en.png");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                bitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bitmapdata = stream.toByteArray();
                Image image = null;

                File root =new File(Environment.getExternalStorageDirectory(),"Pdf file");
                if (!root.exists())
                {
                    root.mkdir();
                }
                File pdffile = new File(root,"order.pdf");

                Document doc = new Document();
                doc.setPageSize(PageSize.A4);
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(pdffile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    PdfWriter.getInstance(doc, fileOutputStream);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                //open the document
                doc.open();

                BaseFont bfTimes = null;
                try {
                    bfTimes = BaseFont.createFont("assets/arial.ttf",BaseFont.IDENTITY_H,BaseFont.EMBEDDED);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

                Font urFontName = new Font(bfTimes, 8, Font.NORMAL);
                Font titlefont = new Font(bfTimes,18, Font.BOLD, BaseColor.WHITE);

                PdfPTable del_table = new PdfPTable(3);
                del_table.setWidthPercentage(100);
                PdfPCell cell = new PdfPCell();

                int indentation = 0;
                try {
                    image = Image.getInstance(stream.toByteArray());
                } catch (BadElementException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                float scaler = ((doc.getPageSize().getWidth() - doc.leftMargin()
                        - doc.rightMargin() - indentation) / image.getWidth()) * 100;
                image.scalePercent(scaler);
                image.setAlignment(Element.ALIGN_LEFT);

                //Logo header
                try {
                    doc.add(image);
                    doc.add( Chunk.NEWLINE );
                    doc.add( Chunk.NEWLINE );
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

                //Stoixeia paraggeleias
                del_table.addCell(new Paragraph("ID ORDER",urFontName));
                del_table.addCell(new Paragraph("DATE: ",urFontName));
                del_table.addCell(new Paragraph("SALESMAN: ",urFontName));
                del_table.addCell(new Paragraph(""+carts.get(0).getOrderid(),urFontName));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());
                del_table.addCell(new Paragraph(""+currentDateandTime,urFontName));
                del_table.addCell(new Paragraph(""+number,urFontName));
                try {
                    doc.add(del_table);
                    doc.add( Chunk.NEWLINE );
                    doc.add( Chunk.NEWLINE );
                } catch (DocumentException e) {
                    e.printStackTrace();
                }


                PdfPTable all_cust_table = new PdfPTable(3);
                all_cust_table.setWidthPercentage(100);
                all_cust_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

                //stoixeia pelati
                PdfPTable cust_table = new PdfPTable(1);
                cust_table.setWidthPercentage(100);
                cust_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                Paragraph c = new Paragraph("CUSTOMER",urFontName);
                cust_table.addCell(c);
                Paragraph c1 = new Paragraph("ID CUSTOMER: " + custid,urFontName);
                cust_table.addCell(c1);
                Paragraph c2 = new Paragraph("NAME: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCompanyName(),urFontName);
                cust_table.addCell(c2);
                Paragraph c3 = new Paragraph("VAT.: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getAfm(),urFontName);
                cust_table.addCell(c3);
                Paragraph c4 = new Paragraph("TAX OFFICE: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getTaxOffice(),urFontName);
                cust_table.addCell(c4);
                Paragraph c5 = new Paragraph("COUNTRY: " + iliadisDatabase.daoAccess().getCountry(iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCountry()).getCountry(),urFontName);
                cust_table.addCell(c5);

                all_cust_table.addCell(cust_table);
                all_cust_table.addCell("");

                //Dieuthinsi pelati
                PdfPTable addr_table = new PdfPTable(1);
                addr_table.setWidthPercentage(100);
                addr_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                if(Integer.parseInt(shopId) == 0) {
                    Paragraph f1 = new Paragraph("CITY: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCity(),urFontName);
                    addr_table.addCell(f1);
                }else{
                    Paragraph f1 = new Paragraph("CITY: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCity(),urFontName);
                    addr_table.addCell(f1);
                }
                if(Integer.parseInt(shopId) == 0) {
                    Paragraph f2 = new Paragraph("ADDRESS: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getAddress(),urFontName);
                    addr_table.addCell(f2);
                }else{
                    Paragraph f2 = new Paragraph("ADDRESS: " + iliadisDatabase.daoAccess().getShopsByShopid(shopId).getAddress(),urFontName);
                    addr_table.addCell(f2);
                }
                if(Integer.parseInt(shopId) == 0) {
                    Paragraph f3 = new Paragraph("POSTAL CODE: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getPostalCode(),urFontName);
                    addr_table.addCell(f3);
                }else{
                    Paragraph f3 = new Paragraph("POSTAL CODE: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getPostalCode(),urFontName);
                    addr_table.addCell(f3);
                }
                Paragraph f4 = new Paragraph("PHONE: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getPhone(),urFontName);
                addr_table.addCell(f4);
                Paragraph f5 = new Paragraph("EMAIL: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getEmail(),urFontName);
                addr_table.addCell(f5);

                all_cust_table.addCell(addr_table);

                try {
                    doc.add(all_cust_table);
                    doc.add( Chunk.NEWLINE );
                    doc.add( Chunk.NEWLINE );
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

                PdfPTable prod_lb_table = new PdfPTable(new float[] { 150f, 600f, /*350f,*/ 150f, 150f, 150f });
                prod_lb_table.setWidthPercentage(100);

                Paragraph lb = new Paragraph("CODE",urFontName);
                prod_lb_table.addCell(lb);
                Paragraph lb2 = new Paragraph("DESCRIPTION",urFontName);
                prod_lb_table.addCell(lb2);
                Paragraph lb4 = new Paragraph("QUANTITY",urFontName);
                prod_lb_table.addCell(lb4);
                Paragraph lb5 = new Paragraph("PRICE",urFontName);
                prod_lb_table.addCell(lb5);
                Paragraph lb6 = new Paragraph("TOTAL PRICE",urFontName);
                prod_lb_table.addCell(lb6);

                try {
                    doc.add(prod_lb_table);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

                PdfPTable prod_table = new PdfPTable(new float[] { 150f, 600f, /*350f,*/ 150f, 150f, 150f });
                prod_table.setWidthPercentage(100);
                prod_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

                for(int i = 0; i < carts.size(); i++){
                    prod_table.addCell(new PdfPCell(new Phrase(carts.get(i).getRealcode(),urFontName)));
                    prod_table.addCell(new PdfPCell(new Phrase(iliadisDatabase.daoAccess().getProductByRealCode(carts.get(i).getRealcode()).getProdescriptionEn()+"\n"+carts.get(i).getComment(),urFontName)));
                    prod_table.addCell(new PdfPCell(new Phrase("" + carts.get(i).getQuantity(),urFontName)));
                    Catalog catalog = iliadisDatabase.daoAccess().getCatalogueDiscount(iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCatalogueid(),iliadisDatabase.daoAccess().getProductByProdCode(carts.get(i).getProdcode()).getPriceid());
                    prod_table.addCell(new PdfPCell(new Phrase("" + new DecimalFormat("##.##").format(myutils.getProductPrice(Double.parseDouble(iliadisDatabase.daoAccess().getProductByProdCode(carts.get(i).getProdcode()).getPrice().replace(",",".")),catalog.getDiscount1())),urFontName)));
                    prod_table.addCell(new PdfPCell(new Phrase("" + (new DecimalFormat("##.##").format(Double.parseDouble(carts.get(i).getPrice()))),urFontName)));
                    progressStatus += 1;
                    dialog1.setProgress(progressStatus);
                    if (progressStatus == carts.size()-1){
                        dialog1.dismiss();
                    }
                }
                try {
                    doc.add(prod_table);
                    doc.add( Chunk.NEWLINE );
                    doc.add( Chunk.NEWLINE );
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

                PdfPTable aSum_table = new PdfPTable(3);
                aSum_table.setWidthPercentage(100);
                aSum_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

                PdfPTable sum_table = new PdfPTable(1);
                sum_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);


                PdfPCell s = new PdfPCell(new Phrase("TOTAL QUANTITY: " + new DecimalFormat("##.####").format(prodSum),urFontName));
                s.setBorder(Rectangle.NO_BORDER);
                s.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sum_table.addCell(s);

                PdfPCell s1 = new PdfPCell(new Phrase("ORDER AMOUNT: " + new DecimalFormat("##.####").format(totalprice)  + "€",urFontName));
                s1.setBorder(Rectangle.NO_BORDER);
                s1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sum_table.addCell(s1);

                PdfPCell s3 = new PdfPCell(new Phrase("AMOUNT AFTER DISCOUNT: " + new DecimalFormat("##.####").format(priceSum) + "€",urFontName));
                s3.setBorder(Rectangle.NO_BORDER);
                s3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sum_table.addCell(s3);


                PdfPCell s5 = new PdfPCell(new Phrase("TOTAL AMOUNT: " + new DecimalFormat("##.####").format((priceSum + vTotal)) + "€",urFontName));
                s5.setBorder(Rectangle.NO_BORDER);
                s5.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sum_table.addCell(s5);

                aSum_table.addCell("");
                aSum_table.addCell("");
                aSum_table.addCell(sum_table);
                try {
                    doc.add(aSum_table);
                    doc.add( Chunk.NEWLINE );
                    doc.add( Chunk.NEWLINE );
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

                PdfPTable com_table = new PdfPTable(new float[] { 600f, 150f, 150f });
                com_table.setWidthPercentage(100);
                com_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                PdfPCell com = new PdfPCell(new Phrase("Comments"+"\n"+mymessage,urFontName));

                //PdfPCell com = new PdfPCell(new Phrase("Παρατηρήσεις"+"\n"+mymessage,urFontName));
                com.setBorder(Rectangle.NO_BORDER);
                com_table.addCell(com);
                PdfPCell com1 = new PdfPCell(new Phrase("Productor",urFontName));
                com1.setBorder(Rectangle.NO_BORDER);
                com_table.addCell(com1);
                PdfPCell com2 = new PdfPCell(new Phrase("Delivered",urFontName));
                com2.setBorder(Rectangle.NO_BORDER);
                com_table.addCell(com2);

                try {
                    doc.add(com_table);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                doc.close();

                if (!doc.isOpen())
                {
                    File root2 =new File(Environment.getExternalStorageDirectory(),"Pdf file");
                    File pdffile2 = new File(root2,"order.pdf");
                    if (pdffile2.exists()){
                        for (int i=0; i<2; i++){
                            utils.printPdf(ipprintpref,CartActivity.this);
                        }

                        SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
                        String currentDateandTime2 = sdf2.format(new Date());
                        Order order = new Order();
                        order.setOrderid(carts.get(0).getOrderid());
                        order.setCustid(custid);
                        order.setStatus(2);
                        order.setDateparsed(currentDateandTime2);
                        order.setShopid(shopId);
                        order.setCommentorder(mymessage);
                        shopDatabase.daoShop().updateOrder(order);
                    }

                    //send csv file
                    Customers customer = iliadisDatabase.daoAccess().getCustomerByCustid(custid);
                    String result="";
                    result = myutils.createCsvFile(cartsList,number,custid,carts.get(0).getOrderid(),iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCustomerid(),custvatid,iliadisDatabase.daoAccess().getCustomerByCustid(custid).getPaymentid(),shopId,customer,iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCatalogueid(),mymessage);
                    if (result.equals("success")) {
                        SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
                        String currentDateandTime2 = sdf2.format(new Date());
                        Order order = new Order();
                        order.setOrderid(carts.get(0).getOrderid());
                        order.setCustid(custid);
                        order.setStatus(1);
                        order.setDateparsed(currentDateandTime2);
                        order.setShopid(shopId);
                        order.setCommentorder(mymessage);
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
            }
        }).start();
    }
}
