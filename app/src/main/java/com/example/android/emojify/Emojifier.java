package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class Emojifier {

    private static final double SMILING_PROB_THRESHOLD = .5;
    private static final double EYE_OPEN_PROB_THRESHOLD = .5;

    public static void detectFaces(Context context, Bitmap bitmap){

        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();

        SparseArray<Face> faces = detector.detect(frame);

        if (faces.size() > 0) {
            Toast.makeText(context, Integer.toString(faces.size()), Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "No Face Detected", Toast.LENGTH_SHORT).show();
        }

        for (int i=0;i<faces.size();i++){
            Face face = faces.valueAt(i);

            whichEmoji(context, face);

        }

        detector.release();

    }

    public static void whichEmoji(Context context, Face face){
        float leftEyeOpenProbability = face.getIsLeftEyeOpenProbability();
        float rightEyeOpenProbability = face.getIsRightEyeOpenProbability();
        float smilingProbability = face.getIsSmilingProbability();

        Boolean smiling = (smilingProbability>SMILING_PROB_THRESHOLD);
        Boolean rightEyeOpen = (rightEyeOpenProbability>EYE_OPEN_PROB_THRESHOLD);
        Boolean leftEyeOpen = (leftEyeOpenProbability>EYE_OPEN_PROB_THRESHOLD);

        Emoji emoji;

        if (smiling){

            if(rightEyeOpen && leftEyeOpen){
                emoji = Emoji.SMILE;
            }else if(!rightEyeOpen && leftEyeOpen){
                emoji = Emoji.RIGHTWINK;
            }else if(rightEyeOpen && !leftEyeOpen){
                emoji = Emoji.LEFTWINK;
            }else {
                emoji = Emoji.CLOSED_SMILE;
            }

        }else {

            if(rightEyeOpen && leftEyeOpen){
                emoji = Emoji.FROWN;
            }else if(!rightEyeOpen && leftEyeOpen){
                emoji = Emoji.RIGHTWINKFROWN;
            }else if(rightEyeOpen && !leftEyeOpen){
                emoji = Emoji.LEFTWINKFROWN;
            }else {
                emoji = Emoji.CLOSED_FROWN;
            }

        }

        Toast.makeText(context, emoji.name(), Toast.LENGTH_LONG).show();

    }



    private enum Emoji{
        SMILE,
        FROWN,
        LEFTWINK,
        RIGHTWINK,
        LEFTWINKFROWN,
        RIGHTWINKFROWN,
        CLOSED_FROWN,
        CLOSED_SMILE
    }

}
