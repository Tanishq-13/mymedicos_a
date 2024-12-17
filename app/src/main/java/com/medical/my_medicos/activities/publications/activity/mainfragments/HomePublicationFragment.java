package com.medical.my_medicos.activities.publications.activity.mainfragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.publications.adapters.BookoftheDayAdapter;
import com.medical.my_medicos.activities.publications.adapters.ProductAdapter;
import com.medical.my_medicos.activities.publications.adapters.RecentHomeProductsAdapter;
import com.medical.my_medicos.activities.publications.adapters.SponsoredProductAdapter;
import com.medical.my_medicos.activities.publications.model.Product;
import com.medical.my_medicos.activities.publications.utils.Constants;
import com.medical.my_medicos.activities.utils.ConstantsDashboard;
import com.medical.my_medicos.databinding.FragmentHomePublicationBinding;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class HomePublicationFragment extends Fragment {

    private FragmentHomePublicationBinding binding;
    private Context context;
    private BookoftheDayAdapter bookofthedayAdapter;
    private ArrayList<Product> bookoftheday;
    private ProductAdapter productAdapter;
    private ArrayList<Product> products;
    private RecentHomeProductsAdapter recentHomeProductsAdapter;
    private ArrayList<Product> recentHomeProducts;
    private SponsoredProductAdapter sponsoredProductsAdapter;
    private ArrayList<Product> sponsoredProduct;
    private LottieAnimationView loader;

    private static final String PREFS_NAME = "HomePublicationPrefs";
    private static final String KEY_BOOK_OF_THE_DAY = "BookOfTheDay";
    private static final String KEY_RECENT_HOME_PRODUCTS = "RecentHomeProducts";
    private static final String KEY_SPONSORED_PRODUCTS = "SponsoredProducts";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomePublicationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();

        if (context != null) {
            initProducts();
            initTopExploredReadables();
            initSlider();
            initSponsorSlider();
            initSponsorProduct();
            initBookReadables();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Clean up the binding reference
    }

    private void saveDataToPreferences(String key, String jsonData) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, jsonData);
        editor.apply();
    }

    private String loadDataFromPreferences(String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(key, null);
    }

    private void initSlider() {
        getRecentOffers();
    }

    void getRecentOffers() {
        if (!isAdded()) {
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(context); // Use context

        StringRequest request = new StringRequest(Request.Method.GET, ConstantsDashboard.GET_PUBLICATION_SLIDER_URL, response -> {
            try {
                JSONArray newssliderArray = new JSONArray(response);
                for (int i = 0; i < newssliderArray.length(); i++) {
                    JSONObject childObj = newssliderArray.getJSONObject(i);
                    binding.libcarousel.addData(
                            new CarouselItem(
                                    childObj.getString("url"),
                                    childObj.getString("action")
                            )
                    );
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // Handle error if needed
        });

        queue.add(request);
    }

    //....... Sponsored Slider.....

    private void initSponsorSlider() {
        getRecentSliderSponsored();
    }

    void getRecentSliderSponsored() {
        if (!isAdded()) {
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.GET, ConstantsDashboard.GET_SPONSORS_SLIDER_URL, response -> {
            try {
                JSONArray newssliderArray = new JSONArray(response);
                if (newssliderArray.length() > 0) {
                    JSONObject childObj = newssliderArray.getJSONObject(0);
                    String imageUrl = childObj.getString("url");
                    loadImageIntoView(imageUrl);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // Handle error if needed
        });
        queue.add(request);
    }

    private void loadImageIntoView(String imageUrl) {
        if (!isAdded()) {
            return;
        }
        Glide.with(context)
                .load(imageUrl)
                .into(binding.imagesponsor);
    }

    private void initTopExploredReadables() {
        recentHomeProducts = new ArrayList<>();
        recentHomeProductsAdapter = new RecentHomeProductsAdapter(context, recentHomeProducts);

        String savedData = loadDataFromPreferences(KEY_RECENT_HOME_PRODUCTS);
        if (savedData != null) {
            try {
                JSONArray recenthomeproductsArray = new JSONArray(savedData);
                for (int i = 0; i < recenthomeproductsArray.length(); i++) {
                    JSONObject childObj = recenthomeproductsArray.getJSONObject(i);
                    JSONObject recenthomeproductObj = childObj.getJSONObject("data");

                    Product recenthomeproduct = new Product(
                            recenthomeproductObj.getString("id"),
                            recenthomeproductObj.getString("Title"),
                            recenthomeproductObj.getString("thumbnail"),
                            recenthomeproductObj.getString("Author"),
                            recenthomeproductObj.getDouble("Price"),
                            recenthomeproductObj.getString("Type"),
                            recenthomeproductObj.getString("Category"),
                            recenthomeproductObj.getString("Subject"),
                            recenthomeproductObj.getString("URL")
                    );

                    recentHomeProducts.add(recenthomeproduct);
                }
                recentHomeProductsAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            getTopExploredReadables();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        binding.recentaddedbooksList.setLayoutManager(layoutManager);
        binding.recentaddedbooksList.setAdapter(recentHomeProductsAdapter);
    }

    private void getTopExploredReadables() {
        if (!isAdded()) {
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(context);

        String url = ConstantsDashboard.GET_SPECIALITY_ALL_PRODUCT_HOME;
        @SuppressLint("NotifyDataSetChanged") StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            if (!isAdded() || context == null) {
                return;
            }
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    JSONArray recenthomeproductsArray = object.getJSONArray("data");
                    recentHomeProducts.clear();
                    for (int i = 0; i < recenthomeproductsArray.length(); i++) {
                        JSONObject childObj = recenthomeproductsArray.getJSONObject(i);
                        JSONObject recenthomeproductObj = childObj.getJSONObject("data");

                        Product recenthomeproduct = new Product(
                                recenthomeproductObj.getString("id"),
                                recenthomeproductObj.getString("Title"),
                                recenthomeproductObj.getString("thumbnail"),
                                recenthomeproductObj.getString("Author"),
                                recenthomeproductObj.getDouble("Price"),
                                recenthomeproductObj.getString("Type"),
                                recenthomeproductObj.getString("Category"),
                                recenthomeproductObj.getString("Subject"),
                                recenthomeproductObj.getString("URL")
                        );

                        recentHomeProducts.add(recenthomeproduct);
                    }
                    recentHomeProductsAdapter.notifyDataSetChanged();
                    saveDataToPreferences(KEY_RECENT_HOME_PRODUCTS, recenthomeproductsArray.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> { });

        queue.add(request);
    }

    private void initBookReadables() {
        bookoftheday = new ArrayList<>();
        bookofthedayAdapter = new BookoftheDayAdapter(context, bookoftheday);

        String savedData = loadDataFromPreferences(KEY_BOOK_OF_THE_DAY);
        if (savedData != null) {
            try {
                JSONObject savedBookObj = new JSONObject(savedData);
                Product savedBook = new Product(
                        savedBookObj.getString("id"),
                        savedBookObj.getString("Title"),
                        savedBookObj.getString("thumbnail"),
                        savedBookObj.getString("Author"),
                        savedBookObj.getDouble("Price"),
                        savedBookObj.getString("Type"),
                        savedBookObj.getString("Category"),
                        savedBookObj.getString("Subject"),
                        savedBookObj.getString("URL")
                );
                bookoftheday.add(savedBook);
                bookofthedayAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            getBookReadables();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        binding.bookoftheday.setLayoutManager(layoutManager);
        binding.bookoftheday.setAdapter(bookofthedayAdapter);
    }

    private void getBookReadables() {
        if (!isAdded()) {
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = ConstantsDashboard.GET_SPECIALITY_ALL_PRODUCT;

        @SuppressLint("NotifyDataSetChanged") StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            if (!isAdded() || context == null) {
                return;
            }
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    JSONArray dataArray = object.getJSONArray("data");
                    List<Product> allProducts = new ArrayList<>();
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject childObj = dataArray.getJSONObject(i);
                        JSONObject productObj = childObj.getJSONObject("data");
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
                        allProducts.add(product);
                    }

                    Product selectedBook = selectRandomBook(allProducts, context);
                    if (selectedBook != null) {
                        bookoftheday.clear();
                        bookoftheday.add(selectedBook);
                        bookofthedayAdapter.notifyDataSetChanged();
                        saveDataToPreferences(KEY_BOOK_OF_THE_DAY, selectedBook.toJson().toString());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // Handle error
        });
        queue.add(request);
    }

    private Product selectRandomBook(List<Product> products, Context context) {
        Set<String> displayedBookIds = loadDisplayedBookIds(context);

        List<Product> filteredProducts = new ArrayList<>();
        for (Product product : products) {
            if (!displayedBookIds.contains(product.getId())) {
                filteredProducts.add(product);
            }
        }

        if (!filteredProducts.isEmpty()) {
            int randomIndex = new Random().nextInt(filteredProducts.size());
            Product selectedBook = filteredProducts.get(randomIndex);
            updateDisplayedBookIds(selectedBook.getId(), context);
            return selectedBook;
        }
        return null;
    }

    private Set<String> loadDisplayedBookIds(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("BookDisplayHistory", Context.MODE_PRIVATE);
        return prefs.getStringSet("DisplayedBookIds", new HashSet<>());
    }

    private void updateDisplayedBookIds(String newBookId, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("BookDisplayHistory", Context.MODE_PRIVATE);
        Set<String> displayedBookIds = new HashSet<>(prefs.getStringSet("DisplayedBookIds", new HashSet<>()));
        if (displayedBookIds.size() >= 3) {
            List<String> tempList = new ArrayList<>(displayedBookIds);
            tempList.remove(0); // Remove the oldest entry
            displayedBookIds = new HashSet<>(tempList);
        }
        displayedBookIds.add(newBookId);
        prefs.edit().putStringSet("DisplayedBookIds", displayedBookIds).apply();
    }
    private void initSponsorProduct() {
        sponsoredProduct = new ArrayList<>();
        sponsoredProductsAdapter = new SponsoredProductAdapter(context, sponsoredProduct);

        String savedData = loadDataFromPreferences(KEY_SPONSORED_PRODUCTS);
        if (savedData != null) {
            try {
                JSONArray sponsoredproductsArray = new JSONArray(savedData);
                for (int i = 0; i < sponsoredproductsArray.length(); i++) {
                    JSONObject childObj = sponsoredproductsArray.getJSONObject(i);
                    JSONObject sponsorproductObj = childObj.getJSONObject("data");

                    Product sponsoredproduct = new Product(
                            sponsorproductObj.getString("id"),
                            sponsorproductObj.getString("Title"),
                            sponsorproductObj.getString("thumbnail"),
                            sponsorproductObj.getString("Author"),
                            sponsorproductObj.getDouble("Price"),
                            sponsorproductObj.getString("Type"),
                            sponsorproductObj.getString("Category"),
                            sponsorproductObj.getString("Subject"),
                            sponsorproductObj.getString("URL")
                    );

                    sponsoredProduct.add(sponsoredproduct);
                }
                sponsoredProductsAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            getSponsorProduct();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        binding.specialproductList.setLayoutManager(layoutManager);
        binding.specialproductList.setAdapter(sponsoredProductsAdapter);
    }

    private void getSponsorProduct() {
        if (!isAdded()) {
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(context);

        String url = ConstantsDashboard.GET_SPECIALITY_ALL_PRODUCT_SPONSORED;
        @SuppressLint("NotifyDataSetChanged") StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            if (!isAdded() || context == null) {
                return;
            }
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    JSONArray sponsoredproductsArray = object.getJSONArray("data");
                    sponsoredProduct.clear();
                    for (int i = 0; i < sponsoredproductsArray.length(); i++) {
                        JSONObject childObj = sponsoredproductsArray.getJSONObject(i);
                        JSONObject sponsorproductObj = childObj.getJSONObject("data");

                        Product sponsoredproduct = new Product(
                                sponsorproductObj.getString("id"),
                                sponsorproductObj.getString("Title"),
                                sponsorproductObj.getString("thumbnail"),
                                sponsorproductObj.getString("Author"),
                                sponsorproductObj.getDouble("Price"),
                                sponsorproductObj.getString("Type"),
                                sponsorproductObj.getString("Category"),
                                sponsorproductObj.getString("Subject"),
                                sponsorproductObj.getString("URL")
                        );

                        sponsoredProduct.add(sponsoredproduct);
                    }
                    sponsoredProductsAdapter.notifyDataSetChanged();
                    saveDataToPreferences(KEY_SPONSORED_PRODUCTS, sponsoredproductsArray.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> { });

        queue.add(request);
    }

    private void initProducts() {
        products = new ArrayList<>();
        productAdapter = new ProductAdapter(context, products);

        String savedData = loadDataFromPreferences("RecentProducts");
        if (savedData != null) {
            try {
                JSONArray productsArray = new JSONArray(savedData);
                for (int i = 0; i < productsArray.length(); i++) {
                    JSONObject childObj = productsArray.getJSONObject(i);
                    JSONObject productObj = childObj.getJSONObject("data");

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
                productAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            getRecentProducts();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        binding.productList.setLayoutManager(layoutManager);
        binding.productList.setAdapter(productAdapter);
    }

    private void getRecentProducts() {
        if (!isAdded()) {
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(context);

        String url = ConstantsDashboard.GET_SPECIALITY_ALL_PRODUCT;
        @SuppressLint("NotifyDataSetChanged") StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            if (!isAdded() || context == null) {
                return;
            }
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    JSONArray productsArray = object.getJSONArray("data");
                    products.clear();
                    for (int i = 0; i < productsArray.length(); i++) {
                        JSONObject childObj = productsArray.getJSONObject(i);
                        JSONObject productObj = childObj.getJSONObject("data");

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
                    productAdapter.notifyDataSetChanged();
                    saveDataToPreferences("RecentProducts", productsArray.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> { });

        queue.add(request);
    }
}
