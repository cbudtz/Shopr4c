package com.nexb.shopr4;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.nexb.shopr4.Utility.SuperMarketFactory;
import com.nexb.shopr4.dataModel.Category;
import com.nexb.shopr4.dataModel.Dictionary;
import com.nexb.shopr4.dataModel.DictionaryItem;
import com.nexb.shopr4.dataModel.ForeignUserlist;
import com.nexb.shopr4.dataModel.ListItem;
import com.nexb.shopr4.dataModel.ShopList;
import com.nexb.shopr4.dataModel.SuperMarket;
import com.nexb.shopr4.dataModel.User;
import com.nexb.shopr4.View.ShopListViewCategory;
import com.nexb.shopr4.View.ShopListViewContent;
import com.nexb.shopr4.View.ShopListViewItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static java.util.Arrays.asList;

/**
 * @author Christian on 12-11-2015.
 */
public class FireBaseController {
    public static FireBaseController instance;

    private static MainActivity activity;
    private static String url;
    //Folders
    @SuppressWarnings("FieldCanBeLocal")
    private Firebase firebaseRoot;
    private Firebase firebaseUserDir;
    private Firebase firebaseShopListDir;
    private Firebase firebaseSuperMarketDir;


    //User data:
    private User user = new User();
    private Firebase firebaseUserRef;

    //Active list
    private Firebase activeListRef;
    private ShopList activeShopList = new ShopList();

    //ArrayAdaptor to be notified on dataChange
    private ArrayAdapter<ShopListViewContent> shoplistAdaptor;
    private ArrayAdapter<DictionaryItem> dictionaryAdapter;
    private ArrayList<TextView> shopListTitleViews = new ArrayList<>();

        public void addTitleListener(TextView t){shopListTitleViews.add(t);}



    //ArrayList
    private ArrayList<ShopListViewContent> shoplistViewContents = new ArrayList<>();
    private ValueEventListener activeListListener;

    //Initialization Methods ---------------------
    public static void setContext(MainActivity mainActivity, String Firebaseurl){
        FireBaseController.activity = mainActivity;
        FireBaseController.url = Firebaseurl;
    }

    public static synchronized FireBaseController getI() {
        if (activity == null || url == null) {
            System.out.println("Must provide url and activity first!!");
            return null;
        }
        if (instance == null) {
            instance = new FireBaseController(activity, url);
            instance.init();
        }
        return instance;

    }

    private FireBaseController(MainActivity mainActivity, String url) {
        activity = mainActivity;
        FireBaseController.url = url;
        Firebase.setAndroidContext(activity);
        Firebase.getDefaultConfig().setPersistenceEnabled(false);
    }

    public void init() {
        firebaseRoot = new Firebase(url);
        firebaseUserDir = firebaseRoot.child(activity.getString(R.string.userDir));
        firebaseShopListDir = firebaseRoot.child(activity.getString(R.string.shopListDir));
        firebaseSuperMarketDir = firebaseRoot.child(activity.getString(R.string.supermarketDir));
        //DEBUG
        LinkedHashMap<String, ArrayList<String>> categories = new LinkedHashMap<>();
        categories.put("Brød", new ArrayList<String>(asList("Rugbrød", "KnækBrød", "Franskbrød", "Flutes")));
        categories.put("Frugt og Grønt", new ArrayList<String>(asList("Bananer", "Æbler", "Gulerødder", "Porrer")));

        SuperMarket s = SuperMarketFactory.generateSuperMarket("Rema",categories, 55.7558436, 12.4750958);

        firebaseSuperMarketDir.child("RemaGammelmosevej261").setValue(s);
        //DEBUG
        resolveUser();
    }

    private void resolveUser() {
        //Find user in android accounts
        //resolve UserID
        user = new User(); //Initializes to new User
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(activity);
        String userString = p.getString("userName", null);
        if (userString == null) {
            AccountManager manager = (AccountManager) activity.getSystemService(Context.ACCOUNT_SERVICE);
            Account[] list = manager.getAccountsByType("com.google");
            if (list != null && list.length > 0 && list[0] != null) {
                String id = list[0].name;
                id = id.replace('.', ':');
                System.out.println(id);
                user.setUserID(id);
                p.edit().putString("userName", id);
            } else {
                String id = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID + "");
                user.setUserID(id);
                p.edit().putString("userName",id);
            }
        } else {
            user.setUserID(userString);
        }
        System.out.println("UserID: " + user.getUserID());
        // Listen to database for changes in User!
        firebaseUserRef = firebaseUserDir.child(user.getUserID());
        System.out.println("FireBaseController - User : " + user.getUserID());
        firebaseUserRef.addValueEventListener(new UserValueEventListener());

        //Should happen on callback! --  setActiveList(user.getActiveList());
    }



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
            user.setUserDictionary(mapper.readValue(activity.getAssets().open("standardDictionary.json"), Dictionary.class).getDictionaryItems());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    //End of initialization ----------------------
    //UI interface methods -----------------
    public void setActiveList(String listID){
        //Make certain that user has at least one shopping list...
        if (listID == null) {
            listID = createNewShopList();
        };
        if (activeListRef!=null) activeListRef.removeEventListener(activeListListener);
        //Change location for active list storage
        user.setActiveList(listID);
        activeListRef = firebaseShopListDir.child(listID);
        if (MainActivity.DEBUG) System.out.println(activeListRef);
        //Listen to new locationss
        activeListListener = new ShopListValueEventListener();
        activeListRef.addValueEventListener(activeListListener);
        firebaseUserRef.setValue(user);

    }

    public String getActiveShopListID(){
        return activeShopList.getId();
    }

    //TODO clean ugly code!
    private void parseShopList() {
        ArrayList<ShopListViewContent> newShopListViewContents = new ArrayList<ShopListViewContent>();
        if (activeShopList==null) return;
        int i = 0;
        for (Category c: activeShopList.getCategories() ) {
            //add category element
            newShopListViewContents.add(new ShopListViewCategory(c.getName(), i));
            int j =0;
            for (ListItem l:c.getItems()) {
                newShopListViewContents.add(new ShopListViewItem(l.getAmount(),l.getUnit(),l.getName(),l.getState(),i,j));
                j++;
            }
            i++;
        }
        if (MainActivity.DEBUG){
            for (ShopListViewContent s: newShopListViewContents ) {
                //System.out.println("ShoplistViewItem: " + s);

            }
        }
        shoplistViewContents.clear();
        shoplistViewContents.addAll(newShopListViewContents);

    }
    // Manipulate Shoplist!! -----------------------
    public String createNewShopList(){
        //Get ref from Firebase
        Firebase newListRef = firebaseShopListDir.push();
        //Create Default list Template
        ShopList newShopList = new ShopList();
        newShopList.setId(newListRef.getKey());
        newShopList.setName("New List");
        newShopList.setCreatedByID(user.getUserID());
        newShopList.addCategory(new Category("No Category"));
        newListRef.setValue(newShopList);
        user.addOwnList(newShopList.getId(), newShopList.getName());
        if (user.getActiveList()==null) user.setActiveList(newShopList.getId());
        firebaseUserRef.setValue(user);
        //setActiveList(newListRef.getKey());
        return newListRef.getKey();

    }

    public void deleteActiveList(){
        String deleteID = user.getActiveList();
        for (String listId : user.getOwnLists()) {
            if (listId.equals(deleteID)) {
                user.getOwnLists().remove(listId);
                user.setActiveList(user.getOwnLists().get(0));
                if (user.getActiveList() == null) {
                    createNewShopList();
                    user.setActiveList(user.getOwnLists().get(0));
                }
                firebaseUserRef.setValue(user);
            }
        }

    }

    public void deleteList(String listID){
        for (String sID :user.getOwnLists()) {
            if (sID.equals(listID)) user.getOwnLists().remove(sID);
        }
    }

    public void updateActiveShoplistName(String name){
        for (String s :user.getOwnLists()) {
            if (s == activeShopList.getName()) s = name;
        }
        activeShopList.setName(name);
    }
    public void addCategory(String name){
        if (activeShopList!=null && activeShopList.getCategories()!=null) {
            activeShopList.getCategories().add(new Category(name));
            updateActiveList();
        }
    }
    public void updateCategory(int catId, Category category){
        if (activeShopList!=null && activeShopList.getCategories()!=null && activeShopList.getCategories().get(catId)!=null) {
            activeShopList.getCategories().set(catId, category);
            updateActiveList();
        }
    }

    public void updateCategoryname (int catID, String name){
        if (activeShopList!= null && activeShopList.getCategories()!=null && activeShopList.getCategories().get(catID)!=null) {
            activeShopList.getCategories().get(catID).setName(name);
            updateActiveList();
        }
    }

    public void deleteCategory(int catId){
        if (catId>0)
        activeShopList.getCategories().remove(catId);
        updateActiveList();
    }

    public void insertCategory(int catId, Category cat){
        activeShopList.getCategories().add(catId, cat);

    }

    public ArrayList<Category> getActiveCategories(){
        return activeShopList.getCategories();
    }

    public void addItemToActiveListNoCategory(ListItem l){
        if (activeShopList.getCategories().size()==0) activeShopList.addCategory(new Category("No Category"));
        activeShopList.getCategories().get(0).getItems().add(l);
        updateActiveList();
    }

    public void addItemToActiveList(String category, ListItem l){
        for (Category c : activeShopList.getCategories()) {
            if (c.getName().equalsIgnoreCase(category)){
                //Found category in Shoplist
                c.getItems().add(l);
                updateActiveList();
                return;
            }
        }
        //Category not found - put in new category
        Category newCat = new Category(category);
        activeShopList.getCategories().add(newCat);
        newCat.getItems().add(l);
        updateActiveList();
    }

    public void deleteItem(int category, int itemID){
        activeShopList.getCategories().get(category).getItems().remove(itemID);
        if (activeShopList.getCategories().get(category).getItems().size()<=0 && category!=0){
            //remove Empty Category - if its not No Category
            activeShopList.getCategories().remove(category);
        }
        updateActiveList();
    }

    public void updateItem(int category, int itemID, ListItem item){
        activeShopList.getCategories().get(category).getItems().set(itemID, item);
        updateActiveList();
    }

    public void insertItem(int category, int itemID, ListItem item){
        activeShopList.getCategories().get(category).getItems().add(itemID, item);
        updateActiveList();
    }


    private void updateActiveList(){
        activeListRef.setValue(activeShopList);
    }


//TODO: probably unnecessary
    //  public ShopList getActiveShopList() {
    //    return activeShopList;
    // }

    public void shareShopListWithUserID(String userID, final String shopListID){
        System.out.println("UserID = " + userID + "Tried to set foreignList on user:" + userID + ", shopListID: " + shopListID);
        if (userID == null) return;
        userID = userID.replace(".",":");
        final Firebase foreignUserRef = firebaseUserDir.child(userID);
        foreignUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User foreignUser = dataSnapshot.getValue(User.class);
                System.out.println(foreignUser.getForeignLists().size());
                if (foreignUser== null) {
                    foreignUser = new User();
                }
                ArrayList<String> foreignShoplistsIDs = new ArrayList<>();
                foreignShoplistsIDs.add(shopListID);
                foreignUser.getForeignLists().add(new ForeignUserlist(user.getUserName(), foreignShoplistsIDs, activeShopList.getName()));

                foreignUserRef.setValue(foreignUser);
                System.out.println(foreignUser.getForeignLists().size());
                System.out.println("FireBaseController Added list to foreignUser");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }

    public void setShoplistAdaptor(ArrayAdapter<ShopListViewContent> shoplistAdaptor) {
        System.out.println("Setting shopListadator "+shoplistAdaptor);
        this.shoplistAdaptor = shoplistAdaptor;
    }

    public User getUser() {        return user;    }
    public void setUser(User user) {
        this.user = user;
    }
    //Adaptor Calls
    public ArrayList<ShopListViewContent> getShoplistViewContents() {
        if (shoplistViewContents == null) {
            ArrayList<ShopListViewContent> dummyList = new ArrayList<ShopListViewContent>();
            dummyList.add(new ShopListViewItem(2,"stk","TestBananer", ListItem.ListItemState.DEFAULT, 0,0));
            return dummyList;
        }
        return shoplistViewContents;
    }

    public ArrayList<DictionaryItem> getDictionaryStrings() {
        if (MainActivity.DEBUG){
//            user.getUserDictionary().add(new DictionaryItem("Bananer", "stk" , 10));
//            user.getUserDictionary().add(new DictionaryItem("Ananas", "stk" , 1));
//            System.out.println("Adding some items to Dictionary");
        }
        ArrayList<String> dictionaryStrings = new ArrayList<>();
        for (DictionaryItem d : user.getUserDictionary()) {
            dictionaryStrings.add(d.getName() + " - " + d.getAmount() + " " + d.getUnit());

        }
        if (dictionaryAdapter!=null) dictionaryAdapter.notifyDataSetChanged();
        return user.getUserDictionary();
    }

    public void setDictionaryAdapter(ArrayAdapter<DictionaryItem> dictionaryAdapter) {
        this.dictionaryAdapter = dictionaryAdapter;
    }
    public String getActiveShopListName(){
        if (activeShopList == null) return "";
        return activeShopList.getName();
    }
    public  void setActiveShopListName(String name){
       //Find active listID
        for (int i = 0; i <user.getOwnLists().size(); i++) {
            if (activeShopList.getId().equals(user.getOwnLists().get(i))){
                user.getOwnListNames().set(i, name);
            }
        }
        activeShopList.setName(name);
        firebaseUserRef.setValue(user);
        updateActiveList();
    }

    private class UserValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            System.out.println("User data changed!");
            //Resolve if user is new
            System.out.println(dataSnapshot);
            User userFromFirebase = null;
            try {
                userFromFirebase = dataSnapshot.getValue(User.class);
            } catch (Exception e ) {
                firebaseUserRef.setValue(user);
                userFromFirebase = user;
            }

            if (userFromFirebase == null || userFromFirebase.getUserName() == null) {
                //Create new user
                firebaseUserRef.setValue(user);
                System.out.println("User created");
            } else {
                //Found user in db
                user = userFromFirebase;
                System.out.println("User already Exists");
                System.out.println(user.getActiveList());
            }
            //Update active shopping list
            setActiveList(user.getActiveList());
            //Update NavigationDrawer
            NavigationView navDrawer = activity.getNavigationView();
            activity.userMail = user.getUserID(); //Tell Main activity the users name and email
            activity.userName = user.getUserName();
            if (activity.findViewById(R.id.userMail)!=null) ((TextView)activity.findViewById(R.id.userMail)).setText(user.getUserID().replace(":","."));
            if (activity.findViewById(R.id.userName)!=null) ((TextView)activity.findViewById(R.id.userName)).setText(user.getUserName());
            navDrawer.getMenu().removeGroup(1);
            navDrawer.getMenu().removeGroup(2);

            int i = 0;
//            for (String s : user.getOwnListNames()) {
//                activity.getNavigationView().getMenu().add(1, i, i, s);
//                i++;
//            }

//            for (ForeignUserlist s : user.getForeignLists()){
//                if (s!=null && s.getShopListIDs()!=null && s.getShopListIDs().size()>0) {
//                    final int finalI = i;
//                    firebaseShopListDir.child(s.getShopListIDs().get(0)).addListenerForSingleValueEvent(new ValueEventListener() {
//
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            activity.getNavigationView().getMenu().add(2, finalI, finalI, dataSnapshot.getValue(ShopList.class).getName());
//                        }
//
//                        @Override
//                        public void onCancelled(FirebaseError firebaseError) {
//
//                        }
//                    });
//                    i++;
//                }
//            }
            System.out.println(shopListTitleViews.size());
            for (TextView t:shopListTitleViews ) {
                if (activeShopList!=null) {
                    t.setText(activeShopList.getName());
                    System.out.println("Updated title to: " + activeShopList.getName() + t.getId());
                }
            }


            View header = LayoutInflater.from(activity).inflate(R.layout.nav_header_main, null);
            boolean standardized = false;
            if (user.getUserCategories() == null || user.getUserCategories().size() <= 0) {
                System.out.println("Initializing Dictionary");
                initializeStandardCategories(user);
                standardized = true;
            }
            if (user.getUserUnits() == null || user.getUserUnits().size() <= 0) {
                initializeStandardUnits(user);
                standardized = true;
            }
            if (user.getUserDictionary() == null || user.getUserDictionary().size() <= 0) {
                initializeStandardDictionary(user);
                standardized = true;
            }
            if (dictionaryAdapter != null) {
                dictionaryAdapter.clear();
                dictionaryAdapter.addAll(getDictionaryStrings());
                dictionaryAdapter.notifyDataSetChanged();
            }

            if (standardized) firebaseUserRef.setValue(user);
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }

    private class ShopListValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            ShopList newShopList = dataSnapshot.getValue(ShopList.class);
            activeShopList = newShopList;
            System.out.println("ShopList Changed:" + ((activeShopList != null) ? newShopList.getId() + newShopList.getName() : "null"));
            //Parse for arrayadaptor.
            parseShopList();
            if (shoplistAdaptor != null) {
                shoplistAdaptor.notifyDataSetChanged();
                System.out.println("Notified shopListAdaptor "+shoplistAdaptor);
            }
            //Tell title views that data Changed:
            for (TextView t :
                    shopListTitleViews) {
                if (newShopList!=null) {
                    t.setText(newShopList.getName());
                    System.out.println("Notified " + shopListTitleViews.size() + " views" + ". Title : " + newShopList.getName());
                }
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }
}
