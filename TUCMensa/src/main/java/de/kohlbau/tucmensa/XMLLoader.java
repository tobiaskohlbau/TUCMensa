package de.kohlbau.tucmensa;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class XMLLoader extends AsyncTask<Void, Void, String> {
    public static final String BASE_URL = "http://www.swcz.de/bilderspeiseplan";
    public static final int MENSA_REICH = 1479835489;
    public static final int MENSA_STRANA = 773823070;

    private Context context;
    private int year;
    private int month;
    private int day;
    private int mensa;

    ArrayList<Meal> entries = null;

    public XMLLoader(Context context, int year, int month, int day, int mensa) {
        this.context = context;
        this.year = year;
        this.month = month;
        this.day = day;
        this.mensa = mensa;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            return loadXML(BASE_URL + "/xml.php?plan="+ mensa + "&jahr=" + year + "&monat=" + (month + 1) + "&tag=" + day);
        } catch (IOException e) {
            Log.e("TUCMensa", e.toString() + "doInBackground");
        } catch (XmlPullParserException e) {
            Log.e("TUCMensa", e.toString() + "doInBackground");
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {

    }


    private String loadXML(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        MensaXMLParser mensaXMLParser = new MensaXMLParser();

        try {
            stream = downloadURL(urlString);
            entries = mensaXMLParser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        Intent intent = new Intent("de.kohlbau.tucmensa.XMLParsed");
        intent.putParcelableArrayListExtra("entries", entries);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        return null;
    }

    public static InputStream downloadURL(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000 );
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }
}
