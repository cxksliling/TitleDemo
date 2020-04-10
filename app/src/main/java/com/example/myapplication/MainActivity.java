package com.example.myapplication;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView = findViewById(R.id.text);
        final String title =
                "这是一个非常长的标题这是一个常长的标题这是一个非常长的标题这是一个非常长的标题这是一个非常长的标题这是一个非常长的标题这是一个非常长的标题这是一个非常长的标题这是一个非常长的标题这是一个非常长的标题这是一个非常长的标题这是一个非常长的标题这是一个非常长的标题这是一个非常长的标题这是一个非常长的标题";
        final SpannableString ss = new SpannableString(title);
        ForegroundColorSpan startSpan = new ForegroundColorSpan(Color.RED);
        ss.setSpan(startSpan, 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        final ForegroundColorSpan endSpan = new ForegroundColorSpan(Color.GREEN);
        ss.setSpan(endSpan, ss.length() - 2, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ss);
        //加一个preDrawListener 确保已经layout完成 可以拿到textView的测量信息
        textView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override public boolean onPreDraw() {
                        //如果只有一行就不考虑了
                        if (textView.getLineCount() > 1) {
                            //如果没有省略就不考虑了
                            if (textView.getLayout().getEllipsisCount(1) > 0) {
                                //新spannableString 用title的长度 减去省略的长度 再减去...8.8分的长度
                                // 大概是四个字符, 看情况可以再调整, 或者用textView.getPaint再测量一下 比较稳妥 我这里就简单写了
                                SpannableStringBuilder lastSS = new SpannableStringBuilder(
                                        ss.subSequence(0, ss.length() - textView.getLayout()
                                                .getEllipsisCount(1) - 4));
                                lastSS.append("...8.8分");
                                //重新设下span样式 也可以考虑从上面ss里截取 都可
                                lastSS.setSpan(endSpan, lastSS.length() - 4, lastSS.length(),
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                textView.setText(lastSS);
                            }
                        }
                        //很关键 取消监听 不然每次draw之前都会走一遍判断很浪费
                        textView.getViewTreeObserver().removeOnPreDrawListener(this);
                        //返回false代表取消这次draw 当然要返回true啦
                        return true;
                    }
                });
        //changeTitle(textView, 2, 2);
    }

    //不考虑半角的解决方案 比较简单
    //参考https://juejin.im/post/5b2c6be3e51d4558bd51849d写的
    private void changeTitle(TextView textView, int endSpanLength, int lineCount) {
        CharSequence originText = textView.getText();
        float originTextWidth = textView.getPaint().measureText(originText.toString());
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        float textViewWidth = point.x * lineCount;
        Log.d(TAG, "changeTitle: " + originTextWidth + " : " + textViewWidth);

        if (textViewWidth >= originTextWidth) {

        } else {
            int cutIndex = originText.length() - endSpanLength;
            CharSequence prefixText = originText.subSequence(0, cutIndex);
            CharSequence suffixText = "..." + originText.subSequence(cutIndex, originText.length());

            float prefixWidth = textView.getPaint().measureText(prefixText.toString());
            float suffixWidth = textView.getPaint().measureText(suffixText.toString());

            while (textViewWidth - prefixWidth < suffixWidth) {
                prefixText = prefixText.subSequence(0, prefixText.length() - 1);
                prefixWidth = textView.getPaint().measureText(prefixText.toString());
            }

            SpannableStringBuilder builder = new SpannableStringBuilder(prefixText);
            builder.append("...").append(originText, cutIndex, originText.length());
            textView.setText(builder);
        }
    }
}
