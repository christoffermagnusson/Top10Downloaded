package com.example.top10downloaded;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * First param : URL
 * Second param : progressbar (if needed)
 * Third param : type of result
 */

public class XMLDownloader extends AsyncTask<String,Void,String>{
    private static final String TAG = "XMLDownloader";
    @Override
    protected String doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: Downloading from "+strings[0]);
        String rssFeed = downloadXML(strings[0]);
        if(rssFeed == null){
            Log.e(TAG, "doInBackground: Error while downloading from "+strings[0]);
        }
        return rssFeed;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d(TAG, "onPostExecute: parameter : "+s);
    }

    private String downloadXML(String path) {
        StringBuilder xmlResult = new StringBuilder();
        int response = 0;
        try {
            URL url = new URL(path);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            response = http.getResponseCode();
            Log.d(TAG, "downloadXML: Response code = "+response);


            BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
            int charsRead;
            char[] inputBuffer = new char[500];

            while(true){
                charsRead = reader.read(inputBuffer);
                if(charsRead < 0){
                    break;
                }
                if(charsRead > 0){
                    xmlResult.append(String.copyValueOf(inputBuffer, 0, charsRead));
                }

            }
            reader.close();

        }catch(MalformedURLException mue){
            Log.e(TAG, "downloadXML: Error with the URL formatting");
            return null;
        }catch(IOException ioe){
            Log.e(TAG, "downloadXML: Error when opening the connection: responseCode= "+response+" + stacktrace = "+ ioe.getMessage());
            return null;
        }catch (SecurityException se){
            Log.e(TAG, "downloadXML: Error regarding permissions (INTERNET) "+se.getMessage());
            return null;
        }

        return xmlResult.toString();
    }




}
