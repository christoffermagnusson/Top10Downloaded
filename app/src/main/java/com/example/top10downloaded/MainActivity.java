package com.example.top10downloaded;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.top10downloaded.Adapters.FeedListAdapter;
import com.example.top10downloaded.Models.FeedEntry;
import com.example.top10downloaded.Interfaces.AsyncTaskCallback;
import com.example.top10downloaded.Utils.RepeatedURLException;
import com.example.top10downloaded.Utils.XMLDownloader;
import com.example.top10downloaded.Utils.XMLParser;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements AsyncTaskCallback{

    private static final String TAG = "MainActivity";

    private ListView feedEntryListView;
    private MenuItem top10Item;
    private MenuItem top25Item;



    // Variable used to keep track of lastdownloaded
    // between filtering top10 and top25
    private String lastDownloaded = "";
    private final String LAST_DOWNLOAD = "Last downloaded item";
    private int lastMenuItemClicked = 0;
    private final String LAST_ITEM = "Last item to clicked";

    /**
     * Filtering states of the app
     */
    private enum State{
        TOP10,
        TOP25
    }

    private State currentState = State.TOP10;
    private final String CURRENT_STATE = "Current state";
    private boolean isStarted = false;
    private final String IS_STARTED = "the app is initiated";
    private String cachedUrl = "INVALIDATED";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        feedEntryListView = (ListView) findViewById(R.id.feedEntryList);
        if(savedInstanceState==null){
            String initialDownload = getString(R.string.baseUrl)+getString(R.string.top10_free);
            downloadRSS(initialDownload);
            lastDownloaded = initialDownload;
            Log.d(TAG, "onCreate: Bundle was NULL, starting app first time!");
        }else{
            downloadRSS(savedInstanceState.getString(LAST_DOWNLOAD));
            setState(savedInstanceState.getString(CURRENT_STATE));
            Log.d(TAG, "onCreate: Fetching last downloaded URL");
        }
        super.onCreate(savedInstanceState);




        
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(CURRENT_STATE, currentState.toString());
        outState.putString(LAST_DOWNLOAD, lastDownloaded);
        outState.putBoolean(IS_STARTED,isStarted);
        outState.putInt(LAST_ITEM,lastMenuItemClicked);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        downloadRSS(savedInstanceState.getString(LAST_DOWNLOAD));
        setState(savedInstanceState.getString(CURRENT_STATE));
        isStarted = savedInstanceState.getBoolean(IS_STARTED);
        lastMenuItemClicked = savedInstanceState.getInt(LAST_ITEM);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds_menu, menu);
        top10Item = menu.findItem(R.id.menuTop10);
        top25Item = menu.findItem(R.id.menuTop25);
        switch(currentState){
            case TOP10:
                top10Item.setChecked(true);
                break;
            case TOP25:
                top25Item.setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String baseUrl = getString(R.string.baseUrl);

        if(item.getItemId()==R.id.menuTop10){
            top10Item.setChecked(true);
            setState("TOP10");
        }else if(item.getItemId()==R.id.menuTop25){
            top25Item.setChecked(true);
            setState("TOP25");
        }

        String rssToDownload = decideOnDownload(item.getItemId(), baseUrl, currentState);
        if(rssToDownload!=null) {
            downloadRSS(rssToDownload);
        }else{
            Log.d(TAG, "onOptionsItemSelected: URL already fetched");
            // dont download
        }



        Log.d(TAG, "onOptionsItemSelected: "+decideOnDownload(item.getItemId(), baseUrl, currentState));
        lastMenuItemClicked = item.getItemId();


        return true;
    }

    /**
     * Method deciding upon what RSS feed to download
     * from. Depending on State of the app. Default
     * cases are reached when switching between filtering
     * options.
     * @param id Item in the menu
     * @param rssFeed BaseUrl to download from
     * @param state Current state of app (TOP10/TOP25)
     * @return String of rss to download
     */
    private String decideOnDownload(int id, String rssFeed,State state) {
        Log.d(TAG, "decideOnDownload: id="+id+", lastItem="+lastMenuItemClicked);
        if (state == State.TOP10 && id != lastMenuItemClicked) {
            switch (id) {
                case R.id.menuFree:
                    rssFeed += getString(R.string.top10_free);
                    break;
                case R.id.menuPaid:
                    rssFeed += getString(R.string.top10_paid);
                    break;
                case R.id.menuSongs:
                    rssFeed += getString(R.string.top10_songs);
                    break;
                default:
                    lastDownloaded = lastDownloaded.replace("25","10"); // switch the limit of items in the url
                    rssFeed = lastDownloaded;
                    Log.d(TAG, "decideOnDownload: Switched filtering from 25 to 10");
            }
            lastDownloaded = rssFeed;
            Log.d(TAG, "decideOnDownload: Last download="+lastDownloaded);
            return rssFeed;
        } else if (state == State.TOP25 && id != lastMenuItemClicked) {
            switch (id) {
                case R.id.menuFree:
                    rssFeed += getString(R.string.top25_free);
                    break;
                case R.id.menuPaid:
                    rssFeed += getString(R.string.top25_paid);
                    break;
                case R.id.menuSongs:
                    rssFeed += getString(R.string.top25_songs);
                    break;
                default:
                    lastDownloaded = lastDownloaded.replace("10","25");
                    rssFeed=lastDownloaded;
                    Log.d(TAG, "decideOnDownload: Switched filtering from 10 to 25");


            }
            lastDownloaded = rssFeed;
            Log.d(TAG, "decideOnDownload: Last download="+lastDownloaded);
            return rssFeed;
        }

        return null;
    }

    private void setState(String state){
        if(state=="TOP10"){
            this.currentState=State.TOP10;
        }else if(state=="TOP25"){
            this.currentState=State.TOP25;
        }

    }

    private void downloadRSS(String rssFeed) {
        Log.d(TAG, "downloadRSS: Starting new AsyncTask");
        new XMLDownloader(this).execute(rssFeed);
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
        Log.d(TAG, "onAsyncTaskComplete: AsyncTask finished");
    }

    private void setupFeedEntryListView(ArrayList<FeedEntry> parsedEntries) {
        FeedListAdapter<FeedEntry> adapter = new FeedListAdapter<>(MainActivity.this, R.layout.feedentry_listitem, parsedEntries);
        feedEntryListView.setAdapter(adapter);

    }
}
