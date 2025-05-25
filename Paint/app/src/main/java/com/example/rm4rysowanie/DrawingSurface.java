package com.example.rm4rysowanie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Objects;

public class DrawingSurface extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = DrawingSurface.class.getSimpleName();

    // pozwala kontrolować i monitorować powierzchnię
    private SurfaceHolder mHolder;
    public Paint mFarba;
    private Path path;
    private ArrayList<Path> paths;
    private ArrayList<Point> starts;
    private ArrayList<Point> ends;
    private ArrayList<Paint> paints;


    public DrawingSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder = getHolder();
        mHolder.addCallback(this);
        setWillNotDraw(false);

        mFarba = new Paint();
        mFarba.setColor(Color.BLUE);
        mFarba.setStrokeWidth(2);
        mFarba.setStyle(Paint.Style.FILL);
        mFarba.setStyle(Paint.Style.STROKE);

        path = new Path();

        paths = new ArrayList<>();
        starts = new ArrayList<>();
        ends = new ArrayList<>();
        paints = new ArrayList<>();

        System.out.println("konstruktor");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(event.getX(), event.getY());
                starts.add(new Point((int) event.getX(), (int) event.getY()));
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                paths.add(new Path(path));
                paints.add(new Paint(mFarba));
                ends.add(new Point((int) event.getX(), (int) event.getY()));
                path.reset();
                break;
        }
        for (int i = 0; i < paths.size(); i++) {
            Paint paint = paints.get(i);
            Point start = starts.get(i);
            Point end = ends.get(i);
            mBitmapCanvas.drawCircle(start.x, start.y, 4, paint);
            mBitmapCanvas.drawCircle(end.x, end.y, 4, paint);
            mBitmapCanvas.drawPath(paths.get(i), paint);
        }
        mBitmapCanvas.drawPath(path, mFarba);
        if (starts.size() > paths.size()) {
            Point p = starts.get(starts.size() - 1);
            mBitmapCanvas.drawCircle(p.x, p.y, 4, mFarba);
        }
        invalidate();
        return true;
    }
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    public boolean performClick() {
        return super.performClick();
    }

    //robocza bitmapa na której tworzymy rysunek gdy użytkownik dotknie ekranu
    private Bitmap mBitmap = null;
    private Canvas mBitmapCanvas = null;

    //implementacja interfejsu SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);
        mBitmapCanvas = new Canvas(mBitmap);
        mBitmapCanvas.drawARGB(0, 255, 255, 255);
        invalidate();
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        mBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);
        mBitmapCanvas = new Canvas(mBitmap);
        mBitmapCanvas.drawARGB(0, 255, 255, 255);
        invalidate();
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }
    public void clearScreen() {
        paths.clear();
        paints.clear();
        starts.clear();
        ends.clear();
        invalidate();
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

}