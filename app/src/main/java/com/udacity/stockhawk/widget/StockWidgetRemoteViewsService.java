package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by ericwei on 2017-02-09.
 */

public class StockWidgetRemoteViewsService extends RemoteViewsService {
    private static final String TAG = StockWidgetRemoteViewsService.class.getSimpleName();

    private static final String[] STOCK_COLUMNS = {
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
            Contract.Quote.COLUMN_PERCENTAGE_CHANGE
    };


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                /////////////////////////////////////////////////////////////////////////////////////////////
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();

                data = getContentResolver().query(Contract.Quote.URI,
                        STOCK_COLUMNS,
                        null, null,
                        Contract.Quote.COLUMN_SYMBOL + " ASC");

                Binder.restoreCallingIdentity(identityToken);
                /////////////////////////////////////////////////////////////////////////////////////////////
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int i) {
                if (i == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(i)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.list_item_quote);

                String symbol = data.getString(data.getColumnIndex(STOCK_COLUMNS[0]));
                String price = data.getString(data.getColumnIndex(STOCK_COLUMNS[1]));
                float rawAbsoluteChange = data.getFloat(data.getColumnIndex(STOCK_COLUMNS[2]));
                float percentageChange = data.getFloat(data.getColumnIndex(STOCK_COLUMNS[3]));

                if (rawAbsoluteChange > 0) {
                    views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                } else {
                    views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                }

                views.setTextViewText(R.id.symbol, symbol);
                views.setTextViewText(R.id.price, price);

                //Formatting the display of the stock change textview
                DecimalFormat dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                dollarFormatWithPlus.setPositivePrefix("+$");
                DecimalFormat percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
                percentageFormat.setMaximumFractionDigits(2);
                percentageFormat.setMinimumFractionDigits(2);
                percentageFormat.setPositivePrefix("+");

                if (PrefUtils.getDisplayMode(getApplicationContext()).equals(
                        getApplicationContext().getString(R.string.pref_display_mode_absolute_key))) {
                    String change = dollarFormatWithPlus.format(rawAbsoluteChange);
                    views.setTextViewText(R.id.change, change);
                } else {
                    String percentage = percentageFormat.format(percentageChange / 100);
                    views.setTextViewText(R.id.change, percentage);
                }

                //set the click Intent
                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra("stock_name", symbol);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.list_item_quote);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
//                if (data.moveToPosition(position)) {
//                    return data.getLong();
//                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
