package com.verimagens.Visualizadores.Adptadores;

import android.support.v4.app.FragmentManager;

import com.verimagens.Visualizadores.fragmentos.VisualisadorFragmento;

import java.util.List;



public class VisualizadorDeImagemPagerAdapter extends VisualizadorPagerAdapter {

    public VisualizadorDeImagemPagerAdapter(FragmentManager fm , List<String> listaImagem) {
        super(fm);

        this.mlistaTitles =listaImagem;
        for (Object imagems : mlistaTitles){
            mlistaFragmentos.add(VisualisadorFragmento.newInstance((String) imagems));
        }
    }

    public String getItemAdpeter(int posicao) {
        return (String) mlistaTitles.get(posicao);
    }

    public List<String> getMlistaTitles() {
        //noinspection unchecked
        return (List<String>) mlistaTitles;
    }
}
