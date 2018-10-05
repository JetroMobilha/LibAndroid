package com.carregarimagem.CapturaImagem;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import com.carregarimagem.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogoEscolheFonte extends DialogFragment {

    EscolheFonte mListener;


    public DialogoEscolheFonte() {
        // Required empty public constructor
    }

    public static DialogoEscolheFonte instance(EscolheFonte escolheFonte){
        DialogoEscolheFonte dialogoEscolheFonte = new DialogoEscolheFonte();
        dialogoEscolheFonte.mListener = escolheFonte;
        return dialogoEscolheFonte;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.fragment_escolhe_fonte_dialogo, null) ;


        ImageButton  imageButton  =  view.findViewById(R.id.buttonCamera);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onEscolheFonteCamera();
            }
        });

        ImageButton  imageButto2  =  view.findViewById(R.id.buttonPickImage);
        imageButto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onEscolheFontePinck();
            }
        });
        builder.setView(view);

        if (mListener == null) throw new  RuntimeException("cria o objeto com  o metodo instance do dialogo");
        return builder.create();
    }


    public interface EscolheFonte{

        /**
         * Usado para tirar uma foto com  a camero do despositivo
         */
        void  onEscolheFonteCamera();

        /**
         * Usado para escolher imagen do armasenamento do despositivo
         */
        void  onEscolheFontePinck();
    }

}
