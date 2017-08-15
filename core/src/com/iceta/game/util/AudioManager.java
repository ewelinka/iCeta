package com.iceta.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.iceta.game.Assets;

import java.util.ArrayList;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;

/**
 * Created by ewe on 8/10/17.
 */
public class AudioManager {
    public static final String TAG = AudioManager.class.getName();

    public static final AudioManager instance = new AudioManager();
    private Music playingMusic;
    private Sound currentSound;
    private SequenceAction readFeedback, readBlocks;
    private float defaultVolSound = 0.6f;
    private Actor reader;
    private Stage stage;
    private float readBlockDuration = 0.5f;

    private AudioManager () { }

    public void setStage(Stage stage){
        Gdx.app.log(TAG,"set stage in AM");
        this.stage = stage;
        reader = new Actor();
        stage.addActor(reader);
        readFeedback = new SequenceAction();
        readBlocks = new SequenceAction();
    }

    public void play (Sound sound) {
        play(sound, 1);
    }
    public void play (Sound sound, float volume) {
        play(sound, volume, 1);
    }
    public void play (Sound sound, float volume, float pitch) {
        play(sound, volume, pitch, 0);
    }
    public void play (Sound sound, float volume, float pitch, float pan) {
        currentSound = sound;
        currentSound.play(defaultVolSound * volume, pitch, pan);
    }

    public void play (Music music) {
        stopMusic();
        playingMusic = music;

        music.setLooping(true);
        music.setVolume(0.15f);
        music.play();

    }
    public void stopMusic () {
        if (playingMusic != null) playingMusic.stop();
    }

    public void playWithoutInterruption(Sound sound) {
        sound.play(defaultVolSound , 1, 1);
    }

    public void playNumber (int nr) {
        switch (nr){
            case 1:
                playWithoutInterruption(Assets.instance.sounds.one);
                break;
            case 2:
                playWithoutInterruption(Assets.instance.sounds.two);
                break;
            case 3:
                playWithoutInterruption(Assets.instance.sounds.three);
                break;
            case 4:
                playWithoutInterruption(Assets.instance.sounds.four);
                break;
            case 5:
                playWithoutInterruption(Assets.instance.sounds.five);
                break;
        }
    }

    public void addToReadBlock (int nr) {
        switch (nr) {
            case 1:
                readBlocks.addAction(run(new Runnable() {
                    public void run() {
                        playWithoutInterruption(Assets.instance.sounds.oneDo);
                    }
                }));
                readBlocks.addAction(delay(readBlockDuration));
                break;
            case 2:
                readBlocks.addAction(run(new Runnable() {
                    public void run() {
                        playWithoutInterruption(Assets.instance.sounds.oneRe);
                    }
                }));
                readBlocks.addAction(delay(readBlockDuration));
                break;
            case 3:
                readBlocks.addAction(run(new Runnable() {
                    public void run() {
                        playWithoutInterruption(Assets.instance.sounds.oneMi);
                    }
                }));
                readBlocks.addAction(delay(readBlockDuration));
                break;

        }
    }

    public void addToReadFeedback (int nr) {
        for(int i = 0; i<nr;i++){
            readFeedback.addAction(run(new Runnable() {
                public void run() {
                    playWithoutInterruption(Assets.instance.sounds.puck);
                }
            }));
            readFeedback.addAction(delay(readBlockDuration));
        }
    }

    public void readFeedbackAndBlocks(ArrayList<Integer> toReadNums, int numToBuild){

        readBlocks.reset();
        readBlocks.addAction(delay(0.2f));
        for(int i = 0; i<toReadNums.size();i++) {
            int val = toReadNums.get(i);
            for(int j = 0; j<val;j++) {
                addToReadBlock(val);
            }
        }

        readFeedback.reset();
        readFeedback.addAction(delay(0.2f));
        addToReadFeedback(numToBuild);


        reader.addAction(parallel(readBlocks,readFeedback));

    }

    public void readFeedback( int numToBuild){
        Gdx.app.log(TAG,"readFeedback "+numToBuild);
        readFeedback.reset();
        readFeedback.addAction(delay(0.2f));
        addToReadFeedback(numToBuild);
        reader.addAction(readFeedback);
    }
}
