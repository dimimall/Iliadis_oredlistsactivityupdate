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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
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

        String result="";

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

        String url = "https://pod.iliadis.com.gr/UploadFile.php";
        File filecsv = new File(fileDir,"order.txt");
        if(filecsv.exists()) {
            try {
                Log.d("Dimitra",filecsv.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
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
}
