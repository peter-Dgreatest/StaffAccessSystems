package com.itcrusaders.staffaccesssystems;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlusOneFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
            //return super.onCreateView(inflater, container, savedInstanceState);
            View view = inflater.inflate(R.layout.fragment_plus_one,container,false);

            return view;
    }
}