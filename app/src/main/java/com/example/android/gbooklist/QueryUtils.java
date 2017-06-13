package com.example.android.gbooklist;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by PWT on 2017/6/13.
 */

public class QueryUtils {

    private QueryUtils(){

    }

    /**
     * 对外开放的解析方法
     * @param requestUrl
     * @return
     */
    public static List<GBook> fetchGBookListData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<GBook> gBooks = extractGBooks(jsonResponse);

        // Return the {@link Event}
        return gBooks;
    }

    /**
     * 根据得到的JSON解析出实体
     * @return
     */
    private static ArrayList<GBook> extractGBooks(String gBookJSON) {

        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<GBook> gBooks = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            JSONObject jsonObject = new JSONObject(gBookJSON);
            if(jsonObject != null){
                JSONArray jsonArray = jsonObject.optJSONArray("items");
                if(jsonArray.length()>0){
                    for(int i =0;i<jsonArray.length();i++){
                        JSONObject gBook = (JSONObject) jsonArray.get(i);
                        JSONObject volumeInfo = gBook.optJSONObject("volumeInfo");
                        String title = volumeInfo.optString("title");
                        JSONArray authorsArr = volumeInfo.optJSONArray("authors");
                        StringBuilder authors = new StringBuilder();
                        if(authorsArr.length()>0){
                            for(int j = 0;j<authorsArr.length();j++){
                                authors.append(authorsArr.get(j));
                                if(j!=authorsArr.length()-1){
                                    authors.append(",");
                                }
                            }
                        }
                        String publishDate = volumeInfo.optString("authorsArr.get(i)");
                        String imageUrl = volumeInfo.optJSONObject("imageLinks").optString("smallThumbnail");
                        String bookItemShowUrl = volumeInfo.optString("canonicalVolumeLink");
                        GBook gBookEntity = new GBook(title,authors.toString(),publishDate,imageUrl,bookItemShowUrl);
                        gBooks.add(gBookEntity);
                    }
                }
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return gBooks;
    }

    /**
     * 将url字符串解析成URL
     * @param stringUrl
     * @return
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
        }
        return url;
    }

    /**
     * 连接URL,将得到的返回值存入字符串中
     * @param url
     * @return
     * @throws IOException
     */
    private static String makeHttpRequest(URL url) throws IOException{
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
            }
        } catch (IOException e) {
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * 从流中读取数据
     * @param inputStream
     * @return
     */
    private static String readFromStream(InputStream inputStream){
        StringBuilder output = new StringBuilder();
        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            try {
                String line = bufferedReader.readLine();
                while (line != null){
                    output.append(line);
                    line = bufferedReader.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return output.toString();
    }
}
