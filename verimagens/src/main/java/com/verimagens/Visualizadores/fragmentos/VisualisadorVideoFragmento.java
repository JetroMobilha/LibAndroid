package com.verimagens.Visualizadores.fragmentos;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.marcinmoskala.videoplayview.VideoPlayView;
import com.verimagens.R;

public class VisualisadorVideoFragmento extends Fragment {

    protected static String IMAGEM = "imagem";

    public VisualisadorVideoFragmento() {
        // Required empty public constructor
    }

    public static VisualisadorVideoFragmento newInstance(@NonNull String imagem ) {
        VisualisadorVideoFragmento fragment = new VisualisadorVideoFragmento();
        Bundle bundle = new Bundle();
        bundle.putSerializable(IMAGEM,imagem);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //noinspection UnnecessaryLocalVariable
        View view =  inflater.inflate(R.layout.fragment_visualisador_video_fragmento, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLayout(view);
    }

    protected void setLayout(final View view){
        VideoView imageView = view.findViewById(R.id.imagem_visualizador_fragment);
        MediaController mediaController = new MediaController(getContext());
        mediaController.setAnchorView(imageView);
        imageView.setMediaController(mediaController);

        imageView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                view.findViewById(R.id.progress).setVisibility(View.GONE);
            }
        });

        imageView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

            }
        });
        //noinspection ConstantConditions
        if (getArguments().getString(IMAGEM)!= null) {
            imageView.setVideoURI(Uri.parse(getArguments().getString(IMAGEM)));
        }else {
            Toast.makeText(getContext(),"Sem Video para mostrar",Toast.LENGTH_SHORT).show();
        }
    }
}
