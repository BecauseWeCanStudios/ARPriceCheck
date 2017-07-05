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
    private PriceDatabase mDatabase;

    BarcodeTrackerFactory(GraphicOverlay graphicOverlay, PriceDatabase database) {
        mGraphicOverlay = graphicOverlay;
        mDatabase = database;
    }

    @Override
    public Tracker<Barcode> create(Barcode barcode) {
        String[] val = barcode.rawValue.split("[\\n\\r\\s]+");
        if (val.length != 2) {
            return null;
        }
        try {
            Long price = mDatabase.getPrice(Long.parseLong(val[0]));
            BarcodeGraphic graphic = new BarcodeGraphic(mGraphicOverlay, price != Long.parseLong(val[1]));
            return new BarcodeGraphicTracker(mGraphicOverlay, graphic);
        }
        catch (NumberFormatException e) {
            return null;
        }
        catch (NullPointerException e) {
            return null;
        }
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
    public void onDone() {
        try {
            Thread.sleep(100);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        mOverlay.remove(mGraphic);
    }

}