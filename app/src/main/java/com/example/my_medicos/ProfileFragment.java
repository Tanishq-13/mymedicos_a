package com.example.my_medicos;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment {


    RecyclerView recyclerView;

    MyAdapter6 adapter6;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        recyclerView = rootView.findViewById(R.id.proile_recyclerview);

        List<profileitem> profile = new ArrayList<profileitem>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false));


       profile.add(new profileitem("Educational\nInfo"));
        profile.add(new profileitem("Professional\nInfo"));
        profile.add(new profileitem("Person\nInfo"));
        profile.add(new profileitem("Contact\nInfo"));

        adapter6 = new MyAdapter6(getContext(),profile);
        recyclerView.setAdapter(adapter6);
        return rootView;
    }
}