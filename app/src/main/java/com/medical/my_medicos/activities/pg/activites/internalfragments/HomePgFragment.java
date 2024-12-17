package com.medical.my_medicos.activities.pg.activites.internalfragments;

import static android.content.Context.MODE_PRIVATE;
import static androidx.fragment.app.FragmentManager.TAG;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.home.HomeActivity;
import com.medical.my_medicos.activities.job.JobsApplyActivity;
import com.medical.my_medicos.activities.job.fragments.TermsandConditionDialogueFragment;
import com.medical.my_medicos.activities.login.bottom_controls.TermsandConditionsActivity;
import com.medical.my_medicos.activities.news.fragments.JobsUpdatesNewsFragment;
import com.medical.my_medicos.activities.pg.activites.PgprepActivity;
import com.medical.my_medicos.activities.pg.activites.extras.PreparationCategoryDisplayActivity;
import com.medical.my_medicos.activities.pg.activites.extras.PreparationCategoryMaterialDisplayActivity;
import com.medical.my_medicos.activities.pg.activites.extras.RecetUpdatesNoticeActivity;
import com.medical.my_medicos.activities.pg.activites.extras.TermsandConditionsDialogueFragmentPg;
import com.medical.my_medicos.activities.pg.activites.internalfragments.intwernaladapters.ExamQuizAdapter;
import com.medical.my_medicos.activities.pg.adapters.PerDayPGAdapter;
import com.medical.my_medicos.activities.pg.adapters.PlanAdapter;
import com.medical.my_medicos.activities.pg.adapters.QuestionBankPGAdapter;
import com.medical.my_medicos.activities.pg.model.PerDayPG;
import com.medical.my_medicos.activities.pg.model.Plan;
import com.medical.my_medicos.activities.pg.model.QuestionPG;
import com.medical.my_medicos.activities.pg.model.QuizPG;
import com.medical.my_medicos.activities.pg.model.QuizPGExam;
import com.medical.my_medicos.activities.utils.ConstantsDashboard;
import com.medical.my_medicos.activities.utils.UpdatingScreen;
import com.medical.my_medicos.databinding.FragmentHomePgBinding;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class HomePgFragment extends Fragment {
    FragmentHomePgBinding binding;
    ArrayList<PerDayPG> dailyquestionspg;
    SwipeRefreshLayout swipeRefreshLayout;
    String quiztiddaya;
    LinearLayout nocardp;
    FirebaseUser currentUser;
    QuestionBankPGAdapter questionsAdapter;
    ArrayList<QuestionPG> questionsforpg;
    private Handler handlerprepration;
    private ExamQuizAdapter quizAdapter;
    CardView gotoupdatesofpg;
    private ArrayList<QuizPGExam> quizpg;
    private LinearLayout plans;
    String title1;
    TextView practivemcq,material;
    private TermsandConditionsDialogueFragmentPg dialog;
    private final int AUTO_SCROLL_DELAY = 3000;
    int i = 0;

    public static HomePgFragment newInstance() {
        HomePgFragment fragment = new HomePgFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomePgBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        showFirstTimePopup();

        Bundle args = getArguments();
        if (args != null) {

            quizpg = new ArrayList<>();
            quizAdapter = new ExamQuizAdapter(requireContext(), quizpg);

            RecyclerView recyclerViewVideos = binding.specialexam;
            LinearLayoutManager layoutManagerVideos = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
            recyclerViewVideos.setLayoutManager(layoutManagerVideos);
            recyclerViewVideos.setAdapter(quizAdapter);

            getPaidExam(title1);


        } else {
            Log.e("ERROR", "Arguments are null in WeeklyQuizFragment");
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Plan> plansList = new ArrayList<>();


        db.collection("Plans").document("PG").collection("Subscriptions")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get the values from the document fields
                            String planName = document.getString("PlanName");
                            String discountedPrice = document.getString("Discount_Price");
                            String planTagline = document.getString("PlanTagline");
                            String planPrice = document.getString("PlanPrice");
                            String planThumbnail = document.getString("PlanThumbnail");
                            String planID = document.getString("planID");

                            // Get the array of plan features (PlansFeature)
                            List<String> plansFeature = (List<String>) document.get("PlanFeatures");

                            // Log the fetched data for verification
                            Log.d("Firestore", "Plan Name: " + planName);
                            Log.d("Firestore", "Discounted Price: " + discountedPrice);
                            Log.d("Firestore", "Plan Tagline: " + planTagline);
                            Log.d("Firestore", "Plan Price: " + planPrice);
                            Log.d("Firestore", "Plan Thumbnail: " + planThumbnail);
                            Log.d("Firestore", "Plan Features: " + plansFeature);

                            // Create a Plan object with all fields, including features
                            Plan plan = new Plan(planName, planTagline, planPrice, planThumbnail, plansFeature, discountedPrice,planID);

                            // Add the Plan object to the list
                            plansList.add(plan);
                        }

                        // Log that the plan list is ready
                        Log.d("Firestore", "Plans fetched successfully: " + plansList.size() + " plans.");
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());
                    }
                });



        // Setup RecyclerView
//        RecyclerView recyclerViewPlans = view.findViewById(R.id.recyclerViewPlans);
//        recyclerViewPlans.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        PlanAdapter planAdapter = new PlanAdapter(plansList,getContext());
//        recyclerViewPlans.setAdapter(planAdapter);
        plans=view.findViewById(R.id.exploreplans);
        plans.setOnClickListener(v->{
            Intent intt=new Intent(getContext(),exploreplansactivity.class);
            startActivity(intt);
        });
        handlerprepration = new Handler();


        return view;
    }

    private void showFirstTimePopup() {
        SharedPreferences prefs = getActivity().getSharedPreferences("PgPrepPrefs", MODE_PRIVATE);
        boolean isFirstLaunch = prefs.getBoolean("isFirstLaunch", true);

        if (isFirstLaunch) {
            View popupView = LayoutInflater.from(requireContext()).inflate(R.layout.custompopupforpg, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setView(popupView);

            TextView agreeButton = popupView.findViewById(R.id.agreepg);
            TextView disagreeButton = popupView.findViewById(R.id.visit);
            ImageView closeOpt = popupView.findViewById(R.id.closebtndialogue);

            AlertDialog dialog = builder.create();

            agreeButton.setOnClickListener(v -> {
                dialog.dismiss();
            });

            disagreeButton.setOnClickListener(v -> {
                dialog.dismiss();
                Intent intent = new Intent(requireContext(), TermsandConditionsActivity.class);
                Toast.makeText(requireContext(), "Redirecting..", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            });

            closeOpt.setOnClickListener(v -> {
                dialog.dismiss();
                Intent intent = new Intent(requireContext(), HomeActivity.class);
                Toast.makeText(requireContext(), "Returning back...", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                dialog.dismiss();
            });

            dialog.show();
            prefs.edit().putBoolean("isFirstLaunch", false).apply();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getPhoneNumber();
        }

        swipeRefreshLayout = binding.getRoot().findViewById(R.id.swipeRefreshLayoutPg);
        swipeRefreshLayout.setOnRefreshListener(this::refreshContent);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = currentUser.getPhoneNumber();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                Map<String, Object> dataMap = document.getData();
                                String field1 = (String) dataMap.get("Phone Number");

                                if (field1 != null && currentUser.getPhoneNumber() != null) {
                                    int a = field1.compareTo(userId);
                                    Log.d("Issue with the userID", String.valueOf(a));

                                    if (a == 0) {
                                        Log.d("Can't get it", String.valueOf(a));
                                        quiztiddaya = ((String) dataMap.get("QuizToday"));
                                        break;
                                    } else {
                                        quiztiddaya = null;
                                    }
                                }
                            }
                        } else {
                            Log.d(ContentValues.TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        initSliderPg();

        initQuestionsBanks();
    }

    private void initSliderPg() {
        getRecentPgSlider();
    }

    void getRecentPgSlider() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest request = new StringRequest(Request.Method.GET, ConstantsDashboard.GET_PG_SLIDER_URL, response -> {
            try {
                JSONArray pgsliderArray = new JSONArray(response);
                for (int i = 0; i < pgsliderArray.length(); i++) {
                    JSONObject childObj = pgsliderArray.getJSONObject(i);
                    binding.carouselpghome.addData(
                            new CarouselItem(
                                    childObj.getString("url"),
                                    childObj.getString("action")
                            )
                    );
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // Handle error if needed
        });
        queue.add(request);
    }

    void getPaidExam(String title) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        List<String> subcollectionIds = new ArrayList<>();

        if (user != null) {
            String userId = user.getPhoneNumber();

            CollectionReference quizResultsCollection = db.collection("QuizResults").document(userId).collection("Exam");

            quizResultsCollection.get()
                    .addOnCompleteListener(subcollectionTask -> {
                        if (subcollectionTask.isSuccessful()) {
                            for (QueryDocumentSnapshot subdocument : subcollectionTask.getResult()) {
                                String subcollectionId = subdocument.getId();
                                subcollectionIds.add(subcollectionId);
                                Log.d("Subcollection ID", subcollectionId);
                            }

                            for (String id : subcollectionIds) {
                                Log.d("All Subcollection IDs", id);
                            }
                        } else {
                            Log.e("Subcollection ID", "Error fetching subcollections", subcollectionTask.getException());
                        }
                    });
        }

        if (title == null || title.isEmpty()) {
            title = "home";
        }

        CollectionReference quizzCollection = db.collection("PGupload").document("Weekley").collection("Quiz");
        Query query = quizzCollection;
        String finalTitle = title;

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id = document.getId();

//                        if (!subcollectionIds.contains(id)) {
                        if (true) {
                            String quizTitle = document.getString("title");
                            String speciality = document.getString("speciality");
                            Timestamp To = document.getTimestamp("to");
                            Timestamp from = document.getTimestamp("from");
                            List<Object> dataList = (List<Object>) document.get("Data");
                            int dataSize = (dataList != null) ? dataList.size() : 0;  // Get the size of the 'Data' array (number of questions)

                            boolean paid1=document.contains("type");
                            if (paid1==true) {
                                Object field = document.get("type");
                                if (field instanceof String) {
                                    String type1 = document.getString("type");
                                    if (finalTitle.isEmpty() || finalTitle.equals("Home")) {
                                        int r = speciality.compareTo(finalTitle);
                                        if (r == 0) {
                                            QuizPGExam quizday = new QuizPGExam(quizTitle, title1, To, id, type1, "dsf",String.valueOf(dataSize));
                                            quizpg.add(quizday);
                                        }
                                    } else {
                                        int r = speciality.compareTo(finalTitle);
                                        if (r == 0) {
                                            QuizPGExam quizday = new QuizPGExam(quizTitle, finalTitle, To, id, type1, "dsf",String.valueOf(dataSize));
                                            quizpg.add(quizday);
                                        }
                                    }
                                } else if (field instanceof Boolean) {

                                    if (finalTitle.isEmpty() || finalTitle.equals("Home")) {
                                        int r = speciality.compareTo(finalTitle);
                                        if (r == 0) {
                                            QuizPGExam quizday = new QuizPGExam(quizTitle, title1, To, id, "Basic", "dsf",String.valueOf(dataSize));
                                            quizpg.add(quizday);
                                        }
                                    } else {
                                        int r = speciality.compareTo(finalTitle);
                                        if (r == 0) {
                                            QuizPGExam quizday = new QuizPGExam(quizTitle, finalTitle, To, id, "Basic", "dsf",String.valueOf(dataSize));
                                            quizpg.add(quizday);
                                        }
                                    }
                                }

                            }


                        }
                    }
                    quizAdapter.notifyDataSetChanged();
                } else {
                    Log.d(ContentValues.TAG, "Error getting documents: ", task.getException());
                }
            }
        });

    }
    @SuppressLint("RestrictedApi")
    private void checkAndInitJobsUpdatesNewsFragment() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("MedicalNews")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            new Handler().post(this::initJobsUpdatesNewsFragment);
                        } else {
                            //binding.newsFragmentContainer.setVisibility(View.GONE);
                        }
                    } else {
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void initJobsUpdatesNewsFragment() {
        if (isAdded() && getActivity() != null) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            JobsUpdatesNewsFragment jobsUpdatesNewsFragment = new JobsUpdatesNewsFragment();
//            transaction.replace(R.id.news_fragment_container, jobsUpdatesNewsFragment);
            transaction.commit();
        }
    }

    void initQuestionsBanks() {
        questionsforpg = new ArrayList<>();
        questionsAdapter = new QuestionBankPGAdapter(getActivity(), questionsforpg);

        getRecentQuestions();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
//        binding.QB.setLayoutManager(layoutManager);
//        binding.QB.setAdapter(questionsAdapter);
    }

    void getRecentQuestions() {
        RequestQueue queue = Volley.newRequestQueue(requireActivity());

        String url = ConstantsDashboard.GET_PG_QUESTIONBANK_URL_HOME;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if(object.getString("status").equals("success")){
                    JSONArray array = object.getJSONArray("data");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject childObj = array.getJSONObject(i);
                        QuestionPG questionbankItem = new QuestionPG(
                                childObj.getString("Title"),
                                childObj.getString("Description"),
                                childObj.getString("Time"),
                                childObj.getString("file")
                        );
                        questionsforpg.add(questionbankItem);
                    }
                    questionsAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> { });

        queue.add(request);
    }

    private void refreshContent() {
        swipeRefreshLayout.setRefreshing(false);
    }
}
