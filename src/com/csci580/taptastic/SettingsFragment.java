package com.csci580.taptastic;

import java.io.File;
import java.io.FilenameFilter;

import com.csci580.taptastic.MainActivity.NfcMode;
import com.csci580.taptastic.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class SettingsFragment extends Fragment {
	
	public SettingsFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View rootView= inflater.inflate(R.layout.fragment_settings_professor, container, false);;
		if(LoginActivity.appMode.equals("Professor")){
			rootView = inflater.inflate(R.layout.fragment_settings_professor, container, false);
			Button b2=(Button) rootView.findViewById(R.id.button1);
			b2.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					MainActivity.nfcMode=NfcMode.WRITE;
					Toast.makeText(getActivity(), "Tap NFC Tag",10).show();
				}
			});
		}
		else{
			rootView = inflater.inflate(R.layout.fragment_settings_students, container, false);
			ImageButton b1= (ImageButton) rootView.findViewById(R.id.imageButton1);
			b1.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					rootDir= "/storage/sdcard0/";
					mPath = new File("/storage/sdcard0/");
					loadFileList();
					onCreateDialog(0).show();
					
				}
			});
		}
        return rootView;
    }
	private String[] mFileList;
	private String rootDir= "/storage/sdcard0/";
	private File mPath = new File("/storage/sdcard0/");
	private String mChosenFile;
	private static final String FTYPE = ".txt";    
	private static final int DIALOG_LOAD_FILE = 1000;

	private void loadFileList() {
	    try {
	        mPath.mkdirs();
	    }
	    catch(SecurityException e) {
	        
	    }
	    if(mPath.exists()) {
	        FilenameFilter filter = new FilenameFilter() {
	            public boolean accept(File dir, String filename) {
	                File sel = new File(dir, filename);
	                return filename.contains(FTYPE) || sel.isDirectory();
	            }
	        };
	        mFileList = mPath.list(filter);
	    }
	    else {
	        mFileList= new String[0];
	    }
	}
	protected Dialog onCreateDialog(int id) {
	    Dialog dialog = null;
	    AlertDialog.Builder builder = new Builder(getActivity());

	    switch(id) {
	        case 0:
	            builder.setTitle("Choose your file");
	            if(mFileList == null) {
	                dialog = builder.create();
	                return dialog;
	            }
	            builder.setItems(mFileList, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                    mChosenFile = mFileList[which];
	                    rootDir+=mChosenFile+"/";
	                    mPath=new File(rootDir);
	                    loadFileList();
	                    onCreateDialog(0);
	                    //you can do stuff with the file here too
	                }
	            });
	            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						Toast.makeText(getActivity(), rootDir, mChosenFile.length()).show();
						arg0.dismiss();
					}
				});
	            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						arg0.dismiss();
					}
				});
	            break;
	    }
	    dialog = builder.show();
	    return dialog;
	}
}
