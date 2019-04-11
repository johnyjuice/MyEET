package com.johnyjuice.juicyeet;

import android.content.Context;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Johny on 11.02.2017.
 */

public class SaleService {
    private static final String LOGTAG="SaleService";
    private static final String FIK_PATTERN ="eet:Potvrzeni fik=\"";

    private Context context;

    public void setContext(Context context){
        this.context=context;
    }

    /**
     *
     */
    public interface SaleServiceListener {
        /**
         * notifies about change in sale database
         * @param bkp null or zero length when listener needs to check what changed, list of bkps of modified sales otherwise
         */
        void saleDataUpdated(String[] bkp);
    }

    /**
     * Class representing single attempt to send a sale. In fact wraps the header part of the EET registration message which is represented by EetHeaderDTO
     */
    public static class SaleRegisterAttempt implements Serializable {
        public static final long serialVersionUID = 2L;
        public String soapRequest;
        public String soapResponse;
        public EetHeaderDTO header;
        public EetResponse response;
        public String fik;
        public Throwable throwable;
        public String info;
        public long startTime;
        public long startSendingTime;
        public long finishTime;
        public String result;

        public String getSoapRequest(){ return soapRequest; };
        public String getSoapResponse(){ return soapResponse; };
        public EetHeaderDTO getHeader(){ return header; };
        public EetResponse getResponse(){ return response; };
        public String getFik(){ return fik; };
        public String getThrowable(){ return throwable!=null?throwable.toString():"-"; };
        public String getInfo(){ return info; };
        public String getStartTime(){ return new Date(startTime).toString(); };
        public String getStartSendingTime(){ return new Date(startSendingTime).toString(); };
        public String getFinishTime(){ return new Date(finishTime).toString(); };
        public String getResult(){ return result;};

        public String getThrowableTrace(){
            StringWriter sw=new StringWriter();
            PrintWriter pw=new PrintWriter(sw);
            throwable.printStackTrace(pw);
            pw.close();
            return throwable!=null?throwable.getMessage()+"\n"+sw.toString():"-";
        };


        public SaleRegisterAttempt(){
            startTime=System.currentTimeMillis();
        }

    }

    /**
     * Class representing ale to be registered. In fact it wraps the business data part of the EET registrayion message which is represented by EetSaleDTO
     */
    public static class SaleEntry implements Serializable {
        public static final long serialVersionUID = 1L;
        public SaleEntry(){
            attempts=new ArrayList<SaleRegisterAttempt>();
            registered=false;
            error=false;
            fik=null;
            saleData=null;
            inProgress=false;
            offline=false;
        }
        public SaleRegisterAttempt startAttempt(){
            SaleRegisterAttempt ret=new SaleRegisterAttempt();
            attempts.add(ret);
            inProgress=true;
            currentAttempt=ret;
            currentAttempt.result="N/A";
            return ret;
        }

        public void finishAttempt(boolean registered, boolean error, boolean offline, String fik, String info, Throwable throwable){
            this.registered=registered;
            this.error=error;
            this.fik=fik;
            this.offline=offline;
            currentAttempt.fik=fik;
            currentAttempt.info=info;
            currentAttempt.throwable=throwable;
            currentAttempt.finishTime=System.currentTimeMillis();
            if (registered){
                currentAttempt.result="OK";
            }
            else {
                if (error){
                    currentAttempt.result="Error!";
                }
                else if (offline){
                    currentAttempt.result="Retry";
                }
            }

            inProgress=false;
            currentAttempt=null;
        }

        public SaleRegisterAttempt currentAttempt;
        public EetSaleDTO saleData;
        public boolean registered;
        public boolean offline;
        public boolean error;
        public String fik;
        public List<SaleRegisterAttempt> attempts;
        public boolean inProgress;

        public SaleRegisterAttempt getCurrentAttempt(){ return currentAttempt; };
        public EetSaleDTO getSaleData(){ return saleData; };
        public boolean getRegistered(){ return registered; };
        public boolean isOffline(){ return offline; };
        public boolean isError(){ return error; };
        public String getFik(){ return fik; };
        public List<SaleRegisterAttempt> getAttempts(){ return attempts; };
        public boolean isInProgress(){ return inProgress; };

        //public SaleRegisterAttempt[] getAttempts(){
        //    return attempts.toArray(new SaleRegisterAttempt[attempts.size()]);
        //}

        public String wrapping(String s, int charCount){
            if (s==null) return "null";
            StringBuffer sb=new StringBuffer();
            int remain=s.length();
            int offset=0;
            while(remain>0){
                sb.append(s.substring(offset,offset+charCount>s.length()-1?s.length()-1:offset+charCount)).append(" ");
                offset+=charCount;
                remain-=charCount;
            }
            return sb.toString();
        }

        public String groupLine(String s, int charPerGroupCount, int groupsPerLine){
            if (s==null) return "null";
            StringBuffer sb=new StringBuffer();
            int remain=s.length();
            int offset=0;
            int groupCount=0;
            while(remain>0){
                sb.append(s.substring(offset,offset+charPerGroupCount>s.length()?s.length():offset+charPerGroupCount));
                offset+=charPerGroupCount;
                remain-=charPerGroupCount;
                groupCount++;
                if (groupCount==groupsPerLine) {
                    sb.append("\n");
                    groupCount=0;
                }
                else {
                    sb.append(" ");
                }
            }
            return sb.toString();
        }

        public String escapeXml(String s){
            if (s==null) return "null";
            return s.replaceAll("&","&amp;").replaceAll("<","&lt;");
        }


    }

    /**
     * Singleton service.
     */
    private static SaleService service;

    /** Store used by the service
     *
     */
    private SaleStore store;

    /**
     *
     * @param store - store which the service uses
     * @return initialized service
     */
    public static synchronized SaleService getInstance(SaleStore store){
        if (service==null) service=new SaleService(store);
        return service;
    }

    private Set<SaleServiceListener> listeners;

    /**
     * Initializes the service
     * @param store store to use
     */
    private SaleService(SaleStore store){
        this.store=store;
        listeners=new HashSet<SaleServiceListener>();
    }


    /**
     * Adds a listener to a set of listeners. One listener is registred only once. No guarantee of order when notifying the lisener.
     * @param listener the listener to add
     */
    protected void addListener(SaleServiceListener listener){
        listeners.add(listener);
    }

    /**
     * Removes the listener from the listeners set.
     * @param listener listener to remove
     */
    protected void removeListener(SaleServiceListener listener){
        listeners.remove(listener);
    }

    /**
     * Notifies all listeners with the list of chanded bkps
     */
    private void notifyListeners(String[] bkpList){
        for (SaleServiceListener listener: listeners ) {
            listener.saleDataUpdated(bkpList);
        }
    }

    /**
     * Registers new sale. The sale is created, persisted and registered. When the registration is not successful the sale is stored as unregistered.
     * @param dto business data of the sale
     * @param listener listener to notify during registration process
     */
    public void registerSale(EetSaleDTO dto, SaleServiceListener listener){
        addListener(listener);
        try {
            SaleEntry entry = new SaleEntry();
            entry.saleData = dto;
            processEntry(entry, true);
        }
        catch (Exception e){
            Log.e(LOGTAG,"error registering sale",e);
        }
        finally {
            removeListener(listener);
        }
    }

    /**
     * All still unregistered persisted entries are send for registration ..again
     * @param listener listener to notify during registration process
     */
    public void retryUnfinished(SaleServiceListener listener){
        addListener(listener);
        try {
            SaleEntry[] sales=store.findUnregistered(-1,-1, SaleStore.LimitType.COUNT); //all
            for (int i = 0; i < sales.length; i++) {
                SaleService.SaleEntry entry = sales[i];
                if (!entry.registered && !entry.error) {
                    processEntry(entry, false);
                }
            }
        }
        catch (Exception e){
            Log.e(LOGTAG,"erro retrying register", e);
        }
        finally {
            removeListener(listener);
        }
    }

    /**
     * processes entry
     * @param entry
     * @param firstAttempt
     */
    protected void processEntry(SaleEntry entry, boolean firstAttempt){
        String[] bkpList=null;
        try {
            if (entry.registered) return; //already registered


            CertificateInfo.setContext(context);
            CertificateInfo certInfo=CertificateInfo.load();
            boolean overeni= PreferenceManager.getDefaultSharedPreferences(context).getBoolean(MainActivity.PREFERENCE_TEST_MODE,false);

            entry.startAttempt();
            if (!firstAttempt) store.saveSaleEntry(entry);
            notifyListeners(bkpList);
            EetRegisterRequest.Builder builder = EetRegisterRequest.builder()
                    .fromDTO(entry.saleData)
                    .key(certInfo.getKey())
                    .certificate(certInfo.getCertificate());

            EetRegisterRequest request=builder.build();
            if (firstAttempt) {
                entry.saleData = request.getSaleDTO();
                store.saveSaleEntry(entry);
            }
            if (entry.saleData.bkp!=null) bkpList=new String[]{ entry.saleData.bkp };
            notifyListeners(bkpList);

            String soapRequest;
            if (firstAttempt)
                soapRequest=request.generateSoapRequest(null, EetRegisterRequest.PrvniZaslani.PRVNI, null, EetRegisterRequest.Overeni.valueOf(overeni));
            else {
                soapRequest = request.generateSoapRequest(null, EetRegisterRequest.PrvniZaslani.OPAKOVANE, null, EetRegisterRequest.Overeni.valueOf(Boolean.parseBoolean(entry.attempts.get(0).header.getOvereni())));
            }
            //writeStringToFile(soapRequest,"request");

            entry.currentAttempt.header=request.getLastHeader();
            entry.currentAttempt.soapRequest=soapRequest;
            entry.currentAttempt.startSendingTime= System.currentTimeMillis();
            store.saveSaleEntry(entry);

            URL url;
            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(MainActivity.PREFERENCE_PLAYGROUND_MODE,false))
                url=new URL("https://pg.eet.cz:443/eet/services/EETServiceSOAP/v3");
            else
                url=new URL("https://prod.eet.cz:443/eet/services/EETServiceSOAP/v3");


            String soapResponse=request.sendRequest(soapRequest,url);
            entry.currentAttempt.soapResponse=soapResponse;
            entry.currentAttempt.response=new EetResponse(soapResponse);
            entry.currentAttempt.startSendingTime=System.currentTimeMillis();
            //writeStringToFile(soapResponse,"response");
            store.saveSaleEntry(entry);

            if (soapResponse.contains("<eet:Chyba kod=\"0\"")){
                Log.d(LOGTAG, "Uspesne zpracovano v overovacim rezimu");
                String fik="Overovaci rezim OK";
                entry.finishAttempt(true,false, firstAttempt?false:true, fik,null, null);
                store.saveSaleEntry(entry);
            }
            else if (soapResponse.contains(FIK_PATTERN)) {
                int fikIdx=soapResponse.indexOf(FIK_PATTERN)+FIK_PATTERN.length();
                String fik=soapResponse.substring(fikIdx,fikIdx+39);
                entry.finishAttempt(true,false, firstAttempt?false:true, fik,null, null);
                store.saveSaleEntry(entry);
            }
            else {
                //FIXME: better error handling
                Log.e(LOGTAG, "fik not found in response");
                entry.finishAttempt(false,true,false,null,"fik not found in response", null);
                store.saveSaleEntry(entry);
            }
            notifyListeners(bkpList);
        }
        catch (Exception e) {
            Log.e(LOGTAG, "exception while registering",e);
            entry.finishAttempt(false,false,true,null,"exception while registering",e);
            try { store.saveSaleEntry(entry); } catch (SaleStoreException ex) { Log.e(LOGTAG,"errorsaving entry",ex);}
            notifyListeners(bkpList);
        }
    }

    public SaleEntry[] getLastRegistered() {
        //return sales.toArray(new SaleEntry[sales.size()]);
        try {
            return store.findAll(-1, -1, SaleStore.LimitType.COUNT); //FIXME: not return all
        }
        catch (SaleStoreException e){
            Log.e(LOGTAG,"Error while saving",e);
        }
        return new SaleEntry[]{};
    }

    private void writeStringToFile(String data, String filename){
        try {
            File appdir = new File(Environment.getExternalStorageDirectory(), "openeet");
            if (!appdir.exists()) appdir.mkdir();
            File outFile=new File(appdir,String.format("%x016-%s",System.currentTimeMillis(),filename));
            Log.d(LOGTAG, "Writing file to "+outFile.getAbsolutePath());
            FileWriter fw=new FileWriter(outFile);
            fw.write(data);
            fw.close();
        }
        catch (Exception e){
            Log.e(LOGTAG, "failed to write file", e);
        }
    }
}