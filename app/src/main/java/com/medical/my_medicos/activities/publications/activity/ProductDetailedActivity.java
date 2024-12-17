package com.medical.my_medicos.activities.publications.activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.publications.model.Product;
import com.medical.my_medicos.activities.utils.ConstantsDashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.medical.my_medicos.databinding.ActivityProductDetailedBinding;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class ProductDetailedActivity extends AppCompatActivity {
    ActivityProductDetailedBinding binding;
    Product currentProduct;
    TextView  tocartgo;
    String booktitle,booktype;
    String documentid;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String current=user.getPhoneNumber();
    ImageView sharebook;

    String bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);


        String documentId = getIntent().getStringExtra("id");
        String documentId2 = getIntent().getStringExtra("id");

        if (documentId != null && !documentId.isEmpty()) {
            getProductDetails(documentId);
        }else{
            Log.e(TAG,"DocumentId is null or empty");
            documentId2 = getIntent().getStringExtra("bookId");
            Log.e(documentId2,"print");
            getProductDetails2(documentId2);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);

            // Set the navigation bar color to your custom color
            int backgroundColor = ContextCompat.getColor(this, R.color.backgroundcolor);
            window.setNavigationBarColor(backgroundColor);

            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );

        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        showLoadForLib();

        String name = getIntent().getStringExtra("Title");
        String image = getIntent().getStringExtra("thumbnail");
        String id = getIntent().getStringExtra("id");
        double price = getIntent().getDoubleExtra("Price",0);

        Glide.with(this)
                .load(image)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 4))) // Adjust the radius and sampling for desired blur level
                .into(binding.blurimageofthebook);

        Glide.with(this)
                .load(image)
                .into(binding.image);

        getProductDetails(id);


        if (isProductInCart(id)) {
            binding.addToCartBtn.setVisibility(View.GONE);
        }

        String query = getIntent().getStringExtra("Title");

        TextView titleTextView = findViewById(R.id.titleoftheproduct);
        titleTextView.setText(query);

        ImageView backToPublicationActivity = findViewById(R.id.backtothepublicationactivity);
        backToPublicationActivity.setOnClickListener(v -> {
            finish();
        });

        binding.addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDocId();

                String docId = Preferences.userRoot().get("docId", "");

                if (!docId.isEmpty()) {
                    String url = ConstantsDashboard.GET_CART + "/" + docId + "/add/" + id;
                    Log.e("function", url);

                    JSONObject requestBody = new JSONObject();
                    MyVolleyRequest.sendPostRequest(getApplicationContext(), url, requestBody, new MyVolleyRequest.VolleyCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            Log.d("VolleyResponse", response.toString());
                        }

                        @Override
                        public void onError(String error) {
                            Log.e("VolleyError", error);
                        }
                    });
                } else {
                    Log.e("DocIdError", "Empty or not available");
                }
            }
        });
        tocartgo = findViewById(R.id.tocartgo);
        tocartgo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProductDetailedActivity.this, CartFromDetailActivity.class);
                startActivity(i);
            }
        });

        ImageButton btnHeart = findViewById(R.id.btnHeart);
        updateHeartButtonUI(id, btnHeart);
        btnHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the heart state and add/remove from cart
                if (btnHeart.getTag().equals("filled")) {
                    // Product is already in cart, so remove it
                    btnHeart.setImageResource(R.drawable.border_heart); // Change to your border heart drawable
                    btnHeart.setTag("border");
                    Toast.makeText(ProductDetailedActivity.this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                } else {
                    // Product is not in cart, so add it
                    addToCart(id);
                    btnHeart.setImageResource(R.drawable.heart_filled); // Change to your filled heart drawable
                    btnHeart.setTag("filled");
                }
            }
        });

        sharebook = findViewById(R.id.sharebook);
        sharebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createlink(current, documentid, booktitle,booktype);
            }
        });

        getProductDetails(id);
        checkIfBookIsPurchased(id);
        handleDeepLink();
    }

    private void showLoadForLib() {
        binding.loaderforlib.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.loaderforlib.setVisibility(View.GONE);
            }
        }, 2000);
    }

    private void addToCart(String id) {
        getDocId();

        String docId = Preferences.userRoot().get("docId", "");

        if (!docId.isEmpty()) {
            String url = ConstantsDashboard.GET_CART + "/" + docId + "/add/" + id;
            Log.e("function", url);

            JSONObject requestBody = new JSONObject();
            MyVolleyRequest.sendPostRequest(getApplicationContext(), url, requestBody, new MyVolleyRequest.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    Log.d("VolleyResponse", response.toString());
                }

                @Override
                public void onError(String error) {
                    Log.e("VolleyError", error);
                }
            });
        } else {
            Log.e("DocIdError", "Empty or not available");
        }
    }

    private void getDocId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users")
                    .whereEqualTo("Phone Number", currentUser.getPhoneNumber())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

    private void updateHeartButtonUI(String productId, ImageButton heartButton) {
        if (isProductInCart(productId)) {
            heartButton.setImageResource(R.drawable.heart_filled); // Your filled heart icon
            heartButton.setTag("filled");
        } else {
            heartButton.setImageResource(R.drawable.border_heart); // Your border heart icon
            heartButton.setTag("border");
        }
    }

    void getProductDetails(String documentId) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = ConstantsDashboard.GET_SPECIALITY_PRODUCT + documentId;

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    JSONObject product = object.getJSONObject("data");

                     // Extract product details
                    String author = product.getString("Author");
                    String type = product.getString("Type");
                    String description = product.getString("Description");
                    double price = product.getDouble("Price");

                    documentid = product.getString("id");
                    String title = product.getString("Title");
                    booktype = type;
                    bookId = product.getString("id");

                    // Set the UI elements with product details
                    binding.titleoftheproduct.setText(title);
                    binding.author.setText(author);
                    binding.type.setText(type);
                    binding.productDescription.setText(Html.fromHtml(description));
                    binding.productprice.setText("₹" + price);

                    currentProduct = new Product(
                            documentid,
                            title,
                            product.getString("thumbnail"),
                            author,
                            price,
                            product.getString("Type"),
                            product.getString("Category"),
                            product.getString("Subject"),
                            product.getString("URL")
                    );

                    // Adjust visibility based on the price
                    if (price == 0.0) {
                        binding.redeemBtn.setVisibility(View.VISIBLE);
                        binding.redeemBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                redeemBook(documentId);
                            }
                        });
                        binding.addToCartBtn.setVisibility(View.GONE);
                        binding.tocartgo.setVisibility(View.GONE);
                    } else {
                        binding.redeemBtn.setVisibility(View.GONE);
                        binding.addToCartBtn.setVisibility(View.VISIBLE);
                        binding.tocartgo.setVisibility(View.VISIBLE);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // Handle error if needed
        });

        queue.add(request);
    }

    void getProductDetails2(String documentId2) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = ConstantsDashboard.GET_SPECIALITY_PRODUCT + documentId2;

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    JSONObject product = object.getJSONObject("data");

                    // Extract product details
                    String title = product.getString("Title");
                    String author = product.getString("Author");
                    String type = product.getString("Type");
                    String description = product.getString("Description");
                    double price = product.getDouble("Price");
                    String imageUrl = product.getString("thumbnail");

                    // Set the UI elements with product details
                    binding.titleoftheproduct.setText(title);
                    binding.author.setText(author);
                    binding.type.setText(type);
                    binding.productDescription.setText(Html.fromHtml(description));
                    binding.productprice.setText("₹" + price);

                    Glide.with(this)
                            .load(imageUrl)
                            .into(binding.image);

                    Glide.with(this)
                            .load(imageUrl)
                            .into(binding.blurimageofthebook);

                    currentProduct = new Product(
                            documentid,
                            title,
                            product.getString("thumbnail"),
                            author,
                            price,
                            product.getString("Type"),
                            product.getString("Category"),
                            product.getString("Subject"),
                            product.getString("URL")
                    );

                    // Adjust visibility based on the price
                    if (price == 0.0) {
                        binding.redeemBtn.setVisibility(View.VISIBLE);
                        binding.redeemBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                redeemBook(documentId2);
                            }
                        });
                        binding.addToCartBtn.setVisibility(View.GONE);
                        binding.tocartgo.setVisibility(View.GONE);
                    } else {
                        binding.redeemBtn.setVisibility(View.GONE);
                        binding.addToCartBtn.setVisibility(View.VISIBLE);
                        binding.tocartgo.setVisibility(View.VISIBLE);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // Handle error if needed
        });

        queue.add(request);
    }

    private void checkIfBookIsPurchased(String bookId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .whereEqualTo("Phone Number", currentUser.getPhoneNumber())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                List<String> purchasedBooks = (List<String>) document.get("purchased");
                                if (purchasedBooks != null && purchasedBooks.contains(bookId)) {
                                    // If the book is already purchased, disable the redeem button and change its text
                                    binding.redeemBtn.setEnabled(false);
                                    binding.redeemBtn.setText("Claimed Already");
                                    return; // Exit once found
                                }
                            }
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        }
    }

    private void redeemBook(String bookId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String phoneNumber = currentUser.getPhoneNumber();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // You need to find the user's document ID based on their phone number or another unique identifier
            db.collection("users")
                    .whereEqualTo("Phone Number", phoneNumber)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String userDocId = document.getId();

                                    db.collection("users").document(userDocId)
                                            .update("purchased", FieldValue.arrayUnion(bookId))
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d(TAG, "Book successfully redeemed!");
                                                // Disable the button and change its text
                                                runOnUiThread(() -> {
                                                    binding.redeemBtn.setEnabled(false);
                                                    binding.redeemBtn.setText("Claimed Already");
                                                });
                                            })

                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error redeeming book", e);
                                                }
                                            });

                                    break;
                                }
                            } else {
                                Log.e(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    private boolean isProductInCart(String productId) {
        // Assuming you have a method to fetch the cart product IDs
        ArrayList<String> cartProductIds = getCartProductIds();
        return cartProductIds.contains(productId);
    }

    public void createlink(String custid, String bookId,String booktitle, String bookdescription){
        Log.e("main","create link");

        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.mymedicos.in/bookdetails?custid=" + custid + "&bookId=" + bookId))
                .setDomainUriPrefix("https://app.mymedicos.in")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();
        Log.e("main"," Long refer "+ dynamicLink.getUri());

        createreferlink(custid, bookId);
    }
    public void createreferlink(String custid, String bookId) {
        if (bookId == null) {
            Log.e(TAG, "Book ID is null. Cannot create reference link.");
            return;
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Publications").document(bookId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String bookTitle = documentSnapshot.getString("Title");
                String bookThumbnail = documentSnapshot.getString("thumbnail");

                String encodedBookTitle = encode(bookTitle);
                String encodedBookThumbnail = encode(bookThumbnail); // Assuming you want to include this in the URL for some reason

                // The shared text format
                String sharelinktext = bookTitle + "\n\n For entire detail visit: " +
                        "https://app.mymedicos.in/?" +
                        "link=http://www.mymedicos.in/bookdetails?bookId=" + bookId +
                        "&st=" + encodedBookTitle +
                        "&apn=" + getPackageName() +
                        "&si=" + encodedBookThumbnail; // This will not make the thumbnail appear in the share text directly

                Log.e("NewsDetailedActivity", "Sharelink - " + sharelinktext);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, sharelinktext);
                intent.setType("text/plain");
                startActivity(intent);

            } else {
                Log.e(TAG, "No such document with documentId: " + bookId);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error fetching news details for documentId: " + bookId, e);
        });
    }
    private String encode(String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return URLEncoder.encode(s);
        }
    }
    private void handleDeepLink() {
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    if (pendingDynamicLinkData != null) {
                        Uri deepLink = pendingDynamicLinkData.getLink();
                        if (deepLink != null) {
                            String bookId = deepLink.getQueryParameter("bookId");
                            Intent intent = getIntent();
                            intent.putExtra("bookId", bookId);
                            setIntent(intent);
                        }
                    }
                })
                .addOnFailureListener(this, e -> Log.w("DeepLink", "getDynamicLink:onFailure", e));
    }

    private ArrayList<String> getCartProductIds() {
        return new ArrayList<>();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}