package com.example.practicewp;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RetriveFeedTask extends AsyncTask<String, Void, String> {
    private Exception exception;
    @Override
    protected String doInBackground(String... strings) {

        Log.d("xml Parser", strings[0]);
        try {
            String rawText = "";
            URL url = new URL(strings[0]);
            HttpURLConnection http=(HttpURLConnection)url.openConnection();
            http.setDoInput(true);
            http.connect();
            InputStream is = http.getInputStream();
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(is, null);
            int eventType = xpp.getEventType();
            String startTag = "";
            String text = "";
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_DOCUMENT) {
                    Log.d("xml Parser", "Start document");
                } else if(eventType == XmlPullParser.START_TAG) {
                    startTag = xpp.getName();
                    Log.d("xml Parser","Start tag "+xpp.getName());
                } else if(eventType == XmlPullParser.END_TAG) {
                    Log.d("xml Parser", "End tag "+xpp.getName());
                } else if(eventType == XmlPullParser.TEXT) {
                    Log.d("xml Parser", "Text "+xpp.getText());
                    Log.d("xml Parser", "startTag " + startTag);
                    if (startTag.equals("raw_text")) {
                        return xpp.getText();
                    }
                }
                eventType = xpp.next();
            }
            Log.d("xml Parser", "End document");
            is.close();

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
    @Override
    protected void onPostExecute(String text2) {

        super.onPostExecute(text2);
    }
}
