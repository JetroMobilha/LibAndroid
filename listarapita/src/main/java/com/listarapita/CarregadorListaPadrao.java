package com.listarapita;

import android.support.v4.content.AsyncTaskLoader;

import java.util.List;


public class CarregadorListaPadrao <E> extends AsyncTaskLoader<List<E>> {

    private ListaPadrao fragment;
    private List<E> mData;

    CarregadorListaPadrao(ListaPadrao listaPadrao) {
        //noinspection ConstantConditions
        super(listaPadrao.getContext());
        this.fragment = listaPadrao;
    }

    @Override
    public List <E> loadInBackground() {
        //noinspection unchecked
        return   fragment.carregarDadosCarregador();
    }

    @Override
    public void deliverResult(List <E> data) {

        if (isReset()) {

            relealseResouces(data);
            return;
        }

        List<E> oldData = mData;
        mData = data;
        if (isStarted()) {

            super.deliverResult(data);
        }

        if (oldData != null && oldData != data) {
            relealseResouces(oldData);
        }
    }

    @Override
    protected void onStartLoading() {

        if (mData != null) {

            deliverResult(mData);
        }

        // if (mObserver==null){ // insatiar observador }

        if (takeContentChanged() || mData == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStartLoading();

        if (mData != null) {
            mData = null;
        }
    }

    @Override
    public void onCanceled(List <E> data) {
        super.onCanceled(data);
    }

    @SuppressWarnings("unused")
    private void relealseResouces(List data) {}
}
