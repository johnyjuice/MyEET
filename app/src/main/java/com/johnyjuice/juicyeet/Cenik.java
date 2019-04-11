package com.johnyjuice.juicyeet;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class Cenik extends AppCompatActivity {

    public final static String KEY_EXTRA_CONTACT_ID = "KEY_EXTRA_CONTACT_ID";

    private ListView listView;
    CenikDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cenik);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActivityCompat.requestPermissions(Cenik.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);

        if ( ActivityCompat.checkSelfPermission(Cenik.this,Manifest.permission.READ_EXTERNAL_STORAGE) == -1){
            onBackPressed();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Cenik.this, Vytvor_Uprav_Cenik.class);
                intent.putExtra(KEY_EXTRA_CONTACT_ID, 0);
                startActivity(intent);
            }
        });
        dbHelper = new CenikDBHelper(this);

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
        listView = (ListView)findViewById(R.id.listView1);
        listView.setAdapter(cursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView listView, View view,
                                    int position, long id) {
                Cursor itemCursor = (Cursor) Cenik.this.listView.getItemAtPosition(position);
                int zboziID = itemCursor.getInt(itemCursor.getColumnIndex(CenikDBHelper.CENIK_COLUMN_ID));
                Intent intent = new Intent(getApplicationContext(), Vytvor_Uprav_Cenik.class);
                intent.putExtra(KEY_EXTRA_CONTACT_ID, zboziID);
                startActivity(intent);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cenik_setings, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.Vymazat_Cenik:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.smazatCenik)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dbHelper.deleteAll();
                                Intent intent =new Intent(getApplicationContext(), Cenik.class);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "Úspěšně vymazáno", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                AlertDialog d = builder.create();
                d.setTitle("Smazat účtenku?");
                d.show();
                return true;
                 default:
                return super.onOptionsItemSelected(item);
        }
    }

}
