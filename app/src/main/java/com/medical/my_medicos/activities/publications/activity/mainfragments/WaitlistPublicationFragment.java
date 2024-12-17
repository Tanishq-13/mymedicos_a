package com.medical.my_medicos.activities.publications.activity.mainfragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.publications.activity.CartFromDetailActivity;
import com.medical.my_medicos.activities.publications.adapters.CartAdapter;
import com.medical.my_medicos.activities.publications.adapters.WaitlistAdapter;
import com.medical.my_medicos.activities.publications.model.Product;
import com.medical.my_medicos.activities.utils.ConstantsDashboard;
import com.medical.my_medicos.databinding.FragmentWaitlistPublicationBinding;

import org.json.JSONObject;

import java.util.ArrayList;

public class WaitlistPublicationFragment extends Fragment {

    private FragmentWaitlistPublicationBinding binding;
    private WaitlistAdapter waitlistAdapter;
    private ArrayList<Product> products;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWaitlistPublicationBinding.inflate(inflater, container, false);


        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Processing...");

        products = new ArrayList<>();

        waitlistAdapter = new WaitlistAdapter(getContext(), products, this::updateSubtotal);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.cartList.setLayoutManager(layoutManager);
        binding.cartList.setAdapter(waitlistAdapter);

        updateSubtotal(); // Initial update of subtotal

        binding.continueBtn.setOnClickListener(view -> gotocart());

        return binding.getRoot();
    }

    private void gotocart() {
        Intent intent = new Intent(getActivity(), CartFromDetailActivity.class);
        startActivity(intent);
    }


    private void updateSubtotal() {
        double subtotal = calculateSubtotal();
    }

    private double calculateSubtotal() {
        double subtotal = 0;
        for (Product product : products) {
            subtotal += product.getPrice();
        }
        return subtotal;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
