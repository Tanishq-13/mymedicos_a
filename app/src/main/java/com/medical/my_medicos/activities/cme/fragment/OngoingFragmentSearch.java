package com.medical.my_medicos.activities.cme.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.medical.my_medicos.R;
import com.medical.my_medicos.adapter.cme.MyAdapter1;
import com.medical.my_medicos.adapter.cme.items.cmeitem4;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OngoingFragmentSearch extends Fragment {
    RecyclerView recyclerView;

    public static OngoingFragmentSearch newInstance(String speciality, String subSpeciality, String mode) {
        OngoingFragmentSearch fragment = new OngoingFragmentSearch();
        Bundle args = new Bundle();
        args.putString("Speciality", speciality);
        args.putString("SubSpeciality", subSpeciality);
        args.putString("Mode", mode);
        fragment.setArguments(args);
        return fragment;
    }
    String speciality,subSpeciality,mode;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ongoing, container, false);
        recyclerView = view.findViewById(R.id.cme_recyclerview_today);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            speciality = getArguments().getString("Speciality");
            subSpeciality = getArguments().getString("SubSpeciality");
            mode = getArguments().getString("Mode");
        }




        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Query query=db.collection("CME").orderBy("Time", Query.Direction.DESCENDING);

            query.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task ) {
                            List<cmeitem4> items1 = new ArrayList<>();

                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    Map<String, Object> dataMap = document.getData();
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                                    DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                    String Time = document.getString("Selected Time");
                                    String date =  document.getString("Selected Date");

                                    //
                                    LocalTime parsedTime = null;
                                    LocalDate parsedDate=null;
                                    try {
                                        // Parse the time string into a LocalTime object
                                        parsedTime = null;
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            parsedTime = LocalTime.parse(Time, formatter);
                                            parsedDate = LocalDate.parse(date, formatter1);

                                        }

                                        // Display the parsed time
                                        System.out.println("Parsed Time: " + parsedTime);
                                    } catch (DateTimeParseException e) {
                                        // Handle parsing error, e.g., if the input string is in the wrong format
                                        System.err.println("Error parsing time: " + e.getMessage());

                                    }
                                    LocalTime currentTime = null;
                                    LocalDate currentDate = null;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        currentTime = LocalTime.now();
                                        currentDate = LocalDate.now();
                                    }


                                    int r = parsedTime.compareTo(currentTime);

                                    Log.d("vivek", String.valueOf(r));
                                    int r1 = parsedDate.compareTo(currentDate);
                                    if ((r <= 0) && (r1 == 0)) {
                                        String field3 = ((String) dataMap.get("CME Title"));
                                        String field6 = ((String) dataMap.get("Mode"));
                                        List<String> presenters = (List<String>) dataMap.get("CME Presenter");
                                        String field4=presenters.get(0);
                                        String end=((String) dataMap.get("endtime"));
                                        String field1 = (String) dataMap.get("CME Organiser");
                                        String field2;
                                        field2 = ((String) dataMap.get("Speciality"));
                                        String subspeciality=((String) dataMap.get("SubSpeciality"));
                                        String mode=((String) dataMap.get("Mode"));
                                        String Date=((String) dataMap.get("Selected Date"));
                                        String time =((String) dataMap.get("Selected Time"));
                                        String field5=((String ) dataMap.get("User"));
                                        String documentid=((String) dataMap.get("documentId"));
                                        int r4=speciality.compareTo(field2);
                                        int r3=subspeciality.compareTo(subspeciality);
                                        int r2=mode.compareTo(mode);
                                        if (end==null) {
                                            if ((r2==0)&&(r3==0)&&(r4==0)) {
                                                cmeitem4 c = new cmeitem4(field1, field2, Date, field3, field4, 5, time, field5, "LIVE", documentid,field6);
                                                items1.add(c);
                                            }
                                        }



                                    } else {

                                    }


                                }
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true));
                                recyclerView.setAdapter(new MyAdapter1(getContext(), items1));
                            } else {
                                Log.d(FragmentManager.TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }

        return view;
    }
}
