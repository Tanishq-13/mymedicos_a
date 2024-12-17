package com.medical.my_medicos.activities.pg.activites.insiders.fragment;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.pg.activites.NeetExamPayment;
import com.medical.my_medicos.activities.pg.activites.extras.CreditsActivity;
import com.medical.my_medicos.activities.pg.activites.insiders.SpecialityPGInsiderActivity;
import com.medical.my_medicos.activities.pg.adapters.WeeklyQuizAdapter;
import com.medical.my_medicos.activities.pg.model.QuizPG;
import com.medical.my_medicos.activities.publications.activity.PaymentPublicationActivity;
import com.medical.my_medicos.activities.utils.ConstantsDashboard;
import com.medical.my_medicos.databinding.FragmentWeeklyQuizBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeeklyQuizFragment extends Fragment {
    private FragmentWeeklyQuizBinding binding;
    private WeeklyQuizAdapter quizAdapter;
    private ArrayList<QuizPG> quizpg;
    String title1;
    private String quizTitle;

    ProgressDialog progressDialog;
    public static WeeklyQuizFragment newInstance(int catId, String title) {
        WeeklyQuizFragment fragment = new WeeklyQuizFragment();
        Bundle args = new Bundle();
        args.putInt("catId", catId);
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWeeklyQuizBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Processing...");

        Bundle args = getArguments();
        if (args != null) {
            title1= args.getString("title", "");

            if (getActivity() instanceof SpecialityPGInsiderActivity) {
                ((SpecialityPGInsiderActivity) getActivity()).setToolbarTitle(title1);
            }

            quizpg = new ArrayList<>();
            quizAdapter = new WeeklyQuizAdapter(requireContext(), quizpg);

            RecyclerView recyclerViewVideos = binding.quizListWeekly;
            LinearLayoutManager layoutManagerVideos = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
            recyclerViewVideos.setLayoutManager(layoutManagerVideos);
            recyclerViewVideos.setAdapter(quizAdapter);

            getQuestions(title1);
        } else {
            // Handle the case where arguments are null
            Log.e("ERROR", "Arguments are null in WeeklyQuizFragment");
        }

//        LinearLayout goProSection = view.findViewById(R.id.gopro);
//        goProSection.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showGoProBottomSheet();
//            }
//        });

        return view;
    }

//    private void showGoProBottomSheet() {
//        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
//        View bottomSheetView = getLayoutInflater().inflate(R.layout.custom_bottom_sheet_up_insufficient, null);
//
//        TextView text = bottomSheetView.findViewById(R.id.text_insufficient_credits);
//        text.setText("Get the Pro & Be in the Top 1% of the Community");
//
//        TextView closeButton = bottomSheetView.findViewById(R.id.close);
//        TextView intentCredits = bottomSheetView.findViewById(R.id.intenttocredit);
//        CardView puchase100credits = bottomSheetView.findViewById(R.id.puchase100credits);
//        closeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bottomSheetDialog.dismiss();
//            }
//        });
//
//        intentCredits.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bottomSheetDialog.dismiss(); // Dismiss the bottom sheet dialog
//                Intent intent = new Intent(requireContext(), CreditsActivity.class); // Create an intent for the CreditsActivity
//                startActivity(intent); // Start the activity
//            }
//        });
//
//        puchase100credits.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showBottomSheetPurchaser();
//            }
//        });
//
//
//        bottomSheetDialog.setContentView(bottomSheetView);
//        bottomSheetDialog.show();
//    }

    private void showBottomSheetPurchaser() {
        View bottomSheetView = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_up_250, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(bottomSheetView);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        TextView textClickMe = bottomSheetView.findViewById(R.id.paymentpartcredit129);

        textClickMe.setOnClickListener(v -> {
            processCreditsOrder();
        });
        bottomSheetDialog.show();
    }

    void processCreditsOrder() {
        progressDialog.show();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getPhoneNumber();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference usersRef = db.collection("users");

            usersRef.whereEqualTo("Phone Number", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                RequestQueue queue = Volley.newRequestQueue(requireContext());

                                String url = ConstantsDashboard.GET_ORDER_ID_99_41 + userId + "/" + "package2";
                                Log.d("API Request URL", url);

                                StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
                                    try {
                                        Log.d("API Response", response);
                                        JSONObject requestBody = new JSONObject(response);

                                        if (requestBody.getString("status").equals("success")) {
                                            Toast.makeText(requireContext(), "Success order.", Toast.LENGTH_SHORT).show();
                                            String orderNumber = requestBody.getString("order_id");
                                            Log.e("Order ID check", orderNumber);
                                            new android.app.AlertDialog.Builder(requireContext())
                                                    .setTitle("Order Successful")
                                                    .setCancelable(false)
                                                    .setMessage("Your order number is: " + orderNumber)
                                                    .setPositiveButton("Pay Now", (dialogInterface, i) -> {
                                                        Intent intent = new Intent(requireContext(), PaymentPublicationActivity.class);
                                                        intent.putExtra("orderCode", orderNumber);
                                                        startActivity(intent);
                                                    }).show();
                                        } else {
                                            Toast.makeText(requireContext(), "Failed order.", Toast.LENGTH_SHORT).show();
                                        }
                                        progressDialog.dismiss();
                                        Log.e("res", response);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }, error -> {
                                    error.printStackTrace();
                                    progressDialog.dismiss();
                                    Toast.makeText(requireContext(), "Volley Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                });

                                queue.add(request);
                            }
                        } else {
                            // Handle the error when the document is not found
                            progressDialog.dismiss();
                            Toast.makeText(requireContext(), "Failed to retrieve user information", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            progressDialog.dismiss();
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }


    void getQuestions(String title) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        List<String> subcollectionIds = new ArrayList<>();

        if (user != null) {
            String userId = user.getPhoneNumber();

            CollectionReference quizResultsCollection = db.collection("QuizResultsPGPrep").document(userId).collection("Weekley");

            // Array to store subcollection IDs


            // Fetch subcollections for the current user
            quizResultsCollection.get()
                    .addOnCompleteListener(subcollectionTask -> {
                        if (subcollectionTask.isSuccessful()) {
                            for (QueryDocumentSnapshot subdocument : subcollectionTask.getResult()) {
                                // Access each subcollection inside the document
                                String subcollectionId = subdocument.getId();
                                subcollectionIds.add(subcollectionId);
                                Log.d("Subcollection ID", subcollectionId);
                            }

                            // Now you can use the subcollectionIds array outside this block
                            for (String id : subcollectionIds) {
                                Log.d("All Subcollection IDs", id);
                            }
                        } else {
                            // Handle failure
                            Log.e("Subcollection ID", "Error fetching subcollections", subcollectionTask.getException());
                        }
                    });
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CollectionReference quizzCollection = db.collection("PGupload").document("Weekley").collection("Quiz");

            Query query = quizzCollection;

            query.orderBy("from",Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();

                            // Check if the document ID is present in the subcollectionIds array
                            if (!subcollectionIds.contains(id)) {
                                String title = document.getString("title");
                                String speciality = document.getString("speciality");
                                Timestamp to = document.getTimestamp("to");
                                int r = speciality.compareTo(title1);
                                if (r == 0) {
//                                    QuizPG quizday = new QuizPG(title, title1, to, id);
//                                    quizpg.add(quizday);
                                }
                            }
                        }
                        quizAdapter.notifyDataSetChanged();

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        }

    }
}