package com.example.top10downloaded.Adapters;


import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.top10downloaded.Models.FeedEntry;
import com.example.top10downloaded.R;

import org.w3c.dom.Text;

import java.util.List;

public class FeedListAdapter extends ArrayAdapter{

    private static final String TAG = "FeedListAdapter";
    private final int layoutResource;
    private final LayoutInflater inflater;
    private List<FeedEntry> feedEntryList;

    public FeedListAdapter(@NonNull Context context, @LayoutRes int resource, List<FeedEntry> feedEntryList) {
        super(context, resource);
        this.layoutResource = resource;
        this.feedEntryList = feedEntryList;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override // Gets called whenever Listview is initialized or scrolled out of view for new items...
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(layoutResource,parent,false);
        TextView tvName = (TextView) view.findViewById(R.id.tvName);
        TextView tvArtist = (TextView) view.findViewById(R.id.tvArtist);
        TextView tvSummary = (TextView) view.findViewById(R.id.tvSummary);

        FeedEntry currentEntry = feedEntryList.get(position);

        tvName.setText(currentEntry.getName());
        tvArtist.setText(currentEntry.getArtist());
        tvSummary.setText(currentEntry.getSummary());
        return view;
    }

    @Override
    public int getCount() {
        return feedEntryList.size();
    }
}
