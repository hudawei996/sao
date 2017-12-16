package com.wanjian.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;


/**
 * Created by wanjian on 2017/12/16.
 */

public class CoolView extends FrameLayout {
    public CoolView(@NonNull Context context) {
        this(context, null);
    }

    public CoolView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setWillNotDraw(false);
        if (attrs == null) {
            return;
        }

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CoolView);
        borderWidth = typedArray.getDimension(R.styleable.CoolView_border_width, borderWidth);
        speed = typedArray.getFloat(R.styleable.CoolView_speed, speed);
        radius = typedArray.getDimension(R.styleable.CoolView_radius, radius);
        colors = getColorsFromColorStateList(typedArray.getColorStateList(R.styleable.CoolView_colors));
        typedArray.recycle();

    }

    private int[] getColorsFromColorStateList(ColorStateList colorStateList) {
        if (colorStateList == null) {
            return colors;
        }
        try {
            return (int[]) ColorStateList.class.getMethod("getColors").invoke(colorStateList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return colors;
    }

    private Shader shader;
    private Matrix matrix = new Matrix();

    private float degree;
    private int[] colors = new int[]{0xFFFF0000, 0xff00ff00, 0xff0000ff, 0xffffff00, 0xff00ffff, 0xFFFF0000};

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float speed = 1;
    private float borderWidth = 20;
    private float radius = 30;

    private Path path = new Path();
    private RectF rectF = new RectF();

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        shader = new SweepGradient(w / 2, h / 2, colors, null);
        paint.setShader(shader);
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setColors(int[] colors) {
        if (colors == null) {
            throw new IllegalArgumentException("colors can not be null !");
        }
        this.colors = colors;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.clipPath(outRoundPath(getWidth(), getHeight()));
        super.draw(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        int w = getWidth();
        int h = getHeight();

        degree += speed;
        degree %= 360;
        matrix.setRotate(degree, w / 2, h / 2);
        shader.setLocalMatrix(matrix);
        canvas.clipPath(innerRoundPath(w, h), Region.Op.DIFFERENCE);
        rectF.left = 0;
        rectF.top = 0;
        rectF.right = w;
        rectF.bottom = h;
        canvas.drawRoundRect(rectF, radius, radius, paint);
        invalidate();
    }

    private Path outRoundPath(int w, int h) {
        path.reset();
        rectF.left = 0;
        rectF.top = 0;
        rectF.right = w;
        rectF.bottom = h;
        path.addRoundRect(rectF, radius, radius, Path.Direction.CCW);
        return path;
    }

    private Path innerRoundPath(int w, int h) {
        path.reset();
        if (borderWidth > w / 2 || borderWidth > h / 2) {
            return path;
        }
        rectF.left = borderWidth;
        rectF.top = borderWidth;
        rectF.right = w - borderWidth;
        rectF.bottom = h - borderWidth;
        path.addRoundRect(rectF, radius, radius, Path.Direction.CCW);
        return path;
    }


}
