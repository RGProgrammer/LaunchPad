package com.rgp.launchpad.activities;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent ;
import android.widget.RadioButton;
import android.widget.Toast;

import com.rgp.launchpad.classes.LaunchButtonConfig;
import com.rgp.launchpad.classes.SoundEngineInterface;
import com.rgp.launchpad.launchpad.R;

import java.io.UnsupportedEncodingException;

import javax.xml.transform.Result;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ButtonConfig extends Activity {
    Intent resultintent ;
    static int sample ;
    static int audioPlayer ;
    Button previewbutton ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resultintent=new Intent();
        setContentView(R.layout.activity_button_config);
        previewbutton= ((Button)findViewById(R.id.preview));
        previewbutton.setActivated(false);
        ((Button)findViewById(R.id.confirm)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=ButtonConfig.this.getIntent();
                int mode =0 ;
                if(((RadioButton)ButtonConfig.this.findViewById(R.id.simplemode)).isChecked()==true)
                    mode= LaunchButtonConfig.SIMPLEMODE;
                else if(((RadioButton)ButtonConfig.this.findViewById(R.id.holdmode)).isChecked()==true)
                    mode= LaunchButtonConfig.HOLDMODE;
                else if(((RadioButton)ButtonConfig.this.findViewById(R.id.loopmode)).isChecked()==true)
                    mode= LaunchButtonConfig.LOOPMODE;

                resultintent.putExtra("mode",mode);
                setResult(RESULT_OK, resultintent);
                SoundEngineInterface.deleteAudioDataSource(sample);
                sample=0 ;
                SoundEngineInterface.deleteAudioplayer(audioPlayer);
                audioPlayer=0 ;
                finish();
            }
        });


        ((Button)findViewById(R.id.selection)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fileselect= new Intent("android.intent.action.FileSelect");
                startActivityForResult(fileselect,0);
            }
        });

        ((Button)findViewById(R.id.preview)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(audioPlayer!=0){
                   if(SoundEngineInterface.isStopped(audioPlayer))
                       SoundEngineInterface.play(audioPlayer);
                   else
                       SoundEngineInterface.stop(audioPlayer);
               }
            }
        });

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
        Toast.makeText(this,"checking result ",Toast.LENGTH_LONG).show();
        if(resultCode== RESULT_OK && data != null){
            SoundEngineInterface.deleteAudioDataSource(sample);
            sample=0 ;
            SoundEngineInterface.deleteAudioplayer(audioPlayer);
            audioPlayer=0 ;
            String path= data.getExtras().getString("path");
            Toast.makeText(this,"path "+path,Toast.LENGTH_LONG).show();
            resultintent.putExtra("path",path);

            try {
                sample = SoundEngineInterface.createAudioDataSourceFromURI(path.getBytes("UTF-8"),path.length());
                audioPlayer=SoundEngineInterface.createAudioPlayer(sample);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if(audioPlayer!=0)
                previewbutton.setActivated(true);
            else
                previewbutton.setActivated(false);

        }else{
            this.setResult(RESULT_CANCELED);

        }
    }

}
