
package com.medical.my_medicos.activities.cme.fragment;

        import static android.content.ContentValues.TAG;

        import android.annotation.SuppressLint;
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

        import com.medical.my_medicos.adapter.cme.MyAdapter3;
        import com.medical.my_medicos.R;
        import com.medical.my_medicos.adapter.cme.items.cmeitem3;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpcomingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpcomingFragmentReserved extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView3;
    String userPhone;
    String Cmeid;

    public UpcomingFragmentReserved() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UpcomingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpcomingFragment newInstance(String param1, String param2) {
        UpcomingFragment fragment = new UpcomingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);
        recyclerView3 = view.findViewById(R.id.cme_recyclerview_upcoming);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            userPhone = currentUser.getPhoneNumber();
        }


        FirebaseFirestore dc = FirebaseFirestore.getInstance();

        dc.collection("CMEsReserved")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> dataMap = document.getData();
                                Cmeid = (String) dataMap.get("documentidapply");
                                String user = (String) dataMap.get("User");
                                int r=userPhone.compareTo(user);
                                if (r==0){
                                    fetch(Cmeid);
                                }
                                else{

                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });



        return view;
    }
    void fetch( String Cmedid){
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<cmeitem3> item = new ArrayList<>();

    //.....
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        Query query=db.collection("CME").orderBy("Time", Query.Direction.DESCENDING);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            ((Query) query)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task ) {

                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    Log.d(TAG, document.getId() + " => " + document.getData());

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
                                        String usercme = ((String) dataMap.get("User"));

                                        String Date=((String) dataMap.get("Selected Date"));
                                        String time =((String) dataMap.get("Selected Time"));
                                        String documentid=((String) dataMap.get("documentId"));
                                        if (Cmeid != null && usercme != null) {
                                            int r3 = Cmeid.compareTo(documentid);
                                            if (r3 == 0) {


                                                cmeitem3 c = new cmeitem3(field1, field2, Date, field3, field4, 5, time, field5, "UPCOMING", documentid,field6);

                                                item.add(c);
                                            }
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
                                            String documentid=((String) dataMap.get("documentId"));
                                            String usercme = ((String) dataMap.get("User"));

                                            if (Cmeid != null && usercme != null) {
                                                int r3 = Cmeid.compareTo(documentid);
                                                if (r3 == 0) {


                                                    cmeitem3 c = new cmeitem3(field1, field2, Date, field3, field4, 5, time, field5, "UPCOMING", documentid,field6);

                                                    item.add(c);
                                                }
                                            }
                                        }


                                    }

                                }
                                recyclerView3.setLayoutManager(new LinearLayoutManager(getContext()));
                                recyclerView3.setAdapter(new MyAdapter3(getContext(), item));
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }
    }
}
