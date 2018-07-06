#include <jni.h>
#include <string>
#include <vector>
// for native audio
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>

// for native asset manager
#include <sys/types.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

//this is a temporary strcture. will be replaces in the future
typedef struct {
    SLDataFormat_MIME format_mime ;
    SLDataLocator_AndroidFD loc_fd;
    SLDataSource source;
}AudioSourceStruct;


//engine
static SLObjectItf      audioengine=NULL ;
static SLEngineItf      audioInterface ;
static AAssetManager    *mgr=NULL ;
//outputMix
static SLObjectItf         outputmixer =NULL;

//Defaults
SLDataLocator_AndroidFD loc_fd ;
SLDataFormat_MIME format_mime ;
SLDataLocator_OutputMix loc_outmix;
static SLDataSink defaultaudioSnk={NULL,NULL};
static SLDataSource defaultsample={NULL,NULL} ;

//audio sources
const jint MAX_AUDIO_SOURCES = 16;
static jint lastindexaudiosource = 0 ;
static SLObjectItf audiosources[MAX_AUDIO_SOURCES];
//audio samples

const jint MAX_AUDIO_SAMPLES = 16;
static jint lastindexaudiosample = 0 ;
static AudioSourceStruct audiosamples[MAX_AUDIO_SAMPLES];

void DestroyAll(){
    //destroy audio sources
    for(jint i=0 ; i<= MAX_AUDIO_SOURCES ;++i) {
        if (audiosources[i]) {
            (*audiosources[i])->Destroy(audiosources[i]);
            audiosources[i] = NULL;
        }
    }
    for(int i=0 ; i<= MAX_AUDIO_SAMPLES ;++i) {
       audiosamples[i]={NULL,NULL};
    }

    //destroy mixer
    if(outputmixer){
        (*outputmixer)->Destroy(outputmixer);
        outputmixer=NULL ;
    }

    //Destroy Engine
    if(audioengine){
        (*audioengine)->Destroy(audioengine);
        audioengine=NULL ;
        audioInterface=NULL ;
    }

}


extern "C"
JNIEXPORT jint JNICALL
Java_com_rgp_launchpad_launchpad_MainActivity_initAudioEngine(JNIEnv *env, jobject instance,jobject assetmanager) {


    DestroyAll();
    SLresult res ;
    if(assetmanager==NULL )
        return 1 ;
    mgr = AAssetManager_fromJava(env, assetmanager);
    assert(mgr!=NULL);
    if(!mgr) {
        return 2 ;
    }
    res= slCreateEngine(&audioengine,0,NULL,0,NULL,NULL);
    assert(res==SL_RESULT_SUCCESS);
    if(res!= SL_RESULT_SUCCESS) {
        return 3;
    }
    res=(*audioengine)->Realize(audioengine,SL_BOOLEAN_FALSE);
    if(res!=SL_RESULT_SUCCESS){
        DestroyAll();
        return 4 ;
    }

    res= (*audioengine)->GetInterface(audioengine,SL_IID_ENGINE,&audioInterface);
    if(res!=SL_RESULT_SUCCESS){
        DestroyAll();
        return 5 ;
    }

    res= (*audioInterface)->CreateOutputMix(audioInterface,&outputmixer,0,NULL,NULL);
    if(res!=SL_RESULT_SUCCESS) {
        DestroyAll();
        return 6;
    }
    res=(*outputmixer)->Realize(outputmixer,SL_BOOLEAN_FALSE);
    if(res!= SL_RESULT_SUCCESS){
        return 7 ;
    }
  
    //init default audio sample
   AAsset *asset = AAssetManager_open(mgr, "bip.mp3", AASSET_MODE_UNKNOWN);
    assert(asset);
    if(!asset) {
        return 8;
    }

    off_t start, length;
    int fd = AAsset_openFileDescriptor(asset, &start, &length);
    AAsset_close(asset);
    if(fd<=0) {
        return 9;
    }

    loc_fd = {SL_DATALOCATOR_ANDROIDFD, fd, start, length};
    format_mime = {SL_DATAFORMAT_MIME, NULL, SL_CONTAINERTYPE_UNSPECIFIED};
    defaultsample = {&loc_fd, &format_mime};

    // configure audio sink
    loc_outmix = {SL_DATALOCATOR_OUTPUTMIX, outputmixer};

    defaultaudioSnk = {&loc_outmix, NULL};
    
    return 0 ;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_rgp_launchpad_launchpad_MainActivity_releaseAudioEngine(JNIEnv *env, jobject instance) {
    DestroyAll();
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_rgp_launchpad_launchpad_MainActivity_createAudioPlayer(JNIEnv *env, jobject instance,
                                                                        jint data_id) {

    jint index  ;
    SLresult res  ;
    if(lastindexaudiosource<MAX_AUDIO_SOURCES-1) {
        index =lastindexaudiosource+1;
        for (jint i = 0; i <= lastindexaudiosource; ++i)
            if(audiosources[i]==NULL){
                index=i;
                break ;
            }
        if(index ==lastindexaudiosource+1)
            lastindexaudiosource++;
        // configure audio sink

        const SLInterfaceID ids[4] = {SL_IID_PLAY,SL_IID_DYNAMICINTERFACEMANAGEMENT};
        const SLboolean req[4] = {SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE,SL_BOOLEAN_TRUE};
        if(data_id < 1 || data_id>lastindexaudiosample)
            res= (*audioInterface)->CreateAudioPlayer(audioInterface,&(audiosources[index]),&defaultsample,&defaultaudioSnk,2,ids,req);
        else
            res= (*audioInterface)->CreateAudioPlayer(audioInterface,&(audiosources[index]),&(audiosamples[data_id-1].source),&defaultaudioSnk,2,ids,req);
        if(res !=SL_RESULT_SUCCESS) {
            assert(res==SL_RESULT_FEATURE_UNSUPPORTED);
            assert(res==SL_RESULT_PARAMETER_INVALID);
            return 0 ;
        }

        res =(*(audiosources[index]))->Realize(audiosources[index],SL_BOOLEAN_FALSE);

        return index+ 1 ;

    }else
        return 0 ;

}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_rgp_launchpad_launchpad_MainActivity_deleteAudioplayer(JNIEnv *env, jobject instance,jint sourceId) {

    if(sourceId<1 || sourceId>lastindexaudiosource+1)
        return  false ;
    if(audiosources[sourceId-1]){
        (*audiosources[sourceId-1])->Destroy(audiosources[sourceId-1]);
        audiosources[sourceId-1]= NULL;
    }
    return  true ;

}

extern "C"
JNIEXPORT void JNICALL
Java_com_rgp_launchpad_launchpad_MainActivity_play(JNIEnv *env, jobject instance,
                                                   jint audioplayer_id) {

    SLPlayItf playerInterface =NULL ;
    SLresult res ;
    if(audioplayer_id<1 ||audioplayer_id>lastindexaudiosource+1)
        return ;
    if(!audiosources[audioplayer_id-1])
        return ;
    res=(*audiosources[audioplayer_id-1])->GetInterface(audiosources[audioplayer_id-1],SL_IID_PLAY,&playerInterface);
    assert(res==SL_RESULT_SUCCESS);
    if(res!=SL_RESULT_SUCCESS)
        return ;
    (*playerInterface)->SetPlayState(playerInterface,SL_PLAYSTATE_PLAYING);


}

extern "C"
JNIEXPORT void JNICALL
Java_com_rgp_launchpad_launchpad_MainActivity_stop(JNIEnv *env, jobject instance,
                                                   jint audioplayer_id) {

    SLPlayItf playerInterface =NULL ;
    SLresult res ;
    if(audioplayer_id<1 ||audioplayer_id>lastindexaudiosource+1)
        return ;
    if(!audiosources[audioplayer_id-1])
        return ;
    res=(*audiosources[audioplayer_id-1])->GetInterface(audiosources[audioplayer_id-1],SL_IID_PLAY,&playerInterface);
    if(res!=SL_RESULT_SUCCESS)
        return ;
    (*playerInterface)->SetPlayState(playerInterface,SL_PLAYSTATE_STOPPED);

}

extern "C"
JNIEXPORT jint JNICALL
Java_com_rgp_launchpad_launchpad_MainActivity_createAudioDataSource(JNIEnv *env, jobject instance,
                                                                    jstring filename_) {
    const char *filename = env->GetStringUTFChars(filename_, 0);
    jint index  ;
    SLresult res  ;
    if(lastindexaudiosample<MAX_AUDIO_SAMPLES-1) {
        index = lastindexaudiosample + 1;
        for (jint i = 0; i <= lastindexaudiosample; ++i)
            if(audiosamples[i].source.pLocator==NULL && audiosamples[i].source.pFormat ==NULL){
                index=i;
                break ;
            }
        if(index ==lastindexaudiosample+1)
            lastindexaudiosample++;
        AAsset *asset = AAssetManager_open(mgr, filename, AASSET_MODE_UNKNOWN);
        env->ReleaseStringUTFChars(filename_, filename);
        if(!(mgr && asset))
            return 0 ;

        off_t start, length;
        int fd = AAsset_openFileDescriptor(asset, &start, &length);
        AAsset_close(asset);
        if(fd<=0)
            return 0;

        // configure audio source
        audiosamples[index].loc_fd = {SL_DATALOCATOR_ANDROIDFD, fd, start, length};
        audiosamples[index].format_mime = {SL_DATAFORMAT_MIME, NULL, SL_CONTAINERTYPE_UNSPECIFIED};
        audiosamples[index].source = {& (audiosamples[index].loc_fd), &(audiosamples[index])};
        return index+1 ;
    }
    return 0 ;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_rgp_launchpad_launchpad_MainActivity_deleteAudioDataSource(JNIEnv *env, jobject instance,
                                                                    jint data_id) {

    // TODO
    // TODO
return false ;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_rgp_launchpad_launchpad_MainActivity_linkAudioSourceToAudioPlayer(JNIEnv *env,
                                                                           jobject instance,
                                                                           jint data_id,
                                                                           jint audioplayer_id) {

    // TODO
    // TODO
    SLresult  res ;
    SLDynamicSourceItf  itf=NULL ;
    if(audioplayer_id<1 ||audioplayer_id>lastindexaudiosource+1)
        return false ;
    if(!audiosources[audioplayer_id-1])
        return false ;
    if(data_id<1 ||data_id>lastindexaudiosample+1)
        return false ;
    res = (*audiosources[audioplayer_id-1])->GetInterface(audiosources[audioplayer_id-1],SL_IID_DYNAMICSOURCE,&itf);
    if(res!= SL_RESULT_SUCCESS)
        return false ;
    res=(*itf)->SetSource(itf,&(audiosamples[data_id-1].source));
    if(res!=SL_RESULT_SUCCESS)
        return false ;


    return true ;


}