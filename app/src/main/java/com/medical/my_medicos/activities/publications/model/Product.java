package com.medical.my_medicos.activities.publications.model;

import com.hishd.tinycart.model.Item;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;

public class Product implements Item, Serializable {

    private String Title, thumbnail, Author, Type, Category, Subject, URL, id;
    private Double Price;

    public Product() {
    }

    public Product(String documentid, String title, String thumbnail, String author, Double price, String type, String category, String subject, String url) {
        this.Title = title;
        this.thumbnail = thumbnail;
        this.Author = author;
        this.Price = price;
        this.Type = type;
        this.Category = category;
        this.id = documentid;
        this.Subject = subject;
        this.URL = url;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        this.Author = author;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(Double price) {
        this.Price = price;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        this.Type = type;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        this.Category = category;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        this.Subject = subject;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String url) {
        this.URL = url;
    }

    @Override
    public BigDecimal getItemPrice() {
        return BigDecimal.valueOf(Price);
    }

    @Override
    public String getItemName() {
        return Title;
    }

    // Convert Product to JSONObject
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("Title", Title);
            jsonObject.put("thumbnail", thumbnail);
            jsonObject.put("Author", Author);
            jsonObject.put("Price", Price);
            jsonObject.put("Type", Type);
            jsonObject.put("Category", Category);
            jsonObject.put("Subject", Subject);
            jsonObject.put("URL", URL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    // Create Product from JSONObject
    public static Product fromJson(JSONObject jsonObject) {
        try {
            return new Product(
                    jsonObject.getString("id"),
                    jsonObject.getString("Title"),
                    jsonObject.getString("thumbnail"),
                    jsonObject.getString("Author"),
                    jsonObject.getDouble("Price"),
                    jsonObject.getString("Type"),
                    jsonObject.getString("Category"),
                    jsonObject.getString("Subject"),
                    jsonObject.getString("URL")
            );
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
