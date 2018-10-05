package com.autenticar;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Verifica {

    private Context mContext;

    public Verifica(Context context) {
        this.mContext = context;
    }

    public boolean validaEmail(String texto) {
        return texto != null && !texto.isEmpty() && ValidacaoDeEntradas.isEmail(texto);
    }

    public boolean validaEmail(EditText editText) {
        return editText != null &&  validaEmail(editText.getText().toString());
    }

    public boolean validaEmail(@NonNull EditText editText,@NonNull ViewGroup viewGroup) {
        if (validaEmail(editText)){
            return true;
        }else{
            rolaView(viewGroup,editText);
            return false;
        }
    }

    public boolean validaEmail(@NonNull EditText editText, @NonNull TextInputLayout textInputLayout) {

        if (editText.getText().toString().isEmpty()) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(mContext.getString(R.string.obrigatorio));
            return false;
        } else if (!(ValidacaoDeEntradas.isEmail(editText.getText().toString()))) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(mContext.getString(R.string.dado_invalido));
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    public boolean validaEmail(@NonNull EditText editText, @NonNull TextInputLayout textInputLayout, @NonNull ViewGroup viewGroup) {

        if (validaEmail(editText, textInputLayout)) {
            return true;
        } else {
            rolaView(viewGroup, textInputLayout);
            return false;
        }
    }

    public boolean validaEmail(@NonNull EditText editText, @NonNull TextInputLayout textInputLayout, @DrawableRes int backGroundok, @DrawableRes int backGroundErro) {

        if (editText.getText().toString().isEmpty() || !(ValidacaoDeEntradas.isEmail(editText.getText().toString()))) {
            textInputLayout.setBackgroundResource(backGroundErro);
            return false;
        } else {
            textInputLayout.setBackgroundResource(backGroundok);
            return true;
        }
    }

    public boolean validaEmail(@NonNull EditText editText, @NonNull TextInputLayout textInputLayout, @DrawableRes int backGroundok, @DrawableRes int backGroundErro, @NonNull ViewGroup viewGroup) {

        if (!validaEmail(editText, textInputLayout, backGroundok, backGroundErro)) {
            rolaView(viewGroup, textInputLayout);
            return false;
        } else {
            return true;
        }
    }

    public boolean validaNumero(String numero) {
        return numero!= null && !numero.isEmpty() && ValidacaoDeEntradas.isNumero(numero);
    }

    public boolean validaNumero(@NonNull EditText editText) {
        return !editText.getText().toString().isEmpty() && ValidacaoDeEntradas.isNumero(editText.getText().toString());
    }

    public boolean validaNumero(@NonNull EditText editText,@NonNull ViewGroup viewGroup) {
        if (validaNumero(editText)){
            return true;
        }else{
            rolaView(viewGroup,editText);
            return false;
        }
    }

    public boolean validaNumero(@NonNull EditText editText, @NonNull TextInputLayout textInputLayout) {

        if (editText.getText().toString().isEmpty()) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(mContext.getString(R.string.obrigatorio));
            return false;
        } else if (!(ValidacaoDeEntradas.isNumero(editText.getText().toString()))) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(mContext.getString(R.string.dado_invalido));
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    public boolean validaNumero(@NonNull EditText editText, @NonNull TextInputLayout textInputLayout, @NonNull ViewGroup viewGroup) {

        if (validaNumero(editText, textInputLayout)) {
            return true;
        } else {
            rolaView(viewGroup, textInputLayout);
            return false;
        }
    }

    public boolean validaNumero(@NonNull EditText editText, @NonNull TextInputLayout textInputLayout, @DrawableRes int backGroundok, @DrawableRes int backGroundErro) {

        if (editText.getText().toString().isEmpty() || !(ValidacaoDeEntradas.isNumero(editText.getText().toString()))) {
            textInputLayout.setBackgroundResource(backGroundErro);
            return false;
        } else {
            textInputLayout.setBackgroundResource(backGroundok);
            return true;
        }
    }

    public boolean validaNumero(@NonNull EditText editText, @NonNull TextInputLayout textInputLayout, @DrawableRes int backGroundok, @DrawableRes int backGroundErro, @NonNull ViewGroup viewGroup) {

        if (!validaNumero(editText, textInputLayout, backGroundok, backGroundErro)) {
            rolaView(viewGroup, textInputLayout);
            return false;
        } else {
            return true;
        }
    }

    public boolean validaTexto(String texto) {
        return texto != null &&  !texto.isEmpty();
    }

    public boolean validaTexto(@NonNull EditText editText) {
        return !editText.getText().toString().isEmpty();
    }

    public boolean validaTexto(@NonNull EditText editText,@NonNull ViewGroup viewGroup) {
        if (validaTexto(editText)){
            return true;
        }else{
            rolaView(viewGroup,editText);
            return false;
        }
    }

    public boolean validaTexto(@NonNull EditText editText, @NonNull TextInputLayout textInputLayout) {

        if (editText.getText().toString().isEmpty()) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(mContext.getString(R.string.obrigatorio));
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    public boolean validaTexto(@NonNull EditText editText, @NonNull TextInputLayout textInputLayout, @NonNull ViewGroup viewGroup) {

        if (validaTexto(editText, textInputLayout)) {
            return true;
        } else {
            rolaView(viewGroup, textInputLayout);
            return false;
        }
    }

    public boolean validaTexto(@NonNull EditText editText, @NonNull TextInputLayout textInputLayout, @DrawableRes int backGroundok, @DrawableRes int backGroundErro) {

        if (editText.getText().toString().isEmpty()) {
            textInputLayout.setBackgroundResource(backGroundErro);
            return false;
        } else {
            textInputLayout.setBackgroundResource(backGroundok);
            return true;
        }
    }

    public boolean validaTexto(@NonNull EditText editText, @NonNull TextInputLayout textInputLayout, @DrawableRes int backGroundok, @DrawableRes int backGroundErro, @NonNull ViewGroup viewGroup) {

        if (!validaTexto(editText, textInputLayout, backGroundok, backGroundErro)) {
            rolaView(viewGroup, textInputLayout);
            return false;
        } else {
            return true;
        }
    }

    public boolean validaTextoElementar(String texto ,@NonNull ViewGroup viewGroup , @DrawableRes int backGroundok, @DrawableRes int backGroundErro) {

        if (texto == null || texto.isEmpty() || texto.equals("null") ) {
            viewGroup.setBackgroundResource(backGroundErro);
            return false;
        } else {
            viewGroup.setBackgroundResource(backGroundok);
            return true;
        }
    }

    public boolean validaTextoElementar(@NonNull String texto ,@NonNull ViewGroup viewGroup , @DrawableRes int backGroundok, @DrawableRes int backGroundErro,@NonNull ViewGroup viewGroupRolo ) {

        if (!validaTextoElementar(texto,viewGroup,backGroundok,backGroundErro)) {
           rolaView(viewGroupRolo,viewGroup);
            return false;
        } else {
            viewGroup.setBackgroundResource(backGroundok);
            return true;
        }
    }

    public boolean comparaSenha(@NonNull String senha , @NonNull String copiaSenha) {

        if (validaTexto(senha) && validaTexto(copiaSenha)) {
            if (senha.equals(copiaSenha)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    public boolean comparaSenha(@NonNull EditText senha, @NonNull TextInputLayout inputLayoutSenha, @NonNull EditText copiaSenha, @NonNull TextInputLayout copiaSenhaInputLayout) {

        if (validaTexto(senha, inputLayoutSenha) && validaTexto(copiaSenha, copiaSenhaInputLayout)) {
            if (senha.getText().toString().equals(copiaSenha.getText().toString())) {
                return true;
            } else {
                copiaSenhaInputLayout.setErrorEnabled(true);
                copiaSenhaInputLayout.setError(mContext.getString(R.string.senha_diferentes));
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean comparaSenha(@NonNull EditText senha, @NonNull TextInputLayout inputLayoutSenha, @NonNull EditText copiaSenha, @NonNull TextInputLayout copiaSenhaInputLayout, int comprimento) {

        if (validaTexto(senha, inputLayoutSenha) && validaTexto(copiaSenha, copiaSenhaInputLayout)) {

            if (senha.getText().toString().length() >= comprimento) {
                if (senha.getText().toString().equals(copiaSenha.getText().toString())) {
                    return true;
                } else {
                    copiaSenhaInputLayout.setErrorEnabled(true);
                    copiaSenhaInputLayout.setError(mContext.getString(R.string.senha_diferentes));
                    return false;
                }
            } else {
                inputLayoutSenha.setErrorEnabled(true);
                inputLayoutSenha.setError(mContext.getString(R.string.senha_comprimento) + comprimento);
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean comparaSenha(@NonNull EditText senha, @NonNull TextInputLayout inputLayoutSenha, @NonNull EditText copiaSenha, @NonNull TextInputLayout copiaSenhaInputLayout, @NonNull ViewGroup viewGroup) {

        if (comparaSenha(senha, inputLayoutSenha, copiaSenha, copiaSenhaInputLayout)) {
            return true;
        } else {
            rolaView(viewGroup, inputLayoutSenha);
            return false;
        }
    }

    public boolean comparaSenha(@NonNull EditText senha, @NonNull TextInputLayout inputLayoutSenha, @NonNull EditText copiaSenha, @NonNull TextInputLayout copiaSenhaInputLayout, int comprimento, @NonNull ViewGroup viewGroup) {
        if (comparaSenha(senha, inputLayoutSenha, copiaSenha, copiaSenhaInputLayout, comprimento)) {
            return true;
        } else {
            rolaView(viewGroup, inputLayoutSenha);
            return false;
        }
    }

    public boolean comparaSenha(@NonNull EditText senha, @NonNull EditText copiaSenha) {

        return validaTexto(senha) && validaTexto(copiaSenha) && senha.getText().toString().equals(copiaSenha.getText().toString());
    }

    public boolean comparaSenha(@NonNull EditText senha, @NonNull EditText copiaSenha, int comprimento) {

        return validaTexto(senha) && validaTexto(copiaSenha) && senha.getText().toString().equals(copiaSenha.getText().toString()) && senha.getText().toString().length() >= comprimento;
    }

    public boolean comparaSenha(@NonNull EditText senha, @NonNull EditText copiaSenha, @NonNull ViewGroup viewGroup) {

        if (comparaSenha(senha, copiaSenha)) {
            return true;
        } else {
            rolaView(viewGroup, senha);
            return false;
        }

    }

    public boolean comparaSenha(@NonNull EditText senha, @NonNull EditText copiaSenha, int comprimento, @NonNull ViewGroup viewGroup) {
        if (comparaSenha(senha, copiaSenha, comprimento)) {
            return true;
        } else {
            rolaView(viewGroup, senha);
            return false;
        }
    }

    public boolean validaEmailOuNumero(@NonNull EditText editText1, @NonNull TextInputLayout textInputLayout1) {

        if (validaEmail(editText1, textInputLayout1) || validaNumero(editText1, textInputLayout1)) {

            textInputLayout1.setErrorEnabled(false);
            return true;
        } else {
            return false;
        }
    }

    public boolean validaEmailOuNumero(@NonNull EditText editText1, @NonNull TextInputLayout textInputLayout1, @DrawableRes int backGroundok, @DrawableRes int backGroundErro) {

        if (validaEmail(editText1, textInputLayout1) || validaNumero(editText1, textInputLayout1)) {
            textInputLayout1.setBackgroundResource(backGroundok);
            return true;
        } else {
            textInputLayout1.setBackgroundResource(backGroundErro);
            return false;
        }
    }

    public boolean validaEmailOuNumero(@NonNull EditText editText1, @NonNull TextInputLayout textInputLayout1, @DrawableRes int backGroundok, @DrawableRes int backGroundErro,@NonNull ViewGroup viewGroup) {

        if (validaEmailOuNumero(editText1, textInputLayout1,backGroundok,backGroundErro)) {
            return true;
        } else {
           rolaView(viewGroup,textInputLayout1);
            return false;
        }
    }

    public boolean validaEmailOuNumero(@NonNull EditText editText1) {
        return validaEmail(editText1) || validaNumero(editText1);
    }

    public boolean validaEmailOuNumero(@NonNull EditText editText1, @NonNull TextInputLayout textInputLayout1, @NonNull ViewGroup viewGroup) {

        if (validaEmailOuNumero(editText1, textInputLayout1)) {
            return true;
        } else {
            rolaView(viewGroup, editText1);
            return false;
        }
    }

    public boolean validaEmailOuNumero(@NonNull EditText editText1, @NonNull ViewGroup viewGroup) {

        if (validaEmail(editText1) || validaNumero(editText1)) {
            return true;
        } else {
            rolaView(viewGroup, editText1);
            return false;
        }
    }

    public void onRemoveFocusInput(final EditText editText) {

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });
    }

    public void onFocusInput(final EditText editText, final TextInputLayout textInputLayout) {

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validaTexto(editText, textInputLayout);
                } else {
                    textInputLayout.setHintEnabled(false);
                }
            }
        });
    }

    public void onFocusInputEmail(final EditText editText, final TextInputLayout textInputLayout) {

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validaEmail(editText, textInputLayout);
                } else {
                    textInputLayout.setHintEnabled(false);
                }
            }
        });
    }

    public void onFocusInputNumero(final EditText editText, final TextInputLayout textInputLayout) {

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validaNumero(editText, textInputLayout);
                } else {
                    textInputLayout.setHintEnabled(false);
                }
            }
        });
    }

    public void onFocusInputEmailNumero(final EditText editText, final TextInputLayout textInputLayout) {

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validaEmailOuNumero(editText, textInputLayout);
                } else {
                    textInputLayout.setHintEnabled(false);
                }
            }
        });
    }

    public void rolaView(ViewGroup viewGroup, View view) {
        viewGroup.scrollTo((int) (view.getX()), (int) (view.getY()));
    }
}
