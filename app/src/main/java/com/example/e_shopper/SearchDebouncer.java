package com.example.e_shopper;

import android.os.Handler;
import android.os.Looper;
import java.util.function.Consumer;


public class SearchDebouncer {
    private static final long DEBOUNCE_DELAY_MS = 300;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Consumer<String> searchCallback;
    private Runnable runnable;

    public SearchDebouncer(Consumer<String> searchCallback) {
        this.searchCallback = searchCallback;
    }

    public void processQuery(String query) {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }

        runnable = () -> searchCallback.accept(query);
        handler.postDelayed(runnable, DEBOUNCE_DELAY_MS);
    }

    public void destroy() {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }
}