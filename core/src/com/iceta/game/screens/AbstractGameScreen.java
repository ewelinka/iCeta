package com.iceta.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.iceta.game.Assets;
import com.iceta.game.controllers.CvWorldController;
import com.iceta.game.iCeta;

/**
 * Created by ewe on 8/10/17.
 */
public abstract class AbstractGameScreen  implements Screen {
    public static final String TAG = AbstractGameScreen.class.getName();
    protected iCeta game;
    protected Stage stage;
    protected CvWorldController worldController;
    protected int levelJson;
    protected boolean paused;


    public AbstractGameScreen (iCeta game){
        this(game,0);
    }

    public AbstractGameScreen (iCeta game, int levelJson){
        this.game = game;
        this.levelJson = levelJson;
        paused = false;
    }


    public abstract void render (float deltaTime);
    public abstract void resize (int width, int height);
    public abstract void show ();
    public abstract void hide ();
    public abstract void pause ();

    public void resume () {
        Gdx.app.log(TAG,"== resume assets instance");
        //Assets.instance.init(new AssetManager());
    }
    public void dispose () {
        Gdx.app.log(TAG,"== dispose assets instance");
        Assets.instance.dispose();
    }
    public abstract InputProcessor getInputProcessor ();



}


