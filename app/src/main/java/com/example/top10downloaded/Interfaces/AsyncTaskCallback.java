package com.example.top10downloaded.Interfaces;

/**
 * Interface to be used by an Activity that wants to receive
 * updates when an AsyncTask has completed.
 */

public interface AsyncTaskCallback {

    public void onAsyncTaskComplete(Object obj);
}
