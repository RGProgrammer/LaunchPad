package com.rgp.launchpad.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
        int error=SoundEngineInterface.initAudioEngine(this.getAssets()) ;
        Toast.makeText(this,"error init engine",Toast.LENGTH_LONG);
        mode=PLAYERMODE ;
        ButtonTouchListener.ButtonListener.setMainActivity(this);
        this.InitLaunchButtonsConfig();
        this.InitLaunchButtonsListener();
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
                //((ImageView)v).setImageDrawable(getDrawable(R.drawable.configpressed));
                SoundEngineInterface.stopAll();
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
                    selectedPage = 1;
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
        LaunchButtonConfig config=null ;

        config = new LaunchButtonConfig(((Button) findViewById(R.id.source1)), 0, SoundEngineInterface.createAudioPlayer(0));
        LaunchButtonConfig.list.add(config);
        config = new LaunchButtonConfig(((Button) findViewById(R.id.source2)), 0, SoundEngineInterface.createAudioPlayer(0));
        LaunchButtonConfig.list.add(config);
        config = new LaunchButtonConfig(((Button) findViewById(R.id.source3)), 0, SoundEngineInterface.createAudioPlayer(0));
        LaunchButtonConfig.list.add(config);
        config = new LaunchButtonConfig(((Button) findViewById(R.id.source4)), 0, SoundEngineInterface.createAudioPlayer(0));
        LaunchButtonConfig.list.add(config);
        config = new LaunchButtonConfig(((Button) findViewById(R.id.source5)), 0, SoundEngineInterface.createAudioPlayer(0));
        LaunchButtonConfig.list.add(config);
        config = new LaunchButtonConfig(((Button) findViewById(R.id.source6)), 0, SoundEngineInterface.createAudioPlayer(0));
        LaunchButtonConfig.list.add(config);
        config = new LaunchButtonConfig(((Button) findViewById(R.id.source7)), 0, SoundEngineInterface.createAudioPlayer(0));
        LaunchButtonConfig.list.add(config);
        config = new LaunchButtonConfig(((Button) findViewById(R.id.source8)), 0, SoundEngineInterface.createAudioPlayer(0));
        LaunchButtonConfig.list.add(config);
        config = new LaunchButtonConfig(((Button) findViewById(R.id.source9)), 0, SoundEngineInterface.createAudioPlayer(0));
        LaunchButtonConfig.list.add(config);
        config = new LaunchButtonConfig(((Button) findViewById(R.id.source10)), 0, SoundEngineInterface.createAudioPlayer(0));
        LaunchButtonConfig.list.add(config);
        config = new LaunchButtonConfig(((Button) findViewById(R.id.source11)), 0, SoundEngineInterface.createAudioPlayer(0));
        LaunchButtonConfig.list.add(config);
        config = new LaunchButtonConfig(((Button) findViewById(R.id.source12)), 0, SoundEngineInterface.createAudioPlayer(0));
        LaunchButtonConfig.list.add(config);

        config = new LaunchButtonConfig(((Button) findViewById(R.id.source1)), 1, SoundEngineInterface.createAudioPlayer(0));
        LaunchButtonConfig.list.add(config);
        config = new LaunchButtonConfig(((Button) findViewById(R.id.source2)), 1, SoundEngineInterface.createAudioPlayer(0));
        LaunchButtonConfig.list.add(config);
        config = new LaunchButtonConfig(((Button) findViewById(R.id.source3)), 1, SoundEngineInterface.createAudioPlayer(0));
        LaunchButtonConfig.list.add(config);
        config = new LaunchButtonConfig(((Button) findViewById(R.id.source4)), 1, SoundEngineInterface.createAudioPlayer(0));
        LaunchButtonConfig.list.add(config);
        config = new LaunchButtonConfig(((Button) findViewById(R.id.source5)), 1, SoundEngineInterface.createAudioPlayer(0));
        LaunchButtonConfig.list.add(config);
        config = new LaunchButtonConfig(((Button) findViewById(R.id.source6)), 1, SoundEngineInterface.createAudioPlayer(0));
        LaunchButtonConfig.list.add(config);
        config = new LaunchButtonConfig(((Button) findViewById(R.id.source7)), 1, SoundEngineInterface.createAudioPlayer(0));
        LaunchButtonConfig.list.add(config);
        config = new LaunchButtonConfig(((Button) findViewById(R.id.source8)), 1, SoundEngineInterface.createAudioPlayer(0));
        LaunchButtonConfig.list.add(config);
        config = new LaunchButtonConfig(((Button) findViewById(R.id.source9)), 1, SoundEngineInterface.createAudioPlayer(0));
        LaunchButtonConfig.list.add(config);
        config = new LaunchButtonConfig(((Button) findViewById(R.id.source10)), 1, SoundEngineInterface.createAudioPlayer(0));
        LaunchButtonConfig.list.add(config);
        config = new LaunchButtonConfig(((Button) findViewById(R.id.source11)), 1, SoundEngineInterface.createAudioPlayer(0));
        LaunchButtonConfig.list.add(config);
        config = new LaunchButtonConfig(((Button) findViewById(R.id.source12)), 1, SoundEngineInterface.createAudioPlayer(0));
        LaunchButtonConfig.list.add(config);

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
        if(resultCode==RESULT_OK && data !=null){
            String path=data.getExtras().getString("path");
            if(path.endsWith(".mp3")) {
                SoundEngineInterface.deleteAudioDataSource(requestCode);
                SoundEngineInterface.deleteAudioplayer(requestCode);
                int sample = SoundEngineInterface.createAudioDataSourceFromURI(path);
                int player=0;
                if ((player=SoundEngineInterface.createAudioPlayer(sample) )== 0)
                    Toast.makeText(this, "cannot recreate audio source", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "audio source recreated", Toast.LENGTH_SHORT).show();
                for (int i=0;i< LaunchButtonConfig.list.size();++i){
                    if(LaunchButtonConfig.list.get(i).getAudioID()==requestCode ){
                        LaunchButtonConfig.list.get(i).setAudioID(player);
                        break ;
                    }
                }
            }
            int playmode= data.getExtras().getInt("mode");
            Toast.makeText(this,"mode = "+playmode,Toast.LENGTH_LONG).show();
            for (int i=0;i< LaunchButtonConfig.list.size();++i){
                if(LaunchButtonConfig.list.get(i).getAudioID()==requestCode ){
                    LaunchButtonConfig.list.get(i).setMode(playmode);
                    if(playmode==LaunchButtonConfig.LOOPMODE) {
                        int id = LaunchButtonConfig.list.get(i).getAudioID();
                        SoundEngineInterface.loop(id,true);
                    }else{
                        int id = LaunchButtonConfig.list.get(i).getAudioID();
                        SoundEngineInterface.loop(id,false);
                    }
                    break ;
                }
            }
        }
    }
}
