package com.example.top10downloaded;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.top10downloaded.Utils.XMLDownloader;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String url = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml";
        Log.d(TAG, "onCreate: running AsyncTask");
        XMLDownloader downloadData = new XMLDownloader();
        Log.d(TAG, "onCreate: URL is "+url);
        downloadData.execute(url);
        Log.d(TAG, "onCreate: AsyncTask finished");
        
    }



}
