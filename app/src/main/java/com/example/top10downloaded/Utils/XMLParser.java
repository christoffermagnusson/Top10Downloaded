package com.example.top10downloaded.Utils;


import android.util.Log;

import com.example.top10downloaded.Models.FeedEntry;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class XMLParser {

    private static final String TAG = "XMLParser";

    private ArrayList<FeedEntry> parsedEntries;


    public XMLParser() {
        this.parsedEntries = new ArrayList<FeedEntry>();
    }

    public ArrayList<FeedEntry> getParsedEntries() {
        return parsedEntries;
    }
    private boolean status = true;
    private FeedEntry currentRecord = null;
    private boolean inEntry = false;
    private String textValue = "";

    public boolean parse(String xmlData){


        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlData));
            int eventType = parser.getEventType();

            while(eventType != parser.END_DOCUMENT){
                String tagName = parser.getName(); // current tag
                switch(eventType){
                    case XmlPullParser.START_TAG: // checking for starting tag i.e <name>
                        Log.d(TAG, "parse: Starting tag for "+tagName);
                        if(tagName.equals("entry")){
                            inEntry = true;
                            currentRecord = new FeedEntry();
                        }
                        break;

                    case XmlPullParser.TEXT:  // checking for data in between tags i.e <name> CHRISTOFFER </name>
                        textValue = parser.getText();
                        break;

                    case XmlPullParser.END_TAG: // checking for closing tags i.e </name>
                        Log.d(TAG, "parse: Ending tag for "+tagName);
                        if(inEntry){
                            SetupFeedEntry(tagName);
                        }
                        break;
                    default:
                        // Nothing else to do
                }
                eventType = parser.next();
            }


        }catch(Exception e){
            status = false;
            e.printStackTrace();
        }

        return status;
    }

    private void SetupFeedEntry(String tagName) {
        Log.d(TAG, "SetupFeedEntry: Setting up FeedEntry");
        switch(tagName){
            case "entry":
                parsedEntries.add(currentRecord);
                inEntry=false;
                break;
            case "name":
                currentRecord.setName(textValue);
                break;
            case "artist":
                currentRecord.setArtist(textValue);
                break;
            case "releaseDate":
                currentRecord.setReleaseDate(textValue);
                break;
            case "summary":
                currentRecord.setSummary(textValue);
                break;
            case "image":
                currentRecord.setImageUrl(textValue);
                break;
        }

    }
}
