package com.rgp.launchpad.classes;

import android.content.res.AssetManager;

public class SoundEngineInterface {

    public static native int       initAudioEngine(AssetManager mgr);
    public static native void      releaseAudioEngine();
    public static native int       createAudioPlayer(int data_id);
    public static native boolean   deleteAudioplayer(int sourceId);
    public static native void      play(int audioplayer_id);
    public static native void      stop(int audioplayer_id);
    public static native void      stopAll();
    public static native void      loop(int audioplayer_id,boolean looping);
    public static native boolean   isPlaying(int audioplayer_id);
    public static native boolean   isStopped(int audioplayer_id);
    public static native boolean   isLooping(int audioplayer_id);
    public static native int       createAudioDataSourceFromAssets(String filename);
    public static native int       createAudioDataSourceFromURI(byte[] filename,int length);
    public static native boolean   deleteAudioDataSource(int data_id);
    public static native String    getPath(int button_audioid) ;
}
