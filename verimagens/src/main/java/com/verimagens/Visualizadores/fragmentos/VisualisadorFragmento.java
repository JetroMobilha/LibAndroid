package com.verimagens.Visualizadores.fragmentos;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.imagens.Imagens;
import com.verimagens.ClasseZoom;
import com.verimagens.R;

public class VisualisadorFragmento extends Fragment {

    private OnFragmentInteractionListener mListener;
    public String mImagem;

    public VisualisadorFragmento() {
        // Required empty public constructor
    }

    public static VisualisadorFragmento newInstance(String imagem ) {
        VisualisadorFragmento fragment = new VisualisadorFragmento();
        fragment.setmImagem(imagem);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("imagem",getmImagem());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_visualisador_fragmento, container, false);

         ClasseZoom imageView = (ClasseZoom)
                view.findViewById(R.id.imagem_visualizador_fragment);


         if (savedInstanceState !=null ){

             setmImagem(savedInstanceState.getString("imagem"));
         }

          Imagens imagemsApp = Imagens.getInstance();
          imagemsApp.lendoBitmapParaLista(mImagem,imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.onClickImagemVisualisador(mImagem);
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onClickImagemVisualisador(String objetoFarras);
    }

    public String getmImagem() {
        return mImagem;
    }

    public void setmImagem(String mImagem) {
        this.mImagem = mImagem;
    }
}
