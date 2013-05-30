package de.kohlbau.tucmensa;

import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MensaXMLParser {

    private static final String ns = null;

    public ArrayList<Meal> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readBillOfFare(parser);
        } finally {
            in.close();
        }
    }

    private ArrayList<Meal> readBillOfFare(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<Meal> entries = new ArrayList<Meal>();

        parser.require(XmlPullParser.START_TAG, ns, "speiseplan");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("essen")) {
                entries.add(readMeal(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private Meal readMeal(XmlPullParser parser) throws XmlPullParserException, IOException {
        Meal meal = new Meal();
        parser.require(XmlPullParser.START_TAG, ns, "essen");

        double[] price = new double[3];


        meal.setId(Integer.valueOf(parser.getAttributeValue(null, "id")));
        meal.setCategory(parser.getAttributeValue(null, "kategorie"));
        meal.setRating(Integer.parseInt(parser.getAttributeValue(null, "bewertung")));
        meal.setImageAvailable(Boolean.parseBoolean(parser.getAttributeValue(null, "img")));
        meal.setIngredients(new boolean[]{
                Boolean.parseBoolean(parser.getAttributeValue(null, "schwein")),
                Boolean.parseBoolean(parser.getAttributeValue(null, "rind")),
                Boolean.parseBoolean(parser.getAttributeValue(null, "vegetarisch")),
                Boolean.parseBoolean(parser.getAttributeValue(null, "alkohol"))
        });

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                Log.e("TUCMensa", parser.getName());
                continue;
            }
            String name = parser.getName();
            if (name.equals("deutsch")) {
                meal.setDescription(readDescription(parser));
            } else if (name.equals("pr")) {
                String group = parser.getAttributeValue(null, "gruppe");
                if (group.equals("S")) {
                    price[0] = readPrice(parser);
                } else if (group.equals("M")) {
                    price[1] = readPrice(parser);
                } else if (group.equals("G")) {
                    price[2] = readPrice(parser);
                }
            } else {
                skip(parser);
            }
        }
        meal.setPrice(price);

        if(meal.isImageAvailable()) {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(
                    XMLLoader.downloadURL(XMLLoader.BASE_URL + "/bilder_190/"+ meal.getId() +".png"));
            meal.setImage(BitmapFactory.decodeStream(bufferedInputStream));
        }

        return meal;
    }

    private String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "deutsch");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "deutsch");
        return title;
    }

    private double readPrice(XmlPullParser parser) throws IOException, XmlPullParserException {
        double price;
        parser.require(XmlPullParser.START_TAG, ns, "pr");
        String priceString = readText(parser);
        if (!priceString.equals("")) {
            price = Double.valueOf(priceString);
        } else {
            price = 0;
        }
        parser.require(XmlPullParser.END_TAG, ns, "pr");
        return price;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

}
