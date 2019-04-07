package com.rgp.launchpad.classes;

import android.app.Activity;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.rgp.launchpad.activities.MainActivity;

import java.io.Serializable;

public class ButtonTouchListener implements View.OnTouchListener , Serializable{

    public static  ButtonTouchListener ButtonListener = new ButtonTouchListener();
    private MainActivity Main ;
    private  ButtonTouchListener(){
        this.Main=null;
    }

    public void setMainActivity(MainActivity Main){
        this.Main=Main ;
    }
    public Activity getMainActivity(){
        return this. Main;
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(Main==null)
            return false ;
        LaunchButtonConfig config= LaunchButtonConfig.getButtonConfig(v.getId(), MainActivity.getSelectedPage());
        if(event.getAction()==MotionEvent.ACTION_DOWN) {
            if(Main.getMode()==MainActivity.PLAYERMODE) {
                    if (config != null) {
                        if(config.getMode()==LaunchButtonConfig.SIMPLEMODE) {
                            SoundEngineInterface.stop(config.getAudioID());
                            SoundEngineInterface.play(config.getAudioID());
                            v.setBackgroundColor(LaunchButtonConfig.PRESSCOLOR);

                        }else if(config.getMode()==LaunchButtonConfig.LOOPMODE){
                            if(SoundEngineInterface.isStopped(config.getAudioID())){
                                SoundEngineInterface.play(config.getAudioID());
                                v.setBackgroundColor(LaunchButtonConfig.PRESSCOLOR);

                            }else {
                                SoundEngineInterface.stop(config.getAudioID());
                                v.setBackgroundColor(LaunchButtonConfig.RELEASECOLOR);
                            }
                        }else if(config.getMode()==LaunchButtonConfig.HOLDMODE){
                            SoundEngineInterface.stop(config.getAudioID());
                            SoundEngineInterface.play(config.getAudioID());
                            v.setBackgroundColor(LaunchButtonConfig.PRESSCOLOR);
                        }
                    }
            }else if(Main.getMode()==MainActivity.CONFIGMODE){
                    Intent configIntent= new Intent("android.intent.action.ButtonConfig");
                    if(config!=null) {

                        Main.startActivityForResult(configIntent, LaunchButtonConfig.getButtonConfigIndex(config));
                    }

            }
            return true;
        }else if (event.getAction()==MotionEvent.ACTION_UP){
            if(Main.getMode()==MainActivity.PLAYERMODE ){
                if(config!=null){
                    if(config.getMode()==LaunchButtonConfig.SIMPLEMODE) {
                        v.setBackgroundColor(LaunchButtonConfig.RELEASECOLOR);

                    }else if(config.getMode()==LaunchButtonConfig.HOLDMODE){
                        SoundEngineInterface.stop(config.getAudioID());
                        v.setBackgroundColor(LaunchButtonConfig.RELEASECOLOR);
                    }
                }
            }
            return true ;
        }
        return false ;
    }
}
