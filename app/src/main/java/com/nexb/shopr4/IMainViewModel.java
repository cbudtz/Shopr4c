package com.nexb.shopr4;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nexb.shopr4.View.ShopListViewContent;
import com.nexb.shopr4.dataModel.DictionaryItem;
import com.nexb.shopr4.dataModel.ListItem;

import java.util.ArrayList;

/**
 * @author Christian on 04-12-2015.
 */
public interface IMainViewModel extends IShopListListener, ISuperMarketListener, IUserDataListener{
    void setShoplistAdaptor(ArrayAdapter<ShopListViewContent> shoplistAdaptor);

    void setDictionaryAdapter(ArrayAdapter<DictionaryItem> dictionaryAdapter);

    void addTitleTextView(TextView t);

    void autoBoxClicked();
    void autoBoxTextEntered(ListItem listItem);
    void autoBoxItemSelected(DictionaryItem dictionaryItem);
    ArrayList<ShopListViewContent> getShopListViewContents();

    void insertItemFrom(ListItem tempItem, String newCatName, int oldCatId, int itemId);

    void deleteItem(int categoryID, int itemId);

    void updateItem(int categoryID, int itemId, ListItem item);

    void updateCategoryName(int catId, String newName);

    void deleteCategory(int catId);

    void setActiveShopListName(String listName);
}
