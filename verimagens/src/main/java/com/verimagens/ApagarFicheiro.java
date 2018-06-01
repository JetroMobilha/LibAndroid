package com.verimagens;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.imagens.Imagens;

import java.io.File;

/**
 * Created by Jetro Mobilha on 02/11/2016.
 *
 */
public class ApagarFicheiro extends Service {

    private static int control = 0 ;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Salvar().execute();
        return START_STICKY;
    }

    private class Salvar extends AsyncTask< Void,Void,Void>{

        @Override
        protected final Void doInBackground( Void ... params) {

            File file = new File(Imagens.getInstance().getCaminhoEmagemsTpm());
             File[] files =  file.listFiles();

            if (files!=null){

                for (File file1 : files) {

                    if (file1.delete())
                        Log.d("arquivo", "Ficheiro apagada :" + file1);
                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            emServico();
        }

        @Override
        protected void onPostExecute(Void aVoid) {

                foraSerco();
        }

        }

    private void emServico(){
        control++;
    }

    private void foraSerco() {
        control--;
        if (control <= 0)
            stopSelf();
    }
}
