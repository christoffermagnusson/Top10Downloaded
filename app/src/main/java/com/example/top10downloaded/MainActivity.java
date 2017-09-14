package com.example.top10downloaded;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.top10downloaded.Adapters.FeedListAdapter;
import com.example.top10downloaded.Models.FeedEntry;
import com.example.top10downloaded.Interfaces.AsyncTaskCallback;
import com.example.top10downloaded.Utils.XMLDownloader;
import com.example.top10downloaded.Utils.XMLParser;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements AsyncTaskCallback{

    private static final String TAG = "MainActivity";

    private ListView feedEntryListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        feedEntryListView = (ListView) findViewById(R.id.feedEntryList);


        String url = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml";
        Log.d(TAG, "onCreate: running AsyncTask");
        XMLDownloader downloadData = new XMLDownloader(this); // sends with AsyncTaskCallback Interface in constructor to receive message when update is complete
        Log.d(TAG, "onCreate: URL is "+url);
        downloadData.execute(url);


        
    }

    /**
     * Callback method used to inform this Activity that
     * an AsyncTask has been completed.
     *
     * @param obj Object that in this case is returned by
     *             the "onPostExecute" method in
     *            XMLDownloader class. This case will
     *            contain a String of an XML download
     *            to be parsed.
     */
    @Override
    public void onAsyncTaskComplete(Object obj) {
        XMLParser parser = new XMLParser();
        parser.parse((String) obj);
        setupFeedEntryListView(parser.getParsedEntries());

        // LOGGING PURPOSES ONLY
        for(FeedEntry e : parser.getParsedEntries()){
            if(e==null){
                Log.d(TAG, "onAsyncTaskComplete: NULL");
            }
            Log.d(TAG, "onAsyncTaskComplete: ENTRY : "+e);
        }
        Log.d(TAG, "onAsyncTaskComplete: AsyncTask finished");
    }

    private void setupFeedEntryListView(ArrayList<FeedEntry> parsedEntries) {
        FeedListAdapter adapter = new FeedListAdapter(MainActivity.this, R.layout.feedentry_listitem, parsedEntries);
        feedEntryListView.setAdapter(adapter);

    }
}
