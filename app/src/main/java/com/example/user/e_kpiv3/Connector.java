package com.example.user.e_kpiv3;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by USER on 11/24/2016.
 */

public class Connector {

    public static HttpURLConnection connect(String urlAddress)
    {

        try {
            URL url = new URL(urlAddress);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            //SET PROPERTIES
            con.setRequestMethod("GET");
            con.setConnectTimeout(20000);
            con.setReadTimeout(20000);
            con.setDoInput(true);

            return con;

        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }
}
