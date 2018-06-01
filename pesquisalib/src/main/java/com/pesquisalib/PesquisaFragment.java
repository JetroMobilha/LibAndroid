package com.pesquisalib;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.listarapita.ObjetoLista;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public abstract class PesquisaFragment<T> extends Fragment {

    protected RecyclerView mRecyclerView;
    protected ProgressBar progressBarConteudo;
    protected NestedScrollView progressBar;
    protected NestedScrollView mSemDados;

    protected ArrayList<T> pesquisaItemList;
    protected String mpesquisa;
    protected String mPesquisaAntiga;
    protected boolean pesquisar;
    protected TarefaPesquisar tarefaPesquisar;
    protected TextView texto;
    protected lal.adhish.gifprogressbar.GifView imagem;

    protected int posicao = 0;
    protected int posicaoServidor = 0;
    protected boolean baixando = true;
    public boolean servidor = false;


    // atributo para controlar se a tarefa ja foi canselada
    protected boolean canceladaTarefa = true;

    public PesquisaFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mpesquisa = savedInstanceState.getString("mpesquisa", "");
            mPesquisaAntiga = savedInstanceState.getString("mPesquisaAntiga", "");
            pesquisar = savedInstanceState.getBoolean("pesquisar", false);
            canceladaTarefa = savedInstanceState.getBoolean("canceladaTarefa", true);
            setPosicao(savedInstanceState.getInt("posica"));
            setPosicaoServidor(savedInstanceState.getInt("posicaServidor"));
            setBaixando(savedInstanceState.getBoolean("baixar"));
            setServidor(savedInstanceState.getBoolean("servidor"));
            //noinspection unchecked
            setPesquisaItemList((ArrayList<T>) savedInstanceState.get("list"));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("mpesquisa", mpesquisa);
        outState.putString("mPesquisaAntiga", mPesquisaAntiga);
        outState.putBoolean("pesquisar", pesquisar);
        outState.putBoolean("canceladaTarefa", canceladaTarefa);
        outState.putInt("posica", getPosicao());
        outState.putInt("posicaServidor", getPosicaoServidor());
        outState.getBoolean("baixar", isBaixando());
        outState.getBoolean("servidor", isServidor());
        //noinspection unchecked
        outState.putSerializable("list", getPesquisaItemList());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.root, container, false);

        mRecyclerView = view.findViewById(R.id.lista);
        mRecyclerView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        progressBar = view.findViewById(R.id.lista_progresso);
        progressBarConteudo = view.findViewById(R.id.lista_progresso_caregamento);
        mSemDados = view.findViewById(R.id.lista_semdados_raiz);
        texto = view.findViewById(R.id.lista_sem_dados_texto);
        imagem = view.findViewById(R.id.lista_imagem_sem_dados);

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.lista_swipe_refresh);
        swipeRefreshLayout.setEnabled(false);

        // Enviando parametro para o loarder
        pesquisaItemList = new ArrayList<>();


        AppCompatButton carregar = view.findViewById(R.id.lista_carregar);
        carregar.setVisibility(View.GONE);

        mostrarProgresso();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        setAdptadorRecyclerView(mRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

                int pos = mRecyclerView.getAdapter().getItemCount() - 2;

                if (pos <= layoutManager.findLastVisibleItemPosition() + 1) {

                    if (isBaixando()) {
                        setmPesquisaAntiga(getMpesquisa());
                        progressBarConteudo.setVisibility(View.VISIBLE);
                        setBaixando(false);
                        lancarTarefa();
                    }
                }
            }
        });

        lancarTarefa();
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        // iniciando atributo para comecar a pesquisa
        pesquisar = true;
    }

    public synchronized Integer getPosicao() {
        return posicao;
    }

    public synchronized void setPosicao(int posicao) {
        this.posicao = posicao;
    }

    public synchronized void setBaixando(boolean baixando) {
        this.baixando = baixando;
    }

    public synchronized boolean isBaixando() {
        return baixando;
    }


    public synchronized String getMpesquisa() {
        return mpesquisa;
    }


    public synchronized void setMpesquisa(String mpesquisa) {
        this.mpesquisa = mpesquisa;
    }

    public synchronized void setNovapesquisa(String mpesquisa) {

        if (!mpesquisa.isEmpty()) {
            // guardar a antiga pesquisa
            if (getMpesquisa() == null) {

                setmPesquisaAntiga(mpesquisa);
                setMpesquisa(mpesquisa);

                if (pesquisar) {
                    mostrarProgresso();
                    // iniciando a tarefa
                    pequisarTarefa();
                }

            } else if (pesquisar) {

                setmPesquisaAntiga(getMpesquisa());
                setMpesquisa(mpesquisa);

                mostrarProgresso();
                // iniciando a tarefa
                pequisarTarefa();
            }
        }
    }

    public String getmPesquisaAntiga() {
        return mPesquisaAntiga;
    }

    public void setmPesquisaAntiga(String mPesquisaAntiga) {
        this.mPesquisaAntiga = mPesquisaAntiga;
    }

    public Integer getPosicaoServidor() {
        return posicaoServidor;
    }

    public void setPosicaoServidor(Integer posicaoServidor) {
        this.posicaoServidor = posicaoServidor;
    }

    public boolean isCanceladaTarefa() {
        return canceladaTarefa;
    }

    public void setCanceladaTarefa(boolean canceladaTarefa) {
        this.canceladaTarefa = canceladaTarefa;
    }

    // tarefa para a pesquisa

    @SuppressLint("StaticFieldLeak")
    protected class TarefaPesquisar extends AsyncTask<String, Void, List<T>> {

        @Override
        protected void onPostExecute(List<T> pesquisaItems) {

            if (pesquisaItems != null && pesquisaItems.size() > 0) {

                // adicionando dados na lista se estiver visivel
                if (mRecyclerView.getVisibility() == View.VISIBLE) {

                    for (T pesquisaItem : pesquisaItems) {

                        if (!pesquisaItemList.contains(pesquisaItem)) {

                            pesquisaItemList.add(pesquisaItem);
                            mRecyclerView.getAdapter().notifyItemInserted(pesquisaItemList.size() - 1);

                            if (pesquisaItem instanceof ObjetoLista) {
                                if (((ObjetoLista) pesquisaItem).getItemId() > 0)
                                    setPosicao(((ObjetoLista) pesquisaItem).getItemId());
                                if (((ObjetoLista) pesquisaItem).getItemIdServedor() > 0)
                                    setPosicaoServidor(((ObjetoLista) pesquisaItem).getItemIdServedor());

                            } else {
                                inplementarObjetoLista();
                            }
                        }
                    }

                } else {

                    // limpando daos da ultima pesquisa para setar o nova presquisa

                    pesquisaItemList.clear();
                    pesquisaItemList.addAll(pesquisaItems);

                    int id = pesquisaItemList.size() - 1;
                    // set o id do ultimo evento carregado
                    if (pesquisaItems.get(id) instanceof ObjetoLista) {
                        if (((ObjetoLista) pesquisaItems.get(id)).getItemId() > 0)
                            setPosicao(((ObjetoLista) pesquisaItems.get(id)).getItemId());
                        if (((ObjetoLista) pesquisaItems.get(id)).getItemId() > 0)
                            setPosicaoServidor(((ObjetoLista) pesquisaItems.get(id)).getItemIdServedor());

                    } else {
                        inplementarObjetoLista();
                    }
                }

                if (mRecyclerView.getVisibility() == View.GONE)
                    mRecyclerView.getAdapter().notifyDataSetChanged();

                mostrarRecycler();
            } else {

                if (progressBar.getVisibility() == View.VISIBLE) mostrarImagem();
            }

            // livre para carregar maisdados e servidor
            if (!isBaixando()) {
                progressBarConteudo.setVisibility(View.GONE);
                setBaixando(true);
            }
        }


        @Override
        protected List<T> doInBackground(String... params) {
            return carregarDados(TarefaPesquisar.this);
        }
    }

    /**
     * Metodo usado para lançar uma tarefa com  canselamento
     * de antiga tare se estiver cido  executado
     */
    private void pequisarTarefa() {

        if (tarefaPesquisar.isCancelled()) {

            tarefaPesquisar = new TarefaPesquisar();
            tarefaPesquisar.execute(getMpesquisa());
            // permitir canselamento de tarefa
            setCanceladaTarefa(true);
        } else {

            if (isCanceladaTarefa()) {
                tarefaPesquisar.cancel(true);
                // empedir canselamento de tarefa
                setCanceladaTarefa(false);
                pequisarTarefa();
            } else {
                pequisarTarefa();
            }

        }
    }


    private void mostrarProgresso() {
        progressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mSemDados.setVisibility(View.GONE);
    }

    private void mostrarRecycler() {

        progressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mSemDados.setVisibility(View.GONE);
    }

    private void mostrarImagem() {

        progressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mSemDados.setVisibility(View.VISIBLE);
        imagem.setVisibility(View.GONE);
        texto.setText(textoDica());
    }

    /**
     * Metodo usado para lançar uma tarefa sem canselamento
     * de antiga tare se estiver cido  executado
     */
    protected void lancarTarefa() {
        tarefaPesquisar = new TarefaPesquisar();
        tarefaPesquisar.execute();
    }

    private void inplementarObjetoLista() {
        try {
            throw new Exception("Objeto para a lista não Implementou ObjetoLista");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void setAdptadorRecyclerView(RecyclerView recyclerView);

    public abstract List<T> carregarDados(TarefaPesquisar tarefaPesquisar);

    public String textoDica() {
        return getString(R.string.pesquisa);
    }

    public ArrayList<T> getPesquisaItemList() {
        return pesquisaItemList;
    }

    public void setPesquisaItemList(ArrayList<T> pesquisaItemList) {
        this.pesquisaItemList = pesquisaItemList;
    }

    public boolean isPesquisar() {
        return pesquisar;
    }

    public void setPesquisar(boolean pesquisar) {
        this.pesquisar = pesquisar;
    }

    public void setPosicaoServidor(int posicaoServidor) {
        this.posicaoServidor = posicaoServidor;
    }

    public boolean isServidor() {
        return servidor;
    }

    public void setServidor(boolean servidor) {
        this.servidor = servidor;
    }
}
