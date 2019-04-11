/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.annotation.SuppressLint
 *  android.graphics.Bitmap
 *  android.graphics.Bitmap$CompressFormat
 *  android.graphics.Bitmap$Config
 *  android.graphics.BitmapFactory
 *  android.graphics.Canvas
 *  android.graphics.Paint
 *  android.graphics.Paint$Style
 */
package com.johnyjuice.juicyeet.tiskarna;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

@SuppressLint(value={"SdCardPath"})
public class PrintPic {
    public Canvas canvas = null;
    public Paint paint = null;
    public Bitmap bm = null;
    public int width;
    public float length = 0.0f;
    public byte[] bitbuf = null;

    public int getLength() {
        return (int)this.length + 20;
    }

    public void initCanvas(int w) {
        int h = 10 * w;
        this.bm = Bitmap.createBitmap((int)w, (int)h, (Bitmap.Config)Bitmap.Config.ARGB_4444);
        this.canvas = new Canvas(this.bm);
        this.canvas.drawColor(-1);
        this.width = w;
        this.bitbuf = new byte[this.width / 8];
    }

    public void initPaint() {
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setColor(-16777216);
        this.paint.setStyle(Paint.Style.STROKE);
    }

    public void drawImage(float x, float y, String path) {
        try {
            Bitmap btm = BitmapFactory.decodeFile((String)path);
            this.canvas.drawBitmap(btm, x, y, null);
            if (this.length < y + (float)btm.getHeight()) {
                this.length = y + (float)btm.getHeight();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printPng() {
        File f = new File("/mnt/sdcard/0.png");
        FileOutputStream fos = null;
        Bitmap nbm = Bitmap.createBitmap((Bitmap)this.bm, (int)0, (int)0, (int)this.width, (int)this.getLength());
        try {
            fos = new FileOutputStream(f);
            nbm.compress(Bitmap.CompressFormat.PNG, 50, (OutputStream)fos);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public byte[] printDraw() {
        Bitmap nbm = Bitmap.createBitmap((Bitmap)this.bm, (int)0, (int)0, (int)this.width, (int)this.getLength());
        byte[] imgbuf = new byte[this.width / 8 * this.getLength() + 8];
        int s = 0;
        imgbuf[0] = 29;
        imgbuf[1] = 118;
        imgbuf[2] = 48;
        imgbuf[3] = 0;
        imgbuf[4] = (byte)(this.width / 8);
        imgbuf[5] = 0;
        imgbuf[6] = (byte)(this.getLength() % 256);
        imgbuf[7] = (byte)(this.getLength() / 256);
        s = 7;
        int i = 0;
        while (i < this.getLength()) {
            int k = 0;
            while (k < this.width / 8) {
                int c0 = nbm.getPixel(k * 8 + 0, i);
                int p0 = c0 == -1 ? 0 : 1;
                int c1 = nbm.getPixel(k * 8 + 1, i);
                int p1 = c1 == -1 ? 0 : 1;
                int c2 = nbm.getPixel(k * 8 + 2, i);
                int p2 = c2 == -1 ? 0 : 1;
                int c3 = nbm.getPixel(k * 8 + 3, i);
                int p3 = c3 == -1 ? 0 : 1;
                int c4 = nbm.getPixel(k * 8 + 4, i);
                int p4 = c4 == -1 ? 0 : 1;
                int c5 = nbm.getPixel(k * 8 + 5, i);
                int p5 = c5 == -1 ? 0 : 1;
                int c6 = nbm.getPixel(k * 8 + 6, i);
                int p6 = c6 == -1 ? 0 : 1;
                int c7 = nbm.getPixel(k * 8 + 7, i);
                int p7 = c7 == -1 ? 0 : 1;
                int value = p0 * 128 + p1 * 64 + p2 * 32 + p3 * 16 + p4 * 8 + p5 * 4 + p6 * 2 + p7;
                this.bitbuf[k] = (byte)value;
                ++k;
            }
            int t = 0;
            while (t < this.width / 8) {
                imgbuf[++s] = this.bitbuf[t];
                ++t;
            }
            ++i;
        }
        return imgbuf;
    }
}

