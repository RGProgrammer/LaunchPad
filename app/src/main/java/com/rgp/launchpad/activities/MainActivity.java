package com.rgp.launchpad.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rgp.launchpad.classes.ButtonTouchListener;
import com.rgp.launchpad.classes.LaunchButtonConfig;
import com.rgp.launchpad.classes.SoundEngineInterface;
import com.rgp.launchpad.launchpad.R;

import java.io.UnsupportedEncodingException;

public class MainActivity extends Activity{

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    public static final int PLAYERMODE = 0;
    public static final int CONFIGMODE = 1;
    private  int mode ;
    private static int selectedPage= 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // init audio engine
        int error=SoundEngineInterface.initAudioEngine(this.getAssets()) ;
        Toast.makeText(this,"error init engine",Toast.LENGTH_LONG);
        mode=PLAYERMODE ;

        //applying default config to the launch buttons
        ButtonTouchListener.ButtonListener.setMainActivity(this);

        this.InitLaunchButtonsListener();
        this.InitLaunchButtonsConfig();

        //other ui config
        ((ImageView)findViewById(R.id.returnbutton)).setVisibility(View.INVISIBLE);
        //controls
        // STOP ALL button
        ((Button)findViewById(R.id.stopall)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundEngineInterface.stopAll();
            }
        });
        //config button
        ((ImageView)findViewById(R.id.config)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                    SoundEngineInterface.stopAll();
                    MainActivity.this.switchMode(CONFIGMODE);
                    ((ImageView)findViewById(R.id.returnbutton)).setVisibility(View.VISIBLE);
                    ((ImageView)findViewById(R.id.config)).setVisibility(View.INVISIBLE);
                    ((Button)findViewById(R.id.stopall)).setVisibility(View.INVISIBLE);
            }
        } );
        //return button
        ((ImageView)findViewById(R.id.returnbutton)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                LaunchButtonConfig.ExportConfigtoFile();
                MainActivity.this.switchMode(PLAYERMODE);
                Toast.makeText(MainActivity.this,"PLAYERMODE",Toast.LENGTH_SHORT);
                ((ImageView)findViewById(R.id.config)).setVisibility(View.VISIBLE);
                ((Button)findViewById(R.id.stopall)).setVisibility(View.VISIBLE);
                ((ImageView)findViewById(R.id.returnbutton)).setVisibility(View.INVISIBLE);

            }
        } );



        //Page Select
        ((ImageView) findViewById(R.id.page1)).setImageDrawable(getDrawable(R.drawable.active));
        ((ImageView)findViewById(R.id.page1)).setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    ((ImageView) v).setImageDrawable(getDrawable(R.drawable.active));
                    ((ImageView) findViewById(R.id.page2)).setImageDrawable(getDrawable(R.drawable.deactive));
                    ((ImageView) findViewById(R.id.page3)).setImageDrawable(getDrawable(R.drawable.deactive));
                    selectedPage = 0;
                    return true;
                }
                return false;
            }
        } );

        ((ImageView)findViewById(R.id.page2)).setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    ((ImageView) v).setImageDrawable(getDrawable(R.drawable.active));
                    ((ImageView) findViewById(R.id.page1)).setImageDrawable(getDrawable(R.drawable.deactive));
                    ((ImageView) findViewById(R.id.page3)).setImageDrawable(getDrawable(R.drawable.deactive));
                    selectedPage = 1;
                    return true;
                }
                return false;
            }
        } );

        ((ImageView)findViewById(R.id.page3)).setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    ((ImageView) v).setImageDrawable(getDrawable(R.drawable.active));
                    ((ImageView) findViewById(R.id.page1)).setImageDrawable(getDrawable(R.drawable.deactive));
                    ((ImageView) findViewById(R.id.page2)).setImageDrawable(getDrawable(R.drawable.deactive));
                    selectedPage = 2;
                    return true;
                }
                return false;
            }
        } );


    }
  
    public void onDestroy(){

        SoundEngineInterface.stopAll();
        SoundEngineInterface.releaseAudioEngine();
        super.onDestroy();
    }

    private void InitLaunchButtonsConfig(){
        LaunchButtonConfig.PRESSCOLOR=getResources().getColor(R.color.btPressed);
        LaunchButtonConfig.RELEASECOLOR=getResources().getColor(R.color.btReleased);

        LaunchButtonConfig.ButtonIds.add(R.id.source1);
        LaunchButtonConfig.ButtonIds.add(R.id.source2);
        LaunchButtonConfig.ButtonIds.add(R.id.source3);
        LaunchButtonConfig.ButtonIds.add(R.id.source4);
        LaunchButtonConfig.ButtonIds.add(R.id.source5);
        LaunchButtonConfig.ButtonIds.add(R.id.source6);
        LaunchButtonConfig.ButtonIds.add(R.id.source7);
        LaunchButtonConfig.ButtonIds.add(R.id.source8);
        LaunchButtonConfig.ButtonIds.add(R.id.source9);
        LaunchButtonConfig.ButtonIds.add(R.id.source10);
        LaunchButtonConfig.ButtonIds.add(R.id.source11);
        LaunchButtonConfig.ButtonIds.add(R.id.source12);
            if(LaunchButtonConfig.ImportConfigfromFile() ) {
                Toast.makeText(this, "loaded last config", Toast.LENGTH_LONG).show();
            }else {
                for(int i=0 ; i< 12  ; ++i) {
                    LaunchButtonConfig.AddConfig(i,0,0,LaunchButtonConfig.SIMPLEMODE);
                }
                for(int i=0 ; i< 12  ; ++i) {
                    LaunchButtonConfig.AddConfig(i,1,0,LaunchButtonConfig.SIMPLEMODE);
                }
                for(int i=0 ; i< 12  ; ++i) {
                    LaunchButtonConfig.AddConfig(i,2,0,LaunchButtonConfig.SIMPLEMODE);
                }

            }
    }
    private void InitLaunchButtonsListener(){

        ((Button) findViewById(R.id.source1)).setOnTouchListener(ButtonTouchListener.ButtonListener);
        ((Button) findViewById(R.id.source2)).setOnTouchListener(ButtonTouchListener.ButtonListener);
        ((Button) findViewById(R.id.source3)).setOnTouchListener(ButtonTouchListener.ButtonListener);
        ((Button) findViewById(R.id.source4)).setOnTouchListener(ButtonTouchListener.ButtonListener);
        ((Button) findViewById(R.id.source5)).setOnTouchListener(ButtonTouchListener.ButtonListener);
        ((Button) findViewById(R.id.source6)).setOnTouchListener(ButtonTouchListener.ButtonListener);
        ((Button) findViewById(R.id.source7)).setOnTouchListener(ButtonTouchListener.ButtonListener);
        ((Button) findViewById(R.id.source8)).setOnTouchListener(ButtonTouchListener.ButtonListener);
        ((Button) findViewById(R.id.source9)).setOnTouchListener(ButtonTouchListener.ButtonListener);
        ((Button) findViewById(R.id.source10)).setOnTouchListener(ButtonTouchListener.ButtonListener);
        ((Button) findViewById(R.id.source11)).setOnTouchListener(ButtonTouchListener.ButtonListener);
        ((Button) findViewById(R.id.source12)).setOnTouchListener(ButtonTouchListener.ButtonListener);
    }


    public static int  getSelectedPage(){
        return selectedPage ;
    }
    public void switchMode(int mode){
            this.mode =mode ;
    }
    public int getMode(){
        return this.mode ;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String path=null;
        int playmode=LaunchButtonConfig.SIMPLEMODE ;
        if(resultCode==RESULT_OK && data !=null){
            if(data.hasExtra("path"))
                path=data.getExtras().getString("path");
            if(data.hasExtra("mode"))
                playmode= data.getExtras().getInt("mode");
        }
        ConfigApply(requestCode,playmode,path);
    }

    public void  ConfigApply(int configindex, int mode , String path ) {
            if(path!=null) {
                if (path.endsWith(".mp3") || path.endsWith(".wav") || path.endsWith(".ogg") ||
                        path.endsWith(".MP3") || path.endsWith(".WAV") || path.endsWith(".OGG")) {
                    SoundEngineInterface.deleteAudioDataSource(LaunchButtonConfig.getButtonConfig(configindex).getAudioID());
                    SoundEngineInterface.deleteAudioplayer(LaunchButtonConfig.getButtonConfig(configindex).getAudioID());
                    int player = 0;
                    int sample = 0;
                    try {
                        sample = SoundEngineInterface.createAudioDataSourceFromURI(path.getBytes("UTF-8"),path.length());
                        player=SoundEngineInterface.createAudioPlayer(sample);
                    } catch (UnsupportedEncodingException e) {
                        player=SoundEngineInterface.createAudioPlayer(0);
                    }
                    LaunchButtonConfig.list.get(configindex).setAudioID(player);

                }
            }

            LaunchButtonConfig.list.get(configindex).setMode(mode);
            if(mode==LaunchButtonConfig.LOOPMODE) {
                SoundEngineInterface.loop(LaunchButtonConfig.getButtonConfig(configindex).getAudioID(),true);
            }else{
                SoundEngineInterface.loop(LaunchButtonConfig.getButtonConfig(configindex).getAudioID(),false);
            }

    }

    @Override
    protected void onPause() {
        SoundEngineInterface.stopAll();
        super.onPause();
    }
}
