package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;

public class StockDetailActivity extends AppCompatActivity {
    private static final String TAG = StockDetailActivity.class.getSimpleName();

    String stockSymbol;
    private LineChart mLineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        LineChart mLineChart = (LineChart) findViewById(R.id.history_chart);
        TextView tv = (TextView) findViewById(R.id.historical_stock_data);

        stockSymbol = getIntent().getStringExtra("stock_name");

        String[] stocksHistory = getStockValueOverTime().split("\n");   //"\\r?\\n"
        tv.setText(String.valueOf(stocksHistory.length));


//

    }

    private String getStockValueOverTime() {
        Cursor cursor = getContentResolver().query(Contract.Quote.URI,
                new String[]{Contract.Quote.COLUMN_HISTORY},
                Contract.Quote.COLUMN_SYMBOL + "='" + stockSymbol + "'",
                null, null);
        cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            return cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY));
        }
        Log.d(TAG, "error retrieving Stock values");
        return null;
    }

    //https://discussions.udacity.com/t/how-to-scale-mpandroid-y-axis/187343/11
    private void updateChart(String[] stocksHistory) {
        ArrayList<Entry> entries = new ArrayList<Entry>();
        ArrayList<String> labels = new ArrayList<String>();
        String[] oneEntry = null;

        for (int i = 0; i < stocksHistory.length; i++) {
            oneEntry = stocksHistory[i].split(",");
            entries.add(new Entry(Float.parseFloat(oneEntry[1]), i));
            labels.add(oneEntry[0]);
        }

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        YAxis left = mLineChart.getAxisLeft();
        left.setEnabled(true);
        left.setLabelCount(5, true);  //????
        xAxis.setTextColor(Color.WHITE);
        left.setTextColor(Color.WHITE);

        mLineChart.getAxisRight().setEnabled(false);
        LineDataSet dataSet = new LineDataSet(entries, "Stock Labels");
        dataSet.setColor(Color.RED);
        dataSet.setValueTextColor(Color.BLACK);


        LineData lineData = new LineData(dataSet);

        mLineChart.setData(lineData);
        mLineChart.animateX(1);
        mLineChart.setBackgroundColor(Color.TRANSPARENT);
        mLineChart.invalidate();

    }

}
