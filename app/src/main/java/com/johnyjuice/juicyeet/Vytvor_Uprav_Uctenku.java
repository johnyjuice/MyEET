package com.johnyjuice.juicyeet;

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

public class Vytvor_Uprav_Uctenku extends AppCompatActivity implements View.OnClickListener{

    private UctenkaDBHelper dbHelper ;
    EditText editMnozstvi;
    EditText editZboziNaUctence;
    EditText editCenaZboziNaUctence;
    Button ulozUc;
    Button vymazZboziZUctenky;

    int zboziID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        zboziID = getIntent().getIntExtra(Pokladna_menu.KEY_EXTRA_CONTACT_ID, 0);

        setContentView(R.layout.activity_vytvor__uprav__uctenku);
        editMnozstvi = (EditText) findViewById(R.id.editMnozstvi);
        editZboziNaUctence = (EditText) findViewById(R.id.editZboziNaUctence);
        editCenaZboziNaUctence = (EditText) findViewById(R.id.editCenaZboziNaUctence);

        ulozUc = (Button) findViewById(R.id.ulozUc);
        ulozUc.setOnClickListener(this);
        vymazZboziZUctenky = (Button) findViewById(R.id.vymazZboziZUctenky);
        vymazZboziZUctenky.setOnClickListener(this);

        dbHelper = new UctenkaDBHelper(this);

        if(zboziID > 0) {
            ulozUc  .setVisibility(View.GONE);
            ulozUc.setVisibility(View.VISIBLE);

            Cursor rs = dbHelper.vyberZbozi(zboziID);
            rs.moveToFirst();
            String zboziNazev = rs.getString(rs.getColumnIndex(UctenkaDBHelper.CENIK_COLUMN_NAME));
            float zboziCena = rs.getFloat(rs.getColumnIndex(UctenkaDBHelper.CENIK_COLUMN_CENA));
            int zboziMnozstvi = rs.getInt(rs.getColumnIndex(UctenkaDBHelper.CENIK_COLUMN_MNOZSTVI));
            if (!rs.isClosed()) {
                rs.close();
            }

            editZboziNaUctence.setText(zboziNazev);
            editZboziNaUctence.setFocusable(false);
            editZboziNaUctence.setClickable(false);

            editCenaZboziNaUctence.setText((CharSequence) (zboziCena + ""));
            editMnozstvi.setText((CharSequence) (zboziMnozstvi + ""));

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ulozUc:
                persistPerson();
                return;
            case R.id.editButton:
                ulozUc.setVisibility(View.VISIBLE);
                return;
            case R.id.vymazZboziZUctenky:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.vymazatZbozi)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dbHelper.vymazZbozi(zboziID);
                                Toast.makeText(getApplicationContext(), "Úspěšně vymazáno", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), Pokladna_menu.class);
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

    public void persistPerson() {
        if(zboziID > 0) {
            if(dbHelper.opravZbozi(zboziID, editZboziNaUctence.getText().toString(),
                    Float.parseFloat(editCenaZboziNaUctence.getText().toString()),
                    Integer.parseInt(editMnozstvi.getText().toString()),
            (Float.parseFloat(editCenaZboziNaUctence.getText().toString())*Integer.parseInt(editMnozstvi.getText().toString())))) {
                Toast.makeText(getApplicationContext(), "Úprava zboží byla provdena", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Pokladna_menu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else {
                Toast.makeText(getApplicationContext(), "Úprava zboží nebyla provedena", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            if(dbHelper.vlozZbozi(editZboziNaUctence.getText().toString(),
                    Float.parseFloat(editCenaZboziNaUctence.getText().toString()),
                    Integer.parseInt(editMnozstvi.getText().toString()),
                    ((Float.parseFloat(editCenaZboziNaUctence.getText().toString()))*(Integer.parseInt(editMnozstvi.getText().toString()))))) {
                Toast.makeText(getApplicationContext(), "Zboží vloženo", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "Nebylo přidáno", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(getApplicationContext(), Pokladna_menu.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}
