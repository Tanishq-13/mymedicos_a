package com.medical.my_medicos.activities.publications.activity.mainfragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.medical.my_medicos.activities.publications.activity.SearchingPublicationActivity;
import com.medical.my_medicos.activities.publications.adapters.CategoryAdapter;
import com.medical.my_medicos.activities.publications.model.Category;
import com.medical.my_medicos.activities.utils.ConstantsDashboard;
import com.medical.my_medicos.databinding.FragmentSearchPublicationBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchPublicationFragment extends Fragment {

    private FragmentSearchPublicationBinding binding;
    private CategoryAdapter categoryAdapter;
    private ArrayList<Category> categories;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchPublicationBinding.inflate(inflater, container, false);
        initSearchBar();
        initCategories();
        return binding.getRoot();
    }

    private void initSearchBar() {
        binding.searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed for now
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed for now
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Optional: Implement if needed
            }
        });

        binding.searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                // Not needed for now
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                String query = text.toString();
                if (!TextUtils.isEmpty(query)) {
                    Intent intent = new Intent(requireContext(), SearchingPublicationActivity.class);
                    intent.putExtra("query", query);
                    startActivity(intent);
                }
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                // Handle back button click or other buttons if needed
            }
        });
    }

    private void initCategories() {
        categories = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(), categories);

        getCategories();

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        binding.categoriesList.setLayoutManager(layoutManager);
        binding.categoriesList.setAdapter(categoryAdapter);
    }

    private void getCategories() {
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        StringRequest request = new StringRequest(Request.Method.GET, ConstantsDashboard.GET_SPECIALITY, response -> {
            try {
                JSONObject mainObj = new JSONObject(response);
                if (mainObj.getString("status").equals("success")) {
                    JSONArray categoriesArray = mainObj.getJSONArray("data");

                    for (int i = 0; i < categoriesArray.length(); i++) {
                        JSONObject object = categoriesArray.getJSONObject(i);
                        int priority = object.getInt("priority");
                        if (priority == 1 || priority == 2) {
                            Category category = new Category(
                                    object.getString("id"),
                                    priority
                            );
                            categories.add(category);
                        }
                    }

                    Category moreItem = new Category("-1", -1);
                    categories.add(moreItem);

                    categoryAdapter.notifyDataSetChanged();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {

        });

        queue.add(request);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
