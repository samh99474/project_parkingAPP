package com.example.myapplication_mapnavigationdrawer.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class AutoSplitTextView extends androidx.appcompat.widget.AppCompatTextView {
    private boolean mEnabled = true;

    public AutoSplitTextView(Context context) {
        super(context);
    }

    public AutoSplitTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoSplitTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setAutoSplitEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY
                && MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY
                && getWidth() > 0
                && getHeight() > 0
                && mEnabled) {
            String newText = autoSplitText(this);
            if (!TextUtils.isEmpty(newText)) {
                setText(newText);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private String autoSplitText(final TextView tv) {
        final String rawText = tv.getText().toString(); //原始文字
        final Paint tvPaint = tv.getPaint(); //paint，包含字型等資訊
        final float tvWidth = tv.getWidth() - tv.getPaddingLeft() - tv.getPaddingRight(); //控制元件可用寬度

        //將原始文字按行拆分
        String [] rawTextLines = rawText.replaceAll("\r", "").split("\n");
        StringBuilder sbNewText = new StringBuilder();
        for (String rawTextLine : rawTextLines) {
            if (tvPaint.measureText(rawTextLine) <= tvWidth) {
                //如果整行寬度在控制元件可用寬度之內，就不處理了
                sbNewText.append(rawTextLine);
            } else {
                //如果整行寬度超過控制元件可用寬度，則按字元測量，在超過可用寬度的前一個字元處手動換行
                float lineWidth = 0;
                for (int cnt = 0; cnt != rawTextLine.length(); ++cnt) {
                    char ch = rawTextLine.charAt(cnt);
                    lineWidth += tvPaint.measureText(String.valueOf(ch));
                    if (lineWidth <= tvWidth) {
                        sbNewText.append(ch);
                    } else {
                        sbNewText.append("\n");
                        lineWidth = 0;
                        --cnt;
                    }
                }
            }
            sbNewText.append("\n");
        }

        //把結尾多餘的\n去掉
        if (!rawText.endsWith("\n")) {
            sbNewText.deleteCharAt(sbNewText.length() - 1);
        }

        return sbNewText.toString();
    }
}
