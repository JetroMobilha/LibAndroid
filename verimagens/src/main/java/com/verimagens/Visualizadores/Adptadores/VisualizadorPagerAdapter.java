package com.verimagens.Visualizadores.Adptadores;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;


public abstract class VisualizadorPagerAdapter extends FragmentStatePagerAdapter {

    List<?> mlistaTitles;
    List<Fragment> mlistaFragmentos = new ArrayList<>();

    VisualizadorPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) { return mlistaFragmentos.get(position); }

    @Override
    public int getCount() { return mlistaTitles.size(); }

    public abstract Object getItemAdpeter(int posicao);

    public abstract List<?> getMlistaTitles();
}
