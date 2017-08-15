package com.iceta.game.managers;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ArrayMap;
import com.iceta.game.iCeta;


import edu.ceta.vision.android.topcode.TopCodeDetectorAndroid;
import edu.ceta.vision.core.blocks.Block;
import edu.ceta.vision.core.topcode.TopCodeDetector;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ewe on 8/10/17.
 */
public class CvBlocksManager {
    public static final String TAG = CvBlocksManager.class.getName();
    private TopCodeDetector topCodeDetector;
    private iCeta game;
    private boolean detectionReady;
    public ArrayList<Set> results = new ArrayList<Set>();
    ArrayList<Integer> nowDetectedVals = new ArrayList<Integer>();
    private Set<Block> currentBlocks;


    public CvBlocksManager(iCeta game, Stage stage)
    {
        this.game = game;
        init();
    }


    private void init(){
        Rect detectionZone = new Rect((640-480),0,480,480);
        if((Gdx.app.getType() == Application.ApplicationType.Android)) {
            topCodeDetector = new TopCodeDetectorAndroid(50, true, 70, 5, true, false, false, true, detectionZone);
        }
        detectionReady = false;
    }

    public void updateDetected() {
        if((Gdx.app.getType() == Application.ApplicationType.Android)) {
            new Thread(new Runnable() {
                public void run() {
                    Mat frame = game.getAndBlockLastFrame();
                    Core.flip(frame, frame, 0);

                    final Set<Block> finalSet = ((TopCodeDetectorAndroid) topCodeDetector).detectBlocks(frame, 0.85);
                    //final Set<Block> finalSet = topCodeDetector.detectBlocks(((CetaGame) game).getAndBlockLastFrame());
                    // Gdx.app.log(TAG, "ready with the detection!! framerateee"+Gdx.graphics.getFramesPerSecond());
                    detectionReady = true;
                    game.releaseFrame();

                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            // process the result, e.g. add it to an Array<Result> field of the ApplicationListener.
                            results.clear();
                            results.add(finalSet);
                        }
                    });
                }
            }).start();
        }
        else{
            //PC case
        }
    }

    public void analyseDetected(){
        //Set<Block> currentBlocks = topCodeDetector.detectBlocks(((CetaGame)game).getAndBlockLastFrame());
        if(detectionReady) {
            if(results.size() > 0) {
                currentBlocks = results.get(0);
            }
            else {
                Gdx.app.error(TAG," very very wrong -> empty result!");
                currentBlocks =  new HashSet<Block>();
            }
            nowDetectedVals.clear();

            for (Block i : currentBlocks) {
                // Gdx.app.log(TAG, " orientation (radians) " + i.getOrientation() + " center " + i.getCenter() + " type " + i.getType() + " id " + i.getId());
//                newIds.add(i.getId());
//                newDetectedCVBlocks.add(i);
                nowDetectedVals.add(i.getValue());
            }
           // Gdx.app.log(TAG, "blocks detected " + currentBlocks.size() + " new ids " + Arrays.toString(newIds.toArray()) + " old: " + Arrays.toString(oldIds.toArray()));
            Gdx.app.log(TAG, "now detected vals "+Arrays.toString(nowDetectedVals.toArray()));
            detectionReady = false;
        }
    }

    public boolean isDetectionReady(){
        return detectionReady;
    }

    public ArrayList<Integer> getNewDetectedVals(){
        return new ArrayList(nowDetectedVals);
    }


}
