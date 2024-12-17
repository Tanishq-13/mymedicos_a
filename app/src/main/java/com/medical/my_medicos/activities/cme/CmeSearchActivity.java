package com.medical.my_medicos.activities.cme;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;  // Import MenuItem
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.cme.fragment.OngoingFragmentSearch;
import com.medical.my_medicos.activities.cme.fragment.PastFragmentSearch;
import com.medical.my_medicos.activities.cme.fragment.UpcomingFragmentSearch;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.FirebaseFirestore;

public class CmeSearchActivity extends AppCompatActivity {
    String field1;
    String field2, email, Date, Time1, venue;
    String field3;

    String field4;
    FloatingActionButton floatingActionButton;
    RecyclerView recyclerView;
    private ViewPager2 pager, viewpager;
    RecyclerView recyclerView3;
    RecyclerView recyclerView2;
    RecyclerView recyclerView4;

    private TabLayout tabLayout;
    private Spinner specialitySpinner;
    private FirebaseFirestore db;
    private Spinner subspecialitySpinner;
    private ArrayAdapter<CharSequence> specialityAdapter;
    private ArrayAdapter<CharSequence> subspecialityAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    String Mode, Speciality, SubSpeciality;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_cme_search);
        viewpager = findViewById(R.id.view_pager1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }


        Log.d("123456", "5646");
        Speciality = getIntent().getExtras().getString("Speciality");
        SubSpeciality = getIntent().getExtras().getString("SubSpeciality");
        Mode = getIntent().getExtras().getString("Mode");
        Log.d(Speciality, "SubSpeciality");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle(Speciality); // Set the main title
        getSupportActionBar().setSubtitle(SubSpeciality);

        viewpager.setAdapter(new ViewPagerAdapter(this, Speciality, SubSpeciality, Mode));
        pager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tablayout);
        new TabLayoutMediator(tabLayout, viewpager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Ongoing");
                    break;
                case 1:
                    tab.setText("Upcoming");
                    break;
                case 2:
                    tab.setText("Past");
                    break;
            }
        }).attach();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Handle the back button press
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class ViewPagerAdapter extends FragmentStateAdapter {
        private String speciality;
        private String subSpeciality;
        private String mode;

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, String speciality, String subSpeciality, String mode) {
            super(fragmentActivity);
            this.speciality = speciality;
            this.subSpeciality = subSpeciality;
            this.mode = mode;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return OngoingFragmentSearch.newInstance(speciality, subSpeciality, mode);
                case 1:
                    return UpcomingFragmentSearch.newInstance(speciality, subSpeciality, mode);
                case 2:
                    return PastFragmentSearch.newInstance(speciality, subSpeciality, mode);
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}
