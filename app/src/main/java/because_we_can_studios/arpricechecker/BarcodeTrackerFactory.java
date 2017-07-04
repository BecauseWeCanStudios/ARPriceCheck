package because_we_can_studios.arpricechecker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

class BarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {
    private GraphicOverlay mGraphicOverlay;

    BarcodeTrackerFactory(GraphicOverlay graphicOverlay) {
        mGraphicOverlay = graphicOverlay;
    }

    @Override
    public Tracker<Barcode> create(Barcode barcode) {
        /* Check if barcode is correct here */
        BarcodeGraphic graphic = new BarcodeGraphic(mGraphicOverlay, true);
        return new BarcodeGraphicTracker(mGraphicOverlay, graphic);
    }
}

class BarcodeGraphic extends GraphicOverlay.Graphic {

    private static final int colorCorrect = Color.GREEN;
    private static final int colorIncorrect = Color.RED;
    private Paint mRectPaint;
    private volatile Barcode mBarcode;
    private int mId;

    BarcodeGraphic(GraphicOverlay overlay, boolean isIncorrect) {
        super(overlay);
        mRectPaint = new Paint();
        mRectPaint.setColor(isIncorrect ? colorIncorrect : colorCorrect);
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(10.0f);
    }

    void updateItem(Barcode barcode) {
        mBarcode = barcode;
        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        Barcode barcode = mBarcode;
        if (barcode == null)
            return;
        RectF rect = new RectF(barcode.getBoundingBox());
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);
        canvas.drawRect(rect, mRectPaint);
    }

    void setId(int id) {
        mId = id;
    }

    protected int getId() {
        return mId;
    }
}

class BarcodeGraphicTracker extends Tracker<Barcode> {
    private GraphicOverlay mOverlay;
    private BarcodeGraphic mGraphic;

    BarcodeGraphicTracker(GraphicOverlay overlay, BarcodeGraphic graphic) {
        mOverlay = overlay;
        mGraphic = graphic;
    }

    @Override
    public void onNewItem(int id, Barcode item) {
        mGraphic.setId(id);
    }

    @Override
    public void onUpdate(Detector.Detections<Barcode> detectionResults, Barcode item) {
        mOverlay.add(mGraphic);
        mGraphic.updateItem(item);
    }

    @Override
    public void onMissing(Detector.Detections<Barcode> detectionResults) {
        mOverlay.remove(mGraphic);
    }

    @Override
    public void onDone() {
        mOverlay.remove(mGraphic);
    }

}