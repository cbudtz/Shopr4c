package com.nexb.shopr4;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nexb.shopr4.View.ShopListViewCategory;
import com.nexb.shopr4.View.ShopListViewContent;
import com.nexb.shopr4.View.ShopListViewItem;
import com.nexb.shopr4.dataModel.Category;
import com.nexb.shopr4.dataModel.DictionaryItem;
import com.nexb.shopr4.dataModel.ForeignUserlist;
import com.nexb.shopr4.dataModel.ListItem;
import com.nexb.shopr4.dataModel.ShopList;
import com.nexb.shopr4.dataModel.SuperMarket;
import com.nexb.shopr4.dataModel.User;

import java.util.ArrayList;
import java.util.Map;

/**
 * @authour Christian on 04-12-2015.
 */
public class MainViewModel implements IMainViewModel {

    MainActivity mainActivity;
    IDataBaseController dataBaseController;

    private SuperMarket activeSuperMarket;
    public SuperMarket getActiveSuperMarket() { return activeSuperMarket; }
    private ShopList activeShopList; //public ShopList getActiveShopList() { return activeShopList; }
    //Observable elements
    private ArrayList<ShopListViewContent> shopListViewContents = new ArrayList<>();

    @Override
    public ArrayList<ShopListViewContent> getShopListViewContents() { return shopListViewContents; }

    @Override
    public void insertItemFrom(ListItem tempItem, String newCatName, int oldCatId, int itemId) {
        FireBaseController.getI().addItemToActiveList(newCatName, tempItem);
        FireBaseController.getI().deleteItem(oldCatId, itemId);

    }

    @Override
    public void deleteItem(int categoryID, int itemId) {
        FireBaseController.getI().deleteItem(categoryID, itemId);
    }

    @Override
    public void updateItem(int categoryID, int itemId, ListItem item) {
        FireBaseController.getI().updateItem(categoryID, itemId, item);
    }

    @Override
    public void updateCategoryName(int catId, String newName) {
        FireBaseController.getI().updateCategoryname(catId, newName);
    }

    @Override
    public void deleteCategory(int catId) {
        FireBaseController.getI().deleteCategory(catId);
    }

    @Override
    public void setActiveShopListName(String listName) {
        FireBaseController.getI().setActiveShopListName(listName);
    }


    //Adaptors to be notified on dataChange
    private ArrayAdapter<ShopListViewContent> shoplistAdaptor;
    @Override
    public void setShoplistAdaptor(ArrayAdapter<ShopListViewContent> shoplistAdaptor) { this.shoplistAdaptor = shoplistAdaptor; }

    private ArrayAdapter<DictionaryItem> dictionaryAdapter;
    @Override
    public void setDictionaryAdapter(ArrayAdapter<DictionaryItem> dictionaryAdapter) { this.dictionaryAdapter = dictionaryAdapter; }

    private ArrayList<TextView> shopListTitleViews = new ArrayList<>();
    @Override
    public void addTitleTextView(TextView t){shopListTitleViews.add(t);}

    //Views to be updatesd on dataChange
    private NavigationView navigationDrawerView;


    public MainViewModel(MainActivity mainActivity,
                         IDataBaseController dataBaseController) {
        this.mainActivity = mainActivity;
        this.dataBaseController = dataBaseController;
    }
    //---------------------------------- Input
    //AutoBoxClicks----------------
    @Override
    public void autoBoxClicked() {
        //TODO Some manipulation of autobox? Drop down?
    }

    @Override
    public void autoBoxTextEntered(ListItem listItem) {
        //TODO different behavior in Shop fragment
        dataBaseController.addItemToActiveList("Ingen kategori",listItem);
    }

    @Override
    public void autoBoxItemSelected(DictionaryItem dictionaryItem) {
        dataBaseController.addItemToActiveList(dictionaryItem.getCategory(), new ListItem(dictionaryItem.getAmount(), dictionaryItem.getUnit(), dictionaryItem.getName()));
    }



    //----------------------------------- Output
    //CallBacks from database
    @Override
    public void userdataChanged(User user) {
        //Update active shopping list
        System.out.println("MainViewModel - Got callback from Database - userData: " + ((user==null)?"null":user.getUserName()));
        mainActivity.userMail = user.getUserID(); //Tell Main activity the users name and email
        mainActivity.userName = user.getUserName();
        if (mainActivity.findViewById(R.id.userMail)!=null) ((TextView)mainActivity.findViewById(R.id.userMail)).setText(user.getUserID().replace(":","."));
        if (mainActivity.findViewById(R.id.userName)!=null) ((TextView)mainActivity.findViewById(R.id.userName)).setText(user.getUserName());
        if (navigationDrawerView!= null) navigationDrawerView.getMenu().removeGroup(1);  //Own Lists
        if (navigationDrawerView!= null) navigationDrawerView.getMenu().removeGroup(2); //Foreign Lists

        int i = 0;
        for (String s : user.getOwnListNames()) {
            mainActivity.getNavigationView().getMenu().add(1, i, i, s);
            i++;
        }
        //reset id counter ;))
        i = 0;
        for (ForeignUserlist s : user.getForeignLists()){
            if (s!=null && s.getShopListIDs()!=null && s.getShopListIDs().size()>0) {
                mainActivity.getNavigationView().getMenu().add(2, i, i, s.getUserName() + " - " + s.getListName());
                i++;
            }
        }

        View header = LayoutInflater.from(mainActivity).inflate(R.layout.nav_header_main, null);
        boolean standardized = false;

        if (dictionaryAdapter != null) {
            dictionaryAdapter.clear();
            dictionaryAdapter.addAll(getDictionaryStrings(user));
            dictionaryAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void shopListDataChanged(ShopList shopList) {
        System.out.println("MainViewModel - Got callback from database. Shoplist Changed: " + ((shopList==null)?"null":shopList.getName()));
        System.out.println("Test");
        activeShopList = shopList;
        //sortShopList();  //Sort shopList by superMarket
        parseShopListToViewList();
        if (shoplistAdaptor!=null) shoplistAdaptor.notifyDataSetChanged();
        for (TextView t : shopListTitleViews) {
            if (t !=null) {
                t.setText(activeShopList.getName());
            }
        }

    }

    private void parseShopListToViewList() {
        ArrayList<ShopListViewContent> newShopListViewContents = new ArrayList<ShopListViewContent>();
        if (activeShopList==null) return;
        //TODO clean up:
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
                System.out.println("ShoplistViewItem: " + s);

            }
        }
        shopListViewContents.clear();
        shopListViewContents.addAll(newShopListViewContents);
    }

    @Override
    public void superMarketChanged(SuperMarket superMarket) {
        System.out.println("MainViewModel - got callBackFrom database - supermarket Changed: " + ((superMarket==null)?"null":superMarket.getName()));
        activeSuperMarket = superMarket;
        sortShopList();
    }

    private void sortShopList() {
        //TODO make data structure and nice sort algorithm for shopList (should be a map of ItemName, category instead
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                if (activeSuperMarket==null) return null;
                ShopList newShopList = new ShopList();
                newShopList.setName(activeShopList.getName());
                newShopList.setId(activeShopList.getId());
                for (Category category : activeShopList.getCategories()) {
                    for (ListItem listItem: category.getItems()) {
                        boolean listItemUsed = false;
                        for (Map.Entry<String, ArrayList<String>> entry : activeSuperMarket.getCategories().entrySet()) {
                            for (String s : entry.getValue()) {
                                if (s.equals(listItem.getName())){
                                    newShopList.addItem(s, listItem);
                                }
                            }
                        }

                    }
                }
                activeShopList = newShopList;
                parseShopListToViewList();
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                if (shoplistAdaptor!=null)
                    shoplistAdaptor.notifyDataSetChanged();;
            }
        };
        task.execute();
    }

    //

    public ArrayList<DictionaryItem> getDictionaryStrings(User user) {
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

}
