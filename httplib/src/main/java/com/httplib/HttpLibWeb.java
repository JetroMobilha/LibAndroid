package com.httplib;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
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

    public synchronized HttpURLConnection request(String host, String path, int metodo, Map<String, Object> params) throws Exception {

        HttpURLConnection connection = request(host, path);

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

    public synchronized HttpURLConnection request(String host, String path, int metodo, Map<String, Object> params, boolean useTempo) throws Exception {

        HttpURLConnection connection = request(host, path, metodo, params);
        if (useTempo) connection.setConnectTimeout(60000);
        return connection;
    }

    public synchronized HttpURLConnection request(String host, String path, int metodo, Map<String, Object> params, int tempoConecção) throws Exception {
        HttpURLConnection connection = request(host, path, metodo, params);
        connection.setConnectTimeout(tempoConecção);
        return connection;
    }

    public synchronized HttpURLConnection request(String host, String path) throws Exception {
        String urlString = host;
        if (path != null) urlString += path;
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

    private String getStringInputStream(InputStream is) throws Exception {

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
            throw new Exception(TAG + " comonicação HttpLibWeb erro na resposta :" + httpURLConnection.getResponseMessage());
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
            throw new Exception(TAG + " comonicação http  WebComons erro na resposta :" + httpURLConnection.getResponseMessage());
        }
    }

    public synchronized HttpURLConnection request(String host, String path, String file, Map<String, Object> params) throws Exception {

        HttpURLConnection conn = request(host, path);
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;

        File sourceFile = new File(file);
        if (!sourceFile.isFile()) {
            Log.e("Huzza", "Source File Does not exist");
            return null;
        }

        FileInputStream fileInputStream = new FileInputStream(sourceFile);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("ENCTYPE", "multipart/form-data");
        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
        conn.setRequestProperty("file", file);
        dos = new DataOutputStream(conn.getOutputStream());

        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"myFile\";filename=\"" + file + "\"" + lineEnd);
        dos.writeBytes(lineEnd);

        bytesAvailable = fileInputStream.available();

        bufferSize = Math.min(bytesAvailable, maxBufferSize);
        buffer = new byte[bufferSize];

        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            dos.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        dos.writeBytes(lineEnd);
        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

        fileInputStream.close();
        dos.flush();
        dos.close();

        return conn;
    }


    public synchronized HttpURLConnection requestArquivo(String host, String path, String file, Map<String, Object> params) throws Exception {

        HttpURLConnection connection = request(host, path);
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";


        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        //noinspection PointlessArithmeticExpression
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(file);

        FileInputStream fileInputStream = new FileInputStream(selectedFile);
        connection.setDoInput(true);//Allow Inputs
        connection.setDoOutput(true);//Allow Outputs
        connection.setUseCaches(false);//Don't use a cached Copy
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("ENCTYPE", "multipart/form-data");
        connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
        connection.setRequestProperty("uploaded_file", file);

        //creating new dataoutputstream
        DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());

        //writing bytes to data outputstream
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                + file + "\"" + lineEnd);

        dataOutputStream.writeBytes(lineEnd);

        //returns no. of bytes present in fileInputStream
        bytesAvailable = fileInputStream.available();
        //selecting the buffer size as minimum of available bytes or 1 MB
        bufferSize = Math.min(bytesAvailable, maxBufferSize);
        //setting the buffer as byte array of size of bufferSize
        buffer = new byte[bufferSize];

        //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        //loop repeats till bytesRead = -1, i.e., no bytes are left to read
        while (bytesRead > 0) {
            //write the bytes read from inputstream
            dataOutputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        dataOutputStream.writeBytes(lineEnd);

        String urlParameters = criarParams(params);
        dataOutputStream.writeBytes(urlParameters);
        dataOutputStream.writeBytes(lineEnd);

        dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

        //closing the input and output streams
        fileInputStream.close();
        dataOutputStream.flush();
        dataOutputStream.close();
        return connection;
    }

    public synchronized HttpURLConnection requestArquivo(String host, String path, Uri uri, Context context, Map<String, Object> params) throws Exception {

        HttpURLConnection connection = request(host, path);
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";


        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        //noinspection PointlessArithmeticExpression
        int maxBufferSize = 1 * 1024 * 1024;

        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

        parcelFileDescriptor.close();
        FileInputStream fileInputStream = new FileInputStream(fileDescriptor);
        connection.setDoInput(true);//Allow Inputs
        connection.setDoOutput(true);//Allow Outputs
        connection.setUseCaches(false);//Don't use a cached Copy
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("ENCTYPE", "multipart/form-data");
        connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
        connection.setRequestProperty("uploaded_file", uri.toString());

        //creating new dataoutputstream
        DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());

        //writing bytes to data outputstream
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                + uri.toString() + "\"" + lineEnd);

        dataOutputStream.writeBytes(lineEnd);

        //returns no. of bytes present in fileInputStream
        bytesAvailable = fileInputStream.available();
        //selecting the buffer size as minimum of available bytes or 1 MB
        bufferSize = Math.min(bytesAvailable, maxBufferSize);
        //setting the buffer as byte array of size of bufferSize
        buffer = new byte[bufferSize];

        //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        //loop repeats till bytesRead = -1, i.e., no bytes are left to read
        while (bytesRead > 0) {
            //write the bytes read from inputstream
            dataOutputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        dataOutputStream.writeBytes(lineEnd);

        String urlParameters = criarParams(params);
        dataOutputStream.writeBytes(urlParameters);
        dataOutputStream.writeBytes(lineEnd);

        dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

        //closing the input and output streams
        fileInputStream.close();
        dataOutputStream.flush();
        dataOutputStream.close();
        return connection;
    }

}
