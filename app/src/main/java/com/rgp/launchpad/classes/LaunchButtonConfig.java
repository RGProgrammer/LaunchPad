package com.rgp.launchpad.classes;
import android.app.Activity;
import android.app.LauncherActivity;
import android.os.Environment;
import android.util.Xml;
import android.widget.Button;
import android.widget.Toast;

import com.rgp.launchpad.launchpad.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class LaunchButtonConfig {
    public static final int         SIMPLEMODE = 0;
    public static final int         HOLDMODE   =1 ;
    public static final int         LOOPMODE   =2;


    private int pageid;
    private int buttonRef;
    private int audioid;
    private int mode ;

    //STATIC
    public  static int PRESSCOLOR;
    public  static int RELEASECOLOR ;
    public static ArrayList<Integer> ButtonIds = new ArrayList<Integer>();
    public static ArrayList<LaunchButtonConfig> list=new ArrayList<LaunchButtonConfig>();
    public static LaunchButtonConfig getButtonConfig(int button_id,int page_id)
    {
        int wrapper=0,i=0 ;
        for(; i<ButtonIds.size();++i) {
            if (button_id == ButtonIds.get(i)) {
                wrapper = i;
                break;
            }
        }
        i=0;
        for(; i< LaunchButtonConfig.list.size();i++)
            if(LaunchButtonConfig.list.get(i).getButtonref()==wrapper && LaunchButtonConfig.list.get(i).getPageID()==page_id)
                return LaunchButtonConfig.list.get(i);
        return null ;
    }

    public static boolean AddConfig(int button,int page_id, int audio_id, int mode){
        LaunchButtonConfig c = new LaunchButtonConfig(button,page_id,audio_id);
        if(c!=null) {
            c.setMode(mode);
            list.add(c);
            return true ;

        }
        return false ;
    }

    public static boolean ExportConfigtoFile()
    {
        XmlSerializer serializer = Xml.newSerializer();
        Writer writer ;
        String path= Environment.getExternalStorageDirectory().getPath() + "/Audio/launchpadconig.xml";
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            String Path ;
            for (int i = 0; i < list.size(); i++) {
                Path=null;
                serializer.startTag("", "buttonconfig");
                serializer.attribute("", "buttonID", String.valueOf(LaunchButtonConfig.list.get(i).buttonRef));
                serializer.attribute("", "pageID", String.valueOf(LaunchButtonConfig.list.get(i).pageid));
                Path=SoundEngineInterface.getPath(list.get(i).audioid);
                if(Path!=null)
                    serializer.attribute("", "path",Path );
                serializer.attribute("", "mode", String.valueOf(LaunchButtonConfig.list.get(i).mode));
                serializer.endTag("", "buttonconfig");

            }
            serializer.endDocument();
            writer.close();

        }catch(Exception e){
            return false ;
        }
        return true ;
    }
    public static boolean ImportConfigfromFile()
    {
        XmlPullParserFactory factory ;
        InputStream in ;
        int eventType ;
        //ClearCurrentConfig();
        try {
            //opening and reading config file content
           in= new FileInputStream(Environment.getExternalStorageDirectory().getPath() + "/Audio/launchpadconig.xml");
            if (in == null)
                return false;
            //parsing content and adding config
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(in,"UTF-8");
            eventType = parser.getEventType();
            if (eventType != XmlPullParser.START_DOCUMENT)
                //not a valid xml file
                return false;
            int Buttonid = 0;
            int Pageid = 0;
            int Mode = 0;
            boolean tag=false ;
            String Path = null;
            while ((eventType = parser.getEventType()) != XmlPullParser.END_DOCUMENT ) {

                if (eventType == XmlPullParser.START_TAG) {
                    tag=true;
                    if (parser.getName().equals("buttonconfig")==true) {
                        Buttonid = Integer.parseInt(parser.getAttributeValue("", "buttonID"));
                        Pageid = Integer.parseInt(parser.getAttributeValue("", "pageID"));
                        Path = parser.getAttributeValue("", "path");
                        Mode = Integer.parseInt(parser.getAttributeValue("", "mode"));

                        int sample = 0;
                        if (Path != null && Path.equals("") == false) {
                            sample = SoundEngineInterface.createAudioDataSourceFromURI(Path.getBytes("UTF-8"), Path.length());
                        }
                        int audio = SoundEngineInterface.createAudioPlayer(sample);

                        if(LaunchButtonConfig.AddConfig(Buttonid,Pageid,audio,Mode)==false)
                            return false ;

                    }
                }
                parser.next();
            }
            return true ;
        }catch(Exception e){
            return false ;
        }
    }

    public static void ClearCurrentConfig()
    {
        int id ;
        for(int i =0 ; i< LaunchButtonConfig.list.size();i++){
            id= LaunchButtonConfig.list.get(i).audioid ;
            SoundEngineInterface.deleteAudioDataSource(id);
            SoundEngineInterface.deleteAudioplayer(id);
        }
        LaunchButtonConfig.list.clear();
    }

    //
    public LaunchButtonConfig(int button,int page_id){
        this(button,page_id,0);
    }
    public LaunchButtonConfig(int button,int page_id, int audio_id){
        this.buttonRef=button ;
        this.pageid= page_id ;
        this.audioid= audio_id ;
        mode=SIMPLEMODE;

    }
    public void setAudioID(int id){
        audioid=id;
    }
    public void setMode(int mode){
        if(mode>=0 && mode<=2)
            this.mode=mode ;
    }
    public int getMode(){
        return mode ;
    }
    public int getAudioID(){
        return this.audioid ;
    }
    public int getButtonref(){
        return this.buttonRef;
    }
    public int getPageID(){
        return this.pageid;
    }
}
