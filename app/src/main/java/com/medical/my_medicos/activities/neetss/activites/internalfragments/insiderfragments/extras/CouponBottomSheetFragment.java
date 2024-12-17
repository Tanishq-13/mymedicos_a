package com.medical.my_medicos.activities.neetss.activites.internalfragments.insiderfragments.extras;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.neetss.activites.internalfragments.insiderfragments.extras.adapters.CouponAdapter;
import com.medical.my_medicos.activities.neetss.activites.internalfragments.insiderfragments.extras.model.Coupon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CouponBottomSheetFragment extends BottomSheetDialogFragment {

    private RecyclerView recyclerViewAvailable;
    private CouponAdapter availableAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coupon_bottom_sheet, container,false);

        recyclerViewAvailable = view.findViewById(R.id.coupons_recycleview_available);
        recyclerViewAvailable.setLayoutManager(new LinearLayoutManager(getContext()));

        loadCoupons();

        return view;
    }
    private void loadCoupons() {
        List<Coupon> couponList = new ArrayList<>();
        FirebaseUser user = auth.getCurrentUser();

        db.collection("Coupons")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> dataMap = document.getData();
                            String about = (String) dataMap.get("about");
                            String code = (String) dataMap.get("code");
                            String couponid = (String) dataMap.get("couponid");
                            int discount = 0;
                            boolean isActive = dataMap.get("status") != null ? (boolean) dataMap.get("status") : false;

                            Object discountObj = dataMap.get("discount");
                            if (discountObj instanceof Number) {
                                discount = ((Number) discountObj).intValue();
                            } else if (discountObj instanceof String) {
                                try {
                                    discount = Integer.parseInt((String) discountObj);
                                } catch (NumberFormatException e) {
                                    Log.e("CouponLoadError", "Invalid format for discount", e);
                                }
                            }

                            Coupon c = new Coupon(about, code, couponid, discount, isActive);
                            couponList.add(c);
                        }

                        availableAdapter = new CouponAdapter(requireContext(), couponList);
                        recyclerViewAvailable.setAdapter(availableAdapter);
                    } else {
                        Log.e("CouponLoadError", "Failed to load coupons", task.getException());
                    }
                });
    }

}
