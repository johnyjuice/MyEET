package com.johnyjuice.juicyeet;

import android.Manifest;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    private static final String LOGTAG="MainActivity";
    public static final String PREFERENCE_TEST_MODE = "test-mode";
    public static final String PREFERENCE_PLAYGROUND_MODE = "playground-mode";
    public static final String PREFERENCE_DIC = "dic";
    public static final String PREFERENCE_CHANGED_TIME = "changed-time";
    public static final String PREFERENCE_FILE = "APP";

    Button pokladna;
    Button cenik;
    Button nastaveni;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        pokladna = (Button) findViewById(R.id.button);
        pokladna.setOnClickListener(myhandler1);

        cenik = (Button) findViewById(R.id.button2);
        cenik.setOnClickListener(myhandler2);

        nastaveni = (Button) findViewById(R.id.button3);
        nastaveni.setOnClickListener(myhandler3);
          }
    View.OnClickListener myhandler1 = new View.OnClickListener() {
        public void onClick(View v) {
        Intent intent =new Intent(getApplicationContext(), Pokladna_menu.class);
        startActivity(intent);
        }
    };
    View.OnClickListener myhandler2 = new View.OnClickListener() {
        public void onClick(View v) {
        Intent intent =new Intent(getApplicationContext(), Cenik.class);
        startActivity(intent);
        }
    };
    View.OnClickListener myhandler3 = new View.OnClickListener() {
        public void onClick(View v) {
        Intent intent =new Intent(getApplicationContext(), Nastaveni_menu.class);
        startActivity(intent);
        }
    };


}
