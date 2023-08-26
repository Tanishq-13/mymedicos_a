package com.example.my_medicos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    ImageView jobs,cme,news,publication,update,pg_prep,ugexams;
    MyAdapter adapter;
    RecyclerView recyclerView;


    Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);


        toolbar=rootView.findViewById(R.id.toolbar);


        recyclerView = rootView.findViewById(R.id.recyclerview_job1);

        ugexams=rootView.findViewById(R.id.ugexams);
        ugexams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), UgexamsActivity.class);
                startActivity(i);
            }
        });

        pg_prep=rootView.findViewById(R.id.pg_prep);
        pg_prep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(getActivity(), PgprepActivity.class);
                startActivity(i);
            }
        });

        update=rootView.findViewById(R.id.university);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(getActivity(), UniversityupdatesActivity.class);
                startActivity(i);
            }
        });

        cme=rootView.findViewById(R.id.cme_img1);

        cme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(), CmeActivity.class);
                startActivity(i);
            }
        });

        jobs = rootView.findViewById(R.id.jobs_img);

        jobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), JobsActivity.class);
                startActivity(i);
            }
        });

        news=rootView.findViewById(R.id.news_home);
        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(), NewsActivity.class);
                startActivity(i);
            }
        });


        List<jobitem> joblist = new ArrayList<jobitem>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false));

        joblist.add(new jobitem("Dentist", "ESI hospital", "Hubli"));
        joblist.add(new jobitem("Surgen", "Shushruta hospital", "Hubli"));
        joblist.add(new jobitem("Gynacologist", "Tatvadarshi hospital", "Hubli"));
        joblist.add(new jobitem("Pediatric", "KMC", "Hubli"));

        adapter = new MyAdapter(getContext(),joblist); // Pass the joblist to the adapter
        recyclerView.setAdapter(adapter);

        publication=rootView.findViewById(R.id.pub_image);
        publication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(),PublicationsActivity.class);
                startActivity(i);
            }
        });


        return rootView;



    }


}
