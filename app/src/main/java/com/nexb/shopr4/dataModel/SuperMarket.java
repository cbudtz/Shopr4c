package com.nexb.shopr4.dataModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * @author Christian on 04-12-2015.
 */
public class SuperMarket {

    private String name = "";
    private double latitude = 0.0;
    private double longitude = 0.0;
    private LinkedHashMap<String, ArrayList<String>> categories = new LinkedHashMap<>();



    public void addCategory(String categoryName){
        categories.put(categoryName, new ArrayList<String>());
    }
    public void addItemToCategory(String categoryName, String itemName){
        ArrayList<String> categoryList = categories.get(categoryName);
        if (categoryList != null){
            categoryList.add(itemName);
        }
    }

    public String getName() { return name; }    public void setName(String name) { this.name = name; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public LinkedHashMap<String, ArrayList<String>> getCategories() { return categories; }
    public void setCategories(LinkedHashMap<String, ArrayList<String>> categories) { this.categories = categories; }

}
