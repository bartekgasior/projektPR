package com.example.barte.projektpr;

 import android.content.Context;
 import android.graphics.Bitmap;

 import org.opencv.core.Mat;
 import org.opencv.core.Point;
 import org.opencv.core.Rect;
 import org.opencv.core.Size;
 import org.opencv.imgproc.Imgproc;

 import java.util.ArrayList;
 import java.util.List;


public class ImageOperations {
    private boolean overlap(Rect r1, Rect r2){
        /*wspolrzedne r1*/
        int x1 = r1.x;
        int y1 = r1.y;
        int x2 = r1.x + r1.width;
        int y2 = r1.y + r1.height;

        /*wspolrzedne r2*/
        int x3 = r2.x;
        int y3 = r2.y;
        int x4 = r2.x + r2.width;
        int y4 = r2.y + r2.height;

        if (y4<y1 || y3>y2 || x4<x1 || x3>x2)
            return false;
        else return true;
    }

    public List<Rect> removeOverlapedRectangles(List<Rect> arrayList){
        Rect rectA,rectB = new Rect();

        for (int i=0; i<arrayList.size(); i++){
            rectA = arrayList.get(i);

            for (int j=0; j<arrayList.size(); j++){
                if(i!=j) {
                    rectB = arrayList.get(j);
                    if (overlap(rectA, rectB) == true){
                        arrayList.remove(rectA);
                    }
                }
            }
        }
        return arrayList;
    }

    public List<Rect> removeToSmallRectangles(List<Rect> arrayList){
        Rect rect = new Rect();
        for(int i=0;i<arrayList.size();i++){
            rect=arrayList.get(i);
            if(rect.width<50 || rect.height<50)
                arrayList.remove(i);
        }
        return arrayList;
    }

    public Mat morphology(Mat img){
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size( 10, 10 ), new Point( -1,-1 ));
        Imgproc.morphologyEx(img, img, Imgproc.MORPH_CLOSE, element);
        Imgproc.morphologyEx(img, img, Imgproc.MORPH_OPEN, element);
        return img;
    }

    public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h= (int) (newHeight*densityMultiplier);
        int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));

        photo=Bitmap.createScaledBitmap(photo, w, h, true);

        return photo;
    }

    public List<Rect> orderRectangles(List<Rect> arrayList){
        Rect tmpRect = new Rect();
        for (int i=0;i<arrayList.size();i++){
            for (int j=1; j<arrayList.size()-i;j++){
                if(arrayList.get(j-1).x > arrayList.get(j).x){
                    tmpRect = arrayList.get(j-1);
                    arrayList.set(j-1,arrayList.get(j));
                    arrayList.set(j,tmpRect);
                }
            }
        }
        return arrayList;
    }

    public List<Integer> orderValues(List<Integer> arrayList, List<Integer> arrayList2){
        int tmp,tmp2;
        for (int i=0;i<arrayList.size();i++){
            for (int j=1; j<arrayList.size()-i;j++){
                if(arrayList.get(j-1) > arrayList.get(j)){

                    tmp = arrayList.get(j-1);
                    arrayList.set(j-1,arrayList.get(j));
                    arrayList.set(j,tmp);

                    tmp2 = arrayList2.get(j-1);
                    arrayList2.set(j-1,arrayList2.get(j));
                    arrayList2.set(j,tmp2);
                }
            }
        }
        return arrayList;
    }
}
