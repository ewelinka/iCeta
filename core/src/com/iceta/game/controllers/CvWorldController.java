package com.iceta.game.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Disposable;
import com.iceta.game.Assets;
import com.iceta.game.iCeta;
import com.iceta.game.managers.CvBlocksManager;
import com.iceta.game.screens.DirectedGame;
import com.iceta.game.util.AudioManager;
import com.iceta.game.util.CameraHelper;
import com.iceta.game.util.Constants;

import java.util.ArrayList;

/**
 * Created by ewe on 8/10/17.
 */
public class CvWorldController extends InputAdapter {
    private static final String TAG = CvWorldController.class.getName();
    protected Stage stage;
    public CameraHelper cameraHelper;
    public iCeta game;
    private int randomNumber;
    private boolean lastAnswerRight;

    private float timeToWait, timePassed;


    protected CvBlocksManager cvBlocksManager;

    public CvWorldController(iCeta game, Stage stage){
        this.game = game;
        this.stage = stage;
        cvBlocksManager = new CvBlocksManager(game,stage);
       // level = new Level(stage,this);
        init();

    }

    private void init(){
        Gdx.app.log(TAG,"init in the cv blocks manager");

        timePassed = 0;
        randomNumber = getNewNumber();
        timeToWait = randomNumber + Constants.WAIT_AFTER_KNOCK;
        AudioManager.instance.setStage(stage);
        AudioManager.instance.readFeedback(randomNumber);
        lastAnswerRight = false;
    }


    public void update(float deltaTime) {
        timePassed+=deltaTime;

        /// detection-related start
        if (game.hasNewFrame()) {
            cvBlocksManager.updateDetected();
        }
        if (cvBlocksManager.isDetectionReady()) {
            cvBlocksManager.analyseDetected();
        }

        if(isTimeToStartNewLoop()){
            Gdx.app.log(TAG,"new loop! with random number "+randomNumber);
            if(lastAnswerRight){
                AudioManager.instance.play(Assets.instance.sounds.yuju);
                randomNumber = getNewNumber();
                timeToWait = randomNumber + Constants.WAIT_AFTER_KNOCK;
                timePassed = 0;
                AudioManager.instance.readFeedback(randomNumber);
                lastAnswerRight = false;

            }else {
                ArrayList<Integer> nowDetected = cvBlocksManager.getNewDetectedVals(); // to know the blocks on the table
                int sum = 0;
                for (int i = 0; i < nowDetected.size(); i++)
                    sum += nowDetected.get(i);
                //check what longer to see how long to wait
                if (sum > randomNumber) {
                    timeToWait = sum;
                } else
                    timeToWait = randomNumber;

                if(sum == randomNumber){
                    Gdx.app.log(TAG,"iguality!!! "+sum+" "+randomNumber);
                    lastAnswerRight = true;
                }else
                    timeToWait += Constants.WAIT_AFTER_KNOCK;
                timePassed = 0;

                AudioManager.instance.readFeedbackAndBlocks(nowDetected, randomNumber);
            }

        }
    }

    private int getNewNumber(){
        return MathUtils.random(1,5);

    }

    private boolean isTimeToStartNewLoop(){
        //Gdx.app.log(TAG,"NEW LOOOOOOOP "+timePassed+" "+timeToWait+" "+(timePassed > timeToWait));
        return (timePassed > timeToWait);
    }


}
