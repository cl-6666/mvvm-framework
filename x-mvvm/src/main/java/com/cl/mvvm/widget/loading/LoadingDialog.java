package com.cl.mvvm.widget.loading;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.LayoutRes;
import androidx.core.content.ContextCompat;

import com.cl.mvvm.R;
import com.cl.mvvm.utils.SizeUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @desc: 提供一个浮层展示在屏幕中间, 一般使用 {@link Builder} 或 {@link CustomBuilder} 生成。
 * <ul>
 * <li>{@link Builder} 提供了一个图标和一行文字的样式, 其中图标有几种类型可选, 见 {@link Builder.IconType}</li>
 * <li>{@link CustomBuilder} 支持传入自定义的 layoutResId, 达到自定义 LoadingDialog 的效果。</li>
 * </ul>
 * @date: 2020/4/14
 */

public class LoadingDialog extends Dialog {

    public LoadingDialog(Context context) {
        this(context, R.style.LoadingDialog);
    }

    public LoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDialogWidth();
    }

    private void initDialogWidth() {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams wmLp = window.getAttributes();
            wmLp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            window.setAttributes(wmLp);
        }
    }

    /**
     * 生成默认的 {@link LoadingDialog}
     * <p>
     * 提供了一个图标和一行文字的样式, 其中图标有几种类型可选。见 {@link IconType}
     * </p>
     *
     * @see CustomBuilder
     */
    public static class Builder {
        /**
         * 不显示任何icon
         */
        public static final int ICON_TYPE_NOTHING = 0;
        /**
         * 显示 Loading 图标
         */
        public static final int ICON_TYPE_LOADING = 1;
        /**
         * 显示成功图标
         */
        public static final int ICON_TYPE_SUCCESS = 2;
        /**
         * 显示失败图标
         */
        public static final int ICON_TYPE_FAIL = 3;
        /**
         * 显示信息图标
         */
        public static final int ICON_TYPE_INFO = 4;

        @IntDef({ICON_TYPE_NOTHING, ICON_TYPE_LOADING, ICON_TYPE_SUCCESS, ICON_TYPE_FAIL, ICON_TYPE_INFO})
        @Retention(RetentionPolicy.SOURCE)
        public @interface IconType {
        }

        private @IconType int mCurrentIconType = ICON_TYPE_NOTHING;

        private Context mContext;

        private CharSequence mTipWord;

        public Builder(Context context) {
            mContext = context;
        }

        /**
         * 设置 icon 显示的内容
         *
         * @see IconType
         */
        public Builder setIconType(@IconType int iconType) {
            mCurrentIconType = iconType;
            return this;
        }

        /**
         * 设置显示的文案
         */
        public Builder setTipWord(CharSequence tipWord) {
            mTipWord = tipWord;
            return this;
        }

        public LoadingDialog create(){
            return create(true);
        }

        /**
         * 创建 Dialog, 但没有弹出来, 如果要弹出来, 请调用返回值的 {@link Dialog#show()} 方法
         *
         * @param cancelable 按系统返回键是否可以取消
         * @return 创建的 Dialog
         */
        public LoadingDialog create(boolean cancelable) {
            LoadingDialog dialog = new LoadingDialog(mContext);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(cancelable);
            dialog.setContentView(R.layout.layout_dialog_loading);
            ViewGroup contentWrap = (ViewGroup) dialog.findViewById(R.id.contentWrap);

            if (mCurrentIconType == ICON_TYPE_LOADING) {
                LoadingView loadingView = new LoadingView(mContext);
                loadingView.setColor(Color.WHITE);
                loadingView.setSize(SizeUtils.dp2px(32));
                LinearLayout.LayoutParams loadingViewLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                loadingView.setLayoutParams(loadingViewLP);
                contentWrap.addView(loadingView);

            } else if (mCurrentIconType == ICON_TYPE_SUCCESS || mCurrentIconType == ICON_TYPE_FAIL || mCurrentIconType == ICON_TYPE_INFO) {
                ImageView imageView = new ImageView(mContext);
                LinearLayout.LayoutParams imageViewLP = new LinearLayout.LayoutParams(SizeUtils.dp2px(32), SizeUtils.dp2px(32));
                imageView.setLayoutParams(imageViewLP);

                if (mCurrentIconType == ICON_TYPE_SUCCESS) {
                    imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_notify_done));
                } else if (mCurrentIconType == ICON_TYPE_FAIL) {
                    imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_notify_error));
                } else {
                    imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_notify_info));
                }

                contentWrap.addView(imageView);

            }

            if (mTipWord != null && mTipWord.length() > 0) {
                TextView tipView = new TextView(mContext);
                LinearLayout.LayoutParams tipViewLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                if (mCurrentIconType != ICON_TYPE_NOTHING) {
                    tipViewLP.topMargin = SizeUtils.dp2px(12);
                }
                tipView.setLayoutParams(tipViewLP);

                tipView.setEllipsize(TextUtils.TruncateAt.END);
                tipView.setGravity(Gravity.CENTER);
                tipView.setMaxLines(2);
                tipView.setTextColor(ContextCompat.getColor(mContext, R.color.config_color_white));
                tipView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                tipView.setText(mTipWord);

                contentWrap.addView(tipView);
            }
            return dialog;
        }

    }

    /**
     * 传入自定义的布局并使用这个布局生成 LoadingDialog
     */
    public static class CustomBuilder {
        private Context mContext;
        private int mContentLayoutId;

        public CustomBuilder(Context context) {
            mContext = context;
        }

        public CustomBuilder setContent(@LayoutRes int layoutId) {
            mContentLayoutId = layoutId;
            return this;
        }

        /**
         * 创建 Dialog, 但没有弹出来, 如果要弹出来, 请调用返回值的 {@link Dialog#show()} 方法
         *
         * @return 创建的 Dialog
         */
        public LoadingDialog create() {
            LoadingDialog dialog = new LoadingDialog(mContext);
            dialog.setContentView(R.layout.layout_dialog_loading);
            dialog.setCanceledOnTouchOutside(false);
            ViewGroup contentWrap = (ViewGroup) dialog.findViewById(R.id.contentWrap);
            LayoutInflater.from(mContext).inflate(mContentLayoutId, contentWrap, true);
            return dialog;
        }
    }
}