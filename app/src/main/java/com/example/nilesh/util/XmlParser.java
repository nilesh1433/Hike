package com.example.nilesh.util;

/**
 * Created by Nilesh on 3/28/2015.
 */
import com.example.nilesh.model.UserDetails;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;


public class XmlParser {
    private ArrayList<UserDetails> users= new ArrayList<UserDetails>();
    private UserDetails user;
    private String text;

    public ArrayList<UserDetails> getUsers() {
        return users;
    }

    public ArrayList<UserDetails> parse(InputStream is) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser  parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("user")) {
                            // create a new instance of employee
                            user = new UserDetails();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("user")) {
                            // add employee object to list
                            users.add(user);
                        }else if (tagname.equalsIgnoreCase("username")) {
                            user.setUserName(text);
                        }  else if (tagname.equalsIgnoreCase("name")) {
                            user.setName(text);
                        }
                        break;

                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}

        return users;
    }
}