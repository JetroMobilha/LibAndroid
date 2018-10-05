package com.imagens;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;



/**
 * Created by Jetro Mobilha on 11/11/2016.
 *
 */

 public  class SalvarImagmAarelho extends AsyncTask<Boolean,Void,Boolean> {

    private String nome;
    Imagens imagens = Imagens.getInstance();

    public SalvarImagmAarelho(String nome){
        this.nome= nome;
    }

    @Override
    protected Boolean doInBackground(Boolean... params) {

        return imagens.saveArrayToSDCard( nome , Imagens.getInstance().convertBitmapPraBytArray(imagens.getImagemBitmap(nome),100));
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        if (aBoolean){
            Toast.makeText(imagens.getmContext().getApplicationContext(),"Imagen salva ",Toast.LENGTH_SHORT).show();
        }else{

            Toast.makeText(imagens.getmContext().getApplicationContext(),"Imagem não  salva ",Toast.LENGTH_SHORT).show();
        }
    }
}
