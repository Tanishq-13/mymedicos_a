package com.medical.my_medicos.activities.publications.adapters;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.medical.my_medicos.activities.publications.activity.MyVolleyRequest;
import com.medical.my_medicos.activities.publications.model.Product;
import com.medical.my_medicos.activities.utils.ConstantsDashboard;
import com.medical.my_medicos.databinding.ItemCartBinding;
import com.medical.my_medicos.databinding.QuantityDialogBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.prefs.Preferences;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    Context context;
    ArrayList<Product> products;
    CartListener cartListener;

    public interface CartListener {
        void onQuantityChanged();
    }

    public CartAdapter(Context context, ArrayList<Product> products, CartListener cartListener) {
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
        return new CartViewHolder(LayoutInflater.from(context).inflate(com.medical.my_medicos.R.layout.item_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = products.get(position);
        Glide.with(context)
                .load(product.getThumbnail())
                .into(holder.binding.image);

        holder.binding.name.setText(product.getTitle());
        holder.binding.price.setText("INR " + product.getPrice());
        holder.binding.type.setText(product.getSubject());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                QuantityDialogBinding quantityDialogBinding = QuantityDialogBinding.inflate(LayoutInflater.from(context));

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setView(quantityDialogBinding.getRoot())
                        .create();

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));

                quantityDialogBinding.productName.setText(product.getTitle());
                quantityDialogBinding.productPrice.setText("Type: " + product.getType());
                String type = product.getType();

                quantityDialogBinding.removeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String docId = Preferences.userRoot().get("docId", "");

                        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());

                        String url = ConstantsDashboard.GET_CART + "/" + docId + "/remove" + '/' + "0ACmQESDsQQUZ8CduPYf";
                        JSONObject requestBody = new JSONObject();
                        MyVolleyRequest.sendPostRequest(context.getApplicationContext(), url, requestBody, new MyVolleyRequest.VolleyCallback() {

                            @Override
                            public void onSuccess(JSONObject response) {
                                Log.d("product Removed Successfully", response.toString());

                            }

                            @Override
                            public void onError(String error) {
                                Log.e("VolleyError", error);
                            }
                        });
                    }
                });
                quantityDialogBinding.saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
//
                    }
                });

                dialog.show();
            }
        });
    }

    private void getDocId() {
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
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        ItemCartBinding binding;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCartBinding.bind(itemView);
        }
    }
}
