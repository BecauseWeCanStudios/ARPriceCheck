package because_we_can_studios.arpricechecker;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;

import java.io.IOException;
import java.lang.reflect.Field;

public class CameraSourcePreview extends ViewGroup {
    private static final String TAG = "CameraSourcePreview";

    private SurfaceView mSurfaceView;
    private boolean mStartRequested;
    private boolean mSurfaceAvailable;

    /* Camera used in CameraSource */
    private Camera mCamera;
    private CameraSource mCameraSource;

    private GraphicOverlay mOverlay;

    public CameraSourcePreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        mStartRequested = false;
        mSurfaceAvailable = false;
        mSurfaceView = new SurfaceView(context);
        mSurfaceView.getHolder().addCallback(new SurfaceCallback());
        addView(mSurfaceView);
    }

    public void start(CameraSource cameraSource) throws IOException {
        if (cameraSource == null)
            stop();
        mCameraSource = cameraSource;
        if (mCameraSource != null) {
            mStartRequested = true;
            startIfReady();
        }
    }

    public void start(CameraSource cameraSource, GraphicOverlay overlay) throws IOException {
        mOverlay = overlay;
        start(cameraSource);
    }

    public void stop() {
        if (mCameraSource != null) {
            mCameraSource.stop();
            mCamera = null;
        }
    }

    public void release() {
        if (mCameraSource != null) {
            mCameraSource.release();
            mCameraSource = null;
        }
    }

    private void startIfReady() throws IOException {
        if (mStartRequested && mSurfaceAvailable) {
            mCameraSource.start(mSurfaceView.getHolder());
            findCameraInCameraSource();
            if (mOverlay != null) {
                Size size = mCameraSource.getPreviewSize();
                int min = Math.min(size.getWidth(), size.getHeight());
                int max = Math.max(size.getWidth(), size.getHeight());
                mOverlay.setCameraInfo(min, max);
                mOverlay.clear();
            }
            mStartRequested = false;
        }
    }

    private class SurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surface) {
            mSurfaceAvailable = true;
            try {
                startIfReady();
            } catch (IOException e) {
                Log.e(TAG, "Could not start camera source.", e);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surface) {
            mSurfaceAvailable = false;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = 1080;
        int height = 1920;
        if (mCameraSource != null) {
            Size size = mCameraSource.getPreviewSize();
            if (size != null) {
                height = size.getWidth();
                width = size.getHeight();
            }
        }
        final int layoutWidth = right - left;
        final int layoutHeight = bottom - top;
        int childWidth = layoutWidth;
        int childHeight = (int)(((float) layoutWidth / (float) width) * height);
        if (childHeight > layoutHeight) {
            childHeight = layoutHeight;
            childWidth = (int)(((float) layoutHeight / (float) height) * width);
        }
        for (int i = 0; i < getChildCount(); ++i)
            getChildAt(i).layout(0, 0, childWidth, childHeight);
        try {
            startIfReady();
        } catch (IOException e) {
            Log.e(TAG, "Could not start camera source.", e);
        }
    }

    private void findCameraInCameraSource() {
        if (mCameraSource != null) {
            Field[] fields = mCameraSource.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getType() == Camera.class) {
                    field.setAccessible(true);
                    try {
                        mCamera = (Camera) field.get(mCameraSource);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    public Camera getCamera() {
        return mCamera;
    }
}

