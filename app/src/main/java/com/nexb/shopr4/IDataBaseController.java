package com.nexb.shopr4;

import android.app.Activity;

import com.nexb.shopr4.dataModel.Category;
import com.nexb.shopr4.dataModel.ListItem;

/**
 * Created by Christian on 03-12-2015.
 */
public interface IDataBaseController {
    //DataBaseConnectionSetup

    void setContext(Activity mainActivity, String dataBaseUrl);
    //CUD operations
    String createNewShopList();
    void setActiveList(String ShopListID);
    void setActiveShopListName(String shopListName);
    void deleteList(String shopListID);

    void addCategory(String name);
    void updateCategoryName(int catID, String newName);
    void deleteCategory(int catID);
    void insertCategory(int catID, Category category);

    void addItemToActiveList(String categoryName, ListItem listItem);
    void deleteItem(int catID, int itemID);

    void setActiveSuperMarket(String SuperMarketID);

    //ShopListListening
    void addUserDataListener(IUserDataListener userDataListener);
    void addActiveShopListListener(IShopListListener shopListListner);
    void addActiveSuperMarketListener(ISuperMarketListener superMarketListener);

}
