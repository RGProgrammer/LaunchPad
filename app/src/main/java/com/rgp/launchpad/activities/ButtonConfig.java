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

import javax.xml.transform.Result;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ButtonConfig extends Activity {
    Intent resultintent ;
    int previewAudioID = 0;
    int previewAudioData =0 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resultintent=new Intent();
        setContentView(R.layout.activity_button_config);

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
                SoundEngineInterface.stop(previewAudioID);
                SoundEngineInterface.deleteAudioDataSource(previewAudioData);
                SoundEngineInterface.deleteAudioplayer(previewAudioID);
                finish();
            }
        });

        ((Button)findViewById(R.id.Play)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SoundEngineInterface.play(previewAudioID);
            }
        });

        ((Button)findViewById(R.id.selection)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fileselect= new Intent("android.intent.action.FileSelect");
                startActivityForResult(fileselect,0);
            }
        });


    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== RESULT_OK && data != null){
            //delete old preview wdata preparing for the new data
            SoundEngineInterface.deleteAudioDataSource(previewAudioData);
            SoundEngineInterface.deleteAudioplayer(previewAudioID);
            previewAudioID = 0;
            previewAudioData =0 ;
            String Path= data.getExtras().getString("path");
            Toast.makeText(this,"path "+Path,Toast.LENGTH_SHORT).show();
            resultintent.putExtra("path",Path);
            if(SoundEngineInterface.isInitialized()) {
                try {
                    previewAudioData = SoundEngineInterface.createAudioDataSourceFromURI(Path.getBytes("UTF-8"), Path.length());
                    previewAudioID=SoundEngineInterface.createAudioPlayer(previewAudioID);
                    if(previewAudioData==0)
                        Toast.makeText(this,"cannot preview audio",Toast.LENGTH_LONG);
                    else{
                        Toast.makeText(this,"preview is ready",Toast.LENGTH_LONG).show();
                    }
                }catch ( Exception e){
                    Toast.makeText(this,"error decoding filename",Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this,"cannot preview audio",Toast.LENGTH_LONG).show();
            }
        }else{
            this.setResult(RESULT_CANCELED);

        }
    }

}
