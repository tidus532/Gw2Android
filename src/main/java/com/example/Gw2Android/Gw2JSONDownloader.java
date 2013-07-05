package com.example.Gw2Android;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by tidus on 27/06/13.
 */
public class Gw2JSONDownloader {
    public final static String event_world_names_url = "https://api.guildwars2.com/v1/world_names.json";
    public final static String event_url = "https://api.guildwars2.com/v1/events.json";
    public final static String event_map_names_url = "https://api.guildwars2.com/v1/map_names.json";
    public final static String event_names_url = "https://api.guildwars2.com/v1/event_names.json";

    public static boolean internetAvailable(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static String downloadJSON(String url) {
        InputStream is = null;
        String result = null;

        BufferedReader reader = null;
        try {
            is = new URL(url).openStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();

        String line = null;

        try {
            assert reader != null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result = sb.toString();
    }

}
