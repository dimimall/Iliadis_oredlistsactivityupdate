package gr.cityl.iliadis.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gr.cityl.iliadis.Manager.utils;
import gr.cityl.iliadis.R;

public class SettingsActivity extends AppCompatActivity {

    private Spinner spinnerlang;
    private EditText ipprint,numsale,ipserver;
    private utils myutils;
    private ArrayAdapter arrayAdapter;
    private Configuration configuration;
    private TextView language,print,salesmam;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //getSupportActionBar().setTitle(getString(R.string.action_settings));
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

        final List<Language> language = new ArrayList<>();
        language.add(new Language("el","GR"));
        language.add(new Language("en","US"));

        arrayAdapter = new ArrayAdapter(SettingsActivity.this,android.R.layout.simple_dropdown_item_1line);
        arrayAdapter.add("Ελληνικά");
        arrayAdapter.add("English");
        spinnerlang.setAdapter(arrayAdapter);
        spinnerlang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).equals("Ελληνικά")){
                   configuration = myutils.changeLocaleApp(language.get(0).getKey(), language.get(0).getLang(), SettingsActivity.this);
                    onConfigurationChanged(configuration);
                    SharedPreferences.Editor editor = myutils.sharedpreferences.edit();
                    editor.putBoolean("language",false);
                    editor.commit();
                }
                else {
                   configuration = myutils.changeLocaleApp(language.get(1).getKey(),language.get(1).getLang(),SettingsActivity.this);
                    onConfigurationChanged(configuration);
                    SharedPreferences.Editor editor = myutils.sharedpreferences.edit();
                    editor.putBoolean("language",true);
                    editor.commit();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ipprint.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                        keyCode == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                {
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
                    SharedPreferences.Editor editor = myutils.sharedpreferences.edit();
                    editor.putString("ipserver",ipserver.getText().toString());
                    editor.commit();
                    Intent intent = new Intent(SettingsActivity.this,MainActivity.class);
                    startActivity(intent);
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
        language = (TextView)findViewById(R.id.textView3);
        print = (TextView)findViewById(R.id.textView4);
        salesmam=(TextView)findViewById(R.id.textView5);
        spinnerlang = (Spinner)findViewById(R.id.spinner);
        ipprint = (EditText)findViewById(R.id.editText);
        numsale = (EditText)findViewById(R.id.editText2);
        ipserver = (EditText)findViewById(R.id.editText3);
    }

    public class Language
    {
        String key;
        String lang;

        public Language(String key,String lang)
        {
            this.key=key;
            this.lang=lang;
        }

        public String getKey() {
            return key;
        }

        public String getLang() {
            return lang;
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        getSupportActionBar().setTitle(getString(R.string.action_settings));
        language.setText(getString(R.string.language));
        print.setText(getString(R.string.print));
        salesmam.setText(getString(R.string.numsales));
        super.onConfigurationChanged(newConfig);
    }
}
