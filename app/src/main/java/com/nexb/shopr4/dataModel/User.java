package com.nexb.shopr4.dataModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Christian
 */
public class User {
    private String UserID = "";
    private String UserName = "New user";

    private ArrayList<String> ownLists = new ArrayList<>();

    public ArrayList<String> getOwnListNames() { return ownListNames; }
    public void setOwnListNames(ArrayList<String> ownListNames) { this.ownListNames = ownListNames; }

    private ArrayList<String> ownListNames = new ArrayList<>();
    private ArrayList<ForeignUserlist> foreignLists = new ArrayList<>();
    private String activeList = "TestList";
    private ArrayList<DictionaryItem> userDictionary = new ArrayList<>();
    private ArrayList<String> userUnits = new ArrayList<>();
    private ArrayList<String> userCategories = new ArrayList<>();

    public User(){
        //For Testing purposes
//        ownLists.add("Shoplist3");
//        ownLists.add("Shoplist4");
//        userDictionary.add(new DictionaryItem());
    }


    public String getUserID() {
        return UserID;
    }
    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserName() {
        return UserName;
    }
    public void setUserName(String userName) {
        UserName = userName;
    }

    public ArrayList<String> getOwnLists() {
        return ownLists;
    }
    public void setOwnLists(ArrayList<String> ownLists) {
        this.ownLists = ownLists;
    }

    public ArrayList<ForeignUserlist> getForeignLists() {
        return foreignLists;
    }
    public void setForeignLists(ArrayList<ForeignUserlist> foreignLists) {        this.foreignLists = foreignLists;    }

    
    public String getActiveList() {       return activeList;    }
    public void setActiveList(String activeList) {        this.activeList = activeList;    }

    public void addOwnList(String id, String name) {
        ownLists.add(id); ownListNames.add(name);
    }

    public ArrayList<DictionaryItem> getUserDictionary() {
        return userDictionary;
    }

    public void setUserDictionary(ArrayList<DictionaryItem> userDictionary) {
        this.userDictionary = userDictionary;
    }

    public ArrayList<String> getUserCategories() {
        return userCategories;
    }

    public void setUserCategories(ArrayList<String> userCategories) {
        this.userCategories = userCategories;
    }

    public ArrayList<String> getUserUnits() {
        return userUnits;
    }

    public void setUserUnits(ArrayList<String> userUnits) {
        this.userUnits = userUnits;
    }
}
