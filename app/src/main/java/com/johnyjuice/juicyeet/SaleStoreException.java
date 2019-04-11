package com.johnyjuice.juicyeet;

/**
 * Created by Johny on 11.02.2017.
 */

public class SaleStoreException extends Exception {

    public SaleStoreException(String message){
        super(message);
    }

    public SaleStoreException(String message, Throwable t){
        super(message, t);
    }
}