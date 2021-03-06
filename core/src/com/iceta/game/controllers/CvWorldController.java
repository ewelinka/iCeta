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
    public iCeta game;
    private int randomNumber;
    private boolean lastAnswerRight;

    private float timeToWait, timePassed;


    protected CvBlocksManager cvBlocksManager;

    public CvWorldController(iCeta game, Stage stage){
        this.game = game;
        this.stage = stage;
        cvBlocksManager = new CvBlocksManager(game,stage);
        init();

    }

    private void init(){
        Gdx.app.log(TAG,"init in the cv blocks manager");
        timePassed = 0;
        randomNumber = getNewNumber();

        AudioManager.instance.setStage(stage); // we set current Stage in AudioManager, if not "reader" actor doesn't work
        AudioManager.instance.readFeedback(randomNumber); //first we read the random number
        timeToWait = randomNumber + Constants.WAIT_AFTER_KNOCK; // time we should wait before next loop starts
        lastAnswerRight = false;
    }


    public void update(float deltaTime) {
        timePassed+=deltaTime; // variable used to check in isTimeToStartNewLoop() to decide if new feedback loop should be started

        if (game.hasNewFrame()) {
            cvBlocksManager.updateDetected(); // to get detected blocks
        }
        if (cvBlocksManager.isDetectionReady()) {
            cvBlocksManager.analyseDetected(); // to analyse the blocks (=get blocks values)
        }

        if(isTimeToStartNewLoop()){
            Gdx.app.log(TAG,"new loop! with random number "+randomNumber);
            if(lastAnswerRight){ // if las answer was correct, we celebrate and get new random number
                AudioManager.instance.play(Assets.instance.sounds.yuju);
                randomNumber = getNewNumber();
                timeToWait = randomNumber + Constants.WAIT_AFTER_KNOCK;
                timePassed = 0; // start to count the time
                AudioManager.instance.readFeedback(randomNumber); // we just read feedback, we do not read detected blocks
                lastAnswerRight = false;
            }else { // if last answer was wrong we check the detected values and read feedback and read blocks detected
                ArrayList<Integer> nowDetected = cvBlocksManager.getNewDetectedVals(); // to know the blocks on the table
                int sum = 0;
                for (int i = 0; i < nowDetected.size(); i++)
                    sum += nowDetected.get(i); // we need to know the sum to decide if response is correct

                if (sum > randomNumber) { //check how long to wait (biggest number between sum of blocks and random number)
                    timeToWait = sum;
                } else
                    timeToWait = randomNumber;

                if(sum == randomNumber){ // correct answer! in next loop we will celebrate
                    Gdx.app.log(TAG,"iguality!!! "+sum+" "+randomNumber);
                    lastAnswerRight = true;
                }else
                    timeToWait += Constants.WAIT_AFTER_KNOCK; // we add extra time to wait after feedback reading
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
