package com.example.barte.projektpr;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

public class TessOCR {
    private final TessBaseAPI mTess;
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString();

    public TessOCR(Context context, String language) {
        mTess = new TessBaseAPI();
        Log.v("projektPR", "sciezka: " + DATA_PATH);
        String datapath = DATA_PATH + "/tesseract/";
        mTess.init(datapath, language);
        mTess.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST,"0123456789x+-/");
    }

    public String getOCRResult(Bitmap bitmap) {
        mTess.setImage(bitmap);
        return mTess.getUTF8Text();
    }

    public void onDestroy() {
        if (mTess != null) mTess.end();
    }
}
