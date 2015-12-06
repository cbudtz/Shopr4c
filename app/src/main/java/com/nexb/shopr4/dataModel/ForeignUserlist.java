package com.nexb.shopr4.dataModel;

import java.util.ArrayList;

/**
 * @author Christian
 */
public class ForeignUserlist {
    private String UserName = "noName";
    private ArrayList<String> ShopListIDs = new ArrayList<>();
    private String listName = "";

    public ForeignUserlist() {
    }

    public ForeignUserlist(String userName, ArrayList<String> shopListIDs, String listName) {
        UserName = userName;
        ShopListIDs = shopListIDs;
        this.listName = listName;
    }

    public ArrayList<String> getShopListIDs() {
        return ShopListIDs;
    }

    public void setShopListIDs(ArrayList<String> shopListIDs) {
        ShopListIDs = shopListIDs;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }
}
