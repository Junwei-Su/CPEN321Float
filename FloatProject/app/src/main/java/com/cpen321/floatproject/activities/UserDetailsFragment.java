package com.cpen321.floatproject.activities;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.cpen321.floatproject.R;

/**
 * Created by sfarinas on 12/28/2016.
 */
public class UserDetailsFragment extends Fragment {
    View view; //this fragment

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //inflate the layout for this fragment
        view = inflater.inflate(R.layout.user_details_fragment, container, false);

        ((ImageButton)view.findViewById(R.id.user_details_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(view.getContext(), UserDetails.class);
                startActivity(intent);
            }
        });

        return view;

    }
}
