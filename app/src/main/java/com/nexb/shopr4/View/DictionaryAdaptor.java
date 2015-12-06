package com.nexb.shopr4.View;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.nexb.shopr4.FireBaseController;
import com.nexb.shopr4.dataModel.DictionaryItem;

import java.util.ArrayList;

/**
 * Created by Christian on 19-11-2015.
 */
public class DictionaryAdaptor extends ArrayAdapter {
    private ArrayList<DictionaryItem> dictionaryItems;

    public DictionaryAdaptor(Context context, int resource, ArrayList<DictionaryItem> dictionaryItems) {
        super(context, resource,dictionaryItems);
        this.dictionaryItems = dictionaryItems;

    }

    @Override
    public void notifyDataSetChanged(){
        this.dictionaryItems = FireBaseController.getI().getDictionaryStrings();
        super.notifyDataSetChanged();
    }
}
