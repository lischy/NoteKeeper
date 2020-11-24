package com.example.gads2020;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class ModuleStatusView extends View {
    public static final int EDIT_MODE_MODULE_COUNT = 7;
    public static final int INVALID_INDEX = -1;
    public static final int SHAPE_CIRCLE = 0;
    public static final float DEFAULT_OUTLINE_WIDHT_DP = 2f;
    private String mExampleString; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;
    private float outlineWidth;
    private float mShapeSize;
    private float mSpacing;
    private Rect[] mModuleRectangle;
    private Paint mPaintOutline;
    private int outlineColor;
    private int fillColor;
    private Paint paintFill;
    private float radius;
    private int maxHorizontalModules;
    private int shape;

    public boolean[] getmModuleStatus() {
        return mModuleStatus;
    }

    public void setmModuleStatus(boolean[] mModuleStatus) {
        this.mModuleStatus = mModuleStatus;
    }

    private boolean[] mModuleStatus;

    public ModuleStatusView(Context context) {
        super(context);
        init(null, 0);
    }

    public ModuleStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ModuleStatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        if (isInEditMode()) setUpEditNoteValues();

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        float displayDensity = dm.density;
        float defaultOtlineWidthPixels = displayDensity * DEFAULT_OUTLINE_WIDHT_DP;
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ModuleStatusView, defStyle, 0);
//
//        mExampleString = a.getString(
//                R.styleable.ModuleStatusView_exampleString);
//        mExampleColor = a.getColor(
//                R.styleable.ModuleStatusView_exampleColor,
//                mExampleColor);
//        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
//        // values that should fall on pixel boundaries.
//        mExampleDimension = a.getDimension(
//                R.styleable.ModuleStatusView_exampleDimension,
//                mExampleDimension);
//
//        if (a.hasValue(R.styleable.ModuleStatusView_exampleDrawable)) {
//            mExampleDrawable = a.getDrawable(
//                    R.styleable.ModuleStatusView_exampleDrawable);
//            mExampleDrawable.setCallback(this);
//        }

        outlineColor = a.getColor(R.styleable.ModuleStatusView_outlineColor,Color.BLACK);
        shape = a.getInt(R.styleable.ModuleStatusView_shape, SHAPE_CIRCLE);
        outlineWidth = a.getDimension( R.styleable.ModuleStatusView_outlineWidth,defaultOtlineWidthPixels);
        a.recycle();

//        // Set up a default TextPaint object
//        mTextPaint = new TextPaint();
//        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
//        mTextPaint.setTextAlign(Paint.Align.LEFT);


        mShapeSize = 144f;
        mSpacing = 30f;
        radius = (mShapeSize - outlineWidth) /2;


        mPaintOutline = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintOutline.setStyle(Paint.Style.STROKE);
        mPaintOutline.setStrokeWidth(outlineWidth);
        mPaintOutline.setColor(outlineColor);

        fillColor = Color.YELLOW;
        paintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintFill.setStyle(Paint.Style.FILL);
        paintFill.setColor(fillColor);


        // Update TextPaint and text measurements from attributes
//        invalidateTextPaintAndMeasurements();
    }

    private void setUpEditNoteValues() {
        boolean[] exampleModuleValues = new boolean[EDIT_MODE_MODULE_COUNT];
        int middle = EDIT_MODE_MODULE_COUNT/2;
        for (int i =0 ; i < middle; i++)
            exampleModuleValues[i] = true;
        setmModuleStatus(exampleModuleValues);
    }

    private void setupModuleRectangle(int width) {
      int availableWidth = width - getPaddingRight() - getPaddingLeft();
      int maxModulesThatCanFit = (int)(availableWidth /(mShapeSize + mSpacing));
      int localMaxHorizontalModules = Math.min(maxModulesThatCanFit,mModuleStatus.length);
        mModuleRectangle = new Rect[mModuleStatus.length];
        for(int i = 0 ; i< mModuleStatus.length; i++){
            int row = i / localMaxHorizontalModules;
            int column = i % localMaxHorizontalModules;
            int x = getPaddingLeft () + (int) (column*(mShapeSize + mSpacing));
            int y = getPaddingTop() + (int) (row * ( mShapeSize + mSpacing));
            mModuleRectangle[i] = new Rect(x,y,x+(int)mShapeSize,y+(int)mShapeSize);
        }
    }
//
//    private void invalidateTextPaintAndMeasurements() {
//        mTextPaint.setTextSize(mExampleDimension);
//        mTextPaint.setColor(mExampleColor);
//        mTextWidth = mTextPaint.measureText(mExampleString);
//
//        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
//        mTextHeight = fontMetrics.bottom;
//    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
            return true;
            case MotionEvent.ACTION_UP:
                int moduleIndex = findItemAtPoint (event.getX(),event.getY());
                onModuleSelected(moduleIndex);
                return  true;
        }
        return super.onTouchEvent(event);
    }

    private void onModuleSelected(int moduleIndex) {
        if(moduleIndex == INVALID_INDEX) return;
        mModuleStatus[moduleIndex] = ! mModuleStatus[moduleIndex];
        invalidate();
    }

    private int findItemAtPoint(float x, float y) {
        int moduleIndex = INVALID_INDEX;
        for(int i = 0 ; i < mModuleRectangle.length ; i++){
            if(mModuleRectangle[i].contains((int)x , (int)y)){
                moduleIndex = i;
                break;
            }
        }

        return moduleIndex;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = 0;
        int desiredHeight = 0;

        int specWidth = MeasureSpec.getSize(widthMeasureSpec);
        int availableWidth = specWidth - getPaddingLeft() -getPaddingRight();
        int horizontalModuleThatCanFit = (int) (availableWidth / (mShapeSize+mSpacing));
        maxHorizontalModules = Math.min(horizontalModuleThatCanFit,mModuleStatus.length);

        desiredWidth  = (int) ((maxHorizontalModules* (mShapeSize + mSpacing)) - mSpacing);
        desiredWidth += getPaddingLeft() + getPaddingRight();
        int rows = ((mModuleStatus.length -1 ) / maxHorizontalModules ) +1;
        desiredHeight = (int) ((rows * (mShapeSize + mSpacing)) - mSpacing);
        desiredHeight += getPaddingBottom() + getPaddingTop();

        int width = resolveSizeAndState(desiredWidth,widthMeasureSpec,0);
        int height = resolveSizeAndState(desiredHeight,heightMeasureSpec,0);

        setMeasuredDimension(width,height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        setupModuleRectangle(w);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for ( int moduleIndex = 0 ; moduleIndex < mModuleRectangle.length ; moduleIndex++){
            if(shape == SHAPE_CIRCLE) {


                float x = mModuleRectangle[moduleIndex].centerX();
                float y = mModuleRectangle[moduleIndex].centerY();

                if (mModuleStatus[moduleIndex])
                    canvas.drawCircle(x, y, radius, paintFill);
                canvas.drawCircle(x, y, radius, mPaintOutline);

            } else {
                drawSquare(canvas, moduleIndex);
            }


        }

//        // TODO: consider storing these as member variables to reduce
//        // allocations per draw cycle.
//        int paddingLeft = getPaddingLeft();
//        int paddingTop = getPaddingTop();
//        int paddingRight = getPaddingRight();
//        int paddingBottom = getPaddingBottom();
//
//        int contentWidth = getWidth() - paddingLeft - paddingRight;
//        int contentHeight = getHeight() - paddingTop - paddingBottom;
//
//        // Draw the text.
//        canvas.drawText(mExampleString,
//                paddingLeft + (contentWidth - mTextWidth) / 2,
//                paddingTop + (contentHeight + mTextHeight) / 2,
//                mTextPaint);
//
//        // Draw the example drawable on top of the text.
//        if (mExampleDrawable != null) {
//            mExampleDrawable.setBounds(paddingLeft, paddingTop,
//                    paddingLeft + contentWidth, paddingTop + contentHeight);
//            mExampleDrawable.draw(canvas);
//        }
    }
    private void drawSquare(Canvas canvas, int moduleIndex) {
        Rect moduleRectangle = mModuleRectangle[moduleIndex];

        if(mModuleStatus[moduleIndex])
            canvas.drawRect(moduleRectangle, paintFill);

        canvas.drawRect(moduleRectangle.left + (outlineWidth/2),
                moduleRectangle.top + (outlineWidth/2),
                moduleRectangle.right - (outlineWidth/2),
                moduleRectangle.bottom - (outlineWidth/2),
                mPaintOutline);
    }
    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
    public String getExampleString() {
        return mExampleString;
    }

    /**
     * Sets the view's example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param exampleString The example string attribute value to use.
     */
    public void setExampleString(String exampleString) {
        mExampleString = exampleString;
//        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getExampleColor() {
        return mExampleColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param exampleColor The example color attribute value to use.
     */
    public void setExampleColor(int exampleColor) {
        mExampleColor = exampleColor;
//        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getExampleDimension() {
        return mExampleDimension;
    }

    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     */
    public void setExampleDimension(float exampleDimension) {
        mExampleDimension = exampleDimension;
//        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
    }
}
