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

    /**
     * Filtering states of the app
     */
    private enum State{
        TOP10,
        TOP25
    }

    private State currentState = State.TOP10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        feedEntryListView = (ListView) findViewById(R.id.feedEntryList);

        String initialDownload = getString(R.string.baseUrl)+getString(R.string.top10_free);
        downloadRSS(initialDownload);
        lastDownloaded = initialDownload;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds_menu, menu);
        top10Item = menu.findItem(R.id.menuTop10);
        top25Item = menu.findItem(R.id.menuTop25);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String rssFeed = getString(R.string.baseUrl);

        if(item.getItemId()==R.id.menuTop10){
            top10Item.setChecked(true);
            setState(State.TOP10);
        }else if(item.getItemId()==R.id.menuTop25){
            top25Item.setChecked(true);
            setState(State.TOP25);
        }

        downloadRSS(decideOnDownload(item.getItemId(), rssFeed,currentState));


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

        if (state == State.TOP10) {
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
                    Log.d(TAG, "decideOnDownload: Result "+rssFeed);
            }
            lastDownloaded = rssFeed;
            Log.d(TAG, "decideOnDownload: Last download="+lastDownloaded);
        } else if (state == State.TOP25) {
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
                    Log.d(TAG, "decideOnDownload: Result "+rssFeed);


            }
            lastDownloaded = rssFeed;
            Log.d(TAG, "decideOnDownload: Last download="+lastDownloaded);
        }
        return rssFeed;
    }

    private void setState(State state){
        this.currentState=state;
    }

    private void downloadRSS(String rssFeed){
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
        FeedListAdapter adapter = new FeedListAdapter(MainActivity.this, R.layout.feedentry_listitem, parsedEntries);
        feedEntryListView.setAdapter(adapter);

    }
}
