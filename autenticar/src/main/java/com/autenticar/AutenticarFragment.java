package com.autenticar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

@SuppressWarnings("unused")
public abstract class AutenticarFragment extends Fragment {

    protected Verifica mVerifica;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mVerifica = new Verifica(getActivity());
    }

    private EsperaDialogo esperaDialogo = new EsperaDialogo();

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
        esperaDialogo.show(getActivity().getSupportFragmentManager(),"");
    }

    public void showDialogo(DialogFragment dialogFragment){
        if (dialogFragment != null)
            dialogFragment.show(getActivity().getSupportFragmentManager(),"");
    }

    public void canselarDialogo(){
        if (esperaDialogo != null)
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
