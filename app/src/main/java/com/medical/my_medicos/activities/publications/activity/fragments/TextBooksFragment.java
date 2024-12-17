package com.medical.my_medicos.activities.publications.activity.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.publications.adapters.ProductAdapter;
import com.medical.my_medicos.activities.publications.model.Product;
import com.medical.my_medicos.activities.utils.ConstantsDashboard;
import com.medical.my_medicos.databinding.FragmentTextBooksBinding;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class TextBooksFragment extends Fragment {

    private FragmentTextBooksBinding binding;
    private ProductAdapter productAdapter;
    private ArrayList<Product> products;
    private int catId;
    private String categoryName;
    private String filterType = "Overview"; // Default filter type is Overview

    public static TextBooksFragment newInstance(int catId, String categoryName) {
        TextBooksFragment fragment = new TextBooksFragment();
        Bundle args = new Bundle();
        args.putInt("catId", catId);
        args.putString("categoryName", categoryName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            catId = getArguments().getInt("catId", 0);
            categoryName = getArguments().getString("categoryName", "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTextBooksBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        products = new ArrayList<>();
        productAdapter = new ProductAdapter(requireContext(), products);

        RecyclerView recyclerView = binding.recyclerViewTextBooks;
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(productAdapter);

        TextView categoryNameTextView = view.findViewById(R.id.titleofthecategorywillcomehere);
        categoryNameTextView.setText(categoryName);

        getTextBooks(catId);

        // Set up filter options click listeners
        RadioGroup filterRadioGroup = view.findViewById(R.id.filterRadioGroup);
        RadioButton overviewFilter = view.findViewById(R.id.all);
        RadioButton freeFilter = view.findViewById(R.id.freeee);
        RadioButton paidFilter = view.findViewById(R.id.paiddd);

        filterRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.all) {
                updateFilter("Overview");
            } else if (checkedId == R.id.freeee) {
                updateFilter("FREE");
            } else if (checkedId == R.id.paiddd) {
                updateFilter("PAID");
            }
        });

        return view;
    }

    private void updateFilter(String filterType) {
        this.filterType = filterType;
        products.clear(); // Clear existing data
        getTextBooks(catId);
    }

    private void getTextBooks(int catId) {
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        String url = ConstantsDashboard.GET_SPECIALITY_INSIDER + '/' + categoryName;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    JSONArray productsArray = object.getJSONArray("data");
                    for (int i = 0; i < productsArray.length(); i++) {
                        JSONObject childObj = productsArray.getJSONObject(i);
                        JSONObject productObj = childObj.getJSONObject("data");

                        // Check if the product type is "Textbook"
                        if (productObj.getString("Type").equalsIgnoreCase("Textbook")) {
                            // Apply filter based on the selected type
                            if (filterType.equals("Overview") ||
                                    (filterType.equals("FREE") && (productObj.getDouble("Price") == 0.0 || productObj.getString("Category").equalsIgnoreCase("FREE"))) ||
                                    (filterType.equals("PAID") && productObj.getString("Category").equalsIgnoreCase("PAID"))) {

                                String documentId = childObj.getString("id");

                                Product product = new Product(
                                        productObj.getString("id"),
                                        productObj.getString("Title"),
                                        productObj.getString("thumbnail"),
                                        productObj.getString("Author"),
                                        productObj.getDouble("Price"),
                                        productObj.getString("Type"),
                                        productObj.getString("Category"),
                                        productObj.getString("Subject"),
                                        productObj.getString("URL")
                                );

                                products.add(product);
                            }
                        }
                    }
                    productAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {});

        queue.add(request);
    }
}
