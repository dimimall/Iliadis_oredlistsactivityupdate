package gr.cityl.iliadis.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Spinner;

import gr.cityl.iliadis.Manager.utils;
import gr.cityl.iliadis.R;

public class SettingsActivity extends AppCompatActivity {

    Spinner spinnerlang;
    EditText ipprint,numsale,ipserver;
    utils myutils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Ρυθμίσεις");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
        myutils= new utils();

        myutils.sharedpreferences = getSharedPreferences(myutils.MyPREFERENCES, Context.MODE_PRIVATE);
        String number = myutils.sharedpreferences.getString("numsale", "");
        String ipprintpref = myutils.sharedpreferences.getString("ipprint", "");
        String ipserverpref = myutils.sharedpreferences.getString("ipserver", "");
        ipprint.setText(ipprintpref);
        numsale.setText(number);
        ipserver.setText(ipserverpref);

        ipprint.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                        keyCode == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                {
                    Log.d("Dimitra","passs");
                    SharedPreferences.Editor editor = myutils.sharedpreferences.edit();
                    editor.putString("ipprint",ipprint.getText().toString());
                    editor.commit();
                    return true;
                }
                return false;
            }
        });

        numsale.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                        keyCode == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                {
                    Log.d("Dimitra","passs");
                    SharedPreferences.Editor editor = myutils.sharedpreferences.edit();
                    editor.putString("numsale",numsale.getText().toString());
                    editor.commit();
                    return true;
                }
                return false;
            }
        });

        ipserver.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                        keyCode == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                {
                    Log.d("Dimitra","passs");
                    SharedPreferences.Editor editor = myutils.sharedpreferences.edit();
                    editor.putString("ipserver",ipserver.getText().toString());
                    editor.commit();
                    return true;
                }
                return false;
            }
        });
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

    public void init()
    {
        spinnerlang = (Spinner)findViewById(R.id.spinner);
        ipprint = (EditText)findViewById(R.id.editText);
        numsale = (EditText)findViewById(R.id.editText2);
        ipserver = (EditText)findViewById(R.id.editText3);
    }
}
