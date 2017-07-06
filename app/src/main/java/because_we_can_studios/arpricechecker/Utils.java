package because_we_can_studios.arpricechecker;


import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.qrcode.encoder.ByteMatrix;

final class Utils {

    private Utils() {
    }

    static public Bitmap BitmapFromByteMatrix(ByteMatrix byteMatrix) {
        Bitmap bitmap = Bitmap.createBitmap(byteMatrix.getHeight(), byteMatrix.getWidth(),
                Bitmap.Config.ARGB_8888);
        for (int i = 0; i < bitmap.getHeight(); ++i)
            for (int j = 0; j < bitmap.getWidth(); ++j)
                bitmap.setPixel(i, j, byteMatrix.get(i, j) == 0 ? Color.BLACK : Color.WHITE);
        return bitmap;
    }

}
