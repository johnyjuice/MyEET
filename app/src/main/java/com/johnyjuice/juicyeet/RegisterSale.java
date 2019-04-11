package com.johnyjuice.juicyeet;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.johnyjuice.juicyeet.tiskarna.BluetoothService;

public class RegisterSale extends AppCompatActivity {

    static String id_provozovny;
    static String id_pokladny;
    static int licencniKod;
    static int kontrolaLicence;
    UctenkaDBHelper dbHelper;
    static String pocet_zbozi;
    static String[] zbozi;
    static String[] mnozstvi;
    static Double[] cenaZbozi;
    int cis_zbozi = 1;
    public static float cena_celkem = 0;
    private static final String LOGTAG="RegisterSale";
    public static final String RESULT="com.johnyjuice.juicyeet.RegisterSale.RESULT";

    Button potvrdEv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_sale);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        potvrdEv = (Button) findViewById(R.id.button9);
        potvrdEv.setOnClickListener(myhandler1);

        dbHelper = new UctenkaDBHelper(this);

        RegisterSaleTemplate template=RegisterSaleTemplate.getSimpleTemplate();
        template.applyAll(findViewById(R.id.register_sale_main));

        String dic= PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(MainActivity.PREFERENCE_DIC,"Importujte certifikát");
        TextView txtDic=(TextView) findViewById(R.id.dic_popl);
        txtDic.setText(dic);
        TextView txttrzba=(TextView) findViewById(R.id.celk_trzba);
        cena_celkem=dbHelper.CenaCelkem();
        txttrzba.setText(Float.toString(cena_celkem));

        kontrolaLicence = 1592017;

        id_provozovny = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("idprovozovny", "Empty");
        id_pokladny = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("idpokladny", "Empty");
        licencniKod = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getInt("licence", 0);


        if(id_provozovny=="Empty" || id_pokladny=="Empty"){
            Toast.makeText(this, "Nejprve proveďte nastavení aplikace", Toast.LENGTH_LONG).show();
            onBackPressed();
        }

        if(licencniKod != kontrolaLicence){
            Toast.makeText(this, "Licenční kód není správný", Toast.LENGTH_LONG).show();
            onBackPressed();
        }


    }


    public static String getIdprovozovny() {
        return id_provozovny;
    }

    public static String getIdpokladny() {  return id_pokladny;  }
    public static String getPocetzbozi() {
        return pocet_zbozi;
    }
    public static String[] getZbozi() {  return zbozi; }
    public static String[] getMnozstvi() {  return mnozstvi; }
    public static Double[] getCenaZbozi() {  return cenaZbozi; }

    View.OnClickListener myhandler1 = new View.OnClickListener() {
        public void onClick(View v) {
        Intent resultIntent=new Intent();
        EetSaleDTO dtoSale=new EetSaleDTO();

        if (findViewById(R.id.dic_popl).getVisibility()==View.VISIBLE) dtoSale.dic_popl=((EditText)findViewById(R.id.dic_popl)).getText().toString();
        if (findViewById(R.id.dic_poverujiciho).getVisibility()==View.VISIBLE) dtoSale.dic_poverujiciho=((EditText)findViewById(R.id.dic_poverujiciho)).getText().toString();
        if (findViewById(R.id.id_provoz).getVisibility()==View.VISIBLE) dtoSale.id_provoz=((EditText)findViewById(R.id.id_provoz)).getText().toString();
        if (findViewById(R.id.id_pokl).getVisibility()==View.VISIBLE) dtoSale.id_pokl=((EditText)findViewById(R.id.id_pokl)).getText().toString();
        if (findViewById(R.id.porad_cis).getVisibility()==View.VISIBLE) dtoSale.porad_cis=((EditText)findViewById(R.id.porad_cis)).getText().toString();
        if (findViewById(R.id.dat_trzby).getVisibility()==View.VISIBLE) dtoSale.dat_trzby=((EditText)findViewById(R.id.dat_trzby)).getText().toString();
        if (findViewById(R.id.celk_trzba).getVisibility()==View.VISIBLE) dtoSale.celk_trzba=((EditText)findViewById(R.id.celk_trzba)).getText().toString();
        if (findViewById(R.id.zakl_nepodl_dph).getVisibility()==View.VISIBLE) dtoSale.zakl_nepodl_dph=((EditText)findViewById(R.id.zakl_nepodl_dph)).getText().toString();
        if (findViewById(R.id.zakl_dan1).getVisibility()==View.VISIBLE) dtoSale.zakl_dan1=((EditText)findViewById(R.id.zakl_dan1)).getText().toString();
        if (findViewById(R.id.dan1).getVisibility()==View.VISIBLE) dtoSale.dan1=((EditText)findViewById(R.id.dan1)).getText().toString();
        if (findViewById(R.id.zakl_dan2).getVisibility()==View.VISIBLE) dtoSale.zakl_dan2=((EditText)findViewById(R.id.zakl_dan2)).getText().toString();
        if (findViewById(R.id.dan2).getVisibility()==View.VISIBLE) dtoSale.dan2=((EditText)findViewById(R.id.dan2)).getText().toString();
        if (findViewById(R.id.zakl_dan3).getVisibility()==View.VISIBLE) dtoSale.zakl_dan3=((EditText)findViewById(R.id.zakl_dan3)).getText().toString();
        if (findViewById(R.id.dan3).getVisibility()==View.VISIBLE) dtoSale.dan3=((EditText)findViewById(R.id.dan3)).getText().toString();
        if (findViewById(R.id.cest_sluz).getVisibility()==View.VISIBLE) dtoSale.cest_sluz=((EditText)findViewById(R.id.cest_sluz)).getText().toString();
        if (findViewById(R.id.pouzit_zboz1).getVisibility()==View.VISIBLE) dtoSale.pouzit_zboz1=((EditText)findViewById(R.id.pouzit_zboz1)).getText().toString();
        if (findViewById(R.id.pouzit_zboz2).getVisibility()==View.VISIBLE) dtoSale.pouzit_zboz2=((EditText)findViewById(R.id.pouzit_zboz2)).getText().toString();
        if (findViewById(R.id.pouzit_zboz3).getVisibility()==View.VISIBLE) dtoSale.pouzit_zboz3=((EditText)findViewById(R.id.pouzit_zboz3)).getText().toString();
        if (findViewById(R.id.urceno_cerp_zuct).getVisibility()==View.VISIBLE) dtoSale.urceno_cerp_zuct=((EditText)findViewById(R.id.urceno_cerp_zuct)).getText().toString();
        if (findViewById(R.id.cerp_zuct).getVisibility()==View.VISIBLE) dtoSale.cerp_zuct=((EditText)findViewById(R.id.cerp_zuct)).getText().toString();
        if (findViewById(R.id.rezim).getVisibility()==View.VISIBLE) dtoSale.rezim=((EditText)findViewById(R.id.rezim)).getText().toString();


        //cis_zbozi=1;
        Cursor rs = dbHelper.vyberVsechnoZbozi();
        rs.moveToFirst();
        dtoSale.zbozi[0]= rs. getString(rs.getColumnIndex(UctenkaDBHelper.CENIK_COLUMN_NAME));
        dtoSale.mnozstvi[0] = rs. getString(rs.getColumnIndex(UctenkaDBHelper.CENIK_COLUMN_MNOZSTVI));
        dtoSale.cena_zbozi[0] = rs.getDouble(rs.getColumnIndex(UctenkaDBHelper.CENIK_COLUMN_CENA));
        while (rs.moveToNext()) {
           dtoSale.zbozi[cis_zbozi]= rs. getString(rs.getColumnIndex(UctenkaDBHelper.CENIK_COLUMN_NAME));
           dtoSale.mnozstvi[cis_zbozi]= rs. getString(rs.getColumnIndex(UctenkaDBHelper.CENIK_COLUMN_MNOZSTVI));
           dtoSale.cena_zbozi[cis_zbozi]= rs.getDouble(rs.getColumnIndex(UctenkaDBHelper.CENIK_COLUMN_CENA));
           cis_zbozi++;
        }

        zbozi=new String[dtoSale.zbozi.length];
        mnozstvi=new String[dtoSale.zbozi.length];
        cenaZbozi = new Double[dtoSale.zbozi.length];
        for (int i = 0; i < dtoSale.zbozi.length; i++) {
            zbozi[i] = dtoSale.zbozi[i];
            mnozstvi[i] = dtoSale.mnozstvi[i];
            cenaZbozi[i] = dtoSale.cena_zbozi[i];
        }

        dtoSale.pocet_zbozi = Integer.toString(cis_zbozi);;
        pocet_zbozi= Integer.toString(cis_zbozi);

        dbHelper.deleteAll();
        Pokladna_menu.zmenVymaz();
        resultIntent.putExtra(RESULT, dtoSale);
        Log.i(LOGTAG,"finish activity");
        setResult(RESULT_OK, resultIntent);
        finish();

    }};
}
