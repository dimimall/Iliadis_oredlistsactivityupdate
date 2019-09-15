package gr.cityl.iliadis.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v4.os.ConfigurationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import gr.cityl.iliadis.Manager.utils;
import gr.cityl.iliadis.Models.Cart;
import gr.cityl.iliadis.Models.Catalog;
import gr.cityl.iliadis.Models.IliadisDatabase;
import gr.cityl.iliadis.Models.Products;
import gr.cityl.iliadis.Models.ShopDatabase;
import gr.cityl.iliadis.R;

public class ProductActivity extends AppCompatActivity {

    TextInputLayout barcodetext;
    TextView desctext,pricetext,balancetext,reservedtext,renewtext,datereceivetext,availabletext;
    Button basket,scan;
    ImageView cartbutton;
    IliadisDatabase iliadisDatabase;
    ShopDatabase shopDatabase;
    Products product;
    List<Cart> carts;
    utils myutils;
    String realcodecart="";
    String prodcart="";
    private boolean lang ;
    private int orderid=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                myutils.DialogBackbutton(getString(R.string.cancelorder),ProductActivity.this);
            }
        });

        init();

        myutils = new utils();

        iliadisDatabase = IliadisDatabase.getInstance(this);
        shopDatabase = ShopDatabase.getInstance(this);

        final String custid = getIntent().getStringExtra("custid");
        final String custvatid = getIntent().getStringExtra("custvatid");
        final String shopid = getIntent().getStringExtra("shopid");
        final int custcatid = getIntent().getIntExtra("catalogueid",0);
        orderid = getIntent().getExtras().getInt("orderid");


        myutils.sharedpreferences = getSharedPreferences(myutils.MyPREFERENCES, Context.MODE_PRIVATE);
        lang = myutils.sharedpreferences.getBoolean("language",false);


        cartbutton = (ImageView) toolbar.findViewById(R.id.basket);
        cartbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                carts = shopDatabase.daoShop().getCartList(orderid);
                Intent intent = new Intent(ProductActivity.this,CartActivity.class);
                intent.putExtra("custvatid",custvatid);
                intent.putExtra("custid",custid);
                intent.putExtra("shopid",shopid);
                intent.putExtra("cart", (Serializable) carts);
                intent.putExtra("catalogueid",custcatid);
                intent.putExtra("orderid",orderid);
                startActivity(intent);
            }
        });

        barcodetext.getEditText().setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                        keyCode == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                {
                    //if (barcodetext.getEditText().getText().toString().length()>=13)
                    //{
                        product = iliadisDatabase.daoAccess().getProductByProdCode(barcodetext.getEditText().getText().toString());
                        if (product != null)
                        {
                            prodcart = shopDatabase.daoShop().getCartProdCode(orderid,product.getProdcode());
                            realcodecart = shopDatabase.daoShop().getCartRealCode(orderid,product.getRealcode());
                            if (prodcart == null || realcodecart == null) {
                                prodcart = " ";
                                realcodecart =" ";
                            }
                            if (!prodcart.equals(product.getProdcode()) || !realcodecart.equals(product.getRealcode()))
                            {
                                desctext.setText(localeChange(product.getProdescriptionEn(),product.getProdescription()));
                                Catalog catalog = iliadisDatabase.daoAccess().getCatalogueDiscount(custcatid,product.getPriceid());
                                pricetext.setText(getString(R.string.price)+":"+new DecimalFormat("##.##").format(myutils.getProductPrice(Double.parseDouble(product.getPrice().replace(",",".")),catalog.getDiscount1())));
                                balancetext.setText(getString(R.string.totalrest)+":"+product.getQuantitytotal());
                                reservedtext.setText(getString(R.string.reserved)+":"+product.getReserved());
                                renewtext.setText(getString(R.string.renew)+":"+product.getQuantitywaiting());
                                availabletext.setText(getString(R.string.available)+":"+product.getQuantityav());
                                if (product.getAdate()!= null)
                                    datereceivetext.setText(getString(R.string.datedelivery)+":"+product.getAdate());
                            }
                            else {
                                myutils.createDialog(getString(R.string.existproduct),ProductActivity.this);
                                desctext.setText(localeChange(product.getProdescriptionEn(),product.getProdescription()));
                                Catalog catalog = iliadisDatabase.daoAccess().getCatalogueDiscount(custcatid,product.getPriceid());
                                pricetext.setText(getString(R.string.price)+":"+new DecimalFormat("##.##").format(myutils.getProductPrice(Double.parseDouble(product.getPrice().replace(",",".")),catalog.getDiscount1())));
                                balancetext.setText(getString(R.string.totalrest)+":"+product.getQuantitytotal());
                                reservedtext.setText(getString(R.string.reserved)+":"+product.getReserved());
                                renewtext.setText(getString(R.string.renew)+":"+product.getQuantitywaiting());
                                availabletext.setText(getString(R.string.available)+":"+product.getQuantityav());
                                if (product.getAdate()!= null)
                                    datereceivetext.setText(getString(R.string.datedelivery)+":"+product.getAdate());
                            }
                        }
                   // }
//                    else if (barcodetext.getEditText().getText().toString().length()>=5) {
//                        product = iliadisDatabase.daoAccess().getProductByRealCode(barcodetext.getEditText().getText().toString());
//                        if (product != null)
//                        {
//                            realcodecart = shopDatabase.daoShop().getCartRealCode(orderid,product.getRealcode());
//                            if (realcodecart == null)
//                                realcodecart = " ";
//                            if (!realcodecart.equals(product.getRealcode()))
//                            {
//                                desctext.setText(localeChange(product.getProdescriptionEn(),product.getProdescription()));
//                                Catalog catalog = iliadisDatabase.daoAccess().getCatalogueDiscount(custcatid,product.getPriceid());
//                                pricetext.setText(getString(R.string.price)+":"+new DecimalFormat("##.##").format(myutils.getProductPrice(Double.parseDouble(product.getPrice().replace(",",".")),catalog.getDiscount1())));
//                                balancetext.setText(getString(R.string.totalrest)+":"+product.getQuantitytotal());
//                                reservedtext.setText(getString(R.string.reserved)+":"+product.getReserved());
//                                renewtext.setText(getString(R.string.renew)+":"+product.getQuantitywaiting());
//                                availabletext.setText(getString(R.string.available)+":"+product.getQuantityav());
//                                if (product.getAdate()!= null)
//                                    datereceivetext.setText(getString(R.string.datedelivery)+":"+product.getAdate());
//                            }
//                            else {
//                                myutils.createDialog(getString(R.string.existproduct),ProductActivity.this);
//                                desctext.setText(localeChange(product.getProdescriptionEn(),product.getProdescription()));
//                                Catalog catalog = iliadisDatabase.daoAccess().getCatalogueDiscount(custcatid,product.getPriceid());
//                                pricetext.setText(getString(R.string.price)+":"+new DecimalFormat("##.##").format(myutils.getProductPrice(Double.parseDouble(product.getPrice().replace(",",".")),catalog.getDiscount1())));
//                                balancetext.setText(getString(R.string.totalrest)+":"+product.getQuantitytotal());
//                                reservedtext.setText(getString(R.string.reserved)+":"+product.getReserved());
//                                renewtext.setText(getString(R.string.renew)+":"+product.getQuantitywaiting());
//                                availabletext.setText(getString(R.string.available)+":"+product.getQuantityav());
//                                if (product.getAdate()!= null)
//                                    datereceivetext.setText(getString(R.string.datedelivery)+":"+product.getAdate());
//                            }
//                        }
                    //}
                    return true;
                }
                return false;
            }
        });


        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barcodetext.requestFocus();
                desctext.setText("");
                pricetext.setText(getString(R.string.price)+":");
                balancetext.setText(getString(R.string.totalrest)+":");
                reservedtext.setText(getString(R.string.reserved)+":");
                renewtext.setText(getString(R.string.renew)+":");
                availabletext.setText(getString(R.string.available)+":");
                datereceivetext.setText(getString(R.string.datedelivery)+":");
                barcodetext.getEditText().setText("");
            }
        });

        basket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!barcodetext.getEditText().getText().toString().equals("") && product != null)
                {
                    if (Integer.parseInt(product.getQuantityav()) > 0)
                    {
                        Intent intent = new Intent(ProductActivity.this,AddToCartActivity.class);
                        intent.putExtra("prodcode", (Serializable) product);
                        intent.putExtra("custid",custid);
                        intent.putExtra("custvatid",custvatid);
                        intent.putExtra("shopid",shopid);
                        intent.putExtra("orderid",orderid);
                        intent.putExtra("catalogueid",custcatid);
                        startActivity(intent);
                    }
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }


    public void init()
    {
        barcodetext = (TextInputLayout)findViewById(R.id.textInputLayout4);
        desctext = (TextView)findViewById(R.id.textView9);
        pricetext = (TextView)findViewById(R.id.textView10);
        balancetext = (TextView)findViewById(R.id.textView11);
        reservedtext = (TextView)findViewById(R.id.textView12);
        renewtext = (TextView)findViewById(R.id.textView13);
        datereceivetext = (TextView)findViewById(R.id.textView14);
        availabletext = (TextView)findViewById(R.id.textView15);
        scan = (Button)findViewById(R.id.button10);
        basket = (Button)findViewById(R.id.button11);
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
}
