package com.grp.application.export;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.grp.application.R;

import androidx.annotation.NonNull;

public class ShareView extends FrameLayout {

    private final int IMAGE_WIDTH = 720;
    private final int IMAGE_HEIGHT = 1280;

    private TextView tvInfo;

    public ShareView(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        View layout = View.inflate(getContext(), R.layout.share_view_layout, this);
        tvInfo = (TextView) layout.findViewById(R.id.tv_info);
    }

    /**
     * @param info
     */
    public void setInfo(String info) {
        tvInfo.setText(info);
    }

    /**
     * @return
     */
    public Bitmap createImage() {


        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(IMAGE_WIDTH, MeasureSpec.EXACTLY);
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(IMAGE_HEIGHT, MeasureSpec.EXACTLY);

        measure(widthMeasureSpec, heightMeasureSpec);
        layout(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
        Bitmap bitmap = Bitmap.createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        draw(canvas);

        return bitmap;
    }
}