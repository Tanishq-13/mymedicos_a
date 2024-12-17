package com.medical.my_medicos.activities.home.exclusive.faculty;
import android.content.Context;
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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.home.exclusive.adapter.CoursesAdapter;
import com.medical.my_medicos.activities.home.exclusive.model.CourseCard;

import java.util.ArrayList;
import java.util.List;

public class CoursesFragment extends Fragment {
    private CourseCheckListener listener;
    private RecyclerView recyclerView;
    private CoursesAdapter coursesAdapter;
    private List<CourseCard> courseList = new ArrayList<>();
    private static final String TAG = "CoursesFragment";
    private String docId; // This will be passed from the activity

    public CoursesFragment(String docId) {
        this.docId = docId; // Set the instructor's docId
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CourseCheckListener) {
            listener = (CourseCheckListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement CourseCheckListener");
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_courses, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        coursesAdapter = new CoursesAdapter(courseList,getContext());
        recyclerView.setAdapter(coursesAdapter);

        fetchCourses(docId);

        return view;
    }

    private void fetchCourses(String docId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("crses",docId);
        db.collection("Exclusive_Course")
                .whereEqualTo("instructorId", docId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
//                        if (listener != null) {
//                            listener.onCoursePresent();
//                        }
//                        listener.onCoursePresent();
                        String crsid=document.getId();
                        String coverUrl = document.getString("cover");
                        String description = document.getString("description");
//                        String docId = document.getString("docId");
                        boolean isFeatured = document.getBoolean("featured");
                        String lang = document.getString("lang");
                        String lastUpdated =("last_updated");
                        String name = document.getString("name");
                        boolean premiumStatus = document.getBoolean("premium_status");
                        long ratedBy = document.getLong("rated_by");
                        String ratingAvg = document.getString("rating_avg");
                        String subject = document.getString("subject");
                        String title = document.getString("title");

                        // Create the CourseCard object and add to the list if it's featured
                        CourseCard course = new CourseCard(
                                coverUrl, description, docId, isFeatured, lang, lastUpdated,
                                name, premiumStatus, ratedBy, ratingAvg, subject, title
                        );
                        course.setCourseId(crsid);
                        courseList.add(course);
                    }
                    coursesAdapter.notifyDataSetChanged(); // Notify the adapter
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching courses: " + e.getMessage()));
    }
    public interface CourseCheckListener {
        void onCoursePresent();
    }
}
