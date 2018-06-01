package com.verimagens.Visualizadores.Adptadores;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.verimagens.Visualizadores.fragmentos.VisualisadorFragmento;

import java.util.ArrayList;
import java.util.List;



public class VisualizadorDeImagemPagerAdapter extends FragmentStatePagerAdapter {
    private List<VisualisadorFragmento> mlistaFragmentos = new ArrayList<>();
    private List<String> mlistaTitles;

    public VisualizadorDeImagemPagerAdapter(FragmentManager fm , List<String> listaImagem) {
        super(fm);

        this.mlistaTitles =listaImagem;
        for (String imagems : mlistaTitles){
            mlistaFragmentos.add(VisualisadorFragmento.newInstance(imagems));
        }

    }


    @Override
    public Fragment getItem(int position) { return mlistaFragmentos.get(position); }

    @Override
    public int getCount() { return mlistaTitles.size(); }

    public String getItemAdpeter(int posicao) {
        return mlistaTitles.get(posicao);
    }

    public List<String> getMlistaTitles() {
        return mlistaTitles;
    }
}
