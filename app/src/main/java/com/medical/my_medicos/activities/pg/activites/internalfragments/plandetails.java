package com.medical.my_medicos.activities.pg.activites.internalfragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.home.ApiService;
import com.medical.my_medicos.activities.home.RetrofitClient;
import com.medical.my_medicos.activities.home.model.PlanRequest;
import com.medical.my_medicos.activities.pg.adapters.plan_detailed_adapter;
import com.medical.my_medicos.activities.pg.model.Plan_detailed;
import com.medical.my_medicos.databinding.ActivityPlandetailsBinding;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.ResponseBody;

public class plandetails extends AppCompatActivity implements PaymentResultWithDataListener   {
    private String documentId;
    private String documentID, selectedCollection, name, email, phoneNumber, whatsappNumber, gender,planID;
    private String currentOrderId="";
    String amount;
    private Date dob = new Date();
    private Button pymnt;
    private TextView headname;
    private TextView selectedPlanPrice;
    private LinearLayout bottomPriceLayout;
    private Button proceedToPayButton;

    private String headnamest;
    private RecyclerView recyclerView;
    private plan_detailed_adapter planAdapter;
    private List<Plan_detailed> planList = new ArrayList<>(); // Initialize here
    private FirebaseFirestore db;
    private ActivityPlandetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_plandetails);
        fetchUserDetails();

//        View decorView = getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//        decorView.setSystemUiVisibility(uiOptions);
//        binding.backtothehomefrompg.setOnClickListener(v -> {
//            finish(); // Finish the current activity
//        });
        pymnt=findViewById(R.id.proceed_to_pay_button);
        selectedPlanPrice = findViewById(R.id.selected_plan_price);
        bottomPriceLayout = findViewById(R.id.bottom_price_layout);
        proceedToPayButton = findViewById(R.id.proceed_to_pay_button);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(com.hbb20.R.color.test)); // Replace with your color resource
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        Checkout.preload(plandetails.this);

        planID = getIntent().getStringExtra("planID");
        headnamest = getIntent().getStringExtra("planName");
        headname = findViewById(R.id.headname);
        headname.setText(headnamest);
        selectedCollection= String.valueOf(planID.charAt(0)+""+planID.charAt(1));
        selectedCollection=selectedCollection.toUpperCase();
        if(planID.charAt(0)=='p'){
            selectedCollection="PG";
        }
        else if(planID.charAt(0)=='f'){
            selectedCollection="FMGE";

        }
        else{
            selectedCollection="NEET SS";
        }
        Log.d("selectedcollection",selectedCollection);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.plan_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        planAdapter = new plan_detailed_adapter(this, planList);
        recyclerView.setAdapter(planAdapter);

        // Fetch the document ID and populate the list
        fetchDocumentId(planID);

    }

    private void fetchDocumentId(String planID) {
        CollectionReference plansRef = db.collection("Plans").document(selectedCollection).collection("Subscriptions");
        plansRef.whereEqualTo("planID", planID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            documentId = document.getId();  // Get the document ID
                            Log.d("PlanDetails", "Document ID: " + documentId);
                        }
                        // Proceed to populate the list after fetching the document ID
                        populateList(documentId);
                    } else {
                        Log.d("FirestoreError", "No matching plan found for planID: " + planID);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error searching planID", e);
                });
    }

    private void populateList(String documentId) {
        DocumentReference docRef = db.collection("Plans").document(selectedCollection).collection("Subscriptions").document(documentId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String,Object> planData=documentSnapshot.getData();
                Map<String, Object> durations = (Map<String, Object>) planData.get("Durations");
                 int discpr= Integer.parseInt(String.valueOf( planData.get("Discount_Price")));
                if (durations != null) {
                    // Clear the existing planList
                    planList.clear();

                    for (Map.Entry<String, Object> entry : durations.entrySet()) {
                        String month = entry.getKey();
                        Long price = (Long) entry.getValue();
                        boolean isRecommended=price==discpr;
                        // Validate price before adding to the list
                        String originalPrice = "₹" + getOriginalPriceForMonth(month);
                        String discountedPrice = "₹" + price;

                        planList.add(new Plan_detailed(month + " Months", originalPrice, discountedPrice,isRecommended,selectedCollection,Integer.parseInt(month),planID));
                    }
                    // Notify the adapter that the data has changed
                    planAdapter.notifyDataSetChanged();
                } else {
                    Log.d("Firestore", "No durations found in document");
                }
            } else {
//                Log.d("Firestore", "get failed with ", "task.getException()");
            }
        });
    }

    private String getOriginalPriceForMonth(String month) {
        switch (month) {
            case "1": return "1348.65";
            case "3": return "2999.00";
            case "6": return "4999.00";
            case "9": return "6999.00";
            default: return "0.00"; // Default case if no match
        }
    }
    public void onPlanSelected(String month, String discountedPrice,String duration) {
        // Update the price in the TextView
        selectedPlanPrice.setText(discountedPrice);
        amount=discountedPrice;
        pymnt.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         fetchOrderId(planID, selectedCollection, Integer.parseInt("9"));
                                     }
                                 });
        // Make the bottom layout visible
        bottomPriceLayout.setVisibility(View.VISIBLE);
    }
    public void startPayment(String amnt,String orderid){
        amnt = amnt.replaceAll("[^\\d.]", "");

        int price=Math.round(Float.parseFloat(amnt)*100);
        Checkout checkout=new Checkout();
        checkout.setKeyID("rzp_test_zK3Nwtz9le5dPW");
        final Activity activity=this;
        try{
            JSONObject options=new JSONObject();
            options.put("name","mymedicos");
            options.put("description",headnamest);
            options.put("amount",Integer.valueOf(price));
            options.put("currency","INR");
//            options.put("order_id",orderid);
            JSONObject prefill = new JSONObject();
            prefill.put("contact", phoneNumber);
            prefill.put("email", email);
//
            options.put("prefill", prefill);
            checkout.open(activity,options);
        }
        catch(Exception e){

        }

    }

    private void fetchUserDetails() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            phoneNumber = user.getPhoneNumber();
            db = FirebaseFirestore.getInstance();
            db.collection("users").whereEqualTo("Phone Number", phoneNumber)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                            Map<String, Object> data = document.getData();
                            name = (String) data.get("Name");
                            email = (String) data.get("Email ID");
                            phoneNumber = (String) data.get("Phone Number");
                            documentID = (String) data.get("DocID");
//                            dob = ((Timestamp) data.get("DOB")).toDate();
                            gender = (String) data.get("Gender");
                        }
                    });
        }
    }
    private void fetchOrderId(String section, String planId, int duration) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        // Create the PlanRequest object
        PlanRequest planRequest = new PlanRequest(section, planId, duration, phoneNumber," coupon");

        // Make the API call
        Call<ResponseBody> call = apiService.generatePlan(planRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject json = new JSONObject(responseBody);
                        String orderId = json.optString("order_id", null);

                        if (orderId != null) {
                            Log.d("OrderID", "Order ID: " + orderId);
//                            callback.onOrderIdReceived(orderId);
                        } else {
                            Log.e("OrderID", "Order ID missing in response");
//                            callback.onOrderIdReceived(null);
                        }
                    } catch (Exception e) {
                        Log.e("OrderID", "Error parsing response: " + e.getMessage(), e);
//                        callback.onOrderIdReceived(null);
                    }
                } else {
                    Log.e("OrderID", "Failed with code: " + response.code());
//                    callback.onOrderIdReceived(null);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("OrderID", "API call failed: " + t.getMessage(), t);
//                callback.onOrderIdReceived(null);
            }
        });
    }

    private void updateFirestoreWithPaymentDetails(String order, String paymentId, String signature) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("razorpay_payment_id", paymentId);
        updates.put("razorpay_signature", signature);
        updates.put("status", "success");
        updates.put("updated_at", FieldValue.serverTimestamp());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("PlansOrders").document(order)
                .update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Payment details successfully updated in Firestore
                        Log.d("FirestoreUpdate", "Payment details successfully updated.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the error
                        Log.e("FirestoreUpdateError", "Error updating payment details: " + e.getMessage());
                    }
                });
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID, PaymentData paymentData) {
        Log.d("PaymentSuccess", "Order ID: " + paymentData.getOrderId());
        // Handle payment success and update Firestore
        try {
            // Handle payment success and update Firestore
            updateFirestoreWithPaymentDetails(paymentData.getOrderId(), razorpayPaymentID, paymentData.getSignature());

            // Optionally, show a success message
            Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Catch any exception and show an error message
            Log.e("PaymentSuccessError", "Error in onPaymentSuccess: " + e.getMessage());
            Toast.makeText(this, "Error processing payment: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        try{
//            alertDialogBuilder.setMessage("Payment Failed:\nPayment Data: "+paymentData.getData());
//            alertDialogBuilder.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public interface OrderIdCallback {
        void onOrderIdReceived(String orderId);
    }

}
