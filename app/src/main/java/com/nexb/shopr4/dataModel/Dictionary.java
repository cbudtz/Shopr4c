package com.nexb.shopr4.dataModel;

import java.util.ArrayList;

/**
 * Created by Christian on 26-11-2015.
 */
public class Dictionary {

    private ArrayList<DictionaryItem> dictionaryItems = new ArrayList<>();

    public ArrayList<DictionaryItem> getDictionaryItems() {
        return dictionaryItems;
    }

    public void setDictionaryItems(ArrayList<DictionaryItem> dictionaryItems) {
        this.dictionaryItems = dictionaryItems;
    }
}
