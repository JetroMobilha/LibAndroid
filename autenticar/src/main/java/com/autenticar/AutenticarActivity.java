package com.autenticar;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;

@SuppressWarnings("unused")
public abstract class AutenticarActivity extends AppCompatActivity {

    protected DialogFragment esperaDialogo = new EsperaDialogo();
    protected Verifica mVerifica;

    public AutenticarActivity(){
        mVerifica = new Verifica(this);
    }

    @SuppressLint("StaticFieldLeak")
    private class Tarefa extends AsyncTask<Object, Void, Object> {

        @Override
        protected void onPreExecute() {
            anteDocarregamento();
        }

        @Override
        protected Object doInBackground(Object... params) {
            return carregamentoDeDados(params[0]);
        }

        @Override
        protected void onPostExecute(Object o) {
            deposDocarregamento(o);
        }
    }

    protected abstract void anteDocarregamento();

    protected abstract Object carregamentoDeDados(Object o);

    protected abstract void deposDocarregamento(Object o);

    public void showDialogo(){

        if (esperaDialogo == null){
            esperaDialogo = new EsperaDialogo();
        }
        esperaDialogo.show(getSupportFragmentManager(),"");
    }

    public void showDialogo(DialogFragment dialogFragment){
        if (dialogFragment != null)
            dialogFragment.show(getSupportFragmentManager(),"");
    }

    public void canselarDialogo(){

        if ( esperaDialogo != null )
         esperaDialogo.dismiss();
    }

    public void canselarDialogo(DialogFragment dialogFragment){

        if ( dialogFragment != null)
            dialogFragment.dismiss();
    }

    public void iniciarTarefa(Object o){
        new Tarefa().execute(o);
    }

    public Verifica getmVerifica() {
        return mVerifica;
    }
}
