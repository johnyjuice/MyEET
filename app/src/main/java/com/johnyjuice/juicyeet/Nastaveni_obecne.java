package com.johnyjuice.juicyeet;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Nastaveni_obecne extends AppCompatActivity {

    String id_provozovny;
    String id_pokladny;
    int licencni_kod;
    EditText Edit_id_prov;
    EditText Edit_id_pokl;
    EditText Edit_licencni;
    Button ulozobec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nastaveni_obecne);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ulozobec = (Button) findViewById(R.id.button9);
        ulozobec.setOnClickListener(myhandler1);

        Edit_id_prov = (EditText) findViewById(R.id.edit_idprovoz);
        Edit_id_pokl = (EditText) findViewById(R.id.edit_idpokl);
        Edit_licencni = (EditText) findViewById(R.id.edit_Licencni_kod);

        id_provozovny = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("idprovozovny", "Empty");
        if (id_provozovny != "Empty")
            Edit_id_prov.setText(id_provozovny);

        Edit_id_pokl.setText("an-"+ Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID));
        id_pokladny = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("idpokladny", "Empty");
        if (id_pokladny != "Empty")
            Edit_id_pokl.setText(id_pokladny);

        licencni_kod = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getInt("licence", 0);
        if (licencni_kod != 0)
            Edit_licencni.setText(Integer.toString(licencni_kod));

    }

    View.OnClickListener myhandler1 = new View.OnClickListener() {
        public void onClick(View v) {


        PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("idprovozovny", Edit_id_prov.getText().toString()).commit();
        PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("idpokladny", Edit_id_pokl.getText().toString()).commit();
        PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putInt("licence", Integer.parseInt(Edit_licencni.getText().toString())).commit();
        Toast.makeText(getBaseContext(), "Změny uloženy", Toast.LENGTH_LONG).show();
    }
        };
}
