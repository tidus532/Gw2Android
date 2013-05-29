package com.example.Gw2Android;

import android.os.StrictMode;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by tidus on 26/05/13.
 */
public class Gw2ApiBase {
    protected final static String event_world_names_url = "https://api.guildwars2.com/v1/world_names.json";
    protected final static String event_url = "https://api.guildwars2.com/v1/events.json";
    protected final static String event_map_names_url = "https://api.guildwars2.com/v1/map_names.json";

    public Gw2ApiBase() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }

    public String fetchJSONfromURL(String url){
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
