package com.johnyjuice.juicyeet;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.johnyjuice.juicyeet.tiskarna.BluetoothService;

import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Pokladna_menu extends AppCompatActivity {

    public final static String KEY_EXTRA_CONTACT_ID = "KEY_EXTRA_CONTACT_ID";

    private ListView listView;
    UctenkaDBHelper dbHelper;
    public static float cena_celkem = 0;
    public static boolean vymaz = false;

    Button seznamBt;
    Button novyBt;
    Button vymazBt;
    Button zaevidujBt;

    /*EET*/

    private static final String LOGTAG="MainActivity";
    public static final String PREFERENCE_TEST_MODE = "test-mode";
    public static final String PREFERENCE_PLAYGROUND_MODE = "playground-mode";
    public static final String PREFERENCE_DIC = "dic";
    public static final String PREFERENCE_CHANGED_TIME = "changed-time";
    public static final String PREFERENCE_FILE = "APP";

    private enum ListViewContent {
        ALL,
        ONLINE,
        OFFLINE,
        UNREGISTERED,
        ERROR
    }


    private ListViewContent listViewContent=ListViewContent.ALL;

    private static final int REGISTER_SALE=0;

    final protected List<EetSaleDTO> list = new ArrayList<EetSaleDTO>();

    private BroadcastReceiver broadcastReceiver;

    SimpleCursorAdapter cursorAdapter = null;

    private void updateTestMode(){
        //get mode from prefs
        boolean testMode= PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(PREFERENCE_TEST_MODE,false);
        Log.d(LOGTAG,"Update test mode by preference: "+testMode);

        //set menu


        //snackbar
        /*
        if (testMode)
            Snackbar.make(findViewById(R.id.content_main_activity),"Nastaven režim testování, odeslané účtenky nebudou evidovány!",4000).show();
        else
            Snackbar.make(findViewById(R.id.content_main_activity),"Nastaven evidenční režim, odeslané účtenky BUDOU evidovány!",4000).show();
        */

        //update title
        TextView lbl=(TextView) findViewById(R.id.lblTestMode);
        if (lbl==null) return;
        if (testMode)
            lbl.setText("TEST");
        else
            lbl.setText((""));
    }

    private void updatePlagroundMode(){
        boolean playgroundMode=PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(PREFERENCE_PLAYGROUND_MODE,false);
        Log.d(LOGTAG,"Update playground mode by preference: "+playgroundMode);
        TextView lbl=(TextView) findViewById(R.id.lblPlayground);
        if (lbl==null) {
            Log.d(LOGTAG,"no label for playground mode");
            return;
        }
        if (playgroundMode)
            lbl.setText("PLAYGROUND");
        else
            lbl.setText((""));
    }

    private void updateDic(){
        String dic=PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(PREFERENCE_DIC,"Importujte certifikát");
        Log.d(LOGTAG,"Update dic label:"+dic);
        TextView lbl=(TextView)findViewById(R.id.lblDic);
        if (lbl==null) {
            Log.d(LOGTAG,"no label for dic");
            return;
        }
        lbl.setText("DIČ: "+dic);
    }

    private void updateOfflineCount(){
        TextView lbl=(TextView) findViewById(R.id.lblOfflineCount);
        if (lbl==null) return;
        lbl.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokladna_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        seznamBt = (Button) findViewById(R.id.button5);
        seznamBt.setOnClickListener(myhandler1);

        novyBt = (Button) findViewById(R.id.button12);
        novyBt.setOnClickListener(myhandler2);

        vymazBt = (Button) findViewById(R.id.button6);
        vymazBt.setOnClickListener(myhandler3);

        zaevidujBt = (Button) findViewById(R.id.button4);
        zaevidujBt.setOnClickListener(myhandler4);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        ActivityCompat.requestPermissions(Pokladna_menu.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);

        if ( ActivityCompat.checkSelfPermission(Pokladna_menu.this,Manifest.permission.READ_EXTERNAL_STORAGE) == -1){
            onBackPressed();
        }

        dbHelper = new UctenkaDBHelper(this);


        final Cursor cursor = dbHelper.vyberVsechnoZbozi();
        String [] columns = new String[] {
                UctenkaDBHelper.CENIK_COLUMN_NAME,
                UctenkaDBHelper.CENIK_COLUMN_CENA,
                UctenkaDBHelper.CENIK_COLUMN_MNOZSTVI
        };
        int [] widgets = new int[] {
                R.id.jmeno_ZboziU,
                R.id.cena_ZboziU,
                R.id.mnozstvi_ZboziU
        };
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.uctenka_info,
                cursor, columns, widgets, 0);
        listView = (ListView)findViewById(R.id.listView3);
        listView.setAdapter(cursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView listView, View view,
                                    int position, long id) {
                Cursor itemCursor = (Cursor) Pokladna_menu.this.listView.getItemAtPosition(position);
                int personID = itemCursor.getInt(itemCursor.getColumnIndex(UctenkaDBHelper.CENIK_COLUMN_ID));
                Intent intent = new Intent(getApplicationContext(), Vytvor_Uprav_Uctenku.class);
                intent.putExtra(KEY_EXTRA_CONTACT_ID, personID);
                startActivity(intent);

            }
        });


    /*EET*/
        broadcastReceiver=new MainBroadcastReceiver(this);

        ListView salesList = (ListView) findViewById(R.id.salesList);
        salesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SaleService.SaleEntry entry=(SaleService.SaleEntry) adapterView.getItemAtPosition(i);
                Intent detail=new Intent(Pokladna_menu.this, SaleDetailActivity.class);
                detail.putExtra(SaleDetailActivity.EXTRA_SALE_ENTRY, entry);
                startActivity(detail);
            }
        });


    }
    View.OnClickListener myhandler1 = new View.OnClickListener() {
        public void onClick(View v) {
        Intent intent =new Intent(getApplicationContext(), Pridej_zbozi_seznam.class);
        startActivity(intent);
    }};
        View.OnClickListener myhandler2 = new View.OnClickListener() {
            public void onClick(View v) {
        Intent intent = new Intent(Pokladna_menu.this, Vytvor_Uprav_Uctenku.class);
        intent.putExtra(KEY_EXTRA_CONTACT_ID, 0);
        startActivity(intent);
    }};
            View.OnClickListener myhandler4 = new View.OnClickListener() {
                public void onClick(View v) {
        Intent registerSaleIntent =new Intent(Pokladna_menu.this, RegisterSale.class);
        startActivityForResult(registerSaleIntent,REGISTER_SALE);
    }};
                View.OnClickListener myhandler3 = new View.OnClickListener() {
                    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
        builder.setMessage(R.string.smazatUctenku)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dbHelper.deleteAll();
                        Intent intent =new Intent(getApplicationContext(), Pokladna_menu.class);
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

    }};

    /*EET*/

    private void listProviders() {
        try {
            Provider p[] = Security.getProviders();
            for (int i = 0; i < p.length; i++) {
                Log.d(LOGTAG,p[i].toString());
                for (Enumeration e = p[i].keys(); e.hasMoreElements();)
                    Log.d(LOGTAG,"      " + e.nextElement().toString());
            }
        } catch (Exception e) {
            Log.e(LOGTAG,"error listing providers/algs");
        }
    }

    protected void updateList() {
        SaleStore store=SaleStore.getInstance(getBaseContext().getApplicationContext());
        SaleService.SaleEntry[] items=null;

        try {
            switch (listViewContent) {
                case ALL:
                    items = store.findAll(-1, -1, SaleStore.LimitType.COUNT);
                    break;
                case ONLINE:
                    items = store.findOnline(-1, -1, SaleStore.LimitType.COUNT);
                    break;
                case OFFLINE:
                    items = store.findOffline(-1, -1, SaleStore.LimitType.COUNT);
                    break;
                case UNREGISTERED:
                    items = store.findUnregistered(-1, -1, SaleStore.LimitType.COUNT);
                    break;
                case ERROR:
                    items = store.findError(-1, -1, SaleStore.LimitType.COUNT);
                    break;
                default:
                    items = store.findAll(-1, -1, SaleStore.LimitType.COUNT);
                    break;
            }
        }
        catch (Exception e){
            Log.e(LOGTAG,"Error while updating data from storaqe",e);
            Snackbar.make(findViewById(R.id.activity_pokladna_menu),"Chyba úložiště",3000).show();
        }

        if (items!=null) {
            ArrayAdapter<SaleService.SaleEntry> adapter = new SaleListArrayAdapter(this, items);
            ListView salesList = (ListView) findViewById(R.id.salesList);
            salesList.setAdapter(adapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REGISTER_SALE: processRegisterSaleResult(resultCode,data);break;
        }
    }

    protected void processRegisterSaleResult(int resultCode, Intent data){
        Log.d(LOGTAG,"Processing result");
        if (resultCode==RESULT_OK && data!=null) {
            EetSaleDTO dtoSale = (EetSaleDTO) data.getSerializableExtra(RegisterSale.RESULT);
            new RegisterSaleTask(getApplicationContext()).execute(dtoSale);

        }
    }

    @Override
    protected void onStart() {
        Log.d(LOGTAG,"onStart");
        super.onStart();
        registerBroadcastReceivers();
        updatePlagroundMode();
        updateTestMode();
        updateDic();
        updateOfflineCount();
        updateList();
    }

    @Override
    protected void onStop() {
        Log.d(LOGTAG,"onStop");
        super.onStop();

    }

    @Override
    protected void onResume() {
        Log.d(LOGTAG,"onResume");
        super.onResume();
        registerBroadcastReceivers();
        updatePlagroundMode();
        updateTestMode();
        updateOfflineCount();
        updateDic();
        updateList();
        TextView labelcena=(TextView) findViewById(R.id.lblCena);
        cena_celkem=dbHelper.CenaCelkem();
        labelcena.setText( Float.toString(cena_celkem )+" Kč");

        if (vymaz) {
            vymaz = false;
            Intent intent =new Intent(getApplicationContext(), Pokladna_menu.class);
            startActivity(intent);
        }


    }

    @Override
    protected void onPause() {
        Log.d(LOGTAG,"onPause");
        super.onPause();
        unregisterBroadcastReceivers();
    }

    public static void zmenVymaz() {
        vymaz=true;

    }

    private void unregisterBroadcastReceivers() {
        Log.d(LOGTAG,"unregister br. recv");
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);
    }

    private void registerBroadcastReceivers() {
        Log.d(LOGTAG,"register br. recv");
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver,RegisterSaleTask.getMatchAllFilter());
    }

    public void processBroadcast(Context context, Intent intent) {
        Log.d(LOGTAG,"onReceive: "+intent.getAction());
        updateList();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(LOGTAG, "Shared pref changed: "+key);
        updatePlagroundMode();
        updateTestMode();
        updateOfflineCount();
        updateDic();
    }

}
