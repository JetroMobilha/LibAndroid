package com.listarapita;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import lal.adhish.gifprogressbar.GifView;

public abstract class ListaRapidaFragmentAbstract extends Fragment {

    // atributos de layout
    protected RecyclerView mRecyclerView;
    protected ProgressBar progressBarConteudo;
    protected NestedScrollView progressBar;
    protected NestedScrollView mImagem;
    protected TextView textoSendados;
    protected SwipeRefreshLayout refreshLayout;

    // adpter para a lista
    protected RecyclerView.Adapter<?> mAdapter;

    // Atributos de estado de requesição
    protected int posicao = 0;
    protected int posicaoServidor = 0;
    protected boolean carregando = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null){

            setPosicao(savedInstanceState.getInt("posica"));
            setPosicaoServidor(savedInstanceState.getInt("posicaServidor"));
            setCarregando(savedInstanceState.getBoolean("carregando"));
        }

        setHasOptionsMenu(true);
        onCrieatLista();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("posica",getPosicao());
         outState.putInt("posicaServidor",getPosicaoServidor());
         outState.putBoolean("carregando",isCarregando());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.root, container, false);

        // usa para mudar o layout do fragmento padrão
       view = setLayout(view,inflater,container);

        mRecyclerView = view.findViewById(R.id.lista);
        progressBar = view.findViewById(R.id.lista_progresso);
        progressBarConteudo = view.findViewById(R.id.lista_progresso_caregamento);
        mImagem = view.findViewById(R.id.lista_semdados_raiz);
        textoSendados = view.findViewById(R.id.lista_sem_dados_texto);
        refreshLayout = view.findViewById(R.id.lista_swipe_refresh);

        GifView pGif = view.findViewById(R.id.lista_imagem_sem_dados);
        pGif.setImageResource(R.drawable.img_sem_dados);

        // carregando novoa dados com um deslisar para baixo no inicio da lista
        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        carregarDados();
                        progressBarConteudo.setVisibility(View.GONE);
                    }
                }
        );

        // ativando ou desativando ouvinte de atualisação com deslisamento para baixo
        refreshLayout.setEnabled(isSwipeRefreshLayout());

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = adpdatorLista();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

        // botao para carregarDados manualmente quando sem dados no primeiro carregamento
        final AppCompatButton carregar = view.findViewById(R.id.lista_carregar);
        carregar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                carregarDados();
            }
        });

        // codigo para acarregar mais dados por demanda
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                if ( mRecyclerView.getLayoutManager() instanceof  LinearLayoutManager){
                    LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

                    if (mRecyclerView.getAdapter().getItemCount() == layoutManager.findLastVisibleItemPosition() + 1) {

                        if (!isCarregando()) {
                            if (!refreshLayout.isRefreshing()) processeBar();
                            setCarregando(true);
                            carregarMaisDados();
                        }
                    }

                }else if (mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager){

                    StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) mRecyclerView.getLayoutManager();

                    if (mRecyclerView.getAdapter().getItemCount() <= staggeredGridLayoutManager.findLastVisibleItemPositions(null)[0] + 3) {

                        if (!isCarregando()) {
                            if (!refreshLayout.isRefreshing()) processeBar();
                            setCarregando(true);
                            carregarMaisDados();
                        }
                    }
                }
            }

        });

        onCreatViewLista();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null) {
            mostrarProgresso();
            iniciaLord();
        } else if (onCrieatLista().size() == 0) {
            carregarDados();
        } else {
            mostrarConteudo();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mRecyclerView = null;
        progressBar = null;
        progressBarConteudo = null;
        mImagem = null;
        textoSendados = null;
        mAdapter=null;
    }

    /**
     * Mostra um progresso geral para carregamento
     */
    public void mostrarProgresso() {
        progressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mImagem.setVisibility(View.GONE);
    }

    /**
     * Mostra o conteudo depos de ser  carregado com dados
     */
    public void mostrarConteudo() {
        progressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mImagem.setVisibility(View.GONE);

       if (refreshLayout.isRefreshing()) refreshLayout.setRefreshing(false);
    }

    /**
     * Mostra informação de sem dados encontrado no carregamento
     */
    public void mostrarSendados() {
        progressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);

        if (coneccao())
            textoSendados.setText(getString(R.string.sem_coneccao));
        mImagem.setVisibility(View.VISIBLE);

        if (refreshLayout.isRefreshing()) refreshLayout.setRefreshing(false);
    }

    /**
     * retorna a posição do ultimo item na lista
     * @return posição
     */
    public int getPosicao() {
        return posicao;
    }

    protected void setPosicao(int posicao) {
        this.posicao = posicao;
    }

    protected void setPosicaoServidor(Integer posicaoServidor) {
        this.posicaoServidor = posicaoServidor;
    }

    /**
     * retorna a posição do ultimo item na lista no servidor
     * @return
     */
    public int getPosicaoServidor() {
        return posicaoServidor;
    }

    /**
     * Controlador do estado de carregamento na lista
     */
    public void setCarregando(boolean valor) {
        this.carregando = valor;
    }

    /**
     * Controlador do estado de carregamento na lista
     * @return se true ja esta a carregarDados novos dados
     */
    public boolean isCarregando() {
        return carregando;
    }

    public abstract void carregarDados();

    public abstract void carregarMaisDados();

    /**
     * Chamdo em oncriate fragment podendo inicial o fragment aqui
     *  Metodo que retorna o tipo de Lista de Objeto usado na lista
     * @return lista de objeto da lista
     */
    protected abstract List<?> onCrieatLista();

    /**
     * Chamado no final de oncriateView do fragmento podendo modar o comportamento de tudo aqui
     */
    public void onCreatViewLista() {}

    /**
     * Usado para inicial geralmento o carregador da lista
     */
    public abstract void iniciaLord();

    /**
     * Retorna o adpter da lista
     * @return
     */
    public abstract RecyclerView.Adapter<?> adpdatorLista();

    /**
     * MOstra prosseco de novo carregamento por demanda
     */
    protected void processeBar(){
        progressBarConteudo.setVisibility(View.VISIBLE);
    }

    /**
     *  Usado para carregar uma nova view que extende a view paradrão da lista
     * @param view
     * @param inflater
     * @param container
     * @return
     */
    @SuppressWarnings("unused")
    public View setLayout(View view, LayoutInflater inflater, ViewGroup container){
     return view;
    }

    private    boolean coneccao() {

        try {
            ConnectivityManager connMgr = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            assert connMgr != null;
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * usado para ativar atualisação por deslisamento para baixo
     * @return true activado / false desativado
     */
    public abstract boolean isSwipeRefreshLayout();

    public RecyclerView getmRecyclerView() {
        return mRecyclerView;
    }
}
