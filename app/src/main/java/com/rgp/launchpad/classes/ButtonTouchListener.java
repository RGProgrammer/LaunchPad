package com.rgp.launchpad.classes;

import android.app.Activity;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
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
        LaunchButtonConfig config=config = LaunchButtonConfig.getButtonConfig((Button) v, MainActivity.getSelectedPage());
        if(event.getAction()==MotionEvent.ACTION_DOWN) {
            if(Main.getMode()==MainActivity.PLAYERMODE) {
                    if (config != null) {
                        if(config.getMode()==LaunchButtonConfig.SIMPLEMODE) {
                            SoundEngineInterface.stop(config.getAudioID());
                            SoundEngineInterface.play(config.getAudioID());
                            v.setBackgroundColor(0xff00ff00);
                        }else if(config.getMode()==LaunchButtonConfig.LOOPMODE){
                            if(SoundEngineInterface.isStopped(config.getAudioID())==true){
                                SoundEngineInterface.play(config.getAudioID());
                                v.setBackgroundColor(0xff00ff00);
                            }else {
                                SoundEngineInterface.stop(config.getAudioID());
                                v.setBackgroundColor(0xffaaaaaa);
                            }
                        }else if(config.getMode()==LaunchButtonConfig.HOLDMODE){
                            SoundEngineInterface.stop(config.getAudioID());
                            SoundEngineInterface.play(config.getAudioID());
                            v.setBackgroundColor(0xff00ff00);
                        }
                    }
            }else if(Main.getMode()==MainActivity.CONFIGMODE){
                    Intent configIntent= new Intent("android.intent.action.ButtonConfig");
                    Main.startActivityForResult(configIntent,config.getAudioID());
            }
            return true;
        }else{
            if(Main.getMode()==MainActivity.PLAYERMODE ){
                if(config!=null){
                    if(config.getMode()==LaunchButtonConfig.SIMPLEMODE) {
                        v.setBackgroundColor(0xffaaaaaa);
                    }else if(config.getMode()==LaunchButtonConfig.HOLDMODE){
                        SoundEngineInterface.stop(config.getAudioID());
                        v.setBackgroundColor(0xffaaaaaa);
                    }
                }
            }
            return true ;
        }
    }
}
