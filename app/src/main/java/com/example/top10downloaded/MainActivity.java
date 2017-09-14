package com.example.top10downloaded;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.top10downloaded.Models.FeedEntry;
import com.example.top10downloaded.Utils.AsyncTaskCallback;
import com.example.top10downloaded.Utils.XMLDownloader;
import com.example.top10downloaded.Utils.XMLParser;


public class MainActivity extends AppCompatActivity implements AsyncTaskCallback{

    private static final String TAG = "MainActivity";

    private String CURRENT_RESULT = "";
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String url = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml";
        Log.d(TAG, "onCreate: running AsyncTask");
        XMLDownloader downloadData = new XMLDownloader(this); // sends with AsyncTaskCallback Interface in constructor to receive message when update is complete
        Log.d(TAG, "onCreate: URL is "+url);
        downloadData.execute(url);

        Log.d(TAG, "onCreate: AsyncTask finished");
        
    }


    @Override
    public void onAsyncTaskComplete(Object obj) {
        XMLParser parser = new XMLParser();
        parser.parse((String) obj);
        for(FeedEntry e : parser.getParsedEntries()){
            if(e==null){
                Log.d(TAG, "onCreate: NULL");
            }
            Log.d(TAG, "onCreate: ENTRY : "+e);
        }
    }
}
