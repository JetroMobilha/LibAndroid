package com.verimagens.Visualizadores.fragmentos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.verimagens.R;
import com.verimagens.Visualizadores.Adptadores.VisualizadorDeImagemPagerAdapter;
import com.verimagens.Visualizadores.Adptadores.VisualizadorDeVIdeoPagerAdapter;
import com.verimagens.Visualizadores.Adptadores.VisualizadorPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@SuppressWarnings("unused")
public class VerMidia extends Fragment {

    private static String LISTA = "lista";
    private static String LISTA_URI = "lista_uri";
    private static String POSICAO = "posicao";
    private static String IMAGEM = "imagem";
    private static String VIDEO = "video";
    private static String STRING = "string";

    private HashMap<String, Boolean> mVerToolbar = new HashMap<>();

    @Deprecated
    public static VerMidia imagem(@NonNull String imagem){
        ArrayList<String> list = new ArrayList<>();
        list.add(imagem);
        VerMidia verFragmentos = new VerMidia();
        Bundle bundle = new Bundle();
        bundle.putBoolean(STRING,true);
        bundle.putBoolean(IMAGEM,true);
        bundle.putSerializable(LISTA,list);
        verFragmentos.setArguments(bundle);
        return verFragmentos;
    }

    @Deprecated
    public static VerMidia imagem(@NonNull List<String> imagem){
        ArrayList<String> list = new ArrayList<>(imagem);
        VerMidia verFragmentos = new VerMidia();
        Bundle bundle = new Bundle();
        bundle.putBoolean(STRING,true);
        bundle.putBoolean(IMAGEM,true);
        bundle.putSerializable(LISTA,list);
        verFragmentos.setArguments(bundle);
        return verFragmentos;
    }

    @Deprecated
    public static VerMidia imagem(@NonNull List<String> imagem,int posicao){
        ArrayList<String> list = new ArrayList<>(imagem);
        VerMidia verFragmentos = new VerMidia();
        Bundle bundle = new Bundle();
        bundle.putSerializable(LISTA,list);
        bundle.putBoolean(STRING,true);
        bundle.putBoolean(IMAGEM,true);
        bundle.putInt(POSICAO,posicao);
        verFragmentos.setArguments(bundle);
        return verFragmentos;
    }

    @Deprecated
    public static VerMidia video(@NonNull String video){
        ArrayList<String> list = new ArrayList<>();
        list.add(video);
        VerMidia verFragmentos = new VerMidia();
        Bundle bundle = new Bundle();
        bundle.putSerializable(LISTA,list);
        bundle.putBoolean(STRING,true);
        bundle.putBoolean(VIDEO,true);
        verFragmentos.setArguments(bundle);
        return verFragmentos;
    }

    @Deprecated
    public static VerMidia video(@NonNull List<String> video){
        ArrayList<String> list = new ArrayList<>(video);
        VerMidia verFragmentos = new VerMidia();
        Bundle bundle = new Bundle();
        bundle.putBoolean(STRING,true);
        bundle.putBoolean(VIDEO,true);
        bundle.putSerializable(LISTA,list);
        verFragmentos.setArguments(bundle);
        return verFragmentos;
    }

    public static void imagem(@NonNull FragmentManager fragmentManager,@NonNull String imagem){
        ArrayList<String> list = new ArrayList<>();
        list.add(imagem);
        VerMidia verFragmentos = new VerMidia();
        Bundle bundle = new Bundle();
        bundle.putBoolean(STRING,true);
        bundle.putBoolean(IMAGEM,true);
        bundle.putSerializable(LISTA,list);
        verFragmentos.setArguments(bundle);
        mostrar(fragmentManager,verFragmentos);
    }

    public static void imagem(@NonNull FragmentManager fragmentManager,@NonNull List<String> imagem){
        ArrayList<String> list = new ArrayList<>(imagem);
        VerMidia verFragmentos = new VerMidia();
        Bundle bundle = new Bundle();
        bundle.putBoolean(STRING,true);
        bundle.putBoolean(IMAGEM,true);
        bundle.putSerializable(LISTA,list);
        verFragmentos.setArguments(bundle);
        mostrar(fragmentManager,verFragmentos);
    }

    public static void imagem(@NonNull FragmentManager fragmentManager,@NonNull List<String> imagem,int posicao){
        ArrayList<String> list = new ArrayList<>(imagem);
        VerMidia verFragmentos = new VerMidia();
        Bundle bundle = new Bundle();
        bundle.putSerializable(LISTA,list);
        bundle.putBoolean(STRING,true);
        bundle.putBoolean(IMAGEM,true);
        bundle.putInt(POSICAO,posicao);
        verFragmentos.setArguments(bundle);
        mostrar(fragmentManager,verFragmentos);
    }

    public static void video(@NonNull FragmentManager fragmentManager,@NonNull String video){
        ArrayList<String> list = new ArrayList<>();
        list.add(video);
        VerMidia verFragmentos = new VerMidia();
        Bundle bundle = new Bundle();
        bundle.putSerializable(LISTA,list);
        bundle.putBoolean(STRING,true);
        bundle.putBoolean(VIDEO,true);
        verFragmentos.setArguments(bundle);
        mostrar(fragmentManager,verFragmentos);
    }

    public static void video(@NonNull FragmentManager fragmentManager,@NonNull List<String> video){
        ArrayList<String> list = new ArrayList<>(video);
        VerMidia verFragmentos = new VerMidia();
        Bundle bundle = new Bundle();
        bundle.putBoolean(STRING,true);
        bundle.putBoolean(VIDEO,true);
        bundle.putSerializable(LISTA,list);
        verFragmentos.setArguments(bundle);
        mostrar(fragmentManager,verFragmentos);
    }

    public static void mostrar(@NonNull FragmentManager fragmentManager,Fragment fragment){
        //noinspection ConstantConditions
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //especificando o tipo de animação para a entrada do fragmento
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(android.R.id.content, fragment,null);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.visualisador_fragmento, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        ViewPager viewPager = view.findViewById(R.id.visualizador_viewpager);
        VisualizadorPagerAdapter adapter = null;
        if (isString() && isImagem()) {
            //noinspection ConstantConditions,unchecked
            adapter = new VisualizadorDeImagemPagerAdapter(getActivity().getSupportFragmentManager(), (List<String>) getArguments().getSerializable(LISTA));
        }  else if (isString() && isVideo()){
            //noinspection ConstantConditions,unchecked
            adapter = new VisualizadorDeVIdeoPagerAdapter(getActivity().getSupportFragmentManager(), (List<String>) getArguments().getSerializable(LISTA));

        }

        viewPager.setAdapter(adapter);

        if (isPorPosicao()){
            assert getArguments() != null;
            viewPager.setCurrentItem(getArguments().getInt(POSICAO));
            getArguments().putInt(POSICAO,0);
        }

        view.findViewById(R.id.fechar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    public  boolean isPorPosicao() {
        return  getArguments() != null && getArguments().getInt(POSICAO) != 0;
    }

    public boolean isString() {
        return  getArguments() != null && getArguments().getBoolean(STRING);
    }

    public boolean isVideo() {
        return  getArguments() != null && getArguments().getBoolean(VIDEO);
    }

    public boolean isImagem() {
        return  getArguments() != null && getArguments().getBoolean(IMAGEM);
    }
}
