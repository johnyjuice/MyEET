package com.johnyjuice.juicyeet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Johny on 11.02.2017.
 */

public class MainBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_SALE_REGISTERED_CHANGE="com.action.SaleRegisteredChange";
    public static final String ACTION_SALE_EXTRA_BKP_LIST="com.github.openeet.openeet.action.extra.BkpList";

    protected Pokladna_menu pokladnaMenu;

    protected MainBroadcastReceiver(Pokladna_menu pokladnaMenu){
        this.pokladnaMenu=pokladnaMenu;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        pokladnaMenu.processBroadcast(context,intent);
    }
}
