package com.johnyjuice.juicyeet;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Base64DataException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.WebView;
import android.widget.Toast;
import com.johnyjuice.juicyeet.tiskarna.BluetoothService;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.StringWriter;
import java.util.Locale;


public class SaleDetailActivity extends AppCompatActivity {
    public static final String LOGCAT="SaleDetailActivity";

    public String tiska ="nothing";
    public String hlavickaNazev= "";
    public String hlavickaDal= "";
    public String hlavickaDal1= "";
    public String hlavickaDal2= "";
    public String fik =null;
    public String bkp = null;
    public String datum = null;
    public String rezimtrzby = null;
    public String idprovozovny = null;
    public String cisuctenky = null;
    public String idpokladny = null;
    public String dicprov = null;
    public String celkTrzba = null;
    public String prodaneZbozi= "\n";
    BluetoothService mServiceS = null;
    BluetoothDevice con_dev = null;
    public String pocet_zbozi = null;
    public String[] zbozi= null;
    public String[] mnozstvi= null;
    public Double[] cena_zbozi= null;

    public static final String EXTRA_SALE_ENTRY="com.johnyjuice.juicyeet.SaleDetailActivity.ExtraSaleEntry";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sale_detail);

        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        SaleService.SaleEntry entry=(SaleService.SaleEntry)getIntent().getSerializableExtra(EXTRA_SALE_ENTRY);
        fik = entry.fik;
        bkp = entry.saleData.bkp;
        datum = entry.saleData.dat_trzby;
        idprovozovny= entry.saleData.id_provoz;
        idpokladny= entry.saleData.id_pokl;
        cisuctenky = entry.saleData.porad_cis;
        dicprov = entry.saleData.dic_popl;

        rezimtrzby= entry.saleData.rezim;

        if (Integer.parseInt(entry.saleData.rezim) == 0)
            rezimtrzby = "Běžný";

        if (Integer.parseInt(entry.saleData.rezim) == 1)
            rezimtrzby = "Zjednodušený";

        celkTrzba = entry.saleData.celk_trzba;
        pocet_zbozi = entry.saleData.pocet_zbozi;


        zbozi = new String[entry.saleData.zbozi.length];
        mnozstvi = new String[entry.saleData.zbozi.length];
        cena_zbozi = new Double[entry.saleData.zbozi.length];

        for (int i = 0; i < entry.saleData.zbozi.length; i++) {
            zbozi[i] = entry.saleData.zbozi[i];
            mnozstvi[i] = entry.saleData.mnozstvi[i];
            cena_zbozi[i] = entry.saleData.cena_zbozi[i];
        }


        FloatingActionButton fab_print = (FloatingActionButton) findViewById(R.id.fab_print);
        fab_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Start printing", Snackbar.LENGTH_LONG).show();
                PrintUctenka();

            }
        });

        FloatingActionButton fab_share = (FloatingActionButton) findViewById(R.id.fab_share);
        fab_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Start share", Snackbar.LENGTH_LONG).show();
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, "********************************"+"\n"+
                        "Celková tržba:"+ celkTrzba+"\n" +
                        "********************************"+"\n"+
                        "********************************"+"\n"+
                        "Datum:"+ datum +"\n"+
                        "--------------------------------"+"\n"+
                        "DIČ poplatnika:"+ dicprov +"\n"+
                        "--------------------------------"+"\n"+
                        "Číslo účtenky:"+ cisuctenky +"\n"+
                        "Režim tržby:" + rezimtrzby +"\n"+
                        "ID provozovny:"+ idprovozovny +"\n"+
                        "ID pokladny:"+ idpokladny +"\n"+
                        "FIK:"+ fik+"\n"+
                        "BKP:"+ bkp+"\n"+
                        "Počet zboží:" + pocet_zbozi+"\n");
                startActivity(Intent.createChooser(share, "Pošli e-účtenku"));
            }
        });
    }


}
