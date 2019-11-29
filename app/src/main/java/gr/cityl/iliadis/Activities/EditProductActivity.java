package gr.cityl.iliadis.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

import gr.cityl.iliadis.Manager.utils;
import gr.cityl.iliadis.Models.Cart;
import gr.cityl.iliadis.Models.Catalog;
import gr.cityl.iliadis.Models.IliadisDatabase;
import gr.cityl.iliadis.Models.Products;
import gr.cityl.iliadis.Models.ShopDatabase;
import gr.cityl.iliadis.R;

public class EditProductActivity extends AppCompatActivity {

    private TextView realProdCodeText,descriptionText,quantityText,priceText;
    private TextInputLayout commentText;
    private EditText editQuantity;
    private Button submit,scan;
    private Cart cart;
    private IliadisDatabase iliadisDatabase;
    private ShopDatabase shopDatabase;
    private Catalog catalog;
    private ImageView basket;
    private double totalprice;
    private utils myutlis = new utils();
    private String comment="";
    private boolean lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        myutlis.sharedpreferences = getSharedPreferences(myutlis.MyPREFERENCES, Context.MODE_PRIVATE);
        lang = myutlis.sharedpreferences.getBoolean("language",false);

        final String custid = getIntent().getExtras().getString("custid");
        final String custvatid = getIntent().getExtras().getString("custvatid");
        final String shopid = getIntent().getExtras().getString("shopid");
        final int custcatid = getIntent().getExtras().getInt("catalogueid");
        final int orderid = getIntent().getExtras().getInt("orderid");
        cart = (Cart) getIntent().getExtras().getSerializable("cart");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                onBackPressed();
            }
        });


        basket = (ImageView)toolbar.findViewById(R.id.basket);
        basket = (ImageView)toolbar.findViewById(R.id.basket);
        basket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Cart> carts = shopDatabase.daoShop().getCartList(orderid);
                Intent intent = new Intent(EditProductActivity.this,CartActivity.class);
                intent.putExtra("custvatid",custvatid);
                intent.putExtra("custid",custid);
                intent.putExtra("cart", (Serializable) carts);
                intent.putExtra("shopid",shopid);
                intent.putExtra("catalogueid",custcatid);
                intent.putExtra("orderid",orderid);
                startActivity(intent);
            }
        });

        init();

        iliadisDatabase = IliadisDatabase.getInstance(this);
        shopDatabase = ShopDatabase.getInstance(this);

        realProdCodeText.setText(cart.getProdcode()+"-"+cart.getRealcode());

        descriptionText.setText(localeChange(iliadisDatabase.daoAccess().getProductByRealCode(cart.getRealcode()).getProdescriptionEn(),iliadisDatabase.daoAccess().getProductByRealCode(cart.getRealcode()).getProdescription()));
        editQuantity.setText(""+cart.getQuantity());
        editQuantity.setSelection(editQuantity.getText().length());
        catalog = iliadisDatabase.daoAccess().getCatalogueDiscount(custcatid,cart.getDiscountid());
        totalprice = cart.getQuantity() * myutlis.getProductPrice(Double.parseDouble(cart.getPriceid().replace(",",".")),catalog.getDiscount1());
        priceText.setText(getString(R.string.price)+":"+new DecimalFormat("##.####").format(totalprice));
        commentText.getEditText().setText(cart.getComment());

        editQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().equals(""))
                {
                    if (Integer.parseInt(editable.toString()) > Integer.parseInt(iliadisDatabase.daoAccess().getProductByProdCode(cart.getProdcode()).getQuantityav()))
                    {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                EditProductActivity.this);
                        // set title
                        alertDialogBuilder.setTitle("");
                        // set dialog message
                        alertDialogBuilder
                                .setMessage(getString(R.string.biggestquantity))
                                .setCancelable(false)
                                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        //editQuantity.setText(iliadisDatabase.daoAccess().getProductByProdCode(cart.getProdcode()).getMinquantity());
                                        editQuantity.setText(""+cart.getQuantity());
                                        dialog.cancel();
                                    }
                                });
                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // show it
                        alertDialog.show();
                    }
                    else
                    {
                        totalprice = Integer.parseInt(editable.toString()) * myutlis.getProductPrice(Double.parseDouble(cart.getPriceid().replace(",",".")),catalog.getDiscount1());
                        priceText.setText(getString(R.string.price)+": "+new DecimalFormat("##.####").format(totalprice));
                    }
                }
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
                Cart updatecart = new Cart(cart.getCartid(), cart.getOrderid(),cart.getRealcode(),cart.getProdcode(),String.valueOf(totalprice),comment,cart.getDescription(),Integer.parseInt(editQuantity.getText().toString()),cart.getVatcode(),cart.getPriceid(),cart.getDiscountid());
                shopDatabase.daoShop().updateCart(updatecart);

                Intent intent = new Intent(EditProductActivity.this,ProductActivity.class);
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
        submit = (Button)findViewById(R.id.button12);
        scan = (Button)findViewById(R.id.button13);
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
