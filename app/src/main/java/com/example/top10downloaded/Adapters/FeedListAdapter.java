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
        ViewHolder holder;
        if(convertView==null) { // minimize amount of views by only creating when needed
            convertView = inflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder(convertView); // holderObj for instantiating TextView exactly once
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        FeedEntry currentEntry = feedEntryList.get(position);

        holder.tvName.setText(currentEntry.getName());
        holder.tvArtist.setText(currentEntry.getArtist());
        holder.tvSummary.setText(currentEntry.getSummary());
        return convertView;
    }

    /**
     * Holding variables of UI elements in order
     * to not instantiate multiple times, thus
     * saving processing power
     */
    private class ViewHolder{
        final TextView tvName;
        final TextView tvArtist;
        final TextView tvSummary;

        ViewHolder(View view){
            this.tvName = view.findViewById(R.id.tvName);
            this.tvArtist = view.findViewById(R.id.tvArtist);
            this.tvSummary = view.findViewById(R.id.tvSummary);
        }
    }

    @Override
    public int getCount() {
        return feedEntryList.size();
    }
}
