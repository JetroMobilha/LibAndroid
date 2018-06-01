package com.imagens;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;


/**
 * Created by Jetro Mobilha on 05/11/2016.
 *
 */
public class FarrasImagemView extends AppCompatImageView implements ImagemInterface {


    private boolean clik = false;

    public FarrasImagemView(Context context) {
        super(context);
    }

    public FarrasImagemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FarrasImagemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

     @Override
    public boolean erroBaixado(){
        Drawable drawable = getDrawable();

        return drawable instanceof Imagens.ErroBaixado;
    }

    @Override
    public void setClik(boolean clik) {
        this.clik = clik;
    }

    @Override
    public boolean isClik() {
        return clik;
    }
}
