package com.nexb.shopr4.listeners;

import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.nexb.shopr4.IMainViewModel;
import com.nexb.shopr4.dataModel.ListItem;

/**
 * @author Christian on 04-12-2015.
 */
public class AutoBoxListener implements AutoCompleteTextView.OnClickListener, AdapterView.OnItemClickListener, TextView.OnEditorActionListener {
    private IMainViewModel mainViewModel;

    public AutoBoxListener(IMainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    @Override
    public void onClick(View v) {
        mainViewModel.autoBoxClicked();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
        ListItem newItem = new ListItem(1, " ", textView.getText().toString());
        System.out.println("AutoBox - New ListItemEntered:" + newItem);
        textView.setText("");
        mainViewModel.autoBoxTextEntered(newItem);
        return true;
    }
}
