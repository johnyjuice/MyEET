package com.johnyjuice.juicyeet;

import android.Manifest;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Nastaveni_menu extends AppCompatActivity {

    Button obecne;
    Button certifikat;
    Button tiskarna;
    Button uctenka;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nastaveni);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActivityCompat.requestPermissions(Nastaveni_menu.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);

        if ( ActivityCompat.checkSelfPermission(Nastaveni_menu.this,Manifest.permission.READ_EXTERNAL_STORAGE) == -1){
            onBackPressed();
        }

        obecne = (Button) findViewById(R.id.button8);
        obecne.setOnClickListener(myhandler1);

        certifikat = (Button) findViewById(R.id.button7);
        certifikat.setOnClickListener(myhandler2);

        tiskarna = (Button) findViewById(R.id.tiskarna);
        tiskarna.setOnClickListener(myhandler3);

        uctenka = (Button) findViewById(R.id.nastaTisk);
        uctenka.setOnClickListener(myhandler4);

    }
    View.OnClickListener myhandler1 = new View.OnClickListener() {
        public void onClick(View v) {
        Intent intent =new Intent(getApplicationContext(), Nastaveni_obecne.class);
        startActivity(intent);
    }
    };
        View.OnClickListener myhandler2 = new View.OnClickListener() {
            public void onClick(View v) {
        Intent intent =new Intent(getApplicationContext(), CertificateImport.class);
        startActivity(intent);
    }
        };
            View.OnClickListener myhandler3 = new View.OnClickListener() {
                public void onClick(View v) {
        Intent intent =new Intent(getApplicationContext(), Tiskarna_activity.class);
        startActivity(intent);
    }
            };
                View.OnClickListener myhandler4 = new View.OnClickListener() {
                    public void onClick(View v) {
        Intent intent =new Intent(getApplicationContext(), Vzhled_uctenky.class);
        startActivity(intent);
    }
                };
}
