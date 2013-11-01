/*      This file is part of Gw2Android.

        Gw2Android is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        Gw2Android is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with Gw2Android.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.github.gw2app.events;

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
    protected final static String event_names_url = "https://api.guildwars2.com/v1/event_names.json";

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
