package com.example.ele_me.activity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Size;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ele_me.R;
import com.example.ele_me.util.ImageSaver;

public class Camera2Activity extends Activity implements TextureView.SurfaceTextureListener,View.OnClickListener {

    private static String TAG = "POPUWAL";
    private TextureView textureView;
    private ImageView imageView;
    private ImageView captureView;
    CameraManager cameraManager;
    SurfaceTexture surfaceTexture = null;
    private CameraCaptureSession captureSession;
    private ImageReader imageReader;
    private Size size;
    private CameraDevice cameraDevice;
    private String[] cameraList;

    private Handler MyHandle;
    private File file;
    private int state;
    private CaptureRequest previewCaptureRequest;
    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private HandlerThread mBackgroundThread;
static {
     File file = new File(Environment.getExternalStorageDirectory(), TAG);
    if (!file.exists()) {
        file.mkdir();
    }
}
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2_camera);

        // preview view
        textureView = (TextureView) findViewById(R.id.surfaceView);
        textureView.setSurfaceTextureListener(this);
        //textureView.setAlpha(0.8f);

        // switch view
        imageView = (ImageView)findViewById(R.id.img_switch_camera);
        imageView.setOnClickListener(this);
        captureView = (ImageView) findViewById(R.id.img_capture_button);
        captureView.setOnClickListener(this);

        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            cameraList = Objects.requireNonNull(cameraManager).getCameraIdList();   //generally is 0/1,0 is main camera,1 is the front camera.
            if (checkSelfPermission("android.permission.CAMERA") != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG,"request permission") ;
                requestPermissions(new String[]{"android.permission.CAMERA"}, 110);
            }
        } catch (CameraAccessException | NullPointerException e) {
            e.printStackTrace();
        }

        getActionBar().hide();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        MyHandle = new Handler(mBackgroundThread.getLooper());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 110) {
            Log.e(TAG, "onRequestPermissionsResult " + grantResults[0]); // grantResults[0] == -1 for not have Permission
            if (grantResults[0] < 0)
                finish();
        }
    }

    private void openCamera(String id) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Log.e(TAG, "openCamera No permission");
                finish();
                return;
            }

        try {
            cameraManager.openCamera(id,mCallback,null);
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(id);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            size = map.getOutputSizes(SurfaceTexture.class)[0];
            setUpImageReader();
            surfaceTexture = textureView.getSurfaceTexture();
            Log.e(TAG, "openCamera to init surfaceTexture" );
            surfaceTexture.setDefaultBufferSize(size.getWidth(), size.getHeight());
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "No CameraAccess", Toast.LENGTH_SHORT);
            finish();
        }
    }
    CameraDevice.StateCallback mCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.e(TAG, "Running here onOpened");
            cameraDevice = camera;
            final CaptureRequest.Builder captureRequestBuilder;
            final Surface surface = new Surface(surfaceTexture);
            final Surface surface1 = imageReader.getSurface();
            try {
                captureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

                camera.createCaptureSession(Arrays.asList(surface, surface1), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    captureSession = session;
                    captureRequestBuilder.addTarget(surface);   // 0516的坑，需要添加该target
                    captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                    previewCaptureRequest = captureRequestBuilder.build();
                    try {
                        Log.e(TAG, "Running here onConfigured");
                        session.setRepeatingRequest(previewCaptureRequest, null, null);//成功配置后，便开始进行相机图像的监听
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            },null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.e(TAG, "onDisconnected");
                camera.close();
                camera = null;
            cameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {

        }
    } ;

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.e(TAG, "running here to onSurfaceTextureAvailable and openCamera.");
        openCamera(String.valueOf(0));
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.e(TAG, "running here to onSurfaceTextureDestroyed.");
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    /**
     * keep to handle sth later.
     */
    private static class MyHandler extends Handler{
        WeakReference<Camera2Activity> activityWeakReference;

       MyHandler(Camera2Activity activity){
           activityWeakReference = new WeakReference<Camera2Activity>(activity);
       }

          @Override
          public void handleMessage(Message msg) {
              super.handleMessage(msg);
          }
      }

    private void setUpImageReader() {
        imageReader = ImageReader.newInstance(size.getWidth(), size.getHeight(), ImageFormat.JPEG, 2);
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Image image = reader.acquireNextImage();
                Log.e(TAG, "onImageAvailable"+image);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                Date curDate    =   new    Date(System.currentTimeMillis());//获取当前时间
                String    str    =    formatter.format(curDate);
                File mFile = new File(Environment.getExternalStorageDirectory().getPath()+"/"+TAG, str+".jpg");
                Log.e(TAG, "onImageAvailable: "+mFile.getAbsolutePath());
                MyHandle.post(new ImageSaver(image, mFile));
                Toast.makeText(getApplicationContext(), str+".jpg"+"saved on \\n"+Environment.getExternalStorageDirectory().getPath()+"/"+TAG, Toast.LENGTH_SHORT);
            }
        }, MyHandle);
    }


    @Override
    public void onClick(View v) {
       switch (v.getId()) {
           case R.id.img_switch_camera:
               String usingCameraId;
               String pendingCameraId ="0";
               if (cameraDevice != null) {
                   usingCameraId = cameraDevice.getId();
                   Log.e(TAG, "usingCameraId "+usingCameraId);
                   cameraDevice.close();
                   cameraDevice = null;
                   for (String id:cameraList) {
                       Log.e(TAG, "Id "+id);
                        if (id.equals(usingCameraId)){
                            continue;
                        }
                            pendingCameraId = id;
                   }
                   openCamera(pendingCameraId);
               }
               break;
           case R.id.img_capture_button:
               if (cameraDevice != null && captureSession != null) {
                   CaptureRequest.Builder captureRequestBuilder;
                   final Surface surface1 = imageReader.getSurface();
                   try {
                       captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                       captureRequestBuilder.addTarget(surface1);   // 0516的坑，需要添加该target
                       captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                       captureSession.capture(captureRequestBuilder.build(), mCallBack , null);
                       captureSession.stopRepeating();
                       imageView.setVisibility(View.INVISIBLE);
                       captureView.setVisibility(View.INVISIBLE);
                       state = 1;
                   } catch (CameraAccessException e) {
                       e.printStackTrace();
                   }
               }
               break;
       }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (state == 1) {
                    imageView.setVisibility(View.VISIBLE);
                    captureView.setVisibility(View.VISIBLE);
                    if (captureSession != null) {
                        try {
                            captureSession.setRepeatingRequest(previewCaptureRequest, null,null );
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    state = 0;
                    return true;
                }
            break;
        }
        return super.onKeyDown(keyCode, event);
    }

    CameraCaptureSession.CaptureCallback mCallBack = new CameraCaptureSession.CaptureCallback() {
        private void process(CaptureResult result) {

        }
        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            Log.e(TAG, "onCaptureCompleted");
            process(result);
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
            process(partialResult);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }
}
