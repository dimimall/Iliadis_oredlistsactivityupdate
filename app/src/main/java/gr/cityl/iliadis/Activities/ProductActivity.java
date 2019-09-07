package gr.cityl.iliadis.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import gr.cityl.iliadis.Models.Cart;
import gr.cityl.iliadis.Models.IliadisDatabase;
import gr.cityl.iliadis.Models.Order;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                onBackPressed();
            }
        });

        init();

        iliadisDatabase = IliadisDatabase.getInstance(this);
        shopDatabase = ShopDatabase.getInstance(this);

        final String custid = getIntent().getStringExtra("custid");
        final String custvatid = getIntent().getStringExtra("custvatid");
        final String shopid = getIntent().getStringExtra("shopid");
        final int custcatid = getIntent().getIntExtra("catalogueid",0);
            Log.d("Dimitra","ProductActivity custcatid id: "+custcatid);
        final int orderid = getIntent().getExtras().getInt("orderid");

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
                    if (barcodetext.getEditText().getText().toString().length()>=13)
                    {
                        product = iliadisDatabase.daoAccess().getProductByProdCode(barcodetext.getEditText().getText().toString());
                        desctext.setText(product.getProdescription());
                        pricetext.setText(getString(R.string.price)+":"+product.getPrice());
                        balancetext.setText(getString(R.string.totalrest)+":"+product.getQuantitytotal());
                        reservedtext.setText(getString(R.string.reserved)+":"+product.getReserved());
                        renewtext.setText(getString(R.string.renew)+":"+product.getQuantitywaiting());
                        availabletext.setText(getString(R.string.available)+":"+product.getQuantityav());
                        if (product.getAdate()!= null)
                            datereceivetext.setText(getString(R.string.datedelivery)+":"+product.getAdate());
                    }
                    else if (barcodetext.getEditText().getText().toString().length()>=5)
                    {
                        product = iliadisDatabase.daoAccess().getProductByRealCode(barcodetext.getEditText().getText().toString());
                        if (product != null)
                        {
                            desctext.setText(product.getProdescription());
                            pricetext.setText(getString(R.string.price)+":"+product.getPrice());
                            balancetext.setText(getString(R.string.totalrest)+":"+product.getQuantitytotal());
                            reservedtext.setText(getString(R.string.reserved)+":"+product.getReserved());
                            renewtext.setText(getString(R.string.renew)+":"+product.getQuantitywaiting());
                            availabletext.setText(getString(R.string.available)+":"+product.getQuantityav());
                            if (product.getAdate()!= null)
                                datereceivetext.setText(getString(R.string.datedelivery)+":"+product.getAdate());
                        }
                    }
                    return true;
                }
                return false;
            }
        });


        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                desctext.setText("");
                pricetext.setText(getString(R.string.price)+":");
                balancetext.setText(getString(R.string.totalrest)+":");
                reservedtext.setText(getString(R.string.reserved)+":");
                renewtext.setText(getString(R.string.renew)+":");
                availabletext.setText(getString(R.string.available)+":");
                datereceivetext.setText(getString(R.string.datedelivery)+":");
            }
        });

        basket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductActivity.this,AddToCartActivity.class);
                intent.putExtra("prodcode", (Serializable) product);
                intent.putExtra("custid",custid);
                intent.putExtra("custvatid",custvatid);
                intent.putExtra("shopid",shopid);
                intent.putExtra("orderid",orderid);
                intent.putExtra("catalogueid",custcatid);
                startActivity(intent);
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
}
