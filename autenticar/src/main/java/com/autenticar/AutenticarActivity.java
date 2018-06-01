package com.autenticar;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

@SuppressWarnings("unused")
public abstract class AutenticarActivity extends AppCompatActivity {

    protected EsperaDialogo esperaDialogo = new EsperaDialogo();

    public boolean validaEmail(EditText editText, TextInputLayout textInputLayout) {

        if (editText.getText().toString().isEmpty()) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(getString(R.string.obrigatorio));
            return false;
        } else if (!(ValidacaoDeEntradas.isEmail(editText.getText().toString()))) {

            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(getString(R.string.dado_invalido));
            return false;

        } else {
            textInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    public boolean validaEmail(EditText editText) {
        return  ValidacaoDeEntradas.isEmail(editText.getText().toString());
    }

    public boolean validaNumero(EditText editText, TextInputLayout textInputLayout) {

        if (editText.getText().toString().isEmpty()) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(getString(R.string.obrigatorio));
            return false;
        } else if (!(ValidacaoDeEntradas.isNumero(editText.getText().toString()))) {

            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(getString(R.string.dado_invalido));
            return false;

        } else {
            textInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    public boolean validaNumero(EditText editText) {
        return ValidacaoDeEntradas.isNumero(editText.getText().toString());
    }

    public boolean validaTexto(EditText editText, TextInputLayout textInputLayout) {

        if (editText.getText().toString().isEmpty()) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(getString(R.string.obrigatorio));
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    public boolean validaTexto(EditText editText) {
        return !editText.getText().toString().isEmpty();
    }

    public boolean comparaSenha(EditText senha, TextInputLayout inputLayoutSenha,EditText copiaSenha, TextInputLayout copiaSenhaInputLayout) {

        if (validaTexto(senha,inputLayoutSenha ) && validaTexto(copiaSenha, copiaSenhaInputLayout)) {

            if (senha.getText().toString().equals(copiaSenha.getText().toString())) {
                return true;
            } else {
                copiaSenhaInputLayout.setErrorEnabled(true);
                copiaSenhaInputLayout.setError(getString(R.string.senha_diferentes));
                return false;
            }

        } else {
            return false;
        }
    }

    public boolean comparaSenha(EditText senha,EditText copiaSenha) {

        return validaTexto(senha) && validaTexto(copiaSenha) && senha.getText().toString().equals(copiaSenha.getText().toString());
    }

    public boolean validaEmailOuNumero(EditText editText1,TextInputLayout textInputLayout1) {

        if (validaEmail(editText1,textInputLayout1) || validaNumero(editText1,textInputLayout1)) {

            textInputLayout1.setErrorEnabled(false);
            return true;
        } else {
            return false;
        }
    }

    public boolean validaEmailOuNumero(EditText editText1) {
        return validaEmail(editText1) || validaNumero(editText1);
    }

    public void onRemoveFocusInput(final EditText editText){

         editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });
    }

    public void onFocusInput(final EditText editText , final TextInputLayout textInputLayout){

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validaTexto(editText,textInputLayout);
                } else {
                    textInputLayout.setHintEnabled(false);
                }
            }
        });
    }

    public void onFocusInputEmail(final EditText editText , final TextInputLayout textInputLayout){

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validaEmail(editText,textInputLayout);
                } else {
                    textInputLayout.setHintEnabled(false);
                }
            }
        });
    }

    public void onFocusInputNumero(final EditText editText , final TextInputLayout textInputLayout){

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validaNumero(editText,textInputLayout);
                } else {
                    textInputLayout.setHintEnabled(false);
                }
            }
        });
    }

    public void onFocusInputEmailNumero(final EditText editText , final TextInputLayout textInputLayout){

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validaEmailOuNumero(editText,textInputLayout);
                } else {
                    textInputLayout.setHintEnabled(false);
                }
            }
        });
    }


    @SuppressLint("StaticFieldLeak")
    private class Tarefa extends AsyncTask<Object, Void, Object> {

        @Override
        protected void onPreExecute() {
            anteDocarregamento();
        }

        @Override
        protected Object doInBackground(Object... params) {
            return carregamentoDeDados(params[0]);
        }

        @Override
        protected void onPostExecute(Object o) {
            deposDocarregamento(o);
        }
    }

    protected abstract void anteDocarregamento();

    protected abstract Object carregamentoDeDados(Object o);

    protected abstract void deposDocarregamento(Object o);

    public void showDialogo(){

        if (esperaDialogo == null){
            esperaDialogo = new EsperaDialogo();
        }
        esperaDialogo.show(getSupportFragmentManager(),"");
    }

    public void canselarDialogo(){

        if ( esperaDialogo != null && esperaDialogo.isVisible())
         esperaDialogo.dismiss();
    }

    public void iniciarTarefa(Object o){
        new Tarefa().execute(o);
    }
}
