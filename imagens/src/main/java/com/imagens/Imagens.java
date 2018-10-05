package com.imagens;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import static android.support.v4.graphics.TypefaceCompatUtil.closeQuietly;

/**
 * Classe usada para o tratamento de mUrl
 */

public class Imagens {

    @SuppressLint("StaticFieldLeak")
    private static Imagens ourInstance;
    private boolean emServico = false;
    private String caminho;
    private String caminhoExterno;
    private String caminhoTemp;
    private int imagemDeErro = R.drawable.icon_imagen;
    private int imagemDeEspera = R.drawable.espera;
    private int w, h;
    private CachApp cachApp;
    private Context mContext;
    private LinkedList<RequisitarImagem> arrayList = new LinkedList<>();

    // ---------------------   INICIALIZAR ------------------------------------------------------------------
    public static void init(Context context, @NonNull String nomePastaSalvar) {
        ourInstance = new Imagens(context, nomePastaSalvar);
    }

    public static void init(Context context, @NonNull String nomePastaSalvar, @NonNull String nomePastaTpm) {
        ourInstance = new Imagens(context, nomePastaSalvar, nomePastaTpm);
    }

    private Imagens(Context context, @NonNull String nomePastaSalvar) {
        this.mContext = context;
        cachApp = new CachApp(context);
        caminhoExterno = "/" + nomePastaSalvar + "/";
        caminho = context.getFilesDir().getAbsolutePath() + "/" + nomePastaSalvar + "/";
        caminhoTemp = context.getFilesDir().getAbsolutePath() + "/" + Constantes.PREFER_NOME_TPM + "/";
        setDimencoes();
    }

    private Imagens(Context context, @NonNull String nomePastaSalvar, @NonNull String nomePastaTpm) {
        this.mContext = context;
        cachApp = new CachApp(context);
        caminho = context.getFilesDir().getAbsolutePath() + "/" + nomePastaSalvar + "/";
        caminhoExterno = "/" + nomePastaSalvar + "/";
        caminhoTemp = context.getFilesDir().getAbsolutePath() + "/" + nomePastaTpm + "/";
        setDimencoes();
    }

    public static Imagens getInstance() {
        if (ourInstance != null) {
            return ourInstance;
        } else {
            throw new RuntimeException(
                    "Usa ums dos metodos estaticos  - init - em classe Aplication para poder  usar a classe  !");
        }
    }


    // -------------------------------Metodos de converção ---------------------------------------------------------
    public Bitmap convetApartirResorce(Resources res, int resId, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculaAlturaLargura(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private Bitmap decodeSampledBitmapFromUri(Uri sourceUri, int w, int h) {
        InputStream stream = null;
        Bitmap bitmap = null;


        try {
            stream = mContext.getContentResolver().openInputStream(sourceUri);
            if (stream != null) {

                final BitmapFactory.Options options = new BitmapFactory.Options();
               // options.inJustDecodeBounds = true;
               // BitmapFactory.decodeStream(stream, null, options);

                // Calculate inSampleSize
                options.inSampleSize = calculateInSampleSize(sourceUri,h);

                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(stream, null, options);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public  int calculateInSampleSize(Uri sourceUri, int requestSize)   {
        InputStream is = null;
        // check image size
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            is = mContext.getContentResolver().openInputStream(sourceUri);
            BitmapFactory.decodeStream(is, null, options);
           if (is!= null)is.close();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        int inSampleSize = 1;
        while (options.outWidth / inSampleSize > requestSize
                || options.outHeight / inSampleSize > requestSize) {
            inSampleSize *= 2;
        }
        return inSampleSize;
    }


    public byte[] convertBitmapPraBytArray(Bitmap imagem, int qualidadedaImagem) {
        byte[] retorno = null;
        if (imagem != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imagem.compress(Bitmap.CompressFormat.PNG, qualidadedaImagem, stream);
            retorno = stream.toByteArray();
        }
        return retorno;
    }

    public Bitmap converteByArrayPraBitmap(byte[] imagem) {

        Bitmap b = null;
        if (imagem != null) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(imagem, 0, imagem.length, options);

            // Calculate inSampleSize
            options.inSampleSize = calculaAlturaLargura(options, w, h);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            b = BitmapFactory.decodeByteArray(imagem, 0, imagem.length, options);
        }
        return b;
    }

    public Bitmap converteByArrayPraBitmap(byte[] imagem, int reqWidth, int reqHeight) {

        Bitmap b = null;
        if (imagem != null) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(imagem, 0, imagem.length, options);

            // Calculate inSampleSize
            options.inSampleSize = calculaAlturaLargura(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            b = BitmapFactory.decodeByteArray(imagem, 0, imagem.length, options);
        }
        return b;
    }

    public Bitmap reduzTamanho(Bitmap imagem, int reqWidth, int reqHeight) {

        Bitmap b = null;
        if (imagem != null) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            byte[] bytes = convertBitmapPraBytArray(imagem, 100);
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

            // Calculate inSampleSize
            options.inSampleSize = calculaAlturaLargura(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            b = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        }
        return b;
    }

    public Bitmap converFilePraBitmap(String imagem, int reqWidth, int reqHeight) {

        Bitmap b = null;
        if (imagem != null) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagem, options);

            // Calculate inSampleSize
            options.inSampleSize = calculaAlturaLargura(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            b = BitmapFactory.decodeFile(imagem, options);
        }
        return b;
    }

    public Bitmap converFilePraBitmap(String imagem) {
        Bitmap b = null;
        if (imagem != null) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagem, options);

            // Calculate inSampleSize
            options.inSampleSize = calculaAlturaLargura(options, w, h);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            b = BitmapFactory.decodeFile(imagem, options);
        }
        return b;
    }

    public String convertByteParaString(byte[] imagem) {

        String b = null;
        if (imagem != null) {
            b = Base64.encodeToString(imagem, 0);
        }
        return b;
    }

    private int calculaAlturaLargura(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }



    // -------------------------------- Metodos para ler Imagens -------------------------------------------------------

    public void lendoBitmapParaLista(String imageCaminho, ImageView view) {
        lendoBitmapParaLista(imageCaminho, view, null, null);
    }

    public void lendoBitmapParaLista(String imageCaminho, ImageView view, @Nullable Integer altura, @Nullable Integer comprimento) {

        try {
            if (imageCaminho != null && view != null) {
                if (canselaPotencialTarefa(view.getId(), view)) {
                    RequisitarImagem task  = new RequisitarImagem(view, imageCaminho, view.getId());
                    if (altura != null && comprimento != null) task.setDimencao(comprimento, altura);
                    final AsyncColorDrawable asyncDrawable = new AsyncColorDrawable(task
                            , getImagemBitmap(String.valueOf(imagemDeEspera)));
                    view.setImageDrawable(asyncDrawable);
                    arrayList.offer(task);
                    requisitar();
                }
            }
        }catch (Exception e ){
            e.printStackTrace();
        }

    }

    // usado para canselar uma tarefa
    private synchronized boolean canselaPotencialTarefa(int data, ImageView imageView) {

        final RequisitarImagem requisitarImagem = getConvertImageTask(imageView);

        if (requisitarImagem != null) {
            final int bitmapData = requisitarImagem.data;
            if (bitmapData == 0 || bitmapData != data) {
                requisitarImagem.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }

    // Todo: convete mUrl em plano do fundo
    @SuppressLint("StaticFieldLeak")
    private class RequisitarImagem extends AsyncTask<Void, Void, Object> {

        private final WeakReference<? extends ImageView> weakReference;
        private int mLargura = getW();
        private int mAltura = getH();
        private final String mUrl;
        int data;
        private boolean isDimencao = false;
        RequisitarImagem requisitarImagem;

        RequisitarImagem(ImageView view, String mUrl, int data) {
            this.data = data;
            this.mUrl = mUrl;
            weakReference = new WeakReference<>(view);
            requisitarImagem = this;
        }

        void setDimencao(int largura, int altura) {

            if (largura > 0 && altura > 0) {
                mLargura = largura;
                mAltura = altura;
                setDimencao();
            }
        }

        @Override
        protected void onPreExecute() {
            setEmServico(true);  // bloquear para não carregar mais mUrl
        }

        @Override
        protected Object doInBackground(Void... params) {

            Bitmap bitmap;
            if (isDimencao()) {
                bitmap = getImagemBitmap(mUrl, this.mLargura, this.mAltura);
            } else {
                bitmap = getImagemBitmap(mUrl);
            }
            if (bitmap != null) {
                return bitmap;
            } else {
                return new ErroBaixado(getImagemBitmap(String.valueOf(imagemDeErro)));
            }
        }

        @Override
        protected void onPostExecute(Object object) {
            //noinspection ConstantConditions
            if (weakReference != null && object != null) {
                final ImageView imageView = weakReference.get();

                final RequisitarImagem requisitarImagem = getConvertImageTask(imageView);

                //noinspection ConstantConditions
                if (this == requisitarImagem && imageView != null) {

                    if (object instanceof ErroBaixado) {
                        imageView.setImageDrawable((ErroBaixado) object);
                    } else if (object instanceof Bitmap) {

                        imageView.setImageBitmap((Bitmap) object);
                        if (imageView instanceof FarrasImagemView)
                            ((FarrasImagemView) imageView).setClik(true);
                    }
                }
            }

            // liberar para carregar mais mUrl
            setEmServico(false);

            // carregar mais mUrl
            if (arrayList.peek() != null) requisitar();
        }

        boolean isDimencao() {
            return this.isDimencao;
        }

        void setDimencao() {
            this.isDimencao = true;
        }
    }

    // usado para setar a image view enquando o prosseso esta em andamento

    private class AsyncColorDrawable extends BitmapDrawable {

        private final WeakReference<RequisitarImagem> weakReference;

        AsyncColorDrawable(RequisitarImagem requisitarImagem, Bitmap bit) {
            super(mContext.getResources(), bit);
            weakReference = new WeakReference<>(requisitarImagem);
        }

        RequisitarImagem getConvetImagemTask() {
            return weakReference.get();
        }
    }

    public class ErroBaixado extends BitmapDrawable {
        ErroBaixado(Bitmap bit) {
            super(mContext.getResources(), bit);
        }
    }

    // usado para retira uma intancia converte image a partir da view qu esta a tela
    private static RequisitarImagem getConvertImageTask(ImageView imageView) {

        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncColorDrawable) {
                final AsyncColorDrawable asyncDrawable = (AsyncColorDrawable) drawable;
                return asyncDrawable.getConvetImagemTask();
            }
        }
        return null;
    }

    private void requisitar() {
        if (arrayList.size() > 0 && !isEmServico()) arrayList.poll().execute();
    }

    private boolean isEmServico() {
        return emServico;
    }

    private void setEmServico(boolean emServico) {
        this.emServico = emServico;
    }

    //  Todo metodos utilitarios


    public void setImagemDeErro(int imagemDeErro) {
        this.imagemDeErro = imagemDeErro;
    }

    public void setImagemDeEspera(int imagemDeEspera) {
        this.imagemDeEspera = imagemDeEspera;
    }

    public boolean isExisteImagem(String imagem) {
        File caminho = new File(getCaminhoEmagems(imagem));
        return caminho.exists();
    }

    public boolean apagarImagem(String imagem) {
        File caminho = new File(getCaminhoEmagems(imagem));
        return caminho.delete();
    }

    public synchronized boolean saveArrayToInternalStorageImagems(String fileName, byte[] imagem) {
        try {

            if (imagem != null) {
                // criando a pasta de armazenamento se não existir
                File fil = new File(getCaminhoEmagems());
                boolean bole = fil.exists();
                if (!bole) bole = fil.mkdirs();

                // armazenando a imagem no telefone
                if (bole) {

                    File file = new File(getCaminhoEmagems(), extraiNome(fileName));

                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(imagem);
                    fos.close();
                    Log.d(Constantes.PREFER_NOME, "Imagem Salva : " + fileName);
                    return true;
                } else {
                    Log.d(Constantes.PREFER_NOME, "Imagem não Salva : " + fileName);
                    return false;
                }

            } else {
                Log.d(Constantes.PREFER_NOME, "Array de bytes null : " + fileName);
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.w(Constantes.PREFER_NOME, "InternalStorage : " + "Error writing", e);
            return false;
        }
    }

    public synchronized boolean saveArrayToInternalStorageImagems(String fileName, String path, byte[] imagem) {

        try {

            if (imagem != null) {

                File fil = new File(path);
                boolean bole = fil.exists();

                if (!bole)
                    bole = fil.mkdirs();

                if (bole) {

                    File file = new File(path, extraiNome(fileName));
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(imagem);
                    fos.close();
                    Log.d(Constantes.PREFER_NOME, "Imagem Salva");
                    return true;
                } else {
                    Log.d(Constantes.PREFER_NOME, "Imagem não  Salva");
                    return false;
                }

            } else {
                Log.d(Constantes.PREFER_NOME, "array de bytes null  imposivel salvar ");
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.w(Constantes.PREFER_NOME, "InternalStorage : " + "Error writing", e);
            return false;
        }
    }

    public synchronized boolean saveArrayToSDCard(String fileName, byte[] imagem) {

        File file = new File(getCaminhoEmagensExterno());
        boolean bolea = file.exists();

        if (!bolea)
            bolea = file.mkdirs();

        File fil = new File(getCaminhoEmagensExterno(), extraiNome(fileName));
        try {

            if (bolea && isExternalStorageWritable()) {
                OutputStream os = new FileOutputStream(fil);
                os.write(imagem);
                os.close();
                Log.w(Constantes.PREFER_NOME, "Imagem Salvo ");
                return true;
            } else {

                Log.w(Constantes.PREFER_NOME, "Não pode escrever em cartão externo ou cartão externo não existe");
                return false;
            }

        } catch (IOException e) {
            Log.w(Constantes.PREFER_NOME, "Erro ao escrever no cartão externo: ", e);

            return false;
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public String getCaminhoEmagensExterno() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + caminhoExterno;
    }

    public String getCaminhoEmagensExterno(String nomePasta) {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + nomePasta;
    }

    public String getCaminhoEmagems(String filenome) {
        try {
            URI uri = new URI(filenome);

            switch (uri.getScheme()) {

                case "file":
                    File io = new File(filenome);
                    return caminho + io.getName();
                case "content":

                    return caminho + "erroya";

                case "http":
                case "https":
                    return caminho + new File(uri.getPath()).getName();

                default:
                    File o = new File(filenome);
                    return caminho + o.getName();
            }
        } catch (Exception e) {
            //  e.printStackTrace();
            File o = new File(filenome);
            return caminho + o.getName();
        }
    }

    public String getCaminhoEmagemsTpm(String filenome) {
        try {
            URI uri = new URI(filenome);

            switch (uri.getScheme()) {

                case "file":
                    File io = new File(filenome);
                    return caminhoTemp + io.getName();
                case "http":
                case "https":
                    return caminhoTemp + new File(uri.getPath()).getName();
                default:
                    File o = new File(filenome);
                    return caminhoTemp + o.getName();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            File o = new File(filenome);
            return caminhoTemp + o.getName();
        }
    }

    public String getCaminhoEmagems() {
        return caminho;
    }

    public String getCaminhoEmagemsTpm() {
        return caminhoTemp;
    }

    public String extraiNome(String caminho) {
        try {
            URI uri = new URI(caminho);

            switch (uri.getScheme()) {

                case "file":
                    File io = new File(caminho);
                    return io.getName();
                case "http":
                case "https":
                    return new File(uri.getPath()).getName();
                default:
                    File file = new File(caminho);
                    return file.getName();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            File file = new File(caminho);
            return file.getName();
        }
    }

    public boolean baixarImagem(String imagem) throws IOException, URISyntaxException {
        return baixarImagem(imagem, getW(), getH());
    }

    public boolean baixarImagem(String imagem, int w, int h) throws IOException, URISyntaxException {

        return isExisteImagem(imagem) || saveArrayToInternalStorageImagems(imagem, getImagem(imagem, w, h));
    }

    public int calculaQualidade(int mTamanho) {

        float tamanho;

        tamanho = mTamanho / Constantes.tamanhoMaximo;

        if (tamanho < 0) {
            return 100;
        } else if (tamanho > 0 && tamanho < 2) {
            return 75;
        } else if (tamanho >= 2 && tamanho < 3) {
            return 50;
        } else {
            return 50;
        }
    }

    public void setDimencoes() {

        float ref = mContext.getResources().getDisplayMetrics().xdpi;

        if (ref > 960) {
            h = w = 1000;
        } else if (960 < ref && ref > 640) {
            h = w = 800;
        } else if (640 <= ref && ref > 470) {
            h = w = 500;
        } else if (470 <= ref) {
            h = w = 470;

        } else {
            h = w = 426;
        }
    }

    @SuppressWarnings("WeakerAccess")
    public int getW() {
        return w;
    }

    @SuppressWarnings("WeakerAccess")
    public int getH() {
        return h;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean isNumero(String numero) {
        try {
            Uri url = Uri.parse(numero);
            String s = url.getPath();
            if (url.getScheme() != null){
                String[] list =s.split("/");
                s = list[list.length-1];
                Integer.parseInt(s);
                return true;
            }
            Integer.parseInt(numero);
            return true;
        } catch (Exception e) {

            return false;
        }
    }



    @SuppressWarnings("ResultOfMethodCallIgnored")
    public int getNumero(String numero) {

        try {
            Uri url = Uri.parse(numero);
            String s = url.getPath();
            if (url.getScheme() != null){
                String[] list =s.split("/");
                s = list[list.length-1];
                return Integer.parseInt(s);
            }
            return Integer.parseInt(numero);
        } catch (Exception e) {
            return 0;
        }
    }

    public Bitmap getImagemBitmap(String url) {
        return getImagemBitmap(url, getW(), getH());
    }

    private Bitmap getImagemBitmap(String url, int w, int h) {

        File caminho;
        caminho = new File(getCaminhoEmagems(url));

        Bitmap bitmap;

        // Busca mUrl em cache
        if (cachApp.getLruCache(url)!= null) {
            Log.i(Constantes.PREFER_NOME, "Imagem encontrado em Cach ");
            return cachApp.getLruCache(url);

        }  else if (isNumero(url)) {
            Log.i(Constantes.PREFER_NOME, "Imagem encontrado em Resorce ");
            bitmap = convetApartirResorce(mContext.getResources(), getNumero(url), getW(), getH());
            if (bitmap != null) cachApp.addLruCache(url, bitmap);
            return bitmap;
            // buscando na web
        }else if (Uri.parse(url).getScheme().equals("content")){

            Log.i(Constantes.PREFER_NOME, "Imagem encontrado em Url");
            bitmap = decodeSampledBitmapFromUri(Uri.parse(url), w, h);

            if (bitmap != null) cachApp.addLruCache(url, bitmap);
            return bitmap;

            // buscando na web
        } else if (caminho.exists()) {
            Log.i(Constantes.PREFER_NOME, "Imagem encontrado em File ");
            bitmap = converFilePraBitmap(caminho.toString(), w, h);

            if (bitmap != null) cachApp.addLruCache(url, bitmap);
            return bitmap;

            // buscando na web
        } else {
            Log.i(Constantes.PREFER_NOME, "Imagem encontrado em Baixada ");
            byte[] bytes = null;
            try {
                bytes = baixarImagemPorLink(url);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (bytes != null) {
                return converteByArrayPraBitmap(bytes, w, h);
            } else {
                return null;
            }
        }
    }

    public byte[] getImagem(String url) throws IOException, URISyntaxException {
        return getImagem(url, getW(), getH());
    }

    private byte[] getImagem(String url, int w, int h) throws IOException, URISyntaxException {

        File caminho = new File(getCaminhoEmagems(url));
        Bitmap bitmap;

        // Busca mUrl em cache
        if (cachApp.getLruCache(url) != null) {

            return convertBitmapPraBytArray(reduzTamanho(cachApp.getLruCache(url), w, h), 100);

            // buscando em memoria
        } else if (caminho.exists()) {

            bitmap = converFilePraBitmap(caminho.getPath(), w, h);

            if (bitmap != null) cachApp.addLruCache(url, bitmap);
            return convertBitmapPraBytArray(bitmap, 50);

            // buscando na web
        } else {

            byte[] bytes = baixarImagemPorLink(url);

            if (bytes != null) {

                return convertBitmapPraBytArray(
                        reduzTamanho(
                                converteByArrayPraBitmap(bytes, w, h), w, h)
                        , 100);
            } else {
                return null;
            }
        }
    }

    /**
     * @param linkImagen link da imagen
     * @return a imagen em array de byte
     * @throws URISyntaxException
     * @throws IOException
     */
    public byte[] baixarImagemPorLink(String linkImagen) throws URISyntaxException, IOException {

        byte[] imagebyt;
        URI uri = new URI(linkImagen);
        URL murl = uri.toURL();
        InputStream in = new BufferedInputStream(murl.openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n;
        while (-1 != (n = in.read(buf))) {
            out.write(buf, 0, n);
        }

        imagebyt = out.toByteArray();
        out.close();
        in.close();
        return imagebyt;
    }

    public Context getmContext() {
        return mContext;
    }
}
