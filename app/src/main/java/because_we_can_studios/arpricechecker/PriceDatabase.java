package because_we_can_studios.arpricechecker;

import java.lang.String;
import android.util.LongSparseArray;

public class PriceDatabase {

    private static LongSparseArray<Long> localCache = new LongSparseArray<>();

    /**
     * Connects to database server
     */
    public PriceDatabase(String address) {
        // No server yet
        // Let's fill localCache with test values here
        for (Long i = 0L; i < 20; ++i) {
            localCache.put(i, i);
        }
    }

    public long getPrice(long id) {
        Long price = localCache.get(id);
        if (price != null) {
            return price;
        }
        else {
            // Ask database, probably
            return 0;
        }
    }

}
