package com.example.top10downloaded.Utils;

import android.os.AsyncTask;
import android.util.Log;

import com.example.top10downloaded.Interfaces.AsyncTaskCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * First param  : URL
 * Second param : progressbar (if needed)
 * Third param  : type of result
 *
 * Constructor  : Add AsyncTaskCallback Interface
 *                used by an Activity in order
 *                to get updates when thread
 *                has executed.
 *
 * Note to self : AsyncTask can only be used once, use new instance
 *                for further use
 */

public class XMLDownloader extends AsyncTask<String,Void,String> {
    private static final String TAG = "XMLDownloader";
    private AsyncTaskCallback callback;

    public XMLDownloader(AsyncTaskCallback callback){

        this.callback = callback;
    }

    private String xmlResult = "";
    @Override
    protected String doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: Downloading from "+strings[0]);
        try {
            xmlResult = downloadXML(strings[0]);
        }catch(RepeatedURLException rue){

            Log.d(TAG, "doInBackground: "+rue.getMessage());
            return xmlResult;
        }
        if(xmlResult == null){
            Log.e(TAG, "doInBackground: Error while downloading from "+strings[0]);
        }
        return xmlResult;
    }



    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        callback.onAsyncTaskComplete(s);

    }

    private String downloadXML(String path) throws RepeatedURLException {
        StringBuilder xmlResultStr = new StringBuilder();
        int response = 0;
        try {
            URL url = new URL(path);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            response = http.getResponseCode();
            Log.d(TAG, "downloadXML: Response code = "+response);


            BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
            int charsRead;
            char[] inputBuffer = new char[500]; // Buffer which the reader puts its buffer into

            while(true){
                charsRead = reader.read(inputBuffer);
                if(charsRead < 0){
                    break;
                }
                if(charsRead > 0){
                    xmlResultStr.append(String.copyValueOf(inputBuffer, 0, charsRead));
                }

            }
            reader.close();

        }catch(MalformedURLException mue){
            
            if(mue.getCause() instanceof  NullPointerException){
                Log.d(TAG, "downloadXML: Repeated download");
                throw new RepeatedURLException("URL has previously been fetched, no need for fetching again at this time");
            }else {
                Log.e(TAG, "downloadXML: Error with the URL formatting ");
            }
            return null;
        }catch(IOException ioe){
            Log.e(TAG, "downloadXML: Error when opening the connection: responseCode= "+response+" + stacktrace = "+ ioe.getMessage());
            return null;
        }catch (SecurityException se){
            Log.e(TAG, "downloadXML: Error regarding permissions (INTERNET) "+se.getMessage());
            return null;
        }


        return xmlResultStr.toString();
    }









}
