package com.carregarimagem.CapturaImagem;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.carregarimagem.R;
import com.carregarimagem.util.Util;

public class CarregarImagen
        extends AppCompatActivity
        implements DialogoEscolheFonte.EscolheFonte{
  private static final String TAG = CarregarImagen.class.getSimpleName();
  private ImagemEditada mListener = null;

  public static Intent createIntent(Activity activity,ImagemEditada mListener) {

    Intent intent = new Intent(activity, CarregarImagen.class);
    Util.dados = mListener;
    return intent;
  }

  // Lifecycle Method ////////////////////////////////////////////////////////////////////////////

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_basic);

    if(savedInstanceState == null){
      getSupportFragmentManager().beginTransaction().add(R.id.container, BasicFragment.newInstance()).commit();
    }

    // apply custom font
    FontUtils.setFont(findViewById(R.id.root_layout));
    initToolbar();

    if (Util.dados!= null && Util.dados instanceof ImagemEditada){
      mListener = (ImagemEditada) Util.dados ;
    }
  }

  @Override public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
  }

  @Override public boolean onSupportNavigateUp() {
    onBackPressed();
    return super.onSupportNavigateUp();
  }

  private void initToolbar() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    assert toolbar != null;
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
  }

  public void startResultActivity(Uri uri) {
    if (isFinishing()) return;
    // Start ResultActivity
    startActivity(ResultActivity.createIntent(this, uri));
  }

  public void startResultActivity(Bitmap bitmap) {
    if (isFinishing()) return;
    // Start ResultActivity
   mListener.onImagemEditada(bitmap);
      finish();
  }

  @Override
  public void onEscolheFonteCamera() {

  }

  @Override
  public void onEscolheFontePinck() {

  }
}
