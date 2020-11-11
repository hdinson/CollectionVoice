package com.zjta.collectionvoice.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import com.zjta.collectionvoice.R;


/**
 * Created by Shine on 09/02/16.
 */
public class PasswordEditText extends AppCompatEditText {

    private Drawable mHideDrawable;
    private Drawable mShowDrawable;
    private boolean mPasswordVisible = false;
    private boolean touchDown;


    public PasswordEditText(Context context) {
        super(context);
    }

    public PasswordEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    public PasswordEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.password_edit_text, 0, 0);

        int hideDrawableResId = R.mipmap.in_anshul_hide_password;
        int showDrawableResId = R.mipmap.in_anshul_show_password;

        try {
            mPasswordVisible = a.getBoolean(R.styleable.password_edit_text_petPwdVisible, false);
            hideDrawableResId = a.getResourceId(R.styleable.password_edit_text_petHideDrawable, hideDrawableResId);
            showDrawableResId = a.getResourceId(R.styleable.password_edit_text_petShowDrawable, showDrawableResId);
        } finally {
            a.recycle();
            mHideDrawable = ContextCompat.getDrawable(getContext(), hideDrawableResId);
            mShowDrawable = ContextCompat.getDrawable(getContext(), showDrawableResId);
        }

        mHideDrawable.setBounds(0, 0, mHideDrawable.getIntrinsicWidth(), mHideDrawable.getIntrinsicHeight());
        mShowDrawable.setBounds(0, 0, mShowDrawable.getIntrinsicWidth(), mShowDrawable.getIntrinsicHeight());
        if (mPasswordVisible) {
            showPassword();
        } else {
            hidePassword();
        }
    }


    private void togglePasswordView() {
        if (mPasswordVisible) {
            hidePassword();
        } else {
            showPassword();
        }
    }

    private void showPassword() {
        setCompoundDrawables(null, null, mHideDrawable, null);
        setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        int current = getText() == null ? 0 : getText().length();
        setSelection(current);
        mPasswordVisible = true;
    }

    private void hidePassword() {
        setCompoundDrawables(null, null, mShowDrawable, null);
        setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        int current = getText() == null ? 0 : getText().length();
        setSelection(current);
        mPasswordVisible = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int right = getRight();
        final int drawableSize = getCompoundPaddingRight();
        final int x = (int) event.getX()+getLeft();
        int EXTRA_TOUCH_AREA = 50;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (x + EXTRA_TOUCH_AREA >= right - drawableSize && x <= right + EXTRA_TOUCH_AREA) {
                    touchDown = true;
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (x + EXTRA_TOUCH_AREA >= right - drawableSize && x <= right + EXTRA_TOUCH_AREA && touchDown) {
                    touchDown = false;
                    togglePasswordView();
                    return true;
                }
                touchDown = false;
                break;

        }
        return super.onTouchEvent(event);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable saveState = super.onSaveInstanceState();
        return new SavedState(saveState, mPasswordVisible);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {

        SavedState savedState = (SavedState) state;

        mPasswordVisible = savedState.isPasswordVisible();
        if (mPasswordVisible) {
            showPassword();
        } else {
            hidePassword();
        }
        super.onRestoreInstanceState(savedState.getSuperState());
    }

    private static class SavedState extends BaseSavedState {

        private boolean isPasswordVisible = false;

        public SavedState(Parcel source) {
            super(source);
            isPasswordVisible = source.readByte() != 0;
        }

        public SavedState(Parcelable superState, boolean passwordVisible) {
            super(superState);
            isPasswordVisible = passwordVisible;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeByte((byte) (isPasswordVisible == true ? 1 : 0));
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }

        };

        public boolean isPasswordVisible() {
            return isPasswordVisible;
        }
    }
}