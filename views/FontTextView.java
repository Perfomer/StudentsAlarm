package views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

public class FontTextView extends android.support.v7.widget.AppCompatTextView {

    public FontTextView(Context context) {
        super(context);
        init(context, null);
    }

    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //mFontManager = new FontManager(context);

        if (attrs != null) {
            //TypedArray a = context.getTheme().obtainStyledAttributes(attrs,R.styleable.FontView, 0, 0);
            try {
            //    mFontId = a.getInteger(R.styleable.FontView_font, FONT_LIGHT);
              //  mShadowId = a.getInteger(R.styleable.FontView_shadow, SHADOW_LIGHT);
            } finally {
//                a.recycle();
            }
        }
        this.setPadding(getPaddingLeft() + 2, getPaddingTop() + 2, getPaddingRight() + 2, getPaddingBottom() + 2);
        //this.setFont(mFontId);
        //this.setShadowType(mShadowId);
    }
}
