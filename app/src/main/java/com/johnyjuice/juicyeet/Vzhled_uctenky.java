package com.johnyjuice.juicyeet;

import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Vzhled_uctenky extends AppCompatActivity {

    String hlavickaNazev;
    String hlavickaDal;
    String hlavickaDal1;
    String hlavickaDal2;

    EditText EdithlavickaNazev;
    EditText EdithlavickaDal;
    EditText EdithlavickaDal1;
    EditText EdithlavickaDal2;

    Button ulozvzhled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vzhled_uctenky);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ulozvzhled = (Button) findViewById(R.id.button9);
        ulozvzhled.setOnClickListener(myhandler1);

        EdithlavickaNazev = (EditText) findViewById(R.id.editHlNaz);
        EdithlavickaDal = (EditText) findViewById(R.id.editHldal);
        EdithlavickaDal1 = (EditText) findViewById(R.id.editHldal1);
        EdithlavickaDal2 = (EditText) findViewById(R.id.editHldal2);

        hlavickaNazev = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("HlavickaNazev", "Empty");
        if (hlavickaNazev != "Empty")
            EdithlavickaNazev.setText(hlavickaNazev);


        hlavickaDal = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("HlavickaDal", "Empty");
        if (hlavickaDal != "Empty")
            EdithlavickaDal.setText(hlavickaDal);


        hlavickaDal1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("HlavickaDal1", "Empty");
        if (hlavickaDal1 != "Empty")
            EdithlavickaDal1.setText(hlavickaDal1);


        hlavickaDal2 = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("HlavickaDal2", "Empty");
        if (hlavickaDal2 != "Empty")
            EdithlavickaDal2.setText(hlavickaDal2);


    }
    View.OnClickListener myhandler1 = new View.OnClickListener() {
        public void onClick(View v) {


        PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("HlavickaNazev", EdithlavickaNazev.getText().toString()).commit();
        PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("HlavickaDal", EdithlavickaDal.getText().toString()).commit();
        PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("HlavickaDal1", EdithlavickaDal1.getText().toString()).commit();
        PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("HlavickaDal2", EdithlavickaDal2.getText().toString()).commit();
        Toast.makeText(getApplicationContext(), "Změna provedena", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }};
}
