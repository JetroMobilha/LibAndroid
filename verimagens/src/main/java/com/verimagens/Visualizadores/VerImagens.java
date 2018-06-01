package com.verimagens.Visualizadores;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.imagens.Imagens;
import com.imagens.SalvarImagmAarelho;
import com.verimagens.ApagarFicheiro;
import com.verimagens.R;
import com.verimagens.Visualizadores.Adptadores.VisualizadorDeImagemPagerAdapter;
import com.verimagens.Visualizadores.fragmentos.VisualisadorFragmento;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;


public abstract class VerImagens extends AppCompatActivity
        implements VisualisadorFragmento.OnFragmentInteractionListener {


    // Todo : Implementação experimental que deve ser substituida
    private static List<String> linkedListImagem;
    private static int posicao = 0;
    private static boolean porPosicao = false;

    private ArrayList<String> mImagemlist = new ArrayList<>();  // referencia para a lista de imagem
    private VisualizadorDeImagemPagerAdapter visualisadorGrelhaImagem;
    private ViewPager viewPager;
    private int mCorrentePagina =0;
    private LinkedHashSet<FileTemporario> fileTemporarioList;
    private Imagens imagemsApp;

    private HashMap<String, Boolean> mVerToolbar = new HashMap<>();


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("mImagemlist__", getmImagemlist());
        outState.putInt("mCorrentePagina", getmCorrentePagina());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visualisador_lista_imagem);
        imagemsApp = Imagens.getInstance();
        fileTemporarioList = new LinkedHashSet<>();

        if (savedInstanceState != null) {
            //noinspection unchecked
            mImagemlist.addAll((ArrayList<String>) savedInstanceState.get("mImagemlist__"));
            setmCorrentePagina(savedInstanceState.getInt("mCorrentePagina"));
        } else {


            // Recebemdo a lista de imagems

            if (istLinkedListImagem() != null && istLinkedListImagem().size() > 0) {
                mImagemlist.addAll(VerImagens.getLinkedListImagem());

            } else {
                Log.e("VerImagem", " A lista a esta vasia ");
                finish();
            }
        }

        visualisadorGrelhaImagem = new VisualizadorDeImagemPagerAdapter(getSupportFragmentManager(), mImagemlist);

        viewPager = findViewById(R.id.visualizador_viewpager);
        viewPager.setAdapter(visualisadorGrelhaImagem);

        if (isPorPosicao()){
            viewPager.setCurrentItem(getPosicao());
            setmCorrentePagina(getPosicao());
        }

        //Toolbar para a  arra de baixo da tela
        final Toolbar toolbarBottom = findViewById(R.id.visualisador_toolbar_bottom);

        toolbarBottom.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        toolbarBottom.inflateMenu(R.menu.toolbar_visualizar_button_menu);

        toolbarBottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.visualizador_partilhar) {

                    String objetoFarras = visualisadorGrelhaImagem.getItemAdpeter(viewPager.getCurrentItem());
                    File path = new File(imagemsApp.getCaminhoEmagemsTpm(objetoFarras));

                    if (path.exists() && path.getParent().equals((new File(imagemsApp.getCaminhoEmagemsTpm()).getAbsolutePath()))) {

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(getApplicationContext(),
                                gettAutorFileProvides() // chamdo apartir da classe concreta que executar esta clase
                                , new File(imagemsApp.getCaminhoEmagemsTpm(objetoFarras))));

                        intent.setType("image/*");
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                    }
                    return true;

                } else if (item.getItemId() == R.id.visualizador_gruardar) {

                    String objetoFarras = visualisadorGrelhaImagem.getItemAdpeter(viewPager.getCurrentItem());

                    if (imagemsApp.isExternalStorageWritable()) {
                        new SalvarImagmAarelho(objetoFarras).execute();
                    } else {
                        Toast.makeText(VerImagens.this, " Sem Armazenamento no despositivo", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });

        // Todo : recuperar a pagina corrente

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                if (getmCorrentePagina() != position) {
                    setmCorrentePagina(position);

                    FileTemporario fileTemporario = new FileTemporario((visualisadorGrelhaImagem.getMlistaTitles().get(getmCorrentePagina())));
                    fileTemporario.execute();
                    fileTemporarioList.add(fileTemporario);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (getmCorrentePagina() == 0 ){
            setmCorrentePagina(viewPager.getCurrentItem());
        }else{
            viewPager.setCurrentItem(getmCorrentePagina());
        }

        new FileTemporario(visualisadorGrelhaImagem.getMlistaTitles().get(getmCorrentePagina()))
                .execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            //noinspection unchecked
            startService(new Intent(getApplicationContext(), ApagarFicheiro.class));
            for (FileTemporario f : getFileTemporarioList()) {
                f.cancel(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClickImagemVisualisador(String objetoFarras) {

        final Toolbar toolbarBottom = findViewById(R.id.visualisador_toolbar_bottom);
        if (toolbarBottom.getVisibility() == View.VISIBLE) {

            AnimatorSet set1 = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.desaparecer_alph);
            set1.setTarget(toolbarBottom);
            set1.start();

            set1.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    toolbarBottom.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }

            });

        } else if (mVerToolbar.get(mImagemlist.get(getmCorrentePagina()))) {

            toolbarBottom.setVisibility(View.VISIBLE);
            AnimatorSet set1 = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.aparecer_alph);
            set1.setTarget(toolbarBottom);
            set1.start();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class FileTemporario extends AsyncTask<Void, Void, Boolean> implements Serializable {

        private String mFarras;

        FileTemporario(String farras) {
            this.mFarras = farras;
        }

        @Override
        protected void onPreExecute() {

            final Toolbar toolbarBottom = findViewById(R.id.visualisador_toolbar_bottom);
            toolbarBottom.setVisibility(View.GONE);
            if (mVerToolbar.get(mFarras) == null) mVerToolbar.put(mFarras, false);
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {

            String objetoFarras = visualisadorGrelhaImagem.getItemAdpeter(viewPager.getCurrentItem());
            mVerToolbar.put(mFarras, aVoid);

            if (aVoid && objetoFarras.equals(mFarras)) {

                final Toolbar toolbarBottom = findViewById(R.id.visualisador_toolbar_bottom);
                toolbarBottom.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            File file = new File(imagemsApp.getCaminhoEmagems(mFarras));
            File file2 = new File(imagemsApp.getCaminhoEmagemsTpm(mFarras));

            //noinspection SimplifiableIfStatement
            if (file.exists()) {
                return true;
            } else if (file2.exists()) {
                return true;
            } else {
                Bitmap bitmap = imagemsApp.getImagemBitmap(mFarras);
                if (bitmap != null) {
                    return imagemsApp.saveArrayToInternalStorageImagems(mFarras
                            , imagemsApp.getCaminhoEmagemsTpm(), imagemsApp.convertBitmapPraBytArray(bitmap, 100));
                } else {
                    return false;
                }
            }
        }
    }

    public abstract String gettAutorFileProvides();

    public static List<String> getLinkedListImagem() {
        List<String> o = linkedListImagem;
        linkedListImagem = null;
        return o;
    }

    public static List<String> istLinkedListImagem() {
        return linkedListImagem;
    }

    public static void setLinkedListImagem(List<String> linkedListImagem) {
        VerImagens.linkedListImagem = linkedListImagem;
    }

    public ArrayList<String> getmImagemlist() {
        return mImagemlist;
    }

    public void setmImagemlist(ArrayList<String> mImagemlist) {
        this.mImagemlist = mImagemlist;
    }

    public static int getPosicao() {
        return posicao;
    }

    public static void setPosicao(int posi) {
        posicao = posi;
    }

    public static boolean isPorPosicao() {
        return porPosicao;
    }

    public static void setPorPosicao(boolean porPosi) {
        porPosicao = porPosi;
    }

    public HashMap<String, Boolean> getmVerToolbar() {
        return mVerToolbar;
    }

    public void setmVerToolbar(HashMap<String, Boolean> mVerToolbar) {
        this.mVerToolbar = mVerToolbar;
    }

    public int getmCorrentePagina() {
        return mCorrentePagina;
    }

    public void setmCorrentePagina(int mCorrentePagina) {
        this.mCorrentePagina = mCorrentePagina;
    }

    public LinkedHashSet<FileTemporario> getFileTemporarioList() {
        return fileTemporarioList;
    }

    public void setFileTemporarioList(LinkedHashSet<FileTemporario> fileTemporarioList) {
        this.fileTemporarioList = fileTemporarioList;
    }
}
