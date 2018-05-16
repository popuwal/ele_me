package com.example.ele_me.activity;

import java.lang.ref.WeakReference;
import java.util.Arrays;
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
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import com.example.ele_me.R;

public class Camera2Activity extends Activity implements TextureView.SurfaceTextureListener {

    private static String TAG = "POPUWAL";
    private TextureView textureView;
    CameraManager cameraManager;
    SurfaceTexture surfaceTexture = null;
    private ImageReader imageReader;
    private Size size;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2_camera);

        textureView = (TextureView) findViewById(R.id.surfaceView);
        textureView.setSurfaceTextureListener(this);


        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        Handler handler = new MyHandler(this);
        String[] cameraList;
        try {
            cameraList = Objects.requireNonNull(cameraManager).getCameraIdList();   //generally is 0/1,0 is main camera,1 is the front camera.
            Log.e(TAG, "cameraList is: " + cameraList[1] + " zong chang du " + cameraList.length);
            if (checkSelfPermission("android.permission.CAMERA") != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{"android.permission.CAMERA"}, 110);
            } else {

            }
        } catch (CameraAccessException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void openCamera(int w, int h) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try {
            cameraManager.openCamera(String.valueOf(0),mCallback,null);

            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(String.valueOf(0));
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            size = map.getOutputSizes(SurfaceTexture.class)[0];
            setUpImageReader();
            surfaceTexture = textureView.getSurfaceTexture();
            Log.e(TAG, "Running herasdsadsadsadasasfe??asdsa??" + surfaceTexture);
            surfaceTexture.setDefaultBufferSize(size.getWidth(), size.getHeight());
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    CameraDevice.StateCallback mCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            final CaptureRequest.Builder captureRequestBuilder;
            final Surface surface = new Surface(surfaceTexture);
            final Surface surface1 = imageReader.getSurface();
            try {
                captureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

                camera.createCaptureSession(Arrays.asList(surface, surface1), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    captureRequestBuilder.addTarget(surface);   // 0516的坑，需要添加该target
                    captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                    try {
                        Log.e(TAG, "Running here????");
                        session.setRepeatingRequest(captureRequestBuilder.build(), null, null);//成功配置后，便开始进行相机图像的监听
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

        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {

        }
    } ;

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.e(TAG, "running here");
        openCamera(width,height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

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
        imageReader = ImageReader.newInstance(size.getWidth(), size.getHeight(), ImageFormat.YUV_420_888, 10);
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Image image = reader.acquireLatestImage();
                if (image != null) {
                    image.close();
                }
            }
        }, null);
    }
}
