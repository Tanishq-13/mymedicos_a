package com.example.my_medicos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class CmeActivity extends AppCompatActivity {


    FloatingActionButton floatingActionButton;
    RecyclerView recyclerView;
    RecyclerView recyclerView3;

    RecyclerView recyclerView2;

    private ViewPager2 pager;
    private TabLayout tabLayout;

    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cme);

        toolbar = findViewById(R.id.cmetoolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        floatingActionButton = findViewById(R.id.floatingActionButton);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CmeActivity.this, PostCmeActivity.class);
                startActivity(i);
            }
        });

        recyclerView = findViewById(R.id.cme_recyclerview1);


        List<cmeitem1> items = new ArrayList<>();

        items.add(new cmeitem1("John wick", "Dentist", R.drawable.img_2));
        items.add(new cmeitem1("Robert j", "Pediatrics", R.drawable.img_3));
        items.add(new cmeitem1("James Gunn", "Cardiologist", R.drawable.img_4));
        items.add(new cmeitem1("Ricky tales", "Surgeon", R.drawable.img_5));

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new MyAdapter2(this, items));


        recyclerView3 = findViewById(R.id.recyclerview3);

        List<cmeitem3> item = new ArrayList<>();

        item.add(new cmeitem3("25th AUG", "1:30pm", "abcdefgh", "John wick"));
        item.add(new cmeitem3("26th AUG", "12:30pm", "anscncdc", "Robert k"));
        item.add(new cmeitem3("27th AUG", "10:30pm", "cncncnc", "James Gunn"));
        item.add(new cmeitem3("28th AUG", "10:30pm", "mccncnsocn", "Ricky tales"));

        recyclerView3.setLayoutManager(new LinearLayoutManager(this));
        recyclerView3.setAdapter(new MyAdapter3(this, item));


        recyclerView2 = findViewById(R.id.recyclerview2);

        List<cmeitem2> myitem = new ArrayList<cmeitem2>();

        myitem.add(new cmeitem2("John wick", "ESI Hospital", "Peitiric"));
        myitem.add(new cmeitem2("Robert j", "Shushrta Hospital", "Peitiric"));
        myitem.add(new cmeitem2("James Gunn", "KMC", "Peitiric"));
        myitem.add(new cmeitem2("Ricky tales", "Manipal Hospital", "Peitiric"));

        recyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView2.setAdapter(new MyAdapter4(this, myitem));


        pager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabLayout);

        Spinner spinner = (Spinner) findViewById(R.id.speciality);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.speciality, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        Spinner spinner2 = (Spinner) findViewById(R.id.mode);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> myadapter = ArrayAdapter.createFromResource(this,
                R.array.mode, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner2.setAdapter(myadapter);

        pager.setAdapter(new ViewPagerAdapter(this));

        new TabLayoutMediator(tabLayout, pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Todays");
                        break;
                    case 1:
                        tab.setText("Upcoming");
                        break;
                    case 2:
                        tab.setText("Past");
                        break;
                }
            }
        }).attach();
    }

    class ViewPagerAdapter extends FragmentStateAdapter {

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }


        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new TodaysFragment();
                case 1:
                    return new UpcomingFragment();
                case 2:
                    return new PastFragment();
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.chat) {
            Toast.makeText(this, "Chat clicked", Toast.LENGTH_SHORT).show();
        }
        else{
                Intent i = new Intent(CmeActivity.this, HomeActivity.class);
                startActivity(i);
            }

            return super.onOptionsItemSelected(item);
        }


    }
