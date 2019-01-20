package com.example.barte.projektpr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class KameraActivity extends AppCompatActivity{

    private static final String TAG = "KameraActivity";
    private static final String TESSERACT = "TESSERACT";
    private static final String PERCEPTRON = "PERCEPTRON WIELOWARSTWOWY";

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private int boundingRectangleThicnkess = 5;

    private int coordinatesCounter = 0;
    private int maxCoordinates = 4;
    private Point[] coordinates = new Point[4];
    private Mat gray = new Mat();
    private Mat imgTmp;
    private String znak ="";
    private TextView[] textViewArray;
    private Bitmap myBitmap, myBitmapForPopWindow;
    private ImageOperations imageOperations = new ImageOperations();
    private String wynik;
    private boolean clicked = false;
    private boolean brak = false;

    List<Rect> bounding_rect = new ArrayList<Rect>();
    List<Bitmap> bitmaps = new ArrayList<Bitmap>();
    List<String> results = new ArrayList<String>();
    List<Integer> xValues = new ArrayList<Integer>();
    List<Integer> yValues = new ArrayList<Integer>();
    Rect rect;
    String mCurrentPhotoPath, classifier;
    Mat img;
    ImageView showPhoto;
    ImageButton bMenu;
    Button bKlasyfikacja, bOblicz, bOdswiez;
    RelativeLayout layout;

    ProgressDialog mProgressDialog;
    TessOCR mTessOCR = new TessOCR(this,"pol");

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    //gray = new Mat();
                    //img = new Mat();
                  // imgTmp = new Mat(0,0, CvType.CV_8UC1);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_kamera);

        Intent intent = getIntent();
        classifier = intent.getStringExtra("classifier");

        showPhoto = (ImageView) findViewById(R.id.mat);
        layout = (RelativeLayout) findViewById(R.id.layout);
        bMenu = (ImageButton) findViewById(R.id.bMenu);
        dispatchTakePictureIntent();

        bMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicked == false) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(KameraActivity.this);

                    View mView = getLayoutInflater().inflate(R.layout.dialog_options, null);
                    bKlasyfikacja = (Button) mView.findViewById(R.id.bKlasyfikacja);
                    bOdswiez = (Button) mView.findViewById(R.id.bOdswiez);

                    mBuilder.setView(mView);
                    final AlertDialog dialog = mBuilder.create();

                    bKlasyfikacja.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            bitmaps = getAllSymbols(); /*wszystkie znalezione symbole jako osobne bitmapy*/
                            DisplayMetrics metrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(metrics);
                            int h = metrics.heightPixels;
                            if (classifier.equals(TESSERACT)) {
                                classifyBitmaps(bitmaps);
                                for (int i = 0; i < bounding_rect.size(); i++) {
                                    //if(i==0)
                                        showClassifiedSymbols(results.get(i), xValues.get(i), yValues.get(i), i);
                                    //else
                                    //    showClassifiedSymbols(results.get(i), xValues.get(i)-xValues.get(i-1), yValues.get(i), i);*/
                                    //showClassifiedSymbols(results.get(i), i*35, h-75, i);
                                    Log.v("projekt", "y: " + yValues.get(i));
                                    Log.v("projekt", "x: " + xValues.get(i));
                                }
                                mTessOCR.onDestroy();
                            } else if (classifier.equals(PERCEPTRON)) {
                                classifyBitmapsWithCustomClassifier(bitmaps);
                                for (int i = 0; i < bounding_rect.size(); i++) {
                                    showClassifiedSymbols(results.get(i), xValues.get(i), yValues.get(i), i);
                                }
                            }
                            //wynik = arrayListToString(results);
                            Log.v("projekt", "wynik: " + wynik);
                            dialog.dismiss();
                            clicked = true;

                        }
                    });

                    bOdswiez.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    });

                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                }

                else{
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(KameraActivity.this);

                    View mView = getLayoutInflater().inflate(R.layout.dialog_options1, null);
                    bOblicz = (Button) mView.findViewById(R.id.bOblicz);

                    mBuilder.setView(mView);
                    final AlertDialog dialog = mBuilder.create();

                    bOblicz.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int i=0;i<results.size(); i++){
                                if (results.get(i).equals("brak"))
                                    brak = true;
                            }

                            if (clicked == true) {
                                //if(brak == false) {
                                wynik = getEqFromTVs(textViewArray);
                                Log.v("projekt", "wynik: " + wynik);

                                Intent intent = new Intent(KameraActivity.this, ResultActivity.class);
                                intent.putExtra("wynik", wynik);
                                startActivity(intent);
                               /* }
                                else {
                                    Toast toast = Toast.makeText(KameraActivity.this, "Nie wszystkie elementy zostały sklasyfikowane", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    brak = false;
                                }*/
                            }
                        }
                    });
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                }
            }
        });

        showPhoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Bitmap croppedSymbol;
                    Log.v("projekt", "x clicked: " + event.getX());
                    Log.v("projekt", "y clicked: " + event.getY());
                    for(int i =0; i< bounding_rect.size();i++) {
                        Log.v("projekt", "bounding size: " + bounding_rect.get(i).size());
                        if ( bounding_rect.get(i).x < event.getX() && event.getX() < (bounding_rect.get(i).x + bounding_rect.get(i).width) && bounding_rect.get(i).y < event.getY() && event.getY() < (bounding_rect.get(i).y + bounding_rect.get(i).height)) {
                       //if ( xValues.get(i) < event.getX() && event.getX() < (xValues.get(i) + bounding_rect.get(i).width) && yValues.get(i) < event.getY() && event.getY() < (yValues.get(i) + bounding_rect.get(i).height)) {
                            /*float recX = bounding_rect.get(i).x;
                            float recY = bounding_rect.get(i).y;*/

                            float recX = xValues.get(i);
                            float recY = yValues.get(i);
                            float recWidth = bounding_rect.get(i).width;
                            float recHeight = bounding_rect.get(i).height;

                            Log.v("projekt", "recX: " + recX);
                            Log.v("projekt", "recY: " + recY);
                            Log.v("projekt", "recW: " + recWidth);
                            Log.v("projekt", "recH: " + recHeight);

                            if(recX>31)
                                recX-=30;
                            if(recY>31)
                                recY-=30;
                            if(recX+recWidth<showPhoto.getWidth()-51)
                                recWidth+=51;
                            if(recY+recHeight<showPhoto.getHeight()-51)
                                recHeight+=51;

                            croppedSymbol = Bitmap.createBitmap(myBitmapForPopWindow,(int) recX, (int) recY, (int) recWidth , (int) recHeight);
                            croppedSymbol = imageOperations.scaleDownBitmap(croppedSymbol, 50, KameraActivity.this);

                            Intent intent = new Intent(KameraActivity.this, Pop.class);
                            intent.putExtra("croppedSymbol", croppedSymbol);
                            intent.putExtra("x", bounding_rect.get(i).x);
                            intent.putExtra("y", bounding_rect.get(i).y);
                            intent.putExtra("rectangleNumber", i);

                            startActivityForResult(intent, 0);
                        }
                    }
                }
                return true;
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File imgFile = new File(mCurrentPhotoPath);

            if (imgFile.exists()) {
                myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                myBitmap = getResizedBitmap(myBitmap, 2000, 1200);
                img = new Mat(myBitmap.getWidth(), myBitmap.getHeight(), CvType.CV_8UC1);
                Log.v("projekt", "size: " + img.size());
                Size size = new Size(0, 0);
                size.width = myBitmap.getWidth();
                size.height = myBitmap.getHeight();
                //Imgproc.resize(imgTmp, img, size, 0, 0, Imgproc.INTER_CUBIC);
                myBitmapForPopWindow = myBitmap.copy(myBitmap.getConfig(),true);

                Mat hierarchy = new Mat();
                Utils.bitmapToMat(myBitmap, img);
                Utils.matToBitmap(img,myBitmapForPopWindow);

                Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
                // Imgproc.threshold(gray, gray, 25, 255, Imgproc.THRESH_BINARY_INV);
                Imgproc.adaptiveThreshold(gray, gray, 225, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 55, 15);
                List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
                gray = imageOperations.morphology(gray);
                Imgproc.findContours(gray, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

                Log.v("projekt", "rozmiar gray: " + gray.size());
                for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {

                    bounding_rect.add(Imgproc.boundingRect(contours.get(contourIdx)));

                }

                bounding_rect = imageOperations.removeOverlapedRectangles(bounding_rect);
                bounding_rect = imageOperations.removeToSmallRectangles(bounding_rect);

                for (int i=0;i<bounding_rect.size();i++){
                    Imgproc.rectangle(img, new Point(bounding_rect.get(i).x, bounding_rect.get(i).y), new Point(bounding_rect.get(i).x + bounding_rect.get(i).width, bounding_rect.get(i).y + bounding_rect.get(i).height), new Scalar(255, 0, 0), boundingRectangleThicnkess);
                    xValues.add(bounding_rect.get(i).x);
                    yValues.add(bounding_rect.get(i).y);
                }
                hierarchy.release();
                // Log.v("projekt", "Display width in px is " + img.width());
                Utils.matToBitmap(img, myBitmap);
                //Log.v("projekt", "Display bitmap width in px is " + myBitmap.getWidth());
                //myBitmap = getResizedBitmap(myBitmap, 2560, 1430);
                Log.v("projekt", "bitmap size: " + myBitmap.getWidth() + "x" + myBitmap.getHeight());
                showPhoto.setImageBitmap(myBitmap);
                showPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
                textViewArray = new TextView[contours.size()];
                for(int i=0;i<textViewArray.length;i++){
                    textViewArray[i] = new TextView(KameraActivity.this);
                }
                //showPhoto.setAdjustViewBounds(true);
            }
        }

        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                if(data.getBooleanExtra("flag",true)==false) {
                    int rectX, rectY, recNum;
                    znak = data.getStringExtra("znak");
                    rectX = data.getIntExtra("x", -1);
                    rectY = data.getIntExtra("y", -1);
                    recNum = data.getIntExtra("rectangleNumber", -1);
                    showSymbol(rectX, rectY, recNum);
                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    int h = metrics.heightPixels;
                    //showSymbol(recNum*35, h-75, recNum);
                }

                if(data.getBooleanExtra("flag",true)==true){
                    bounding_rect = deleteRectangle(data.getIntExtra("rectangleNumberToDel",-1),bounding_rect);
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public void showSymbol(int x, int y, int num){
        Log.v("projekt", "num: " + num);

        if(textViewArray[num] !=null || !textViewArray[num].getText().equals("") ){
            layout.removeView(textViewArray[num]);
        }

        //textViewArray[num] = new TextView(KameraActivity.this);
        //textViewArray[num].setId(num);
        textViewArray[num].setText(znak);
        textViewArray[num].setTextSize(25);
        textViewArray[num].setTextColor(Color.GREEN);
        textViewArray[num].setPadding(x, y /*+ (int) textViewArray[num].getTextSize()*/ - boundingRectangleThicnkess, 0, 0);
        layout.addView(textViewArray[num]);
    }

    private void showClassifiedSymbols( String symbol, int x, int y, int i){
        if(textViewArray[i] !=null || !textViewArray[i].getText().equals("") ){
            layout.removeView(textViewArray[i]);
        }

        // textViewArray[i].setId(i);
        textViewArray[i].setText(symbol);
        textViewArray[i].setTextSize(25);
        textViewArray[i].setTextColor(Color.GREEN);
        textViewArray[i].setPadding(x, y /*+ (int) textViewArray[num].getTextSize()*/ - boundingRectangleThicnkess, 0, 0);
        layout.addView(textViewArray[i]);
    }

    public List<Rect> deleteRectangle(int num, List<Rect> arrayList){
        arrayList.remove(num);
        return arrayList;
    }

    public List<Bitmap> getAllSymbols(){
        Bitmap croppedSymbol;
        List<Bitmap> bitmapy = new ArrayList<>();

        bounding_rect = imageOperations.orderRectangles(bounding_rect);
        xValues = imageOperations.orderValues(xValues,yValues);

        for(int i =0; i< bounding_rect.size();i++) {
            float recX = bounding_rect.get(i).x;
            float recY = bounding_rect.get(i).y;
            float recWidth = bounding_rect.get(i).width;
            float recHeight = bounding_rect.get(i).height;

            if (recX > 31)
                recX -= 30;
            if (recY > 31)
                recY -= 30;
            if (recX + recWidth < showPhoto.getWidth() - 51)
                recWidth += 50;
            if (recY + recHeight < showPhoto.getHeight() - 51)
                recHeight += 50;


            Bitmap bitmapTmp = myBitmapForPopWindow.copy(myBitmapForPopWindow.getConfig(),true);

            croppedSymbol = Bitmap.createBitmap(bitmapTmp, (int) recX, (int) recY, (int) recWidth, (int) recHeight);
            croppedSymbol = imageOperations.scaleDownBitmap(croppedSymbol, 30, KameraActivity.this);
            bitmapTmp.recycle();
            bitmapy.add(croppedSymbol);

        }
        return bitmapy;
    }

    public void classifyBitmaps(List<Bitmap> list){
        results.clear();
        for(int i=0;i<list.size();i++){
            doOCR(list.get(i));
        }
    }

    public void classifyBitmapsWithCustomClassifier(List<Bitmap> list){
        results.clear();
        for(int i=0;i<list.size();i++){

            //*wywolanie klasyfikatora + obliczen
            Mat img = new Mat();
            Utils.bitmapToMat(list.get(i), img);
            img.convertTo(img,CvType.CV_8UC3,1);

            Uri path = Uri.parse("android.resource://com.example.barte.projektpr/xml/hog.xml");
            Uri path2 = Uri.parse("android.resource://com.example.barte.projektpr/xml/hog.xml");
            String xml = new String();
            Log.d("sciezka ",xml);
            xml = path + "";
            Log.d(" ",xml);

            int a = Klasa.klasyfikator(xml,img.getNativeObjAddr());
            Log.d("Klasa: ", "= "+ a);
            //BigDecimal wynik = Rownanie.rown("1-2*2/3+3*2");
            // Log.d("Wynik: ", " "+ wynik);

            results.add(assignSymbol(a));
        }
    }

    private void doOCR (final Bitmap bitmap) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(KameraActivity.this, "Processing",
                    "Doing OCR...", true);
        } else {
            mProgressDialog.show();
        }

        final String srcText = mTessOCR.getOCRResult(bitmap);
        if (srcText != null && !srcText.equals("")) {
            Log.v("projekt", "symbol: " + srcText);
            if(srcText.equals("x"))
                results.add("*");
            else
                results.add(srcText);
        }
        else {
            Log.v("projekt", "----------------------nie rozpoznano");
            results.add("brak");
        }

        //mTessOCR.onDestroy();
        mProgressDialog.dismiss();
    }

    private String arrayListToString(List<String> list){
        String res = "";
        for (int i=0;i<list.size();i++){
            res = res + list.get(i);
        }
        return res;
    }

    public String getEqFromTVs(TextView[] tvArray){
        String res="";
        for (int i = 0 ;i<tvArray.length;i++){
            res = res + tvArray[i].getText().toString();
        }
        return res;
    }

    private String assignSymbol(int a){
        String res;
        switch(a){
            case 1:
                res = "1";
                break;
            case 2:
                res = "2";
                break;
            case 3:
                res = "3";
                break;
            case 4:
                res = "4";
                break;
            case 5:
                res = "5";
                break;
            case 6:
                res = "6";
                break;
            case 7:
                res = "7";
                break;
            case 8:
                res = "8";
                break;
            case 9:
                res = "9";
                break;
            case 10:
                res = "+";
                break;
            case 11:
                res = "-";
                break;
            case 12:
                res = "*";
                break;
            case 13:
                res = "/";
                break;
            case 14:
                res = ")";
                break;
            case 15:
                res = "(";
                break;
            case 16:
                res = "0";
                break;
            default : {
                res = "brak";
                Log.v("projekt", "error");
            }

        }
        return res;
    }
}
