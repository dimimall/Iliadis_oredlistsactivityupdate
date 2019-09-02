package gr.cityl.iliadis.Activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.PopupMenu;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import gr.cityl.iliadis.Manager.utils;
import gr.cityl.iliadis.Models.Cart;
import gr.cityl.iliadis.Models.IliadisDatabase;
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
                onBackPressed();
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



        carts = new ArrayList<>();

        subtotal.setText("0");
        vat.setText("0");
        grandtotal.setText("0");

        custid = getIntent().getExtras().getString("custid");
        shopId= getIntent().getExtras().getString("shopid");
        custvatid = getIntent().getExtras().getString("custvatid");
        cartsList = (List<Cart>) getIntent().getExtras().getSerializable("cart");

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
                prodSum = prodSum + carts.get(i).getQuantity();
                priceSum = priceSum + (Double.parseDouble(carts.get(i).getPriceid().replace(",",".")) * carts.get(i).getQuantity());
                sPriceSum = sPriceSum + (Double.parseDouble(carts.get(i).getPrice().replace(",",".")) * carts.get(i).getQuantity());
                vTotal = vTotal + (((k * Double.parseDouble(carts.get(i).getPrice().replace(",","."))) / 100) * carts.get(i).getQuantity());
            }
            subtotal.setText(new DecimalFormat("##.##").format(priceSum) + "€");
            vat.setText(new DecimalFormat("##.##").format(vTotal) + "€");
            double total = Double.parseDouble(new DecimalFormat("##.##").format((sPriceSum + vTotal)).replace(",","."));
            grandtotal.setText(""+total+ "€");
        }else {
            Toast.makeText(CartActivity.this,"Δεν Υπάρχουν Προϊόντα",Toast.LENGTH_LONG).show();
        }

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    createPdfFile(cartsList);
                    //shopDatabase.daoShop().updateOrderStatus(1,cartsList.get(0).getOrderid());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                String result = myutils.createCsvFile(cartsList);
                if (result.equals("success")) {
                    shopDatabase.daoShop().updateOrderStatus(2, cartsList.get(0).getOrderid());
                    myutils.createDialog("Στάλθηκε το αρχείο",CartActivity.this);
                }
                else
                {
                    myutils.createDialog("Δε στάλθηκε το αρχείο",CartActivity.this);
                }
                finishAffinity();
                System.exit(0);
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
                        if(listcart.getRealcode().startsWith(filterPattern)){
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
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.code.setText(cartList.get(position).getRealcode()+"-"+cartList.get(position).getProdcode());
            holder.description.setText(cartList.get(position).getDescription());
            holder.qty.setText("QTY: "+cartList.get(position).getQuantity());
            holder.price.setText(cartList.get(position).getPrice());

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
                                    cartList.remove(holder.getAdapterPosition());
                                    Cart cart = cartList.get(holder.getAdapterPosition());
                                    shopDatabase.daoShop().deleteCart(cart);
                                    notifyDataSetChanged();
                                    return true;
                                case R.id.menu2:
                                    //handle menu2 click
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

    public void createPdfFile(List<Cart> carts) throws IOException, DocumentException
    {
        AssetManager assetManager = getAssets();
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
        Image image;

        File root =new File(Environment.getExternalStorageDirectory(),"Pdf file");
        if (!root.exists())
        {
            root.mkdir();
        }
        File pdffile = new File(root,"order.pdf");

        Document doc = new Document();
        doc.setPageSize(PageSize.A4);
        FileOutputStream fileOutputStream = new FileOutputStream(pdffile);
        PdfWriter.getInstance(doc, fileOutputStream);
        //open the document
        doc.open();

        BaseFont bfTimes = null;
        try {
            bfTimes = BaseFont.createFont("assets/g_ari_i.ttf",BaseFont.IDENTITY_H,BaseFont.EMBEDDED);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Font urFontName = new Font(bfTimes, 8, Font.BOLD);
        Font titlefont = new Font(bfTimes,18, Font.BOLD, BaseColor.WHITE);

        PdfPTable del_table = new PdfPTable(3);
        del_table.setWidthPercentage(100);
        //table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        PdfPCell cell = new PdfPCell();


        int indentation = 0;
        image = Image.getInstance(stream.toByteArray());

        float scaler = ((doc.getPageSize().getWidth() - doc.leftMargin()
                - doc.rightMargin() - indentation) / image.getWidth()) * 100;
        image.scalePercent(scaler);
        image.setAlignment(Element.ALIGN_LEFT);

        //Logo header
        doc.add(image);
        doc.add( Chunk.NEWLINE );
        doc.add( Chunk.NEWLINE );

        //Stoixeia paraggeleias
        del_table.addCell(new Paragraph("Κωδικός παραγγελίας",urFontName));
        del_table.addCell(new Paragraph("DATE: ",urFontName));
        del_table.addCell(new Paragraph("Seller: ",urFontName));
        del_table.addCell(new Paragraph("1",urFontName));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        del_table.addCell(new Paragraph(""+currentDateandTime,urFontName));
        del_table.addCell(new Paragraph("18",urFontName));
        doc.add(del_table);

        doc.add( Chunk.NEWLINE );
        doc.add( Chunk.NEWLINE );

        PdfPTable all_cust_table = new PdfPTable(3);
        all_cust_table.setWidthPercentage(100);
        all_cust_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        //stoixeia pelati
        PdfPTable cust_table = new PdfPTable(1);
        cust_table.setWidthPercentage(100);
        cust_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        Paragraph c = new Paragraph("Πελάτης",urFontName);
        cust_table.addCell(c);
        Paragraph c1 = new Paragraph("CUST ID: " + custid,urFontName);
        cust_table.addCell(c1);
        Paragraph c2 = new Paragraph("NAME: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCompanyName(),urFontName);
        Log.d("Dimitra",iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCompanyName());
        cust_table.addCell(c2);
        Paragraph c3 = new Paragraph("VAT.: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getAfm(),urFontName);
        cust_table.addCell(c3);
        Paragraph c4 = new Paragraph("TAX OFFICE: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getTaxOffice(),urFontName);
        Log.d("Dimitra",iliadisDatabase.daoAccess().getCustomerByCustid(custid).getTaxOffice());
        cust_table.addCell(c4);
        Paragraph c5 = new Paragraph("COUNTRY: " + iliadisDatabase.daoAccess().getCountry(iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCountry()).getCountry(),urFontName);
        Log.d("Dimitra",iliadisDatabase.daoAccess().getCountry(iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCountry()).getCountry());
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
            Paragraph f3 = new Paragraph("POST CODE: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getPostalCode(),urFontName);
            addr_table.addCell(f3);
        }else{
            Paragraph f3 = new Paragraph("POST CODE: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getPostalCode(),urFontName);
            addr_table.addCell(f3);
        }
        Paragraph f4 = new Paragraph("PHONE: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getPhone(),urFontName);
        addr_table.addCell(f4);
        Paragraph f5 = new Paragraph("EMAIL: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getEmail(),urFontName);
        addr_table.addCell(f5);

        all_cust_table.addCell(addr_table);
        doc.add(all_cust_table);

        doc.add( Chunk.NEWLINE );
        doc.add( Chunk.NEWLINE );

        PdfPTable prod_lb_table = new PdfPTable(new float[] { 150f, 600f, 350f, 150f, 150f, 150f });
        //PdfPTable prod_lb_table = new PdfPTable(6);
        prod_lb_table.setWidthPercentage(100);

        Paragraph lb = new Paragraph("CODE",urFontName);
        prod_lb_table.addCell(lb);
        Paragraph lb2 = new Paragraph("DESCRIPTION",urFontName);
        prod_lb_table.addCell(lb2);
        Paragraph lb3 = new Paragraph("COMMENT",urFontName);
        prod_lb_table.addCell(lb3);
        Paragraph lb4 = new Paragraph("QUAN",urFontName);
        prod_lb_table.addCell(lb4);
        Paragraph lb5 = new Paragraph("PRICE",urFontName);
        prod_lb_table.addCell(lb5);
        Paragraph lb6 = new Paragraph("TOTAL AMOUNT",urFontName);
        prod_lb_table.addCell(lb6);

        doc.add(prod_lb_table);

        PdfPTable prod_table = new PdfPTable(new float[] { 150f, 600f, 350f, 150f, 150f, 150f });
        prod_table.setWidthPercentage(100);
        prod_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        for(int i = 0; i < carts.size(); i++){
            prod_table.addCell(new PdfPCell(new Phrase(carts.get(i).getRealcode(),urFontName)));
            prod_table.addCell(new PdfPCell(new Phrase(carts.get(i).getDescription(),urFontName)));
            prod_table.addCell(new PdfPCell(new Phrase("" + carts.get(i).getComment(),urFontName)));
            prod_table.addCell(new PdfPCell(new Phrase("" + carts.get(i).getQuantity(),urFontName)));
            prod_table.addCell(new PdfPCell(new Phrase("" + carts.get(i).getPriceid(),urFontName)));
            prod_table.addCell(new PdfPCell(new Phrase("" + (carts.get(i).getPrice()),urFontName)));
        }
        doc.add(prod_table);

        doc.add( Chunk.NEWLINE );
        doc.add( Chunk.NEWLINE );

        PdfPTable aSum_table = new PdfPTable(3);
        aSum_table.setWidthPercentage(100);
        aSum_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        PdfPTable sum_table = new PdfPTable(1);
        //sum_table.setWidthPercentage(100);
        sum_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        int prodSum = 0;
        double priceSum = 0.0;
        double sPriceSum = 0.0;
        double vTotal = 0.0;

        int j = Integer.parseInt(iliadisDatabase.daoAccess().getFpa(custvatid).getCustvatid());

        int k = 0;

        if(j == 0){
            k = 24;
        }else if(j == 1){
            k = 0;
        }else if(j == 2){
            k = 17;
        }

        for(int i = 0; i < carts.size(); i++){
            prodSum = prodSum + carts.get(i).getQuantity();
            priceSum = priceSum + (Double.parseDouble(carts.get(i).getPriceid().replace(",",".")) * carts.get(i).getQuantity());
            sPriceSum = sPriceSum + (Double.parseDouble(carts.get(i).getPrice().replace(",",".")) * carts.get(i).getQuantity());
            vTotal = vTotal + (((k * Double.parseDouble(carts.get(i).getPrice().replace(",","."))) / 100) * carts.get(i).getQuantity());
        }
        PdfPCell s = new PdfPCell(new Phrase("Total Quantity: " + new DecimalFormat("##.##").format(prodSum),urFontName));
        s.setBorder(Rectangle.NO_BORDER);
        s.setHorizontalAlignment(Element.ALIGN_RIGHT);
        sum_table.addCell(s);
        //sum_table.addCell("" + new DecimalFormat("##.##").format(prodSum));

        PdfPCell s1 = new PdfPCell(new Phrase("Order amount: " + new DecimalFormat("##.##").format(priceSum)  + "€",urFontName));
        s1.setBorder(Rectangle.NO_BORDER);
        s1.setHorizontalAlignment(Element.ALIGN_RIGHT);
        sum_table.addCell(s1);
        //sum_table.addCell("" + new DecimalFormat("##.##").format(priceSum));

        PdfPCell s2 = new PdfPCell(new Phrase("Discount: " + new DecimalFormat("##.##").format((priceSum - sPriceSum)) + "€",urFontName));
        s2.setBorder(Rectangle.NO_BORDER);
        s2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        sum_table.addCell(s2);
        //sum_table.addCell("" + new DecimalFormat("##.##").format((priceSum - sPriceSum)));

        PdfPCell s3 = new PdfPCell(new Phrase("Amount after discount: " + new DecimalFormat("##.##").format(sPriceSum) + "€",urFontName));
        s3.setBorder(Rectangle.NO_BORDER);
        s3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        sum_table.addCell(s3);
        //sum_table.addCell("" + new DecimalFormat("##.##").format(sPriceSum));

        PdfPCell s4 = new PdfPCell(new Phrase("VAT cost.: " + new DecimalFormat("##.##").format(vTotal) + "€",urFontName));
        s4.setBorder(Rectangle.NO_BORDER);
        s4.setHorizontalAlignment(Element.ALIGN_RIGHT);
        sum_table.addCell(s4);
        //sum_table.addCell("" + new DecimalFormat("##.##").format(vTotal));

        PdfPCell s5 = new PdfPCell(new Phrase("Total amount: " + new DecimalFormat("##.##").format((sPriceSum + vTotal)) + "€",urFontName));
        s5.setBorder(Rectangle.NO_BORDER);
        s5.setHorizontalAlignment(Element.ALIGN_RIGHT);
        sum_table.addCell(s5);
        //sum_table.addCell("" + new DecimalFormat("##.##").format((sPriceSum + vTotal)));

        aSum_table.addCell("");
        aSum_table.addCell("");
        aSum_table.addCell(sum_table);
        doc.add(aSum_table);

        doc.add( Chunk.NEWLINE );
        doc.add( Chunk.NEWLINE );
        doc.add( Chunk.NEWLINE );
        doc.add( Chunk.NEWLINE );
        doc.add( Chunk.NEWLINE );
        doc.add( Chunk.NEWLINE );

        PdfPTable com_table = new PdfPTable(new float[] { 600f, 150f, 150f });
        com_table.setWidthPercentage(100);
        com_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        PdfPCell com = new PdfPCell(new Phrase("Παρατηρήσεις",urFontName));
        com.setBorder(Rectangle.NO_BORDER);
        com_table.addCell(com);
        PdfPCell com1 = new PdfPCell(new Phrase("Ο Εκδότης",urFontName));
        com1.setBorder(Rectangle.NO_BORDER);
        com_table.addCell(com1);
        PdfPCell com2 = new PdfPCell(new Phrase("Ο παραλαβών",urFontName));
        com2.setBorder(Rectangle.NO_BORDER);
        com_table.addCell(com2);
        PdfPCell com3 = new PdfPCell(new Phrase(""));
        com3.setBorder(Rectangle.NO_BORDER);
        com_table.addCell(com3);
        com_table.addCell("");
        com_table.addCell("");

        doc.add(com_table);
        doc.close();
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

}
