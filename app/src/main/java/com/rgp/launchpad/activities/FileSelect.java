package com.rgp.launchpad.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.rgp.launchpad.launchpad.R ;
import java.io.File;
import java.util.ArrayList;
import android.content.Intent;

public class FileSelect extends Activity implements AdapterView.OnItemClickListener {
    private File currentfile;
    private String InternalStoragePath =null;
    private String ExternalStoragePath =null;
    private String  path="" ;
    ListView  list ;
    ListLayout mylayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_select);
        list=((ListView)findViewById(R.id.listview));
        ArrayList<String> titles=new ArrayList<String>();
        InternalStoragePath= Environment.getExternalStorageDirectory().getPath();
        titles.add("Internal");
        File external=this.getExternalFilesDir(null);
        if(external.list().length>0){
            ExternalStoragePath=external.getPath();
            titles.add("SD Card");
        }

        mylayout= new ListLayout(this,R.layout.row,titles);
        list.setAdapter(mylayout);
        list.setOnItemClickListener(this);
    }

    private class ListLayout extends ArrayAdapter<String>
    {
        private ArrayList<String> Titles ;

        private int folderIcon;
        private int sdcardIcon ;
        private int audioIcon ;
        private int otherIcon ;
        public ListLayout(@NonNull Context context, int resource, ArrayList<String> titles) {
            super(context, resource,R.id.title,titles);
            Titles=titles;
            folderIcon=R.drawable.folder ;
            sdcardIcon=R.drawable.sdcard ;
            audioIcon=R.drawable.audio;
            otherIcon=R.drawable.other;
        }
        public void UpdateList(String[]titles){
            super.clear();
            Titles.clear();
            for(int i=0 ; i<titles.length ; ++i) {
                Titles.add(titles[i]);
            }
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) this.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.row, parent, false);
            TextView title = (TextView) rowView.findViewById(R.id.title);
            ImageView icon = (ImageView) rowView.findViewById(R.id.icon);
            title.setText(getItem(position));

            if(path=="") {
                if (position == 0)
                    icon.setImageResource(folderIcon);
                else if (position == 1)
                    icon.setImageResource(sdcardIcon);
            }else{
                String tmp = path+"/"+getItem(position);
                File current=new File(tmp);
                if(current.isDirectory())
                    icon.setImageResource(folderIcon);
                else if(current.isFile()){
                    if(current.getName().endsWith(".mp3") || current.getName().endsWith(".wav") )
                        icon.setImageResource(audioIcon);
                    else
                        icon.setImageResource(otherIcon);
                }
            }
            return rowView;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
           String tmp ;
            if(path==""){
                if(position==0){
                    path= InternalStoragePath;
                }else if(position==1){
                    path= ExternalStoragePath;
                }
            }else{
                path=path+"/"+mylayout.getItem(position);
            }
            File file= new File(path);
            if(file!=null)
                if(file.isDirectory()){
                    String[] files =file.list();
                     mylayout.UpdateList(files);
                     mylayout.notifyDataSetChanged();
                 }else{
                    if(file.getName().endsWith(".mp3")|| file.getName().endsWith(".wav")){
                        Intent res= new Intent();
                        path.replace("/","//");
                        res.putExtra("path",path);
                        setResult(RESULT_OK,res);
                        finish();
                    }
                 }
    }

    @Override
    public void onBackPressed() {
        if(path==""){
             setResult(RESULT_CANCELED);
            finish();
        }

        if(path== InternalStoragePath ||
                ExternalStoragePath== path){
            path="";
            mylayout.clear();
            mylayout.add("Internal");
            if(ExternalStoragePath!=null){
                mylayout.add("SD Card");
            }
            mylayout.notifyDataSetChanged();
        }else{
            File file= new File(path);
            File Parent = file.getParentFile();
            if(Parent!=null){
                path=Parent.getPath();
                if(path.contains(InternalStoragePath)== true ||
                        (ExternalStoragePath !=null &&path.contains( ExternalStoragePath)== true)){
                    String[] files = Parent.list();
                    mylayout.UpdateList(files);
                    mylayout.notifyDataSetChanged();
                }else {
                    path="";
                    mylayout.clear();
                    mylayout.add("Internal");
                    if(ExternalStoragePath!=null){
                        mylayout.add("SD Card");
                    }
                    mylayout.notifyDataSetChanged();
                }
            }
        }

    } 
} 

