package com.carregarimagem.CapturaImagem;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carregarimagem.R;
import com.carregarimagem.util.Constantes;
import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;
import com.isseiaoki.simplecropview.util.Logger;
import com.isseiaoki.simplecropview.util.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions public class BasicFragment extends Fragment {
    private static final String TAG = BasicFragment.class.getSimpleName();

    private static final int REQUEST_PICK_IMAGE = 10011;
    private static final int REQUEST_SAF_PICK_IMAGE = 10012;
    private static final String PROGRESS_DIALOG = "ProgressDialog";
    private static final String KEY_FRAME_RECT = "FrameRect";
    private static final String KEY_SOURCE_URI = "SourceUri";

    // Views ///////////////////////////////////////////////////////////////////////////////////////
    private CropImageView mCropView;
    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.JPEG;
    private RectF mFrameRect = null;
    private Uri mSourceUri = null;
    private boolean init= false;


    // Note: only the system can call this constructor by reflection.
    public BasicFragment() {
    }

    public static BasicFragment newInstance() {
        BasicFragment fragment = new BasicFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_basic, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // bind Views
        bindViews(view);

       // mCropView.setDebug(true);

        if (savedInstanceState != null) {
            // restore data
            if (savedInstanceState.getParcelable(KEY_FRAME_RECT)!=null)
                mFrameRect = savedInstanceState.getParcelable(KEY_FRAME_RECT);

            if (savedInstanceState.getParcelable(KEY_SOURCE_URI)!=null)
                mSourceUri = savedInstanceState.getParcelable(KEY_SOURCE_URI);
        }

        if (mSourceUri == null) {
            // default data
            mSourceUri = getUriFromDrawableResId(getContext(), R.drawable.avatar);
            Log.e("aoki", "mSourceUri = "+mSourceUri);
        }
        // load image
        mCropView.load(mSourceUri)
                .initialFrameRect(mFrameRect)
                .useThumbnail(true)
                .execute(mLoadCallback);
    }



    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save data
     if (mCropView.getActualCropRect()!=null)  outState.putParcelable(KEY_FRAME_RECT, mCropView.getActualCropRect());
        if (mCropView !=null)   outState.putParcelable(KEY_SOURCE_URI, mCropView.getSourceUri());
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (resultCode == Activity.RESULT_OK) {
            // reset frame rect
            mFrameRect = null;
            switch (requestCode) {
                case REQUEST_PICK_IMAGE:
                    mSourceUri = result.getData();
                    mCropView.load(mSourceUri)
                            .initialFrameRect(mFrameRect)
                            .useThumbnail(true)
                            .execute(mLoadCallback);
                    break;
                case REQUEST_SAF_PICK_IMAGE:
                    mSourceUri = Utils.ensureUriPermission(getContext(), result);
                    mCropView.load(mSourceUri)
                            .initialFrameRect(mFrameRect)
                            .useThumbnail(true)
                            .execute(mLoadCallback);
                    break;
            }
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        BasicFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    // Bind views //////////////////////////////////////////////////////////////////////////////////

    private void bindViews(View view) {
        mCropView = view.findViewById(R.id.cropImageView);
        view.findViewById(R.id.buttonDone).setOnClickListener(btnListener);
        view.findViewById(R.id.button1_1).setOnClickListener(btnListener);
        view.findViewById(R.id.buttonFree).setOnClickListener(btnListener);
        view.findViewById(R.id.buttonPickImage).setOnClickListener(btnListener);
        view.findViewById(R.id.buttonRotateLeft).setOnClickListener(btnListener);
        view.findViewById(R.id.buttonRotateRight).setOnClickListener(btnListener);
        view.findViewById(R.id.buttonCircle).setOnClickListener(btnListener);
        view.findViewById(R.id.buttonCamera).setOnClickListener(btnListener);
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE) public void pickImage() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),
                    REQUEST_PICK_IMAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_SAF_PICK_IMAGE);
        }
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) public void cropImage() {
        showProgress();
        mCropView.crop(mSourceUri).execute(mCropCallback);
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void showRationaleForPick(PermissionRequest request) {
        showRationaleDialog(R.string.app_name, request);
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void showRationaleForCrop(PermissionRequest request) {
        showRationaleDialog(R.string.app_name, request);
    }

    public void showProgress() {
        ProgressDialogFragment f = ProgressDialogFragment.getInstance();
        getFragmentManager().beginTransaction().add(f, PROGRESS_DIALOG).commitAllowingStateLoss();
    }

    public void dismissProgress() {
        if (!isResumed()) return;
        android.support.v4.app.FragmentManager manager = getFragmentManager();
        if (manager == null) return;
        ProgressDialogFragment f = (ProgressDialogFragment) manager.findFragmentByTag(PROGRESS_DIALOG);
        if (f != null) {
            getFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
        }
    }

    public Uri createSaveUri() {
        return createNewUri(getContext(), mCompressFormat);
    }

    public static String getDirPath() {
        String dirPath = "";
        File imageDir = null;
        File extStorageDir = Environment.getExternalStorageDirectory();
        if (extStorageDir.canWrite()) {
            imageDir = new File(extStorageDir.getPath() + Constantes.CARREGAR);
        }
        if (imageDir != null) {
            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }
            if (imageDir.canWrite()) {
                dirPath = imageDir.getPath();
            }
        }
        return dirPath;
    }

    public static Uri getUriFromDrawableResId(Context context, int drawableResId) {
        StringBuilder builder = new StringBuilder().append(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .append("://")
                .append(context.getResources().getResourcePackageName(drawableResId))
                .append("/")
                .append(context.getResources().getResourceTypeName(drawableResId))
                .append("/")
                .append(context.getResources().getResourceEntryName(drawableResId));
        return Uri.parse(builder.toString());
    }

    public static Uri createNewUri(Context context, Bitmap.CompressFormat format) {
        long currentTimeMillis = System.currentTimeMillis();
        Date today = new Date(currentTimeMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String title = dateFormat.format(today);
        String dirPath = getDirPath();
        String fileName = "scv" + title + "." + getMimeType(format);
        String path = dirPath + "/" + fileName;
        File file = new File(path);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + getMimeType(format));
        values.put(MediaStore.Images.Media.DATA, path);
        long time = currentTimeMillis / 1000;
        values.put(MediaStore.MediaColumns.DATE_ADDED, time);
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, time);
        if (file.exists()) {
            values.put(MediaStore.Images.Media.SIZE, file.length());
        }

        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Logger.i("SaveUri = " + uri);
        return uri;
    }

    public static String getMimeType(Bitmap.CompressFormat format) {
        Logger.i("getMimeType CompressFormat = " + format);
        switch (format) {
            case JPEG:
                return "jpeg";
            case PNG:
                return "png";
        }
        return "png";
    }

    public static Uri createTempUri(Context context) {
        return Uri.fromFile(new File(context.getCacheDir(), "cropped"));
    }

    private void showRationaleDialog(@StringRes int messageResId, final PermissionRequest request) {
        new AlertDialog.Builder(getActivity()).setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.proceed();
                    }
                }).setNegativeButton(R.string.canselar, new DialogInterface.OnClickListener() {
            @Override public void onClick(@NonNull DialogInterface dialog, int which) {
                request.cancel();
            }
        }).setCancelable(false).setMessage(messageResId).show();
    }

    // Handle button event /////////////////////////////////////////////////////////////////////////

    private final View.OnClickListener btnListener = new View.OnClickListener() {
        @Override public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.buttonDone) {
                BasicFragmentPermissionsDispatcher.cropImageWithPermissionCheck(BasicFragment.this);

            } else if (i == R.id.button1_1) {
                mCropView.setCropMode(CropImageView.CropMode.SQUARE);

            } else if (i == R.id.buttonFree) {
                mCropView.setCropMode(CropImageView.CropMode.FREE);

            } else if (i == R.id.buttonCircle) {
                mCropView.setCropMode(CropImageView.CropMode.CIRCLE);

            } else if (i == R.id.buttonRotateLeft) {
                mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);

            } else if (i == R.id.buttonRotateRight) {
                mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);

            } else if (i == R.id.buttonPickImage) {
                BasicFragmentPermissionsDispatcher.pickImageWithPermissionCheck(BasicFragment.this);

            }else if (i == R.id.buttonCamera) {
                BasicFragmentPermissionsDispatcher.pickImageWithPermissionCheck(BasicFragment.this);

            }
        }
    };

    // Callbacks ///////////////////////////////////////////////////////////////////////////////////

    private final LoadCallback mLoadCallback = new LoadCallback() {
        @Override public void onSuccess() {

            // iniciar galeria
         if (!init)  BasicFragmentPermissionsDispatcher.pickImageWithPermissionCheck(BasicFragment.this);

            init=true;
        }

        @Override public void onError(Throwable e) {
        }
    };

    private final CropCallback mCropCallback = new CropCallback() {
        @Override public void onSuccess(Bitmap cropped) {

            ((CarregarImagen) getActivity()).startResultActivity(cropped);

          //  mCropView.save(cropped).compressFormat(mCompressFormat).execute(createSaveUri(), mSaveCallback);
        }

        @Override public void onError(Throwable e) {
        }
    };

    private final SaveCallback mSaveCallback = new SaveCallback() {
        @Override public void onSuccess(Uri outputUri) {
            dismissProgress();
            ((CarregarImagen) getActivity()).startResultActivity(outputUri);
        }

        @Override public void onError(Throwable e) {
            dismissProgress();
        }
    };

}