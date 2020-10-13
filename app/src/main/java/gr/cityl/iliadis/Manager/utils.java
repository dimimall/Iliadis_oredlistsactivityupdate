package gr.cityl.iliadis.Manager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.util.Printer;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;


import android.os.Build;

import java.lang.reflect.Method;



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

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import gr.cityl.iliadis.Activities.MainActivity;
import gr.cityl.iliadis.Activities.ReprintListsActivity;
import gr.cityl.iliadis.Models.Cart;
import gr.cityl.iliadis.Models.Catalog;
import gr.cityl.iliadis.Models.Customers;
import gr.cityl.iliadis.Models.IliadisDatabase;
import gr.cityl.iliadis.R;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by dimitra on 21/07/2019.
 */

public class utils {

    public final String MyPREFERENCES = "MyPrefs" ;
    public SharedPreferences sharedpreferences;

    public static boolean isConnectedToNetwork(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isConnected = false;
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            isConnected = (activeNetwork != null) && (activeNetwork.isConnectedOrConnecting());
        }

        return isConnected;
    }

    public String getMacAddress(Context context)
    {

        String serialNumber;

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);

            // (?) Lenovo Tab (https://stackoverflow.com/a/34819027/1276306)
            serialNumber = (String) get.invoke(c, "gsm.sn1");

            if (serialNumber.equals(""))
                // Samsung Galaxy S5 (SM-G900F) : 6.0.1
                // Samsung Galaxy S6 (SM-G920F) : 7.0
                // Samsung Galaxy Tab 4 (SM-T530) : 5.0.2
                // (?) Samsung Galaxy Tab 2 (https://gist.github.com/jgold6/f46b1c049a1ee94fdb52)
                serialNumber = (String) get.invoke(c, "ril.serialnumber");

            if (serialNumber.equals(""))
                // Archos 133 Oxygen : 6.0.1
                // Google Nexus 5 : 6.0.1
                // Hannspree HANNSPAD 13.3" TITAN 2 (HSG1351) : 5.1.1
                // Honor 5C (NEM-L51) : 7.0
                // Honor 5X (KIW-L21) : 6.0.1
                // Huawei M2 (M2-801w) : 5.1.1
                // (?) HTC Nexus One : 2.3.4 (https://gist.github.com/tetsu-koba/992373)
                serialNumber = (String) get.invoke(c, "ro.serialno");

            if (serialNumber.equals(""))
                // (?) Samsung Galaxy Tab 3 (https://stackoverflow.com/a/27274950/1276306)
                serialNumber = (String) get.invoke(c, "sys.serialnumber");

            if (serialNumber.equals(""))
                // Archos 133 Oxygen : 6.0.1
                // Hannspree HANNSPAD 13.3" TITAN 2 (HSG1351) : 5.1.1
                // Honor 9 Lite (LLD-L31) : 8.0
                // Xiaomi Mi 8 (M1803E1A) : 8.1.0
                serialNumber = Build.SERIAL;

            // If none of the methods above worked
            if (serialNumber.equals(""))
                serialNumber = null;
        } catch (Exception e) {
            e.printStackTrace();
            serialNumber = null;
        }

        return serialNumber;
    }


    public static Configuration changeLocaleApp(String key, String localeLoad, Context context)
    {
        Locale locale = new Locale(key,localeLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());
        return config;
    }

    public String createCsvFile(List<Cart> carts, String salesid, String custid, int orderid, String customerid, String custvatid, String paymentid, String shopid, Customers customer, int custcatid,String commentOrder){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        File fileDir = new File(Environment.getExternalStorageDirectory(),"Csv File");
        if(!fileDir.exists()){
            try{
                fileDir.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File txtfile = new File(fileDir,orderid+".txt");
        if(!txtfile.exists()) {
            try {
                txtfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(txtfile.exists()){
            try {
                FileWriter fileWriter  = new FileWriter(txtfile);
                BufferedWriter bfWriter = new BufferedWriter(fileWriter);
                bfWriter.write(salesid);
                bfWriter.newLine();
                bfWriter.write(custid);
                bfWriter.newLine();
                bfWriter.write(" "+orderid);
                bfWriter.newLine();
                bfWriter.write(commentOrder);
                bfWriter.newLine();
                bfWriter.write(customerid);
                bfWriter.newLine();
                bfWriter.write(custvatid);
                bfWriter.newLine();
                bfWriter.write(paymentid);
                bfWriter.newLine();
                bfWriter.write(shopid);
                bfWriter.newLine();
                bfWriter.write(customer.getCompanyName());
                bfWriter.newLine();
                bfWriter.write(customer.getEmail());
                bfWriter.newLine();
                bfWriter.write(customer.getPhone());
                bfWriter.newLine();
                bfWriter.write(customer.getAddress());
                bfWriter.newLine();
                bfWriter.write(customer.getPostalCode());
                bfWriter.newLine();
                bfWriter.write(customer.getCity());
                bfWriter.newLine();
                bfWriter.write(customer.getCountry());
                bfWriter.newLine();
                for (int i=0; i<carts.size(); i++)
                {
                    bfWriter.write(carts.get(i).getRealcode());
                    bfWriter.newLine();
                    bfWriter.write(" "+carts.get(i).getQuantity());
                    bfWriter.newLine();
                    bfWriter.write(carts.get(i).getVatcode());
                    bfWriter.newLine();
                    bfWriter.write(" "+custcatid);
                    bfWriter.newLine();
                    bfWriter.write(carts.get(i).getPriceid());
                    bfWriter.newLine();
                    bfWriter.write(new DecimalFormat("##.##").format( getProductPrice(Double.parseDouble(carts.get(i).getPriceid().replace(",",".")),custcatid)));
                    bfWriter.newLine();
                    bfWriter.write(carts.get(i).getComment());
                    bfWriter.newLine();
                }
                bfWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String success="";
        String ipserverpref;
        ipserverpref = sharedpreferences.getString("ipserver", "");
        String url = ipserverpref;  //"/UploadFile.php";
        File filecsv = new File(fileDir,orderid+".txt");
        if(filecsv.exists()) {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url+"/UploadFile.php");

                try {
                    FileBody bin = new FileBody(filecsv);
                    //MultipartEntityBuilder builder = MultipartEntityBuilder.create();

                    MultipartEntity reqEntity = new MultipartEntity();
                    reqEntity.addPart("order", bin);
                    httppost.setEntity(reqEntity);

                    System.out.println("Requesting : " + httppost.getRequestLine());
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    String responseBody = httpclient.execute(httppost, responseHandler);

                    System.out.println("responseBody : " + responseBody);

                    JSONArray array = new JSONArray(responseBody);
                    JSONObject object = array.getJSONObject(0);
                    success = object.getString("result");

                } catch (UnsupportedEncodingException e) {
                    Log.e("Error",e.getLocalizedMessage());
                } catch (ClientProtocolException e) {
                    Log.e("Error",e.getLocalizedMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    httpclient.getConnectionManager().shutdown();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    public double getProductPrice(double flatprice, int discount)
    {
        double price = 0;

        price =flatprice - flatprice * discount/100;
        return price;
    }


//    public void createPdfFileGr(List<Cart> carts, String custid, String custvatid, String number, String shopId, IliadisDatabase iliadisDatabase,Context context,String mymessage,int prodSum,
//            double priceSum,
//            double totalprice,
//            double vTotal) throws IOException, DocumentException
//    {
//        AssetManager assetManager = context.getAssets();
//        InputStream is = null;
//        try {
//            is = assetManager.open("pdf_image_gr.png");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Bitmap bitmap = BitmapFactory.decodeStream(is);
//        bitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true);
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] bitmapdata = stream.toByteArray();
//        Image image;
//
//        File root =new File(Environment.getExternalStorageDirectory(),"Pdf file");
//        if (!root.exists())
//        {
//            root.mkdir();
//        }
//        File pdffile = new File(root,"order.pdf");
//
//        Document doc = new Document();
//        doc.setPageSize(PageSize.A4);
//        FileOutputStream fileOutputStream = new FileOutputStream(pdffile);
//        PdfWriter.getInstance(doc, fileOutputStream);
//        //open the document
//        doc.open();
//
//        BaseFont bfTimes = null;
//        try {
//            bfTimes = BaseFont.createFont("assets/arial.ttf",BaseFont.IDENTITY_H,BaseFont.EMBEDDED);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        Font urFontName = new Font(bfTimes, 8, Font.NORMAL);
//        Font titlefont = new Font(bfTimes,18, Font.BOLD, BaseColor.WHITE);
//
//        PdfPTable del_table = new PdfPTable(3);
//        del_table.setWidthPercentage(100);
//        PdfPCell cell = new PdfPCell();
//
//        int indentation = 0;
//        image = Image.getInstance(stream.toByteArray());
//
//        float scaler = ((doc.getPageSize().getWidth() - doc.leftMargin()
//                - doc.rightMargin() - indentation) / image.getWidth()) * 100;
//        image.scalePercent(scaler);
//        image.setAlignment(Element.ALIGN_LEFT);
//
//        //Logo header
//        doc.add(image);
//        doc.add( Chunk.NEWLINE );
//        doc.add( Chunk.NEWLINE );
//
//        //Stoixeia paraggeleias
//        del_table.addCell(new Paragraph("ΚΩΔΙΚΟΣ ΠΑΡΑΓΓΕΛΙΑΣ",urFontName));
//        del_table.addCell(new Paragraph("ΗΜΕΡΟΜΗΝΙΑ: ",urFontName));
//        del_table.addCell(new Paragraph("ΠΩΛΗΤΗΣ: ",urFontName));
//        del_table.addCell(new Paragraph(""+carts.get(0).getOrderid(),urFontName));
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
//        String currentDateandTime = sdf.format(new Date());
//        del_table.addCell(new Paragraph(""+currentDateandTime,urFontName));
//        del_table.addCell(new Paragraph(""+number,urFontName));
//        doc.add(del_table);
//
//        doc.add( Chunk.NEWLINE );
//        doc.add( Chunk.NEWLINE );
//
//        PdfPTable all_cust_table = new PdfPTable(3);
//        all_cust_table.setWidthPercentage(100);
//        all_cust_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
//
//        //stoixeia pelati
//        PdfPTable cust_table = new PdfPTable(1);
//        cust_table.setWidthPercentage(100);
//        cust_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
//        Paragraph c = new Paragraph("ΠΕΛΑΤΗΣ",urFontName);
//        cust_table.addCell(c);
//        Paragraph c1 = new Paragraph("ΚΩΔΙΚΟΣ ΠΕΛΑΤΗ: " + custid,urFontName);
//        cust_table.addCell(c1);
//        Paragraph c2 = new Paragraph("ΟΝΟΜΑ: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCompanyName(),urFontName);
//        cust_table.addCell(c2);
//        Paragraph c3 = new Paragraph("ΑΦΜ.: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getAfm(),urFontName);
//        cust_table.addCell(c3);
//        Paragraph c4 = new Paragraph("ΔΟΥ: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getTaxOffice(),urFontName);
//        cust_table.addCell(c4);
//        Paragraph c5 = new Paragraph("ΧΩΡΑ: " + iliadisDatabase.daoAccess().getCountry(iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCountry()).getCountry(),urFontName);
//        cust_table.addCell(c5);
//
//        all_cust_table.addCell(cust_table);
//        all_cust_table.addCell("");
//
//        //Dieuthinsi pelati
//        PdfPTable addr_table = new PdfPTable(1);
//        addr_table.setWidthPercentage(100);
//        addr_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
//        if(Integer.parseInt(shopId) == 0) {
//            Paragraph f1 = new Paragraph("ΠΟΛΗ: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCity(),urFontName);
//            addr_table.addCell(f1);
//        }else{
//            Paragraph f1 = new Paragraph("ΠΟΛΗ: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCity(),urFontName);
//            addr_table.addCell(f1);
//        }
//        if(Integer.parseInt(shopId) == 0) {
//            Paragraph f2 = new Paragraph("ΔΙΕΥΘΥΝΣΗ: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getAddress(),urFontName);
//            addr_table.addCell(f2);
//        }else{
//            Paragraph f2 = new Paragraph("ΔΙΕΥΘΥΝΣΗ: " + iliadisDatabase.daoAccess().getShopsByShopid(shopId).getAddress(),urFontName);
//            addr_table.addCell(f2);
//        }
//        if(Integer.parseInt(shopId) == 0) {
//            Paragraph f3 = new Paragraph("Τ.Κ: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getPostalCode(),urFontName);
//            addr_table.addCell(f3);
//        }else{
//            Paragraph f3 = new Paragraph("Τ.Κ: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getPostalCode(),urFontName);
//            addr_table.addCell(f3);
//        }
//        Paragraph f4 = new Paragraph("ΤΗΛΕΦΩΝΟ: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getPhone(),urFontName);
//        addr_table.addCell(f4);
//        Paragraph f5 = new Paragraph("EMAIL: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getEmail(),urFontName);
//        addr_table.addCell(f5);
//
//        all_cust_table.addCell(addr_table);
//        doc.add(all_cust_table);
//
//        doc.add( Chunk.NEWLINE );
//        doc.add( Chunk.NEWLINE );
//
//        PdfPTable prod_lb_table = new PdfPTable(new float[] { 150f, 600f, /*350f,*/ 150f, 150f, 150f });
//        prod_lb_table.setWidthPercentage(100);
//
//        Paragraph lb = new Paragraph("ΚΩΔΙΚΟΣ",urFontName);
//        prod_lb_table.addCell(lb);
//        Paragraph lb2 = new Paragraph("ΠΕΡΙΓΡΑΦΗ",urFontName);
//        prod_lb_table.addCell(lb2);
////        Paragraph lb3 = new Paragraph("ΣΧΟΛΙΟ",urFontName);
////        prod_lb_table.addCell(lb3);
//        Paragraph lb4 = new Paragraph("ΠΟΣΟΤΗΤΑ",urFontName);
//        prod_lb_table.addCell(lb4);
//        Paragraph lb5 = new Paragraph("ΤΙΜΗ",urFontName);
//        prod_lb_table.addCell(lb5);
//        Paragraph lb6 = new Paragraph("ΣΥΝΟΛΙΚΟ ΠΟΣΟ",urFontName);
//        prod_lb_table.addCell(lb6);
//
//        doc.add(prod_lb_table);
//
//        PdfPTable prod_table = new PdfPTable(new float[] { 150f, 600f, /*350f,*/ 150f, 150f, 150f });
//        prod_table.setWidthPercentage(100);
//        prod_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
//
//        for(int i = 0; i < carts.size(); i++){
//            Log.d("Dimitra","passssss");
//            prod_table.addCell(new PdfPCell(new Phrase(carts.get(i).getRealcode(),urFontName)));
//            prod_table.addCell(new PdfPCell(new Phrase(carts.get(i).getDescription()+"\n"+carts.get(i).getComment(),urFontName)));
//            //prod_table.addCell(new PdfPCell(new Phrase("" + carts.get(i).getComment(),urFontName)));
//            prod_table.addCell(new PdfPCell(new Phrase("" + carts.get(i).getQuantity(),urFontName)));
//            Catalog catalog = iliadisDatabase.daoAccess().getCatalogueDiscount(iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCatalogueid(),iliadisDatabase.daoAccess().getProductByProdCode(carts.get(i).getProdcode()).getPriceid());
//            prod_table.addCell(new PdfPCell(new Phrase("" + new DecimalFormat("##.####").format(getProductPrice(Double.parseDouble(iliadisDatabase.daoAccess().getProductByProdCode(carts.get(i).getProdcode()).getPrice().replace(",",".")),catalog.getDiscount1())),urFontName)));
//            prod_table.addCell(new PdfPCell(new Phrase(""+new DecimalFormat("##.####").format(Double.parseDouble(carts.get(i).getPrice())),urFontName)));
//        }
//        doc.add(prod_table);
//
//        doc.add( Chunk.NEWLINE );
//        doc.add( Chunk.NEWLINE );
//
//        PdfPTable aSum_table = new PdfPTable(3);
//        aSum_table.setWidthPercentage(100);
//        aSum_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
//
//        PdfPTable sum_table = new PdfPTable(1);
//        //sum_table.setWidthPercentage(100);
//        sum_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
//
//        PdfPCell s = new PdfPCell(new Phrase("ΣΥΝΟΛΟ ΠΟΣΟΤΗΤΑΣ: " + new DecimalFormat("##.####").format(prodSum),urFontName));
//        s.setBorder(Rectangle.NO_BORDER);
//        s.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        sum_table.addCell(s);
//        //sum_table.addCell("" + new DecimalFormat("##.##").format(prodSum));
//
//        PdfPCell s1 = new PdfPCell(new Phrase("ΚΑΘΑΡΗ ΑΞΙΑ: " + new DecimalFormat("##.####").format(totalprice)  + "€",urFontName));
//        s1.setBorder(Rectangle.NO_BORDER);
//        s1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        sum_table.addCell(s1);
//        //sum_table.addCell("" + new DecimalFormat("##.##").format(priceSum));
//
////        PdfPCell s2 = new PdfPCell(new Phrase("ΕΚΠΤΩΣΗ: " + new DecimalFormat("##.##").format(sPriceSum-priceSum) + "€",urFontName));
////        s2.setBorder(Rectangle.NO_BORDER);
////        s2.setHorizontalAlignment(Element.ALIGN_RIGHT);
////        sum_table.addCell(s2);
//        //sum_table.addCell("" + new DecimalFormat("##.##").format((priceSum - sPriceSum)));
//
//        PdfPCell s3 = new PdfPCell(new Phrase("ΠΟΣΟ ΜΕΤΑ ΤΗΝ ΕΚΠΤΩΣΗ: " + new DecimalFormat("##.####").format(priceSum) + "€",urFontName));
//        s3.setBorder(Rectangle.NO_BORDER);
//        s3.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        sum_table.addCell(s3);
//        //sum_table.addCell("" + new DecimalFormat("##.##").format(sPriceSum));
//
//        PdfPCell s4 = new PdfPCell(new Phrase("ΦΠΑ.: " + new DecimalFormat("##.####").format(vTotal) + "€",urFontName));
//        s4.setBorder(Rectangle.NO_BORDER);
//        s4.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        sum_table.addCell(s4);
//
//        PdfPCell s5 = new PdfPCell(new Phrase("ΣΥΝΟΛΙΚΗ ΑΞΙΑ: " + new DecimalFormat("##.####").format((priceSum + vTotal)) + "€",urFontName));
//        s5.setBorder(Rectangle.NO_BORDER);
//        s5.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        sum_table.addCell(s5);
//
//        aSum_table.addCell("");
//        aSum_table.addCell("");
//        aSum_table.addCell(sum_table);
//        doc.add(aSum_table);
//
//        doc.add( Chunk.NEWLINE );
//        doc.add( Chunk.NEWLINE );
//        doc.add( Chunk.NEWLINE );
//        doc.add( Chunk.NEWLINE );
//
//        PdfPTable com_table = new PdfPTable(new float[] { 600f, 150f, 150f });
//        com_table.setWidthPercentage(100);
//        com_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
//        PdfPCell com = new PdfPCell(new Phrase("Παρατηρήσεις"+"\n"+mymessage,urFontName));
//        com.setBorder(Rectangle.NO_BORDER);
//        com_table.addCell(com);
//        PdfPCell com1 = new PdfPCell(new Phrase("Ο Εκδότης",urFontName));
//        com1.setBorder(Rectangle.NO_BORDER);
//        com_table.addCell(com1);
//        PdfPCell com2 = new PdfPCell(new Phrase("Ο παραλαβών",urFontName));
//        com2.setBorder(Rectangle.NO_BORDER);
//        com_table.addCell(com2);
//
//        doc.add(com_table);
//        doc.close();
//
//    }
//
//    public void createPdfFileEn(List<Cart> carts, String custid, String custvatid, String number, String shopId, IliadisDatabase iliadisDatabase,Context context, String mymessage,int prodSum,
//            double priceSum,
//            double totalprice,
//            double vTotal) throws IOException, DocumentException
//    {
//        AssetManager assetManager = context.getAssets();
//        InputStream is = null;
//        try {
//            is = assetManager.open("pdf_image_en.png");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Bitmap bitmap = BitmapFactory.decodeStream(is);
//        bitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true);
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] bitmapdata = stream.toByteArray();
//        Image image;
//
//        File root =new File(Environment.getExternalStorageDirectory(),"Pdf file");
//        if (!root.exists())
//        {
//            root.mkdir();
//        }
//        File pdffile = new File(root,"order.pdf");
//
//        Document doc = new Document();
//        doc.setPageSize(PageSize.A4);
//        FileOutputStream fileOutputStream = new FileOutputStream(pdffile);
//        PdfWriter.getInstance(doc, fileOutputStream);
//        //open the document
//        doc.open();
//
//        BaseFont bfTimes = null;
//        try {
//            bfTimes = BaseFont.createFont("assets/arial.ttf",BaseFont.IDENTITY_H,BaseFont.EMBEDDED);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        Font urFontName = new Font(bfTimes, 8, Font.NORMAL);
//        Font titlefont = new Font(bfTimes,18, Font.BOLD, BaseColor.WHITE);
//
//        PdfPTable del_table = new PdfPTable(3);
//        del_table.setWidthPercentage(100);
//        PdfPCell cell = new PdfPCell();
//
//        int indentation = 0;
//        image = Image.getInstance(stream.toByteArray());
//
//        float scaler = ((doc.getPageSize().getWidth() - doc.leftMargin()
//                - doc.rightMargin() - indentation) / image.getWidth()) * 100;
//        image.scalePercent(scaler);
//        image.setAlignment(Element.ALIGN_LEFT);
//
//        //Logo header
//        doc.add(image);
//        doc.add( Chunk.NEWLINE );
//        doc.add( Chunk.NEWLINE );
//
//        //Stoixeia paraggeleias
//        del_table.addCell(new Paragraph("ID ORDER",urFontName));
//        del_table.addCell(new Paragraph("DATE: ",urFontName));
//        del_table.addCell(new Paragraph("SALESMAN: ",urFontName));
//        del_table.addCell(new Paragraph(""+carts.get(0).getOrderid(),urFontName));
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
//        String currentDateandTime = sdf.format(new Date());
//        del_table.addCell(new Paragraph(""+currentDateandTime,urFontName));
//        del_table.addCell(new Paragraph(""+number,urFontName));
//        doc.add(del_table);
//
//        doc.add( Chunk.NEWLINE );
//        doc.add( Chunk.NEWLINE );
//
//        PdfPTable all_cust_table = new PdfPTable(3);
//        all_cust_table.setWidthPercentage(100);
//        all_cust_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
//
//        //stoixeia pelati
//        PdfPTable cust_table = new PdfPTable(1);
//        cust_table.setWidthPercentage(100);
//        cust_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
//        Paragraph c = new Paragraph("CUSTOMER",urFontName);
//        cust_table.addCell(c);
//        Paragraph c1 = new Paragraph("ID CUSTOMER: " + custid,urFontName);
//        cust_table.addCell(c1);
//        Paragraph c2 = new Paragraph("NAME: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCompanyName(),urFontName);
//        cust_table.addCell(c2);
//        Paragraph c3 = new Paragraph("VAT.: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getAfm(),urFontName);
//        cust_table.addCell(c3);
//        Paragraph c4 = new Paragraph("TAX OFFICE: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getTaxOffice(),urFontName);
//        cust_table.addCell(c4);
//        Paragraph c5 = new Paragraph("COUNTRY: " + iliadisDatabase.daoAccess().getCountry(iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCountry()).getCountry(),urFontName);
//        cust_table.addCell(c5);
//
//        all_cust_table.addCell(cust_table);
//        all_cust_table.addCell("");
//
//        //Dieuthinsi pelati
//        PdfPTable addr_table = new PdfPTable(1);
//        addr_table.setWidthPercentage(100);
//        addr_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
//        if(Integer.parseInt(shopId) == 0) {
//            Paragraph f1 = new Paragraph("CITY: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCity(),urFontName);
//            addr_table.addCell(f1);
//        }else{
//            Paragraph f1 = new Paragraph("CITY: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCity(),urFontName);
//            addr_table.addCell(f1);
//        }
//        if(Integer.parseInt(shopId) == 0) {
//            Paragraph f2 = new Paragraph("ADDRESS: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getAddress(),urFontName);
//            addr_table.addCell(f2);
//        }else{
//            Paragraph f2 = new Paragraph("ADDRESS: " + iliadisDatabase.daoAccess().getShopsByShopid(shopId).getAddress(),urFontName);
//            addr_table.addCell(f2);
//        }
//        if(Integer.parseInt(shopId) == 0) {
//            Paragraph f3 = new Paragraph("POSTAL CODE: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getPostalCode(),urFontName);
//            addr_table.addCell(f3);
//        }else{
//            Paragraph f3 = new Paragraph("POSTAL CODE: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getPostalCode(),urFontName);
//            addr_table.addCell(f3);
//        }
//        Paragraph f4 = new Paragraph("PHONE: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getPhone(),urFontName);
//        addr_table.addCell(f4);
//        Paragraph f5 = new Paragraph("EMAIL: " + iliadisDatabase.daoAccess().getCustomerByCustid(custid).getEmail(),urFontName);
//        addr_table.addCell(f5);
//
//        all_cust_table.addCell(addr_table);
//        doc.add(all_cust_table);
//
//        doc.add( Chunk.NEWLINE );
//        doc.add( Chunk.NEWLINE );
//
//        PdfPTable prod_lb_table = new PdfPTable(new float[] { 150f, 600f, /*350f,*/ 150f, 150f, 150f });
//        prod_lb_table.setWidthPercentage(100);
//
//        Paragraph lb = new Paragraph("CODE",urFontName);
//        prod_lb_table.addCell(lb);
//        Paragraph lb2 = new Paragraph("DESCRIPTION",urFontName);
//        prod_lb_table.addCell(lb2);
////        Paragraph lb3 = new Paragraph("ΣΧΟΛΙΟ",urFontName);
////        prod_lb_table.addCell(lb3);
//        Paragraph lb4 = new Paragraph("QUANTITY",urFontName);
//        prod_lb_table.addCell(lb4);
//        Paragraph lb5 = new Paragraph("PRICE",urFontName);
//        prod_lb_table.addCell(lb5);
//        Paragraph lb6 = new Paragraph("TOTAL PRICE",urFontName);
//        prod_lb_table.addCell(lb6);
//
//        doc.add(prod_lb_table);
//
//        PdfPTable prod_table = new PdfPTable(new float[] { 150f, 600f, /*350f,*/ 150f, 150f, 150f });
//        prod_table.setWidthPercentage(100);
//        prod_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
//
//        for(int i = 0; i < carts.size(); i++){
//            prod_table.addCell(new PdfPCell(new Phrase(carts.get(i).getRealcode(),urFontName)));
//            prod_table.addCell(new PdfPCell(new Phrase(iliadisDatabase.daoAccess().getProductByRealCode(carts.get(i).getRealcode()).getProdescriptionEn()+"\n"+carts.get(i).getComment(),urFontName)));
//            prod_table.addCell(new PdfPCell(new Phrase("" + carts.get(i).getQuantity(),urFontName)));
//            Catalog catalog = iliadisDatabase.daoAccess().getCatalogueDiscount(iliadisDatabase.daoAccess().getCustomerByCustid(custid).getCatalogueid(),iliadisDatabase.daoAccess().getProductByProdCode(carts.get(i).getProdcode()).getPriceid());
//            prod_table.addCell(new PdfPCell(new Phrase("" + new DecimalFormat("##.##").format(getProductPrice(Double.parseDouble(iliadisDatabase.daoAccess().getProductByProdCode(carts.get(i).getProdcode()).getPrice().replace(",",".")),catalog.getDiscount1())),urFontName)));
//            prod_table.addCell(new PdfPCell(new Phrase("" + (new DecimalFormat("##.##").format(Double.parseDouble(carts.get(i).getPrice()))),urFontName)));
//        }
//        doc.add(prod_table);
//
//        doc.add( Chunk.NEWLINE );
//        doc.add( Chunk.NEWLINE );
//
//        PdfPTable aSum_table = new PdfPTable(3);
//        aSum_table.setWidthPercentage(100);
//        aSum_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
//
//        PdfPTable sum_table = new PdfPTable(1);
//        //sum_table.setWidthPercentage(100);
//        sum_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
//
//
//        PdfPCell s = new PdfPCell(new Phrase("TOTAL QUANTITY: " + new DecimalFormat("##.####").format(prodSum),urFontName));
//        s.setBorder(Rectangle.NO_BORDER);
//        s.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        sum_table.addCell(s);
//        //sum_table.addCell("" + new DecimalFormat("##.##").format(prodSum));
//
//        PdfPCell s1 = new PdfPCell(new Phrase("ORDER AMOUNT: " + new DecimalFormat("##.####").format(totalprice)  + "€",urFontName));
//        s1.setBorder(Rectangle.NO_BORDER);
//        s1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        sum_table.addCell(s1);
//        //sum_table.addCell("" + new DecimalFormat("##.##").format(priceSum));
//
////        PdfPCell s2 = new PdfPCell(new Phrase("DISCOUNT: " + new DecimalFormat("##.##").format(sPriceSum-priceSum) + "€",urFontName));
////        s2.setBorder(Rectangle.NO_BORDER);
////        s2.setHorizontalAlignment(Element.ALIGN_RIGHT);
////        sum_table.addCell(s2);
//        //sum_table.addCell("" + new DecimalFormat("##.##").format((priceSum - sPriceSum)));
//
//        PdfPCell s3 = new PdfPCell(new Phrase("AMOUNT AFTER DISCOUNT: " + new DecimalFormat("##.####").format(priceSum) + "€",urFontName));
//        s3.setBorder(Rectangle.NO_BORDER);
//        s3.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        sum_table.addCell(s3);
//        //sum_table.addCell("" + new DecimalFormat("##.##").format(sPriceSum));
//
////        PdfPCell s4 = new PdfPCell(new Phrase("COST VAT.: " + new DecimalFormat("##.##").format(vTotal) + "€",urFontName));
////        s4.setBorder(Rectangle.NO_BORDER);
////        s4.setHorizontalAlignment(Element.ALIGN_RIGHT);
////        sum_table.addCell(s4);
//
//        PdfPCell s5 = new PdfPCell(new Phrase("TOTAL AMOUNT: " + new DecimalFormat("##.####").format((priceSum + vTotal)) + "€",urFontName));
//        s5.setBorder(Rectangle.NO_BORDER);
//        s5.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        sum_table.addCell(s5);
//
//        aSum_table.addCell("");
//        aSum_table.addCell("");
//        aSum_table.addCell(sum_table);
//        doc.add(aSum_table);
//
//        doc.add( Chunk.NEWLINE );
//        doc.add( Chunk.NEWLINE );
//
//
//        PdfPTable com_table = new PdfPTable(new float[] { 600f, 150f, 150f });
//        com_table.setWidthPercentage(100);
//        com_table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
//        PdfPCell com = new PdfPCell(new Phrase("Comments"+"\n"+mymessage,urFontName));
//
//        //PdfPCell com = new PdfPCell(new Phrase("Παρατηρήσεις"+"\n"+mymessage,urFontName));
//        com.setBorder(Rectangle.NO_BORDER);
//        com_table.addCell(com);
//        PdfPCell com1 = new PdfPCell(new Phrase("Productor",urFontName));
//        com1.setBorder(Rectangle.NO_BORDER);
//        com_table.addCell(com1);
//        PdfPCell com2 = new PdfPCell(new Phrase("Delivered",urFontName));
//        com2.setBorder(Rectangle.NO_BORDER);
//        com_table.addCell(com2);
//
//        doc.add(com_table);
//        doc.close();
//
//    }

    public static String  printPdf(String ipprinter,Context context) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        final String SUCCESSFULLY_SENT = "Successfully sent to printer";
        String result="";
        DataOutputStream outToServer = null;
        InputStream is = null;
        Socket clientSocket;
        File root =new File(Environment.getExternalStorageDirectory(),"Pdf file");
        File pdffile = new File(root,"order.pdf");

        try {
            FileInputStream fileInputStream = new FileInputStream(pdffile.getAbsolutePath());
            is =fileInputStream;
            clientSocket = new Socket(ipprinter, 9100);
            outToServer = new DataOutputStream(clientSocket.getOutputStream());
            int i;
            while ((i=is.read()) !=-1){
                outToServer.write(i);
            }
            outToServer.flush();
            result = SUCCESSFULLY_SENT;
        }catch (ConnectException connectException){
            Log.d("Dimitra", connectException.toString(), connectException);
            result = connectException.toString();
        }
        catch (UnknownHostException e1) {
            Log.e("Dimitra", e1.toString(), e1);
            result = e1.toString();
        } catch (IOException e1) {
            Log.e("Dimitra", e1.toString(), e1);
            result = e1.toString();
        }
        finally{
            try {
                if (outToServer!=null){
                    outToServer.close();
                }
                if (is!=null){
                    is.close();
                }
            }catch (IOException ioException){
                result = ioException.toString();
            }
        }
        return result;
    }


    public void createDialog(String message,Context context)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle("");

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void DialogBackbutton(String message, final Context context)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle("");

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
