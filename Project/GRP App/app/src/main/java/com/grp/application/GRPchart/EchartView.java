package com.grp.application.GRPchart;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.github.abel533.echarts.json.GsonOption;

/**
 * This class is to create the chart.
 *
 * @author UNNC GRP G19
 */
public class EchartView extends WebView {
    private static final String TAG = EchartView.class.getSimpleName();

    /**
     * The constructor of echartView.
     *
     * @param context The app context
     */
    public EchartView(Context context) {
        this(context, null);
    }

    /**
     * @param context The app context
     * @param attrs   The attributeSet
     */
    public EchartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EchartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Initialize the chart.
     */
    private void init() {
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportZoom(false);
        webSettings.setDisplayZoomControls(false);
        loadUrl("file:///android_asset/echarts.html");
    }

    /**
     * Refresh the chart each time the data refreshes.
     *
     * @param option the GsonOption
     */
    public void refreshEchartsWithOption(GsonOption option) {
        if (option == null) {
            return;
        }
        String optionString = option.toString();
        String call = "javascript:loadEcharts('" + optionString + "')";
        loadUrl(call);
    }
}