#include <jni.h>
#include <string>
#include <vector>
#include <android/log.h>
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
    void* loc_fd=NULL;
    int locatortype=-1;
    SLDataSource source;
}AudioSourceStruct;

typedef  struct {
    SLObjectItf audioplayer ;
    int data_id;
} AudioPlayerStruct;


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

//audio sources
const jint MAX_AUDIO_SOURCES = 36;
static jint lastindexaudiosource = 0 ;
static AudioPlayerStruct audiosources[MAX_AUDIO_SOURCES];
//audio samples

const jint MAX_AUDIO_SAMPLES = 36;
static jint lastindexaudiosample = 0 ;
static AudioSourceStruct audiosamples[MAX_AUDIO_SAMPLES];

int CatStrings(const char* str1, const char* str2, char** Dest) {
    if (!str1 || !str2 || !Dest)
        return 0;
    *Dest = (char*)malloc((strlen(str1) + strlen(str2) + 1) * sizeof(char));
    if (!*Dest)
        return 0;
    int i, len = strlen(str1);
    for (i = 0; str1[i]; i++)
        (*Dest)[i] = str1[i];
    for (; str2[i - len]; i++)
        (*Dest)[i] = str2[i - len];
    (*Dest)[i] = '\0';
    return 1;
}

void DestroyAll(){
    //destroy audio sources
    for(jint i=0 ; i< MAX_AUDIO_SOURCES ;++i) {
        if (audiosources[i].audioplayer) {
            (*audiosources[i].audioplayer)->Destroy(audiosources[i].audioplayer);
            audiosources[i].audioplayer = NULL;
            audiosources[i].data_id=0;
        }
    }
    lastindexaudiosource=0;
    for(int i=0 ; i< MAX_AUDIO_SAMPLES ;++i) {
        if(audiosamples[i].loc_fd){
            free(audiosamples[i].loc_fd);
            audiosamples[i].loc_fd=NULL;
        }
       audiosamples[i].source={NULL,NULL};
    }
    lastindexaudiosample=0 ;

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
Java_com_rgp_launchpad_classes_SoundEngineInterface_initAudioEngine(JNIEnv *env, jobject instance,jobject assetmanager) {


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
  


    // configure audio sink
    loc_outmix = {SL_DATALOCATOR_OUTPUTMIX, outputmixer};

    defaultaudioSnk = {&loc_outmix, NULL};
    
    return 0 ;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_rgp_launchpad_classes_SoundEngineInterface_releaseAudioEngine(JNIEnv *env, jobject instance) {
    DestroyAll();
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_rgp_launchpad_classes_SoundEngineInterface_createAudioPlayer(JNIEnv *env, jobject instance,
                                                                        jint data_id) {

    jint index= lastindexaudiosource+1 ;
    SLresult res  ;
    for (jint i = 0; i <= lastindexaudiosource; ++i) {
        if (audiosources[i].audioplayer == NULL) {
            index = i;
            break;
        }
    }
    if(index>=MAX_AUDIO_SOURCES)
        return 0 ;
    if(index>lastindexaudiosource){
        lastindexaudiosource=index ;
    }


    const SLInterfaceID ids[2] = {SL_IID_PLAY,SL_IID_SEEK};
    const SLboolean req[2] = {SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE};
    if(data_id < 1 || data_id>lastindexaudiosample+1) {
       return 0;
    }else {
        res = (*audioInterface)->CreateAudioPlayer(audioInterface,
                                                   &(audiosources[index].audioplayer),
                                                   &(audiosamples[data_id - 1].source),
                                                   &defaultaudioSnk, 2, ids, req);
        audiosources[index].data_id = data_id;
    }
    if(res !=SL_RESULT_SUCCESS) {
            assert(res==SL_RESULT_FEATURE_UNSUPPORTED);
            assert(res==SL_RESULT_PARAMETER_INVALID);
            return 0 ;
    }

        res =(*(audiosources[index].audioplayer))->Realize(audiosources[index].audioplayer,SL_BOOLEAN_FALSE);

    return index+ 1 ;

}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_rgp_launchpad_classes_SoundEngineInterface_deleteAudioplayer(JNIEnv *env, jobject instance,jint sourceId) {

    if(sourceId<1 || sourceId>MAX_AUDIO_SOURCES)
        return  false ;
    if(audiosources[sourceId-1].audioplayer){
        (*audiosources[sourceId-1].audioplayer)->Destroy(audiosources[sourceId-1].audioplayer);
        audiosources[sourceId-1].audioplayer= NULL;
        audiosources[sourceId-1].data_id= 0;


    }
    return  true ;

}

extern "C"
JNIEXPORT void JNICALL
Java_com_rgp_launchpad_classes_SoundEngineInterface_play(JNIEnv *env, jobject instance,
                                                   jint audioplayer_id) {

    SLPlayItf playerInterface =NULL ;
    SLresult res ;
    if(audioplayer_id<1 ||audioplayer_id>lastindexaudiosource+1)
        return ;
    if(!audiosources[audioplayer_id-1].audioplayer)
        return ;
    res=(*audiosources[audioplayer_id-1].audioplayer)->GetInterface(audiosources[audioplayer_id-1].audioplayer,SL_IID_PLAY,&playerInterface);
    assert(res==SL_RESULT_SUCCESS);
    if(res!=SL_RESULT_SUCCESS)
        return ;
    (*playerInterface)->SetPlayState(playerInterface,SL_PLAYSTATE_PLAYING);


}

extern "C"
JNIEXPORT void JNICALL
Java_com_rgp_launchpad_classes_SoundEngineInterface_stop(JNIEnv *env, jobject instance,
                                                   jint audioplayer_id) {

    SLPlayItf playerInterface =NULL ;
    SLresult res ;
    if(audioplayer_id<1 ||audioplayer_id>lastindexaudiosource+1)
        return ;
    if(!audiosources[audioplayer_id-1].audioplayer)
        return ;
    res=(*audiosources[audioplayer_id-1].audioplayer)->GetInterface(audiosources[audioplayer_id-1].audioplayer,SL_IID_PLAY,&playerInterface);
    assert(res==SL_RESULT_SUCCESS);
    if(res!=SL_RESULT_SUCCESS)
        return ;
    (*playerInterface)->SetPlayState(playerInterface,SL_PLAYSTATE_STOPPED);


}

extern "C"
JNIEXPORT void JNICALL
Java_com_rgp_launchpad_classes_SoundEngineInterface_stopAll(JNIEnv *env, jobject instance) {

    for(int i=0;i<=lastindexaudiosource;++i)
        Java_com_rgp_launchpad_classes_SoundEngineInterface_stop(env,instance,i+1);

}

extern "C"
JNIEXPORT void JNICALL
Java_com_rgp_launchpad_classes_SoundEngineInterface_loop(JNIEnv *env,jobject instance,
                                                         jint audioplayer_id,jboolean looping) {

    SLSeekItf playerInterface =NULL ;
    SLresult res ;

    if(audioplayer_id<1 ||audioplayer_id>lastindexaudiosource+1)
        return ;
    if(!audiosources[audioplayer_id-1].audioplayer)
        return ;
    res=(*audiosources[audioplayer_id-1].audioplayer)->GetInterface(audiosources[audioplayer_id-1].audioplayer,SL_IID_SEEK,&playerInterface);
    if(res!=SL_RESULT_SUCCESS)
        return ;
    (*playerInterface)->SetLoop(playerInterface,(SLboolean)looping,0,SL_TIME_UNKNOWN);


}


extern "C"
JNIEXPORT jboolean JNICALL
Java_com_rgp_launchpad_classes_SoundEngineInterface_isPlaying(JNIEnv *env, jobject instance,jint audioplayer_id) {

    SLPlayItf playerInterface =NULL ;
    SLresult res ;
    SLuint32 state ;
    if(audioplayer_id<1 ||audioplayer_id>lastindexaudiosource+1)
        return false;
    if(!audiosources[audioplayer_id-1].audioplayer)
        return false;
    res=(*audiosources[audioplayer_id-1].audioplayer)->GetInterface(audiosources[audioplayer_id-1].audioplayer,SL_IID_PLAY,&playerInterface);
    if(res!=SL_RESULT_SUCCESS)
        return false;
    (*playerInterface)->GetPlayState(playerInterface,&state);
    if(state==SL_PLAYSTATE_PLAYING)
        return true;
    else
        return false ;

}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_rgp_launchpad_classes_SoundEngineInterface_isStopped(JNIEnv *env, jobject instance,jint audioplayer_id) {

    SLPlayItf playerInterface =NULL ;
    SLresult res ;
    SLuint32 state ;
    if(audioplayer_id<1 ||audioplayer_id>lastindexaudiosource+1)
        return false;
    if(!audiosources[audioplayer_id-1].audioplayer)
        return false;
    res=(*audiosources[audioplayer_id-1].audioplayer)->GetInterface(audiosources[audioplayer_id-1].audioplayer,SL_IID_PLAY,&playerInterface);
    if(res!=SL_RESULT_SUCCESS)
        return false;
    (*playerInterface)->GetPlayState(playerInterface,&state);
    if(state==SL_PLAYSTATE_STOPPED)
        return true;
    else
        return false ;
}


extern "C"
JNIEXPORT jboolean JNICALL
Java_com_rgp_launchpad_classes_SoundEngineInterface_isLooping(JNIEnv *env, jobject instance ,
                                                              jint audioplayer_id) {

    SLSeekItf playerInterface =NULL ;
    SLresult res ;
    SLboolean looping ;
    if(audioplayer_id<1 ||audioplayer_id>lastindexaudiosource+1)
        return false;
    if(!audiosources[audioplayer_id-1].audioplayer)
        return false;
    res=(*audiosources[audioplayer_id-1].audioplayer)->GetInterface(audiosources[audioplayer_id-1].audioplayer,SL_IID_SEEK,&playerInterface);
    if(res!=SL_RESULT_SUCCESS)
        return false;
    (*playerInterface)->GetLoop(playerInterface,&looping,0,0);
    if(looping==SL_BOOLEAN_TRUE)
        return true;
    else
        return false ;
}



extern "C"
JNIEXPORT jint JNICALL
Java_com_rgp_launchpad_classes_SoundEngineInterface_createAudioDataSourceFromURI(JNIEnv *env,
                                                                                 jobject instance,
                                                                                 jbyteArray filename_,
                                                                                 jint length) {
    //const char *filename = env->GetStringUTFChars(filename_, 0);
    const jbyte *filename = env->GetByteArrayElements(filename_,0);
    jint index= lastindexaudiosample+1 ;
    for (jint i = 0; i <= lastindexaudiosample; ++i) {
        if (audiosources[i].audioplayer == NULL) {
            index = i;
            break;
        }
    }
    if(index>=MAX_AUDIO_SAMPLES)
        return 0 ;
    if(index>lastindexaudiosample){
        lastindexaudiosample=index ;
    }

    audiosamples[index].loc_fd =(SLDataLocator_URI*) malloc(sizeof(SLDataLocator_URI));
    char* path=NULL ;
    ((char*)filename)[length]='\0';//remove the unnecessary part
    CatStrings("file://",(char*)filename,&path);
    __android_log_print(ANDROID_LOG_DEBUG,"no tag"," \npath : \n%s",path);
    if(path)
        (*((SLDataLocator_URI*)audiosamples[index].loc_fd))={SL_DATALOCATOR_URI,(SLchar*)path};
        audiosamples[index].locatortype=1;
        audiosamples[index].format_mime = {SL_DATAFORMAT_MIME, NULL, SL_CONTAINERTYPE_UNSPECIFIED};
        audiosamples[index].source = {(audiosamples[index].loc_fd), &(audiosamples[index].format_mime)};
        return index+1 ;

}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_rgp_launchpad_classes_SoundEngineInterface_deleteAudioDataSource(JNIEnv *env, jobject instance,
                                                                    jint data_id) {
    if(data_id<1 || data_id>MAX_AUDIO_SAMPLES)
        return false ;
    audiosamples[data_id-1].source={NULL,NULL};
    return true ;
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_rgp_launchpad_classes_SoundEngineInterface_getPath(JNIEnv *env, jobject instance,
                                                            jint button_audioid) {


    char *returnValue=NULL;
    if(button_audioid>=1 && button_audioid<=MAX_AUDIO_SOURCES){
        if(audiosources[button_audioid-1].data_id!=0 ){
            if(audiosamples[audiosources[button_audioid-1].data_id-1].locatortype==1)
                returnValue=((char*)((SLDataLocator_URI*)(audiosamples[audiosources[button_audioid-1].data_id-1].loc_fd))->URI)+7;
        }
    }
    return env->NewStringUTF(returnValue);
}