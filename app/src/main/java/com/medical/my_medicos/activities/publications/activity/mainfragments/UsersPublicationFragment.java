package com.medical.my_medicos.activities.publications.activity.mainfragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.publications.adapters.PurchasedAdapter;
import com.medical.my_medicos.activities.publications.model.Product;
import com.medical.my_medicos.databinding.FragmentUsersPublicationBinding;
import java.util.ArrayList;
import java.util.List;

public class UsersPublicationFragment extends Fragment {

    private FragmentUsersPublicationBinding binding;
    private static final String FIELD_PHONE_NUMBER = "Phone Number";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUsersPublicationBinding.inflate(inflater, container, false);



        fetchUserData();
        return binding.getRoot();


    }


    private void fetchUserData() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String phoneNumber = currentUser.getPhoneNumber();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").whereEqualTo(FIELD_PHONE_NUMBER, phoneNumber)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot userSnapshot = task.getResult().getDocuments().get(0);
                            List<String> purchasedProductIds = (List<String>) userSnapshot.get("purchased");
                            if (purchasedProductIds != null && !purchasedProductIds.isEmpty()) {
                                fetchAndDisplayProducts(purchasedProductIds, db);
                            } else {
                                Log.d("UsersPublicationFragment", "No products purchased or purchased field is empty");
                            }
                        } else {
                            Log.e("UsersPublicationFragment", "Error fetching user document or user not found", task.getException());
                        }
                    });
        }
    }

    private void fetchAndDisplayProducts(List<String> productIds, FirebaseFirestore db) {
        List<Product> products = new ArrayList<>();
        for (String productId : productIds) {
            db.collection("Publications").document(productId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Product product = document.toObject(Product.class);
                        if (product != null) {
                            products.add(product);
                            if (products.size() == productIds.size()) {
                                updateRecyclerView(products);
                            }
                        }
                    } else {
                        Log.d("UsersPublicationFragment", "No product found with id: " + productId);
                    }
                } else {
                    Log.e("UsersPublicationFragment", "Error fetching product details", task.getException());
                }
            });
        }
    }

    private void updateRecyclerView(List<Product> products) {
        RecyclerView recyclerView = binding.purchased;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new PurchasedAdapter(getContext(), products));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
