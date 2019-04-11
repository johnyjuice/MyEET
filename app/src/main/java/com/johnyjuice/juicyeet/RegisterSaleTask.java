package com.johnyjuice.juicyeet;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by Johny on 11.02.2017.
 */

public class RegisterSaleTask extends AsyncTask <EetSaleDTO,Integer, String> {
    private static final String LOGTAG="RegisterSaleTask";


    private Context context;

    protected RegisterSaleTask(Context context){
        Log.d(LOGTAG,"Creating instance");
        this.context=context;
    }

    public static IntentFilter getMatchAllFilter(){
        IntentFilter ret=new IntentFilter();
        ret.addAction(MainBroadcastReceiver.ACTION_SALE_REGISTERED_CHANGE);
        return ret;
    }

    @Override
    protected String doInBackground(EetSaleDTO... dtoSales) {
        EetSaleDTO sale=dtoSales[0];
        Log.d(LOGTAG, "Background task started");

        //FIXME from settings
        if (sale.id_pokl==null) sale.id_pokl= RegisterSale.getIdpokladny();
        sale.id_provoz= RegisterSale.getIdprovozovny();
        sale.porad_cis=String.format("%08x",System.currentTimeMillis());
        sale.pocet_zbozi= RegisterSale.getPocetzbozi();

        SaleStore store=SaleStore.getInstance(context.getApplicationContext());
        SaleService.getInstance(store).setContext(context.getApplicationContext());
        SaleService.getInstance(store).registerSale(sale,new SaleService.SaleServiceListener() {
            @Override
            public void saleDataUpdated(String[] bkpList) {
                Intent broadcast=new Intent(MainBroadcastReceiver.ACTION_SALE_REGISTERED_CHANGE);
                if (bkpList!=null)
                    broadcast.putExtra(MainBroadcastReceiver.ACTION_SALE_EXTRA_BKP_LIST,bkpList);
                LocalBroadcastManager.getInstance(context).sendBroadcast(broadcast);
            }
        });
        return null;
    }
}
