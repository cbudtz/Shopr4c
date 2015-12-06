package com.nexb.shopr4.dataModel;

import java.util.ArrayList;

/**
 * Created by Christian on 12-11-2015.
 */
public class Category {
    private String name="new category";
    private ArrayList<ListItem> items = new ArrayList<>();

    public Category() {
            }

    public Category(String name) {
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ListItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<ListItem> items) {
        this.items = items;
    }
}
