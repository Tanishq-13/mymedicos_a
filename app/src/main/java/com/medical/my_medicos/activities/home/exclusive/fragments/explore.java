package com.medical.my_medicos.activities.home.exclusive.fragments;

//import static com.medical.my_medicos.activities.neetss.activites.internalfragments.preprationinternalfragments.ClinicalPgPrep.ARG_PARAM1;
//import static com.medical.my_medicos.activities.neetss.activites.internalfragments.preprationinternalfragments.ClinicalPgPrep.ARG_PARAM2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.home.exclusive.adapter.courseadapter;
import com.medical.my_medicos.activities.home.exclusive.adapter.exclusiveadapter;
import com.medical.my_medicos.activities.home.exclusive.adapter.instructoradapter;
import com.medical.my_medicos.activities.home.exclusive.model.CourseCard;
import com.medical.my_medicos.activities.home.exclusive.model.Instructor;
import com.medical.my_medicos.activities.utils.ConstantsDashboard;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class explore extends Fragment {

    private RecyclerView recyclerView,recyclerView2,recyclerView3;
    private courseadapter adapter;
    private instructoradapter adapter2;
    private exclusiveadapter adapter3;

    private List<CourseCard> courseCardList;
    private List<Instructor> instructorList;
    private List<CourseCard> exclusiveList;

    public explore() {
        // Required empty public constructor
    }

    public static explore newInstance(String param1, String param2) {
        explore fragment = new explore();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView2=view.findViewById(R.id.recyclerview2);
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView3 = view.findViewById(R.id.recyclerview3);

        courseCardList = new ArrayList<>();
        instructorList = new ArrayList<>();
        exclusiveList=new ArrayList<>();
        adapter = new courseadapter(getContext(), courseCardList);
        adapter2=new instructoradapter(getContext(),instructorList);
        adapter3=new exclusiveadapter(getContext(),exclusiveList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setAdapter(adapter);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        recyclerView2.setAdapter(adapter2);
        recyclerView3.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        recyclerView3.setAdapter(adapter3);
        fetchInstructors();
        fetchFeaturedCourses();
        fetchExclusiveCourses();
    }

    private void fetchInstructors() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("MentorRegistration");

        collectionRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    // Initialize the instructorList if it is null
                    if (instructorList == null) {
                        instructorList = new ArrayList<>();  // Initialize the list manually
                    } else {
                        instructorList.clear();  // Clear the list before adding new data
                    }

                    // Loop through all the documents in the querySnapshot
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        // Manually fetch each field from the document
                        String bio = document.getString("Bio");
                        String docId = document.getString("DocID");
                        String email = document.getString("Email ID");
                        String interest1 = document.getString("Interest");
                        String interest2 = document.getString("Interest2");
                        String location = document.getString("Location");
                        String mcn = document.getString("MCN");
                        String name = document.getString("Name");
                        String phoneNumber = document.getString("Phone Number");
                        String prefix = document.getString("Prefix");
                        String profile = document.getString("Profile");
                        boolean verified = document.getBoolean("Verified");
                        String aadharNumber = document.getString("aadharnumber");
                        boolean exclusive = document.getBoolean("exclusive");
                        String msgToStudents = document.getString("msg_to_students");
                        Log.d("dfgh",name+" "+interest1);
                        // Manually create an Instructor object using the fetched fields
                        Instructor instructor = new Instructor();
                        instructor.setBio(bio);
                        instructor.setDocId(docId);
//                        instructor.setEmail(email);
                        instructor.setInterest1(interest1);
                        instructor.setInterest2(interest2);
                        instructor.setLocation(location);
                        instructor.setMcn(mcn);
                        instructor.setName(name);
                        instructor.setPhoneNumber(phoneNumber);
                        instructor.setPrefix(prefix);
                        instructor.setProfile(profile);
                        instructor.setVerified(verified);
                        instructor.setAadharNumber(aadharNumber);
                        instructor.setExclusive(exclusive);
                        instructor.setMsgToStudents(msgToStudents);

                        // Add the instructor object to the list
                        instructorList.add(instructor);
                    }

                    // Notify the adapter about the data change
                    if (adapter2 != null) {
                        adapter2.notifyDataSetChanged();
                    }

                })
                .addOnFailureListener(e -> {
                    // Handle failure (e.g., log error or show a message)
                    Log.e("fetchInstructors", "Error getting documents: ", e);
                });
    }



    private void fetchFeaturedCourses() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("Exclusive_Course");

        collectionRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    courseCardList.clear(); // Clear the list before adding new data
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Log.d("lop", String.valueOf(document));

                        // Manually fetch each field
                        String coverUrl = document.getString("cover");
                        String description = document.getString("description");
                        String docId = document.getString("instructorId");
                        boolean isFeatured = document.getBoolean("featured");
                        String lang = document.getString("lang");
                        String lastUpdated =("last_updated");
                        String name = document.getString("name");
                        boolean premiumStatus = document.getBoolean("premium_status");
                        long ratedBy = document.getLong("rated_by");
                        String ratingAvg = document.getString("rating_avg");
                        String subject = document.getString("subject");
                        String title = document.getString("title");
                        String crsid=document.getId();
                        // Create the CourseCard object and add to the list if it's featured
                        Log.d("lllll",docId);
                        if (isFeatured) {
                            CourseCard course = new CourseCard(
                                    coverUrl, description, docId, isFeatured, lang, lastUpdated,
                                    name, premiumStatus, ratedBy, ratingAvg, subject, title
                            );
//                            Log.d("lllll",course.getDocId());

                            course.setCourseId(crsid);
                            courseCardList.add(course);
                        }
                    }
                    adapter.notifyDataSetChanged(); // Notify adapter about data change
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error fetching data", e);
                });
    }
    void getsliderHome() {
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        StringRequest request = new StringRequest(Request.Method.GET, ConstantsDashboard.GET_HOME_SLIDER_URL, response -> {
            try {
                JSONArray newssliderArray = new JSONArray(response);
                for (int i = 0; i < newssliderArray.length(); i++) {
                    JSONObject childObj = newssliderArray.getJSONObject(i);
//                    homecarousel.addData(
//                            new CarouselItem(
//                                    childObj.getString("url"),
//                                    childObj.getString("action")
//                            )
//                    );
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // Handle error if needed
        });

        queue.add(request);
    }
    private void fetchExclusiveCourses() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("Exclusive_Course");

        collectionRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    exclusiveList.clear(); // Clear the list before adding new data
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Log.d("lop", String.valueOf(document));
                        // Manually fetch each field
                        String coverUrl = document.getString("cover");
                        String description = document.getString("description");
                        String docId = document.getString("instructorId");
                        boolean isFeatured = document.getBoolean("featured");
                        String lang = document.getString("lang");
                        String lastUpdated =("last_updated");
                        String name = document.getString("name");
                        boolean premiumStatus = document.getBoolean("premium_status");
                        long ratedBy = document.getLong("rated_by");
                        String ratingAvg = document.getString("rating_avg");
                        String subject = document.getString("subject");
                        String title = document.getString("title");
                        String crsid=document.getId();

                        // Create the CourseCard object and add to the list if it's featured
                        if (true) {
                            CourseCard course = new CourseCard(
                                    coverUrl, description, docId, isFeatured, lang, lastUpdated,
                                    name, premiumStatus, ratedBy, ratingAvg, subject, title
                            );
                            course.setCourseId(crsid);
                            exclusiveList.add(course);
                        }
                    }
                    adapter3.notifyDataSetChanged(); // Notify adapter about data change
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error fetching data", e);
                });
    }


}
