package com.johnyjuice.juicyeet;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class Pridej_zbozi_seznam extends AppCompatActivity {

    public final static String KEY_EXTRA_CONTACT_ID = "KEY_EXTRA_CONTACT_ID";

    private ListView listView;
    CenikDBHelper dbHelper;
    UctenkaDBHelper dbHelper2;
    int mnozstvi= 0;
    float celkovaCastka=0;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pridej_zbozi_seznam);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new CenikDBHelper(this);
        dbHelper2 = new UctenkaDBHelper(this);
        final Cursor cursor = dbHelper.vyberVsechnoZbozi();
        String [] columns = new String[] {
                CenikDBHelper.CENIK_COLUMN_ID,
                CenikDBHelper.CENIK_COLUMN_ZBOZI,
                CenikDBHelper.CENIK_COLUMN_CENA
        };
        int [] widgets = new int[] {
                R.id.zboziID,
                R.id.jmeno_Zbozi,
                R.id.cena_Zbozi
        };

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.cenik_info,
                cursor, columns, widgets, 0);
        listView = (ListView)findViewById(R.id.listView2);
        listView.setAdapter(cursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView listView, View view,
                                    int position, long id) {
                Cursor itemCursor = (Cursor) Pridej_zbozi_seznam.this.listView.getItemAtPosition(position);
                int zboziID = itemCursor.getInt(itemCursor.getColumnIndex(CenikDBHelper.CENIK_COLUMN_ID));

                Cursor rs = dbHelper.vyberZbozi(zboziID);
                rs.moveToFirst();
                final String nazevZbozi = rs.getString(rs.getColumnIndex(CenikDBHelper.CENIK_COLUMN_ZBOZI    ));
                final float cenaZbozi = rs.getFloat(rs.getColumnIndex(CenikDBHelper.CENIK_COLUMN_CENA));


                View view1 = (LayoutInflater.from(Pridej_zbozi_seznam.this)).inflate(R.layout.vloz_mnozstvi,null);
                AlertDialog.Builder alert = new AlertDialog.Builder(Pridej_zbozi_seznam.this);
                alert.setTitle("Množství");
                alert.setMessage("Zadejte množství");

                final EditText input = (EditText) view1.findViewById(R.id.input_Mnozstvi);
                input.setText("1");
                input.setSelectAllOnFocus(true);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setRawInputType(Configuration.KEYBOARD_12KEY);
                alert.setView(view1);
                input.setText("1");
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mnozstvi = Integer.parseInt(input.getText().toString());
                        celkovaCastka = mnozstvi*cenaZbozi;
                        dbHelper2.vlozZbozi(nazevZbozi,cenaZbozi,mnozstvi,celkovaCastka);
                        Intent intent =new Intent(getApplicationContext(), Pokladna_menu.class);
                        startActivity(intent);

                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        onBackPressed();
                    }
                });
                alert.show();





            }
        });

    }


}
