package com.rgp.launchpad.classes;
import android.app.Activity;
import android.widget.Button;

import com.rgp.launchpad.launchpad.R;

import java.util.ArrayList;

public class LaunchButtonConfig {
    public static final int         SIMPLEMODE = 0;
    public static final int         HOLDMODE   =1 ;
    public static final int         LOOPMODE   =2;


    private int button_pageid;
    private Button  buttonRef;
    private int button_audioid;
    private int mode ;


    public  static int PRESSCOLOR;
    public  static int RELEASECOLOR ;

    public static ArrayList<LaunchButtonConfig> list=new ArrayList<LaunchButtonConfig>();
    public static LaunchButtonConfig getButtonConfig(Button button_id,int page_id){
        for(int i =0 ; i< list.size();i++)
            if(list.get(i).getButtonref()==button_id && list.get(i).getPageID()==page_id)
                return list.get(i);
        return null ;
    }

    public LaunchButtonConfig(Button button,int page_id){
        this(button,page_id,0);
    }
    public LaunchButtonConfig(Button button,int page_id, int audio_id){
        this.buttonRef=button ;
        this.button_pageid= page_id ;
        this.button_audioid= audio_id ;
        mode=SIMPLEMODE;

    }
    public void setAudioID(int id){
        button_audioid=id;
    }
    public void setMode(int mode){
        if(mode>=0 && mode<=2)
            this.mode=mode ;
    }
    public int getMode(){
        return mode ;
    }
    public int getAudioID(){
        return this.button_audioid ;
    }
    public Button getButtonref(){
        return this.buttonRef;
    }
    public int getPageID(){
        return this.button_pageid;
    }

}
