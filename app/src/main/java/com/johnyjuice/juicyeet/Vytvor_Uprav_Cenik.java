package com.johnyjuice.juicyeet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Vytvor_Uprav_Cenik extends AppCompatActivity implements View.OnClickListener{


    public CenikDBHelper dbHelper ;
    EditText zboziEditText;
    EditText cenaEditText;

    Button saveButton;
    LinearLayout buttonLayout;
    Button editButton, deleteButton;

    int zboziID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        zboziID = getIntent().getIntExtra(Cenik.KEY_EXTRA_CONTACT_ID, 0);

        setContentView(R.layout.activity_vytvor__uprav__cenik);
        zboziEditText = (EditText) findViewById(R.id.editTextZbozi);
        cenaEditText = (EditText) findViewById(R.id.editTextCena);

        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);
        buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);
        editButton = (Button) findViewById(R.id.editButton);
        editButton.setOnClickListener(this);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);

        dbHelper = new CenikDBHelper(this);

        if(zboziID > 0) {
            saveButton.setVisibility(View.GONE);
            buttonLayout.setVisibility(View.VISIBLE);

            Cursor rs = dbHelper.vyberZbozi(zboziID);
            rs.moveToFirst();
            String zboziNazev = rs.getString(rs.getColumnIndex(CenikDBHelper.CENIK_COLUMN_ZBOZI));
            int zboziCena = rs.getInt(rs.getColumnIndex(CenikDBHelper.CENIK_COLUMN_CENA));
            if (!rs.isClosed()) {
                rs.close();
            }

            zboziEditText.setText(zboziNazev);
            zboziEditText.setFocusable(false);
            zboziEditText.setClickable(false);


            cenaEditText.setText((CharSequence) (zboziCena + ""));
            cenaEditText.setFocusable(false);
            cenaEditText.setClickable(false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveButton:
                persistPerson();
                return;
            case R.id.editButton:
                saveButton.setVisibility(View.VISIBLE);
                buttonLayout.setVisibility(View.GONE);
                zboziEditText.setEnabled(true);
                zboziEditText.setFocusableInTouchMode(true);
                zboziEditText.setClickable(true);

                cenaEditText.setEnabled(true);
                cenaEditText.setFocusableInTouchMode(true);
                cenaEditText.setClickable(true);
                return;
            case R.id.deleteButton:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.vymazatZbozi)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dbHelper.vymazZbozi(zboziID);
                                Toast.makeText(getApplicationContext(), "Úspěšně vymazáno", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), Cenik.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                AlertDialog d = builder.create();
                d.setTitle("Vymazat zboží?");
                d.show();
                return;
        }
    }
    public static void pridejNaUctenku(String zbozi, int cena, int mnozstvi) {
        Context context;


    }

    public void persistPerson() {
        if(zboziID > 0) {
            if(dbHelper.opravZbozi(zboziID, zboziEditText.getText().toString(),
                   Float.parseFloat(cenaEditText.getText().toString()))) {
                Toast.makeText(getApplicationContext(), "Úprava zboží byla provdena", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Cenik.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else {
                Toast.makeText(getApplicationContext(), "Úprava zboží nebyla provedena", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            if(dbHelper.vlozZbozi(zboziEditText.getText().toString(),
                                        Float.parseFloat(cenaEditText.getText().toString()))) {
                pridejNaUctenku("sla",8,8);
                Toast.makeText(getApplicationContext(), "Zboží vloženo", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "Nebylo přidáno", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(getApplicationContext(), Cenik.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}
