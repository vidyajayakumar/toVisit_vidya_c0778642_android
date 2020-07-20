package com.vidya.toVisit_vidya_c0778642_android.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FetchURL {
    public
    String readUrl(String myUrl) throws IOException {
        String      data        = "";
        InputStream inputStream = null;

        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(myUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer   sb = new StringBuffer();
            // we read data line by line
            String line = "";
            while ((line = br.readLine()) != null)
                sb.append(line);

            data = sb.toString();
            br.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
