package gr.cityl.iliadis.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v4.os.ConfigurationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import gr.cityl.iliadis.Models.Order;
import gr.cityl.iliadis.Models.Products;
import gr.cityl.iliadis.Models.ShopDatabase;
import gr.cityl.iliadis.R;

public class AddToCartActivity extends AppCompatActivity {

    private TextView realProdCodeText,descriptionText,quantityText,priceText;
    private TextInputLayout commentText;
    private EditText editQuantity;
    private Button submit,scan;
    private Products products;
    private IliadisDatabase iliadisDatabase;
    private ShopDatabase shopDatabase;
    private Catalog catalog;
    private ImageView basket;
    private List<Cart> carts;
    private double totalprice;
    private utils myutlis = new utils();
    private String comment="";
    private boolean lang ;
    private Button add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_cart);

        final String custid = getIntent().getExtras().getString("custid");
        final String custvatid = getIntent().getExtras().getString("custvatid");
        final String shopid = getIntent().getExtras().getString("shopid");
        final int custcatid = getIntent().getExtras().getInt("catalogueid");
        final int orderid = getIntent().getExtras().getInt("orderid");
       // Log.d("Dimitra","orderid "+orderid);

        products = (Products) getIntent().getExtras().getSerializable("prodcode");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                onBackPressed();
            }
        });

        basket = (ImageView)toolbar.findViewById(R.id.basket);
        basket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //carts = shopDatabase.daoShop().getCartList(orderid);
                Intent intent = new Intent(AddToCartActivity.this,CartActivity.class);
                intent.putExtra("custvatid",custvatid);
                intent.putExtra("custid",custid);
                //intent.putExtra("cart", (Serializable) carts);
                intent.putExtra("shopid",shopid);
                intent.putExtra("catalogueid",custcatid);
                intent.putExtra("orderid",orderid);
                startActivity(intent);
            }
        });

        init();

        iliadisDatabase = IliadisDatabase.getInstance(this);
        shopDatabase = ShopDatabase.getInstance(this);

        myutlis.sharedpreferences = getSharedPreferences(myutlis.MyPREFERENCES, Context.MODE_PRIVATE);
        lang = myutlis.sharedpreferences.getBoolean("language",false);

        realProdCodeText.setText(products.getProdcode()+"-"+products.getRealcode());
        descriptionText.setText(localeChange(products.getProdescriptionEn(),products.getProdescription()));
        editQuantity.setText(products.getMinquantity());
        editQuantity.setSelection(editQuantity.getText().length());
        catalog = iliadisDatabase.daoAccess().getCatalogueDiscount(custcatid,products.getPriceid());
        totalprice = Integer.parseInt(products.getMinquantity()) * myutlis.getProductPrice(Double.parseDouble(products.getPrice().replace(",",".")),catalog.getDiscount1());
        priceText.setText(getString(R.string.price)+":"+new DecimalFormat("##.####").format(totalprice));

        editQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().equals(""))
                {
                    if (Integer.parseInt(charSequence.toString()) > Integer.parseInt(products.getQuantityav()))

                    {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                AddToCartActivity.this);
                        // set title
                        alertDialogBuilder.setTitle("");
                        // set dialog message
                        alertDialogBuilder
                                .setMessage(getString(R.string.biggestquantity))
                                .setCancelable(false)
                                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        editQuantity.setText(products.getQuantityav());
                                        dialog.cancel();
                                    }
                                });
                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // show it
                        alertDialog.show();
                    }
                    else {
                        totalprice = Integer.parseInt(charSequence.toString()) * myutlis.getProductPrice(Double.parseDouble(products.getPrice().replace(",",".")),catalog.getDiscount1());
                        priceText.setText(getString(R.string.price)+": "+new DecimalFormat("##.####").format(totalprice));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        commentText.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                comment = editable.toString();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( !(editQuantity.getText().toString().equals(""))){

                    Cart cart = new Cart(orderid, products.getRealcode(), products.getProdcode(), String.valueOf(totalprice), comment, products.getProdescription(), Integer.parseInt(editQuantity.getText().toString()), products.getVatcode(), products.getPrice(), products.getPriceid());
                    shopDatabase.daoShop().insertTask(cart);
                    Intent intent = new Intent(AddToCartActivity.this, ProductActivity.class);
                    intent.putExtra("custid", custid);
                    intent.putExtra("custvatid", custvatid);
                    intent.putExtra("orderid", orderid);
                    intent.putExtra("catalogueid", custcatid);
                    intent.putExtra("shopid", shopid);
                    startActivity(intent);
                }

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( !(editQuantity.getText().toString().equals(""))) {
                    Cart cart = new Cart(orderid, products.getRealcode(), products.getProdcode(), String.valueOf(totalprice), comment, products.getProdescription(), Integer.parseInt(editQuantity.getText().toString()), products.getVatcode(), products.getPrice(), products.getPriceid());
                    shopDatabase.daoShop().insertTask(cart);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    Intent intent = new Intent(AddToCartActivity.this, ProductActivity.class);
                    intent.putExtra("custid", custid);
                    intent.putExtra("custvatid", custvatid);
                    intent.putExtra("orderid", orderid);
                    intent.putExtra("catalogueid", custcatid);
                    intent.putExtra("shopid", shopid);
                    startActivity(intent);
                }
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddToCartActivity.this,ProductActivity.class);
                intent.putExtra("custid",custid);
                intent.putExtra("custvatid",custvatid);
                intent.putExtra("orderid",orderid);
                intent.putExtra("catalogueid",custcatid);
                intent.putExtra("shopid",shopid);
                startActivity(intent);
            }
        });
    }

    public void init()
    {
        realProdCodeText = (TextView)findViewById(R.id.textView20);
        descriptionText = (TextView)findViewById(R.id.textView21);
        quantityText = (TextView)findViewById(R.id.textView22);
        priceText = (TextView)findViewById(R.id.textView23);
        commentText = (TextInputLayout)findViewById(R.id.textInputLayout5);
        editQuantity = (EditText)findViewById(R.id.editText4);
        editQuantity.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        submit = (Button)findViewById(R.id.button12);
        scan = (Button)findViewById(R.id.button13);
        add = (Button)findViewById(R.id.button2);
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
