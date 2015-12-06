package com.nexb.shopr4.dataModel;

import android.location.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @author Christian on 04-12-2015.
 */
public class SuperMarket {

    private String name = "";
    private double lattitude = 0.0;
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

    public double getLattitude() { return lattitude; }
    public void setLattitude(double lattitude) { this.lattitude = lattitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public LinkedHashMap<String, ArrayList<String>> getCategories() { return categories; }
    public void setCategories(LinkedHashMap<String, ArrayList<String>> categories) { this.categories = categories; }

}
