package com.verimagens.Visualizadores.fragmentos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.imagens.Imagens;
import com.verimagens.ClasseZoom;
import com.verimagens.R;

public class VisualisadorFragmento extends Fragment {

    protected static String IMAGEM = "imagem";

    public VisualisadorFragmento() {
        // Required empty public constructor
    }

    public static VisualisadorFragmento newInstance(@NonNull String imagem ) {
        VisualisadorFragmento fragment = new VisualisadorFragmento();
        Bundle bundle = new Bundle();
        bundle.putSerializable(IMAGEM,imagem);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_visualisador_fragmento, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLayout(view);
    }


    protected void setLayout(View view){
        ClasseZoom imageView = view.findViewById(R.id.imagem_visualizador_fragment);

        Imagens imagemsApp = Imagens.getInstance();

        //noinspection ConstantConditions
        if (getArguments().getString(IMAGEM)!= null) {
            imagemsApp.lendoBitmapParaLista(getArguments().getString(IMAGEM), imageView);
        }else {
            Toast.makeText(getContext(),"Sem Imagem para mostrar",Toast.LENGTH_SHORT).show();

        }
    }
}
