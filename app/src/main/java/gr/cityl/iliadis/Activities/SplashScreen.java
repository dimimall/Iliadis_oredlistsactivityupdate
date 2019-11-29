package gr.cityl.iliadis.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import gr.cityl.iliadis.Manager.utils;
import gr.cityl.iliadis.R;

public class SplashScreen extends AppCompatActivity {

    ProgressBar progressBar;
    int progressStatus = 0;
    Handler handler = new Handler();
    utils myutils;
    String ipserverpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        progressBar=(ProgressBar)findViewById(R.id.progressBar1);

        myutils = new utils();

        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100)
                {
                    progressStatus += 5;
                    handler.post(new Runnable()
                    {
                        public void run()
                        {
                            progressBar.setProgress(progressStatus);
                        }
                    });
                    try
                    {
                        Thread.sleep(200);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                if (progressStatus==100)
                {
                    myutils.sharedpreferences = getSharedPreferences(myutils.MyPREFERENCES, Context.MODE_PRIVATE);
                    ipserverpref = myutils.sharedpreferences.getString("ipserver", "");
                    if (ipserverpref.equals(""))
                    {
                        Intent intent = new Intent(SplashScreen.this,SettingsActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        }).start();
    }
}
