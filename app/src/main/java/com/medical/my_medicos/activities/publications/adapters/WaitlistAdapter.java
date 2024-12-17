package com.medical.my_medicos.activities.publications.adapters;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.publications.activity.MyVolleyRequest;
import com.medical.my_medicos.activities.publications.model.Product;
import com.medical.my_medicos.activities.utils.ConstantsDashboard;
import com.medical.my_medicos.databinding.ItemWaitlistBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class WaitlistAdapter extends RecyclerView.Adapter<WaitlistAdapter.CartViewHolder> {

    Context context;
    ArrayList<Product> products;
    CartListener cartListener;

    public interface CartListener {
        void onQuantityChanged();
    }

    public WaitlistAdapter(Context context, ArrayList<Product> products, CartListener cartListener) {
        this.context = context;
        this.products = products;
        this.cartListener = cartListener;

        String docId = Preferences.userRoot().get("docId", "");

        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());

        String url = ConstantsDashboard.GET_CART + "/" + docId + "/get";
        JSONObject requestBody = new JSONObject();
        MyVolleyRequest.sendPostRequest(context.getApplicationContext(), url, requestBody, new MyVolleyRequest.VolleyCallback() {

            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray cart = response.getJSONArray("data");
                    for (int i = 0; i < cart.length(); i++) {
                        JSONObject childObj = cart.getJSONObject(i);
                        Product product = new Product(
                                childObj.getString("id"),
                                childObj.getString("Title"),
                                childObj.getString("thumbnail"),
                                childObj.getString("Author"),
                                childObj.getDouble("Price"),
                                childObj.getString("Type"),
                                childObj.getString("Category"),
                                childObj.getString("Subject"),
                                childObj.getString("URL")
                        );
                        products.add(product);
                        Log.d("product loaded", childObj.toString());
                    }
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("VolleyResponse", response.toString());
            }

            @Override
            public void onError(String error) {
                Log.e("VolleyError", error);
            }
        });
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartViewHolder(LayoutInflater.from(context).inflate(R.layout.item_waitlist, parent, false));
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = products.get(position);

        Glide.with(context)
                .load(product.getThumbnail())
                .into(holder.binding.image);

        holder.binding.label.setText(product.getTitle());
        holder.binding.price.setText("INR " + product.getPrice());
        holder.binding.authornamecomehere.setText(product.getAuthor());

        holder.binding.deleteproduct.setOnClickListener(view -> {
            String productIdToRemove = product.getId();

            // Remove product locally
            products.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, products.size());
            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Assuming getDocId() method returns the correct DocID, such as "xYbeyShTZBWOArRPWJWp"
            String userDocId = getDocId(); // Ensure this method fetches the correct DocID from preferences or another source

            if (userDocId != null && !userDocId.isEmpty()) {
                DocumentReference userDocRef = db.collection("users").document(userDocId);

                db.runTransaction(transaction -> {
                            DocumentSnapshot userDocSnapshot = transaction.get(userDocRef);

                            // Fetch the cart field as a raw type
                            Object rawCart = userDocSnapshot.get("cart");
                            List<String> cart;

                            if (rawCart instanceof List) {
                                // Manually cast to List<String>
                                @SuppressWarnings("unchecked")
                                List<String> tempCart = (List<String>) rawCart;
                                cart = tempCart;
                            } else {
                                // Initialize to an empty list if the cart field is not a list (or is null)
                                cart = new ArrayList<>();
                            }

                            // Proceed with removing the productId and updating the document
                            cart.remove(productIdToRemove);
                            transaction.update(userDocRef, "cart", cart);

                            return null;
                        }).addOnSuccessListener(aVoid -> Log.d(TAG, "Transaction success: cart updated"))
                        .addOnFailureListener(e -> Log.e(TAG, "Transaction failure: ", e));

            }

            if (cartListener != null) {
                cartListener.onQuantityChanged();
            }
        });
    }


    private String getDocId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users")
                    .whereEqualTo("Phone Number", currentUser.getPhoneNumber())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String docID = document.getId();

                                    Preferences preferences = Preferences.userRoot();
                                    preferences.put("docId", docID);

                                    // Break out of the loop once the user is found
                                    break;
                                }
                            } else {
                                Log.e(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }

        return Preferences.userRoot().get("docId", "");
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        ItemWaitlistBinding binding;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemWaitlistBinding.bind(itemView);
        }
    }
}
