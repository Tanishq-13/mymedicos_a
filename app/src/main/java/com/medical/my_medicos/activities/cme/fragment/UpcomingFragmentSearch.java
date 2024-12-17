package com.medical.my_medicos.activities.cme.fragment;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.medical.my_medicos.R;
import com.medical.my_medicos.adapter.cme.MyAdapter3;
import com.medical.my_medicos.adapter.cme.items.cmeitem3;
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

public class UpcomingFragmentSearch extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView3;

    public UpcomingFragmentSearch() {
        // Required empty public constructor
    }




    public static UpcomingFragment newInstance(String param1, String param2) {
        UpcomingFragment fragment = new UpcomingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public static UpcomingFragmentSearch newInstance(String speciality, String subSpeciality, String mode) {
        UpcomingFragmentSearch fragment = new UpcomingFragmentSearch();
        Bundle args = new Bundle();
        args.putString("Speciality", speciality);
        args.putString("SubSpeciality", subSpeciality);
        args.putString("Mode", mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    String Speciality,SubSpeciality,Mode;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);
        recyclerView3 = view.findViewById(R.id.cme_recyclerview_upcoming);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<cmeitem3> item = new ArrayList<>();
        if (getArguments() != null) {
            Speciality = getArguments().getString("Speciality");
            SubSpeciality = getArguments().getString("SubSpeciality");
            Mode = getArguments().getString("Mode");
        }

        //.....
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Query query3=db.collection("CME").orderBy("Time", Query.Direction.DESCENDING);
            ((Query) query3)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task ) {

                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    Log.d(ContentValues.TAG, document.getId() + " => " + document.getData());

                                    Map<String, Object> dataMap = document.getData();
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                                    DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                    String Time = document.getString("Selected Time");
                                    String date =  document.getString("Selected Date");

//
                                    LocalTime parsedTime = null;
                                    LocalDate parsedDate = null;
                                    try {
                                        // Parse the time string into a LocalTime object
                                        parsedTime = null;
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            parsedTime = LocalTime.parse(Time, formatter);



                                            Log.d("vivek", "0");
                                        }

                                        // Display the parsed time
                                        System.out.println("Parsed Time: " + parsedTime);
                                    } catch (DateTimeParseException e) {
                                        // Handle parsing error, e.g., if the input string is in the wrong format
                                        System.err.println("Error parsing time: " + e.getMessage());
                                        Log.d("vivek", "Time");
                                    }
                                    parsedDate = LocalDate.parse(date, formatter1);
                                    LocalTime currentTime = null;
                                    LocalDate currentDate = null;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        currentTime = LocalTime.now();
                                        currentDate = LocalDate.now();
                                    }


                                    int r = parsedTime.compareTo(currentTime);
                                    int r1 = parsedDate.compareTo(currentDate);

                                    Log.d("vivek1", String.valueOf(r1));
                                    if ((r1>0)) {
                                        String field3 = ((String) dataMap.get("CME Title"));
                                        String field6 = ((String) dataMap.get("Mode"));
                                        List<String> presenters = (List<String>) dataMap.get("CME Presenter");
                                        String field4=presenters.get(0);
                                        String field1 = (String) dataMap.get("CME Organiser");
                                        String field2;
                                        field2 = ((String) dataMap.get("Speciality"));
                                        String field5=((String ) dataMap.get("User"));
                                        String Date=((String) dataMap.get("Selected Date"));
                                        String time =((String) dataMap.get("Selected Time"));
                                        String subspeciality=((String) dataMap.get("SubSpeciality"));
                                        String mode=((String) dataMap.get("Mode"));
                                        String documentid=((String) dataMap.get("documentId"));

                                        cmeitem3 c = new cmeitem3(field1, field2, Date, field3, field4,5,time,field5,"UPCOMING",documentid,field6);


                                        int r4=Speciality.compareTo(field2);
                                        int r3=SubSpeciality.compareTo(subspeciality);
                                        int r2=Mode.compareTo(mode);

                                        if ((r2==0)&&(r3==0)&&(r4==0)) {
                                            item.add(c);
                                        }



                                    } else {
                                        if ((r>0)&&(r1==0)){
                                            String field3 = ((String) dataMap.get("CME Title"));
                                            String field6 = ((String) dataMap.get("Mode"));
                                            List<String> presenters = (List<String>) dataMap.get("CME Presenter");
                                            String field4=presenters.get(0);
                                            String field1 = (String) dataMap.get("CME Organiser");
                                            String field2;
                                            field2 = ((String) dataMap.get("Speciality"));
                                            String Date=((String) dataMap.get("Selected Date"));
                                            String time =((String) dataMap.get("Selected Time"));
                                            String field5=((String ) dataMap.get("User"));
                                            String subspeciality=((String) dataMap.get("SubSpeciality"));
                                            String mode=((String) dataMap.get("Mode"));
                                            String documentid=((String) dataMap.get("documentId"));

                                            cmeitem3 c = new cmeitem3(field1, field2, Date, field3, field4,5,time,field5,"UPCOMING",documentid,field6);

                                            int r4=Speciality.compareTo(field2);
                                            int r3=SubSpeciality.compareTo(subspeciality);
                                            int r2=Mode.compareTo(mode);

                                            if ((r2==0)&&(r3==0)&&(r4==0)) {
                                                item.add(c);
                                            }

                                        }


                                    }

                                }
                                recyclerView3.setLayoutManager(new LinearLayoutManager(getContext()));
                                recyclerView3.setAdapter(new MyAdapter3(getContext(), item));
                            } else {
                                Log.d(ContentValues.TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }

        return view;
    }
}
