package com.medical.my_medicos.activities.pg.activites.insiders.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.medical.my_medicos.activities.pg.activites.insiders.SpecialityPGInsiderActivity;
import com.medical.my_medicos.activities.pg.adapters.QuestionBankPGAdapter;
import com.medical.my_medicos.activities.pg.model.QuestionPG;
import com.medical.my_medicos.activities.utils.ConstantsDashboard;
import com.medical.my_medicos.databinding.FragmentQuestionbankBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class QuestionbankFragment extends Fragment {

    private FragmentQuestionbankBinding binding;
    private QuestionBankPGAdapter questionsAdapter;

    LottieAnimationView nodatafound;
    private ArrayList<QuestionPG> questionsforpg;
    private int catId;

    public static QuestionbankFragment newInstance(int catId, String title) {
        QuestionbankFragment fragment = new QuestionbankFragment();
        Bundle args = new Bundle();
        args.putInt("catId", catId);
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            catId = getArguments().getInt("catId", 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentQuestionbankBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        String title = getArguments().getString("title", "");
        if (getActivity() instanceof SpecialityPGInsiderActivity) {
            ((SpecialityPGInsiderActivity) getActivity()).setToolbarTitle(title);
        }

        questionsforpg = new ArrayList<>();
        questionsAdapter = new QuestionBankPGAdapter(requireContext(), questionsforpg);

        RecyclerView recyclerViewQuestions = binding.questionsListQuestion;
        LinearLayoutManager layoutManagerQuestions = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewQuestions.setLayoutManager(layoutManagerQuestions);
        recyclerViewQuestions.setAdapter(questionsAdapter);
        getRecentQuestions(title);

        return view;
    }

    void getRecentQuestions(String title) {
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        String url = ConstantsDashboard.GET_PG_QUESTIONBANK_URL + "/" + title;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
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
                    updateNoDataVisibility();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
        });

        queue.add(request);
    }

    private void updateNoDataVisibility() {
        if (questionsforpg.isEmpty()) {
            binding.nodatafound.setVisibility(View.VISIBLE);
        } else {
            binding.nodatafound.setVisibility(View.GONE);
        }
    }
}
