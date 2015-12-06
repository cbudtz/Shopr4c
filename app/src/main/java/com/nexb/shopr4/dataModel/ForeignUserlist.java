package com.nexb.shopr4.dataModel;

import java.util.ArrayList;

/**
 * @author Christian
 */
public class ForeignUserlist {
    private String userName = "noName";
    private ArrayList<String> shopListIDs = new ArrayList<>();
    private String listName = "";

    public ForeignUserlist() {
    }

    public ForeignUserlist(String userName, ArrayList<String> shopListIDs, String listName) {
        this.userName = userName;
        this.shopListIDs = shopListIDs;
        this.listName = listName;
    }

    public ArrayList<String> getShopListIDs() {
        return shopListIDs;
    }

    public void setShopListIDs(ArrayList<String> shopListIDs) {
        this.shopListIDs = shopListIDs;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }
}
