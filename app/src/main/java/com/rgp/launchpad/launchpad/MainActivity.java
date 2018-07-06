package com.rgp.launchpad.launchpad;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends Activity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    private int[] audiosources={0,0,0,0,0,0};
    private int[] audiosamples={0,0,0,0,0,0};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int error=initAudioEngine(this.getAssets()) ;
        Toast.makeText(this,"error init engine",Toast.LENGTH_LONG);

        audiosamples[0]= this.createAudioDataSource("bip.mp3");
        audiosamples[1]= this.createAudioDataSource("baw.mp3");
        audiosamples[2]= this.createAudioDataSource("bam.mp3");
        audiosamples[3]= this.createAudioDataSource("bom.mp3");
        audiosamples[4]= this.createAudioDataSource("bop.mp3");
        audiosamples[5]= this.createAudioDataSource("tak.mp3");

        audiosources[0] = this.createAudioPlayer( audiosamples[0]);
        audiosources[1] = this.createAudioPlayer( audiosamples[1]);
        audiosources[2] = this.createAudioPlayer( audiosamples[2]);
        audiosources[3] = this.createAudioPlayer( audiosamples[3]);
        audiosources[4] = this.createAudioPlayer( audiosamples[4]);
        audiosources[5] = this.createAudioPlayer( audiosamples[5]);


        ((Button) findViewById(R.id.audio1)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

                stop(audiosources[0]);
                play(audiosources[0]);
            }
        });
        ((Button) findViewById(R.id.audio2)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

                stop(audiosources[1]);
                play(audiosources[1]);
            }
        });
        ((Button) findViewById(R.id.audio3)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

                stop(audiosources[2]);
                play(audiosources[2]);
            }
        });
        ((Button) findViewById(R.id.audio4)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

                stop(audiosources[3]);
                play(audiosources[3]);
            }
        });
        ((Button) findViewById(R.id.audio5)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

                stop(audiosources[4]);
                play(audiosources[4]);
            }
        });
        ((Button) findViewById(R.id.audio6)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

                stop(audiosources[5]);
                play(audiosources[5]);
            }
        });

    }
    public void onDestroy(){

        this.releaseAudioEngine();
        super.onDestroy();
    }
    public native int   initAudioEngine(AssetManager mgr);
    public native void      releaseAudioEngine();
    public native int       createAudioPlayer(int data_id);
    public native boolean   deleteAudioplayer(int sourceId);
    public native void      play(int audioplayer_id);
    public native void      stop(int audioplayer_id);
    public native int       createAudioDataSource(String filename);
    public native boolean   deleteAudioDataSource(int data_id);
    public native boolean   linkAudioSourceToAudioPlayer(int data_id,int audioplayer_id );



}
