package com.example.bookofbooks;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomePosts extends Fragment {

    private onFragmentButtonSelected listener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_posts_fragment, container, false);
        Button fragmentButton = view.findViewById(R.id.fragment_button);
        fragmentButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                listener.onButtonSelected();
            }
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof onFragmentButtonSelected){
            listener = (onFragmentButtonSelected) context;
        }
    }

    public  interface onFragmentButtonSelected{
        public  void onButtonSelected();
    }
}
