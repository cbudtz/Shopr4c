package com.nexb.shopr4;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.nexb.shopr4.dataModel.Category;
import com.nexb.shopr4.dataModel.Dictionary;
import com.nexb.shopr4.dataModel.ListItem;
import com.nexb.shopr4.dataModel.ShopList;
import com.nexb.shopr4.dataModel.SuperMarket;
import com.nexb.shopr4.dataModel.User;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Christian on 04-12-2015.
 */
@SuppressWarnings("FieldCanBeLocal")
public class FirebaseHandler implements IDataBaseController{
    //Data objects
    private User activeUser = new User();
    private String activeShopListID = "";
    private ShopList activeShopList = new ShopList();
    private SuperMarket activeSuperMarket;

    //FireBase references
    private Activity mainActivity;
    private String fireBaseURL;
    private Firebase firebaseRoot;//root folder
    private Firebase firebaseUserRef; //User folder
    private Firebase firebaseShopListRef; //ShopList folder

    private Firebase firebaseActiveUserRef; // Active user ref
    private Firebase firebaseActiveShopListRef; //Active shoplist ref
    private Firebase firebaseActiveSupermarketRef;

    private UserValueEventListener firebaseUserValueEventListener;
    private ShopListValueEventListener firebaseShoplistValueEventListener;
    private SuperMarketValueEventListener firebaseSupermarketValueEventListener;

    //Listeners
    private ArrayList<IUserDataListener> userDataListeners = new ArrayList<>();
    private ArrayList<IShopListListener> shopListListeners = new ArrayList<>();
    private ArrayList<ISuperMarketListener> superMarketListeners = new ArrayList<>();


    public FirebaseHandler(Activity mainActivity, String dataBaseUrl){
        this.setContext(mainActivity, dataBaseUrl);

        //DEBUG
        setActiveSuperMarket("RemaGammelmosevej261");

    }

    @Override
    public void setContext(Activity mainActivity, String dataBaseUrl) {
        this.mainActivity = mainActivity;
        this.fireBaseURL = dataBaseUrl;
//        Firebase.setAndroidContext(mainActivity);
//        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        firebaseRoot = new Firebase(fireBaseURL);
        firebaseUserRef = firebaseRoot.child(mainActivity.getString(R.string.userDir));
        firebaseShopListRef = firebaseRoot.child(mainActivity.getString(R.string.shopListDir));
        //Find Unique ID and listen to relevant location:
        String userId = resolveUserId();
        firebaseActiveUserRef = firebaseUserRef.child(userId);
        firebaseUserValueEventListener = new UserValueEventListener();
        firebaseActiveUserRef.addValueEventListener(firebaseUserValueEventListener);
    }

    @Override
    public String createNewShopList() {
        Firebase newListRef = firebaseShopListRef.push();
        ShopList newList = new ShopList();
        newList.setId(newListRef.getKey());
        //add new shopList
        newListRef.setValue(newList);
        activeUser.addOwnList(newListRef.getKey(), "New ShopList");
        firebaseActiveUserRef.setValue(activeUser);
        return newListRef.getKey();
    }

    @Override
    public void setActiveList(String shopListID) {
        activeUser.setActiveList(shopListID);
        firebaseActiveUserRef.setValue(activeUser);

    }

    @Override
    public void setActiveShopListName(String shopListName) {
        activeShopList.setName(shopListName);
        firebaseActiveShopListRef.setValue(activeShopList);

    }

    @Override
    public void deleteList(String shopListID) {
            for(String listID : activeUser.getOwnLists()){
                if (listID == shopListID) activeUser.getOwnLists().remove(listID);
            }
        firebaseActiveUserRef.setValue(activeUser);
    }


    @Override
    public void addCategory(String name) {
        Category cat = new Category();
        cat.setName(name);
        activeShopList.getCategories().add(cat);
        updateActiveList();
    }

    @Override
    public void updateCategoryName(int catID, String newName) {
        activeShopList.getCategories().get(catID).setName(newName);
        updateActiveList();
    }

    @Override
    public void deleteCategory(int catID) {
        activeShopList.getCategories().remove(catID);
        updateActiveList();
    }

    @Override
    public void insertCategory(int catID, Category category) {
        activeShopList.getCategories().add(catID, category);
        updateActiveList();
    }

    @Override
    public void addItemToActiveList(String categoryName, ListItem listItem) {
        for (Category c : activeShopList.getCategories()) {
            if (c.getName().equalsIgnoreCase(categoryName)) {
                //Found category in Shoplist
                c.getItems().add(listItem);
                updateActiveList();
                return;
            }
        }
        Category newCat = new Category(categoryName);
        newCat.getItems().add(listItem);
        activeShopList.getCategories().add(newCat);
        updateActiveList();
    }

    @Override
    public void deleteItem(int catID, int itemID) {
        activeShopList.getCategories().get(catID).getItems().remove(itemID);

    }

    @Override
    public void setActiveSuperMarket(String SuperMarketID) {
        if (firebaseActiveSupermarketRef != null) firebaseActiveSupermarketRef.removeEventListener(firebaseSupermarketValueEventListener);
        firebaseActiveSupermarketRef = firebaseRoot.child(mainActivity.getString(R.string.supermarketDir)).child(SuperMarketID);
        firebaseSupermarketValueEventListener = new SuperMarketValueEventListener();
        firebaseActiveSupermarketRef.addValueEventListener(firebaseSupermarketValueEventListener);

    }


    @Override
    public void addUserDataListener(IUserDataListener userDataListener) {
        userDataListeners.add(userDataListener);
        userDataListener.userdataChanged(activeUser);

    }

    @Override
    public void addActiveShopListListener(IShopListListener shopListListener) {
        shopListListeners.add(shopListListener);
        shopListListener.shopListDataChanged(activeShopList);
    }

    @Override
    public void addActiveSuperMarketListener(ISuperMarketListener superMarketListener) {
        superMarketListeners.add(superMarketListener);
        superMarketListener.superMarketChanged(activeSuperMarket);
    }

    //Look in phones Accounts

    private void updateActiveList() {
        firebaseActiveShopListRef.setValue(activeShopList);
    }

    private String resolveUserId() {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        String userString = p.getString("userName", null);
        if (userString == null) {
            //No userID found in prefManager - make a new one!
            AccountManager manager = (AccountManager) mainActivity.getSystemService(Context.ACCOUNT_SERVICE);
            Account[] list = manager.getAccountsByType("com.google");
            //Find some userID in phone:
            if (list != null && list.length > 0 && list[0] != null) {
                String id = list[0].name;
                userString = id.replace('.', ':');
            } else {
                userString = Settings.Secure.getString(mainActivity.getContentResolver(), Settings.Secure.ANDROID_ID + "");
            }
            p.edit().putString("userName", userString);
        }
        return userString;
    }


//Listen to Database
//Listen to User changes
    private class UserValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            activeUser = dataSnapshot.getValue(User.class);
            if (activeUser == null){
                activeUser = new User();
                initializeStandardCategories(activeUser);
                initializeStandardDictionary(activeUser);
                initializeStandardUnits(activeUser);
                activeUser.setUserID(resolveUserId());
                firebaseActiveUserRef.setValue(activeUser); //Will trigger secondary callback!
            }
            //Check users activeList:
            if (!activeShopListID.equals(activeUser.getActiveList())) {
                //Found new ShopList
                //remove oldListeners:
                if (firebaseActiveShopListRef!=null) firebaseActiveShopListRef.removeEventListener(firebaseShoplistValueEventListener);
                activeShopListID = activeUser.getActiveList();
                firebaseActiveShopListRef = firebaseShopListRef.child(activeShopListID);
                firebaseShoplistValueEventListener = new ShopListValueEventListener();
                firebaseActiveShopListRef.addValueEventListener(firebaseShoplistValueEventListener);
            };

            //Notify interested parties...
            for (IUserDataListener userdataListener :
                    userDataListeners) {
                System.out.println("FireBaseHandler - Notifying userDataListener - " + userdataListener);
                userdataListener.userdataChanged(activeUser);
            }
            System.out.println("FireBasehandler - got notified of User data Change -User: " + activeUser.getUserName() + ", " + activeUser.getUserID());
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }


    //Listen to ShopListChanges
    private class ShopListValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            activeShopList = dataSnapshot.getValue(ShopList.class);
            if (activeShopList == null){ // Check if there's no shopList
                Firebase shopListRef = firebaseShopListRef.push();
                activeShopList = new ShopList();
                activeShopList.setId(shopListRef.getKey());
                firebaseActiveShopListRef.setValue(activeShopList);
            }
            //Notify Interested Parties
            for (IShopListListener shopListListener : shopListListeners) {
                shopListListener.shopListDataChanged(activeShopList);
                System.out.println("FireBasehandler - Notifying IShopListListener: " + shopListListener);
            }
            System.out.println("FireBasehandler - got notified of shoplistChange - activeShopList: " + activeShopList.getName());
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }

    //Listen to supermarket
    private class SuperMarketValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            activeSuperMarket = dataSnapshot.getValue(SuperMarket.class);
            //Notify Interested Parties...
            for(ISuperMarketListener superMarketListener: superMarketListeners){
                superMarketListener.superMarketChanged(activeSuperMarket);
                System.out.println("FireBasehandler - notifying supermarketListeners: " +superMarketListener);
            }
            System.out.println("FireBaseHandler - got notified of SupermarketChange - activeSuperMarket: " + activeSuperMarket.getName());
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }

    //Convenience methods

    private void initializeStandardCategories(User user) {
        ArrayList<String> userCats = user.getUserCategories();
        //Ugly Hardcoding, should be // FIXME: 26-11-2015
        userCats.add("Frugt og Grønt");userCats.add("Brød");userCats.add("Konserves");
        userCats.add("Kolonial");userCats.add("Pålæg");userCats.add("Kød");
        userCats.add("Frost");
        userCats.add("Vin");userCats.add("Rengøring");userCats.add("Drikkevarer");userCats.add("Mejeri");
        userCats.add("Slik og Chips");userCats.add("Andet");
    }

    private void initializeStandardUnits(User user) {
        ArrayList<String> userUnits = user.getUserUnits();
        //Ugly Hardcoding, should be fixed later on....
        userUnits.add("stk");userUnits.add("pk");userUnits.add("bk");userUnits.add("L");
        userUnits.add("g");userUnits.add("kg");userUnits.add("ruller");
    }

    private void initializeStandardDictionary(User user) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            user.setUserDictionary(mapper.readValue(mainActivity.getAssets().open("standardDictionary.json"), Dictionary.class).getDictionaryItems());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


