package com.iceta.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.iceta.game.screens.DirectedGame;
import com.iceta.game.screens.TestScreen;
import com.iceta.game.transitions.ScreenTransition;
import com.iceta.game.transitions.ScreenTransitionFade;
import com.iceta.game.util.AudioManager;
import org.opencv.core.Mat;

public class iCeta extends DirectedGame {
	public static final String TAG = iCeta.class.getName();
	private boolean frameBlocked, hasNewFrame;
	private Mat lastFrame;//, previousFrame;
	private Object syncObject = new Object();
	
	@Override
	public void create () {
		this.frameBlocked = false;
		Assets.instance.init(new AssetManager());


		AudioManager.instance.play(Assets.instance.music.song01);
		//AudioManager.instance.play(Assets.instance.sounds.puck);
		ScreenTransition transition = ScreenTransitionFade.init(1);
		setScreen(new TestScreen(this),transition);
	}

	public void setLastFrame(Mat frame){
		//	Gdx.app.log(TAG,"Setting last frame setLastFrame!");
		synchronized (syncObject) {
			if(!this.frameBlocked) {
				//	Gdx.app.log(TAG,"Setting new frame!");
				this.lastFrame = frame.clone();
				this.hasNewFrame = true;
			}else{
				//		Gdx.app.log(TAG,"blocked frame!");
			}
		}
	}

	public Mat getAndBlockLastFrame(){
		synchronized (syncObject) {
			//		Gdx.app.log(TAG,"blocking frame!");
			this.frameBlocked = true;
			this.hasNewFrame = false;
			return this.lastFrame;
		}

	}

	public void releaseFrame(){

		synchronized (syncObject) {
			Gdx.app.log(TAG,"Frame released!");
			if(this.lastFrame!=null){
				//this.lastFrame.release();
				this.frameBlocked = false;
			}
		}
	}

	public boolean hasNewFrame(){
		return hasNewFrame;
	}
}
