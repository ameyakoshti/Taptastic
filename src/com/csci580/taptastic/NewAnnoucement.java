package com.csci580.taptastic;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NewAnnoucement extends Fragment{
public NewAnnoucement(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.new_annoucement, container, false);
         
        return rootView;
    }
}


