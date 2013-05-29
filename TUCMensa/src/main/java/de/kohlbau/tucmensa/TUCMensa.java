package de.kohlbau.tucmensa;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by tobias on 5/25/13.
 */
public class TUCMensa extends Activity {

    TextView tv;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tucmensa);
        String baseURL =  "http://www.swcz.de/bilderspeiseplan/xml.php?plan=1479835489";
        String year = "2013";
        String month = "5";
        String day = "28";
        String url = baseURL + "&" + year + "&" + month + "&" + day;
        DownloadTUCMensa tuc = new DownloadTUCMensa();
        tuc.execute(url);
        tv = (TextView) findViewById(R.id.text);

    }

    private class DownloadTUCMensa extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            StringBuffer stringBuffer = new StringBuffer("");
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String s = "";

                while((s = bufferedReader.readLine()) != null) {
                    stringBuffer.append(s);
                }

            } catch (IOException e) {
                Log.e("TUC", e.toString());
            }

            return stringBuffer.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            DefaultHandler handler = new DefaultHandler() {
                @Override
                public void startDocument()
                {
                    Log.d("TUC", "Document starts.");
                    tv.setText(tv.getText() + "\n" + "Document starts.");
                }

                @Override
                public void endDocument()
                {
                    Log.d("TUC", "Document ends.");
                    tv.setText(tv.getText() + "\n" + "Document ends.");
                }
                @Override
                public void startElement( String namespaceURI, String localName,
                                          String qName, Attributes atts )
                {
                    Log.d("TUC", "qName: " + qName);
                    tv.setText(tv.getText() + "\n" + "qName: " + qName);
                    for ( int i = 0; i < atts.getLength(); i++ ){
                        Log.d("TUC", "Attribut no. " + i + ": " + atts.getQName(i) + " = " + atts.getValue(i) + "\n");
                        tv.setText(tv.getText() + "\n" + "Attribut no. " + i + ": " + atts.getQName(i) + " = " + atts.getValue(i));
                    }
                }
                @Override
                public void characters( char[] ch, int start, int length )
                {
                    Log.d("TUC", "Characters:");
                    tv.setText(tv.getText() + "\n" + "Characters:" + "\n");

                    for ( int i = start; i < (start + length); i++ ){
                        Log.d("TUC", "" + ch[i]);
                        tv.setText(tv.getText() + "" + ch[i]);
                    }
                }
            };


            InputSource source = new InputSource(new StringReader(s));
            saxParser.parse( source, handler );
        } catch (Exception e) {
            Log.e("TUC", e.toString() + "here");

        }

        }
    }


}