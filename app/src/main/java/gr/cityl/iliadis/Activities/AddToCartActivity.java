package gr.cityl.iliadis.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_cart);

        final String custid = getIntent().getExtras().getString("custid");
        final String custvatid = getIntent().getExtras().getString("custvatid");
        final String shopid = getIntent().getExtras().getString("shopid");
        final int custcatid = getIntent().getExtras().getInt("catalogueid");
        final int orderid = getIntent().getExtras().getInt("orderid");
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
                carts = shopDatabase.daoShop().getCartList(orderid);
                Intent intent = new Intent(AddToCartActivity.this,CartActivity.class);
                intent.putExtra("custvatid",custvatid);
                intent.putExtra("custid",custid);
                intent.putExtra("cart", (Serializable) carts);
                intent.putExtra("shopid",shopid);
                intent.putExtra("catalogueid",custcatid);
                startActivity(intent);
            }
        });

        init();

        iliadisDatabase = IliadisDatabase.getInstance(this);
        shopDatabase = ShopDatabase.getInstance(this);

        realProdCodeText.setText(products.getProdcode()+"-"+products.getRealcode());
        descriptionText.setText(products.getProdescription());
        editQuantity.setText(products.getMinimumstep());
        catalog = iliadisDatabase.daoAccess().getCatalogueDiscount(custcatid,products.getPriceid());
        totalprice = Integer.parseInt(products.getMinimumstep()) * getProductPrice(Double.parseDouble(products.getPrice().replace(",",".")),catalog.getDiscount1());
        priceText.setText("Τιμή: "+new DecimalFormat("##.##").format(totalprice));

        editQuantity.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                        keyCode == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                {
                    if (Integer.parseInt(editQuantity.getText().toString()) < Integer.parseInt(products.getMinimumstep()))
                    {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                AddToCartActivity.this);
                        // set title
                        alertDialogBuilder.setTitle("");
                        // set dialog message
                        alertDialogBuilder
                                .setMessage("Η ποσότητα είναι μικρότερη από την καθορισμένη.")
                                .setCancelable(false)
                                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        editQuantity.setText(products.getMinimumstep());
                                        dialog.cancel();
                                    }
                                });
                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // show it
                        alertDialog.show();
                    }
                    else if (Integer.parseInt(editQuantity.getText().toString()) == Integer.parseInt(products.getMinimumstep()))
                    {
                        totalprice = Integer.parseInt(products.getMinimumstep()) * getProductPrice(Double.parseDouble(products.getPrice().replace(",",".")),catalog.getDiscount1());
                        priceText.setText("Τιμή: "+new DecimalFormat("##.##").format(totalprice));
                    }else if (Integer.parseInt(editQuantity.getText().toString()) > Integer.parseInt(products.getMinimumstep()))
                    {
                        totalprice = Integer.parseInt(editQuantity.getText().toString()) * getProductPrice(Double.parseDouble(products.getPrice().replace(",",".")),catalog.getDiscount1());
                        priceText.setText("Τιμή: "+new DecimalFormat("##.##").format(totalprice));
                    }

                    return true;
                }
                return false;
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cart cart = new Cart(orderid,products.getRealcode(),products.getProdcode(),String.valueOf(totalprice),commentText.getEditText().getText().toString(),products.getProdescription(),Integer.parseInt(editQuantity.getText().toString()),products.getVatcode(),products.getPrice());
                shopDatabase.daoShop().insertTask(cart);
                Intent intent = new Intent(AddToCartActivity.this,ProductActivity.class);
                intent.putExtra("custid",custid);
                intent.putExtra("custvatid",custvatid);
                intent.putExtra("orderid",orderid);
                intent.putExtra("catalogueid",custcatid);
                intent.putExtra("shopid",shopid);
                startActivity(intent);
            }
        });

//        scan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(AddToCartActivity.this,ProductActivity.class);
//                intent.putExtra("custid",custid);
//                intent.putExtra("custvatid",custvatid);
//                startActivity(intent);
//            }
//        });
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
	
	 public double getProductPrice(double flatprice, int discount)
     {
        double price = 0;

        price =flatprice - flatprice * discount/100;
        return price;
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
