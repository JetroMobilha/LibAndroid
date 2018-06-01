package com.httplib;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class HttpLibWeb {

    private String TAG = "HttpLibWeb";

    public static final int POST = 1;
     public static final int GET = 2;

    public HttpLibWeb() {
    }


    public synchronized HttpURLConnection request(String host, String path, int metodo, Map<String, Object> params) throws Exception {

        HttpURLConnection connection = request(host,path);

        if (metodo == 1)
            connection.setRequestMethod("POST");

        if (metodo == 2)
            connection.setRequestMethod("GET");

        connection.setDoOutput(true);

        if (params != null && params.size() > 0) {
            String urlParameters = criarParams(params);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
        }

        return connection;
    }

    public synchronized HttpURLConnection request(String host, String path, int metodo, Map<String, Object> params,boolean useTempo) throws Exception {

        HttpURLConnection connection = request(host,path,metodo,params);
        if (useTempo) connection.setConnectTimeout(60000);
        return connection;
    }

    public synchronized HttpURLConnection request(String host, String path, int metodo, Map<String, Object> params,int tempoConecção) throws Exception {
        HttpURLConnection connection = request(host,path,metodo,params);
        connection.setConnectTimeout(tempoConecção);
        return connection;
    }


    public synchronized HttpURLConnection request(String host, String path) throws Exception {

        String urlString = host;

        if (path!=null) urlString += path;

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(false);
        connection.setUseCaches(false);
        return connection;
    }

    private String criarParams(Map<String, Object> paramsMap) throws Exception {
        StringBuilder paramsData = new StringBuilder();

        Log.d(TAG, "Parametros : " + paramsMap.toString());
        for (Map.Entry<String, Object> param : paramsMap.entrySet()) {
            if (paramsData.length() != 0) paramsData.append('&');

            paramsData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            paramsData.append('=');
            paramsData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));

        }
        return paramsData.toString();
    }

    private String getStringInputStream(InputStream is) throws Exception  {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    public synchronized String resPonde(HttpURLConnection httpURLConnection) throws Exception {

        httpURLConnection.connect();
        String retorno;
        if (httpURLConnection != null && httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            retorno = getStringInputStream(httpURLConnection.getInputStream());
            httpURLConnection.disconnect();
            Log.d(TAG, retorno);
            return retorno;

        } else {

            assert httpURLConnection != null;
            throw new Exception( TAG + " comonicação HttpLibWeb erro na resposta :" + httpURLConnection.getResponseMessage());
        }
    }

    public synchronized JSONObject resPondeJSON(HttpURLConnection httpURLConnection) throws Exception {

        httpURLConnection.connect();
        String retorno;

        if (httpURLConnection != null && httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

            retorno = getStringInputStream(httpURLConnection.getInputStream());
            httpURLConnection.disconnect();
            Log.d(TAG, retorno);
            return new JSONObject(retorno);

        } else {

            assert httpURLConnection != null;
            throw new Exception(TAG +  " comonicação http  WebComons erro na resposta :" + httpURLConnection.getResponseMessage());
        }
    }
}
