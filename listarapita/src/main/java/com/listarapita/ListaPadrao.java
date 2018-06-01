package com.listarapita;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public abstract class ListaPadrao <E> extends ListaRapidaFragmentAbstract implements LoaderManager.LoaderCallbacks<List<E>> {

    protected ArrayList<E> mList;

    public ListaPadrao() {}

    @Override
    public Loader<List<E>> onCreateLoader(int id, Bundle args) {
        return new CarregadorListaPadrao<>(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState !=null && savedInstanceState.get("list") !=null){

            //noinspection unchecked
            mList = (ArrayList<E>) savedInstanceState.get("list");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("list",mList);
    }

    @Override
    public void onLoadFinished(Loader<List<E>> loader, List<E> data) {

        if (data != null && data.size() > 0) {
            if (mRecyclerView.getVisibility() == View.VISIBLE) {

                for ( E e : data) {
                    // adicionando o novo dados
                    if (!mList.contains(e)){
                        mList.add(e);
                        mRecyclerView.getAdapter().notifyItemInserted(mList.size() - 1);
                    }

                    if ( e instanceof ObjetoLista){

                        if (((ObjetoLista) e).getItemId() != 0)
                            setPosicao(((ObjetoLista) e).getItemId());

                        if (((ObjetoLista) e).getItemIdServedor() != 0)
                            setPosicaoServidor(((ObjetoLista) e).getItemIdServedor());
                    }
                }

            } else {

                mList.addAll(data);

                int id = mList.size() - 1;
                // set o id do ultimo evento carregado

                E e = mList.get(id);

                if ( e instanceof ObjetoLista ){

                    if ( ((ObjetoLista)mList.get(id)).getItemId() != 0)
                        setPosicao(((ObjetoLista)mList.get(id)).getItemId());

                    if ( ((ObjetoLista)mList.get(id)).getItemIdServedor() != 0)
                        setPosicaoServidor(((ObjetoLista)mList.get(id)).getItemIdServedor());
                }


                mostrarConteudo();
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }

        } else {

           if ( mList.size() > 0 ){  mostrarConteudo(); }else{  mostrarSendados() ; }

        }

        if (isCarregando()) {
            setCarregando(false);
            progressBarConteudo.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<E>> loader) {}

    @Override
    protected List<?> onCrieatLista () {

        if (mList == null){
            mList = new ArrayList<>();
        }
        return this.mList;
    }

    /**
     * Metodo chamado em backgraund no carregador
     * @return retorna a lista de dados para a lista
     */
    public abstract List<E> carregarDadosCarregador();

    /**
     * Carregar novos dados para o adpeter ter por padrão set as posição para zero e limpa a lista de objetos
     * não implementar o super metodo para evitar o comportamento padrão do metodo.
     */
    @Override
    public void carregarDados() {
        setPosicao(0);
        setPosicaoServidor(0);
        mList.clear();
        mostrarProgresso();
        getLoaderManager().restartLoader(0, null, this).forceLoad();
    }

    @Override
    public void carregarMaisDados() {
        getLoaderManager().restartLoader(0, null,this).forceLoad();
    }

    @Override
    public void iniciaLord() {
        getLoaderManager().initLoader(0,null,this);
    }
}
