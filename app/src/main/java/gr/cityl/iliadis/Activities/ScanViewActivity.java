package gr.cityl.iliadis.Activities;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import gr.cityl.iliadis.Manager.utils;
import gr.cityl.iliadis.Models.Cart;
import gr.cityl.iliadis.Models.Catalog;
import gr.cityl.iliadis.Models.IliadisDatabase;
import gr.cityl.iliadis.Models.Products;
import gr.cityl.iliadis.Models.ShopDatabase;
import gr.cityl.iliadis.R;

public class ScanViewActivity extends AppCompatActivity {

    TextInputLayout barcodetext;
    TextView desctext,pricetext,balancetext,reservedtext,renewtext,datereceivetext,availabletext;
    IliadisDatabase iliadisDatabase;
    ShopDatabase shopDatabase;
    Products product;
    private boolean lang ;
    Button scan;
    utils myutils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_view);

        getSupportActionBar().setTitle(getString(R.string.customer));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

        myutils = new utils();

        iliadisDatabase = IliadisDatabase.getInstance(this);
        shopDatabase = ShopDatabase.getInstance(this);


        myutils.sharedpreferences = getSharedPreferences(myutils.MyPREFERENCES, Context.MODE_PRIVATE);
        lang = myutils.sharedpreferences.getBoolean("language",false);


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
                        if (product != null)
                        {
                            desctext.setText(localeChange(product.getProdescriptionEn(),product.getProdescription()));
                            pricetext.setText(getString(R.string.price)+":"+product.getPrice());
                            balancetext.setText(getString(R.string.totalrest)+":"+product.getQuantitytotal());
                            reservedtext.setText(getString(R.string.reserved)+":"+product.getReserved());
                            renewtext.setText(getString(R.string.renew)+":"+product.getQuantitywaiting());
                            availabletext.setText(getString(R.string.available)+":"+product.getQuantityav());
                            if (product.getAdate()!= null)
                                datereceivetext.setText(getString(R.string.datedelivery)+":"+product.getAdate());
                        }
                    }
                    else if (barcodetext.getEditText().getText().toString().length()>=5) {
                        product = iliadisDatabase.daoAccess().getProductByRealCode(barcodetext.getEditText().getText().toString());
                        if (product != null)
                        {
                            desctext.setText(localeChange(product.getProdescriptionEn(),product.getProdescription()));
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
    }

    public void init()
    {
        scan = (Button)findViewById(R.id.button10);
        barcodetext = (TextInputLayout)findViewById(R.id.textInputLayout4);
        desctext = (TextView)findViewById(R.id.textView9);
        pricetext = (TextView)findViewById(R.id.textView10);
        balancetext = (TextView)findViewById(R.id.textView11);
        reservedtext = (TextView)findViewById(R.id.textView12);
        renewtext = (TextView)findViewById(R.id.textView13);
        datereceivetext = (TextView)findViewById(R.id.textView14);
        availabletext = (TextView)findViewById(R.id.textView15);
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
