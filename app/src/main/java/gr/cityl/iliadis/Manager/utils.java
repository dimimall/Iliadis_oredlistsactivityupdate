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
import java.util.List;
import java.util.Locale;

import gr.cityl.iliadis.Models.Cart;

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

    public String createCsvFile(List<Cart> carts){

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
        File txtfile = new File(fileDir,"order.txt");
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
                for (int i=0; i<carts.size(); i++)
                {
                    bfWriter.write(carts.get(i).getRealcode());
                    bfWriter.write("|");
                    bfWriter.write(carts.get(i).getDescription());
                    bfWriter.write("|");
                    bfWriter.write(carts.get(i).getComment());
                    bfWriter.write("|");
                    bfWriter.write(carts.get(i).getQuantity());
                    bfWriter.write("|");
                    bfWriter.write(carts.get(i).getPriceid());
                    bfWriter.write("|");
                    bfWriter.write(carts.get(i).getPrice());
                    bfWriter.write("||");
                }
                bfWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String success="";

        String url = "https://pod.iliadis.com.gr/UploadFile.php";
        File filecsv = new File(fileDir,"order.txt");
        if(filecsv.exists()) {
            try {
                Log.d("Dimitra",filecsv.getAbsolutePath());
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
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
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
