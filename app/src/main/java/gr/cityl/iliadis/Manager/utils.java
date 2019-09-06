package gr.cityl.iliadis.Manager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;


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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import gr.cityl.iliadis.Models.Cart;
import gr.cityl.iliadis.Models.Customers;

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


    public static void changeLocaleApp(String localeLoad, Context context)
    {
        Locale locale = new Locale(localeLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());

    }

    public String createCsvFile(List<Cart> carts, String salesid, String custid, int orderid, String customerid, String custvatid, String paymentid, String shopid, Customers customer, int custcatid){

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
        File txtfile = new File(fileDir,"order_iliadis.txt");
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
                bfWriter.write(orderid);
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
                    bfWriter.write(carts.get(i).getQuantity());
                    bfWriter.newLine();
                    bfWriter.write(carts.get(i).getVatcode());
                    bfWriter.newLine();
                    bfWriter.write(custcatid);
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

        String url = "https://pod.iliadis.com.gr/UploadFile.php";
        File filecsv = new File(fileDir,"order_iliadis.txt");
        if(filecsv.exists()) {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);

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
}
