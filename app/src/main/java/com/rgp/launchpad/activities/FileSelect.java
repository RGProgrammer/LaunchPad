package com.rgp.launchpad.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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
    private String  path="" ;
    ListView  list ;
    ListLayout mylayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_select);
        list=((ListView)findViewById(R.id.listview));
        ArrayList<String> titles=new ArrayList<String>();
        titles.add("Internal");
        titles.add("SD Card");

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
                    if(current.getName().endsWith(".mp3"))
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
                    path="/storage/emulated/0";
                }else if(position==1){
                    path= "/storage/sdcard0";
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
                    if(file.getName().endsWith("mp3")){
                        Intent res= new Intent();
                        res.putExtra("path",path);
                        setResult(RESULT_OK,res);
                        finish();
                    }
                 }
    }
}

