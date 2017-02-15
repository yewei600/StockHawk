package com.udacity.stockhawk.sync;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import timber.log.Timber;


public class QuoteIntentService extends IntentService {
    Handler mHandler;


    public QuoteIntentService() {
        super(QuoteIntentService.class.getSimpleName());
        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("Intent handled");
        Boolean gotQuotes = QuoteSyncJob.getQuotes(getApplicationContext());
        if (!gotQuotes) {
            mHandler.post(new DisplayToast(getApplicationContext(), "couldn't add the stock!"));
        }
    }

    class DisplayToast implements Runnable {
        private final Context mContext;
        String mText;

        public DisplayToast(Context mContext, String mText) {
            this.mContext = mContext;
            this.mText = mText;
        }

        @Override
        public void run() {
            Toast.makeText(mContext, mText, Toast.LENGTH_SHORT).show();
        }
    }

}
