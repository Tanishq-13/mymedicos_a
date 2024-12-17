package com.medical.my_medicos.activities.fmge.activites.insiders.fragment;

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
import com.medical.my_medicos.activities.fmge.adapters.QuestionBankFMGEAdapter;
import com.medical.my_medicos.activities.fmge.model.QuestionFmge;
import com.medical.my_medicos.activities.pg.activites.insiders.SpecialityPGInsiderActivity;
import com.medical.my_medicos.activities.pg.adapters.QuestionBankPGAdapter;
import com.medical.my_medicos.activities.pg.model.QuestionPG;
import com.medical.my_medicos.activities.utils.ConstantsDashboard;
import com.medical.my_medicos.databinding.FragmentQuestionbankBinding;
import com.medical.my_medicos.databinding.FragmentQuestionbankFmgeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class QuestionbankFmgeFragment extends Fragment {

    private FragmentQuestionbankFmgeBinding binding;
    private QuestionBankFMGEAdapter questionsAdapter;

    LottieAnimationView nodatafound;
    private ArrayList<QuestionFmge> questionsforpg;
    private int catId;

    public static QuestionbankFmgeFragment newInstance(int catId, String title) {
        QuestionbankFmgeFragment fragment = new QuestionbankFmgeFragment();
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
        binding = FragmentQuestionbankFmgeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        String title = getArguments().getString("title", "");
        if (getActivity() instanceof SpecialityPGInsiderActivity) {
            ((SpecialityPGInsiderActivity) getActivity()).setToolbarTitle(title);
        }

        questionsforpg = new ArrayList<>();
        questionsAdapter = new QuestionBankFMGEAdapter(requireContext(), questionsforpg);

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
                        QuestionFmge questionbankItem = new QuestionFmge(
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
