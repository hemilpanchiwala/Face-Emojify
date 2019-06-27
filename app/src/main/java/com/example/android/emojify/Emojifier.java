package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class Emojifier {

    private static final double SMILING_PROB_THRESHOLD = .5;
    private static final double EYE_OPEN_PROB_THRESHOLD = .5;

    public static Bitmap detectFacesAndOverlayEmoji(Context context, Bitmap bitmap){

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

        Bitmap resultBitmap = bitmap;

        for (int i=0;i<faces.size();i++){
            Face face = faces.valueAt(i);

            Bitmap emojiBitmap;
            switch (whichEmoji(context, face)){
                case SMILE:
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.smile);
                    break;

                case FROWN:
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.frown);
                    break;

                case LEFTWINK:
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.leftwink);
                    break;

                case RIGHTWINK:
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.rightwink);
                    break;

                case CLOSED_FROWN:
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.closed_frown);
                    break;

                case CLOSED_SMILE:
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.closed_smile);
                    break;

                case LEFTWINKFROWN:
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.leftwinkfrown);
                    break;

                case RIGHTWINKFROWN:
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.rightwinkfrown);

                    default:
                        emojiBitmap = null;
                        Toast.makeText(context, "No Face Detected", Toast.LENGTH_SHORT).show();
            }

            resultBitmap = addBitmapToFace(bitmap, emojiBitmap, face);


        }

        detector.release();

        return resultBitmap;
    }

    private static Bitmap addBitmapToFace(Bitmap backgroundBitmap, Bitmap emojiBitmap, Face face) {

        Bitmap resultBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(),
                backgroundBitmap.getHeight(), backgroundBitmap.getConfig());

        float scaleFactor = .9f;

        int newEmojiWidth = (int) (face.getWidth() * scaleFactor);
        int newEmojiHeight = (int) (emojiBitmap.getHeight() *
                newEmojiWidth / emojiBitmap.getWidth() * scaleFactor);

        emojiBitmap = Bitmap.createScaledBitmap(emojiBitmap, newEmojiWidth, newEmojiHeight, false);

        float emojiPositionX =
                (face.getPosition().x + face.getWidth() / 2) - emojiBitmap.getWidth() / 2;
        float emojiPositionY =
                (face.getPosition().y + face.getHeight() / 2) - emojiBitmap.getHeight() / 3;

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        canvas.drawBitmap(emojiBitmap, emojiPositionX, emojiPositionY, null);

        return resultBitmap;
    }


    private static Emoji whichEmoji(Context context, Face face){
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

        return emoji;

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
