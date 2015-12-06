package com.nexb.shopr4.View;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.nexb.shopr4.FireBaseController;
import com.nexb.shopr4.IMainViewModel;
import com.nexb.shopr4.MainViewModel;
import com.nexb.shopr4.R;
import com.nexb.shopr4.dataModel.ListItem;

import java.util.ArrayList;

/**
 * Created by mac on 19/11/15.
 */
public class ShopListEditViewAdapter extends ArrayAdapter {


    private ArrayList<ShopListViewContent> list;
    LayoutInflater mInflater;
    IMainViewModel mainViewModel;

    public ShopListEditViewAdapter(Context context, int resource, IMainViewModel mainViewModel) {
        super(context, resource);
        this.mainViewModel = mainViewModel;
        this.list = mainViewModel.getShopListViewContents();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public void notifyDataSetChanged(){

        this.list = mainViewModel.getShopListViewContents();

        super.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ShopListViewContent content = getItem(position);
        final ListItem tempItem = new ListItem();
        View view = null;

        if(content.getType().equals(ShopListViewContent.contentType.ITEM)){
            view = mInflater.inflate(R.layout.list_item_edit_view, parent, false);

            //add listViewItem
            ((TextView)view.findViewById(R.id.itemEditViewName)).setText(((ShopListViewItem) content).getName());
            ((TextView) view.findViewById(R.id.itemEditViewAmount)).setText(String.valueOf(((ShopListViewItem) content).getAmount()));
            ((TextView)view.findViewById(R.id.itemEditViewType)).setText(((ShopListViewItem) content).getUnit());

            // CHANGE CATEGORY
            (view.findViewById(R.id.itemEditViewName)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupMenu popUpCatMenu = new PopupMenu(getContext(), v);
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getType() == ShopListViewContent.contentType.CATEGORY) {
                            popUpCatMenu.getMenu().add(((ShopListViewCategory) list.get(i)).getCatId(), 0, 0, ((ShopListViewCategory) list.get(i)).getName());
                        }
                    }
                    popUpCatMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            tempItem.setName(((ShopListViewItem) content).getName());
                            tempItem.setUnit(((ShopListViewItem) content).getUnit());
                            tempItem.setAmount(((ShopListViewItem) content).getAmount());
                            mainViewModel.insertItemFrom(tempItem, item.getTitle().toString(), ((ShopListViewItem) content).getCategoryID(), ((ShopListViewItem) content).getItemId());
                            return false;
                        }
                    });
                    popUpCatMenu.show();
                }


            });

            // DELETE BUTTON
            view.findViewById(R.id.itemEditViewDelete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainViewModel.deleteItem(((ShopListViewItem) content).getCategoryID(), ((ShopListViewItem) content).getItemId());
                }
            });

            // MINUS ONE BUTTON
            view.findViewById(R.id.itemEditViewMinusOne).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tempItem.setName(((ShopListViewItem) content).getName());
                    tempItem.setUnit(((ShopListViewItem) content).getUnit());

                    if(((ShopListViewItem) content).getAmount()>= 1) {
                        tempItem.setAmount(((ShopListViewItem) content).getAmount() - 1);
                    }else{
                        tempItem.setAmount(((ShopListViewItem) content).getAmount());
                    }
                    mainViewModel.updateItem(((ShopListViewItem) content).getCategoryID(), ((ShopListViewItem) content).getItemId(), tempItem);
                }
            });

            // PLUS ONE BUTTON
            view.findViewById(R.id.itemEditViewPlusOne).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tempItem.setName(((ShopListViewItem) content).getName());
                    tempItem.setUnit(((ShopListViewItem) content).getUnit());
                    tempItem.setAmount(((ShopListViewItem) content).getAmount() + 1);
                    mainViewModel.updateItem(((ShopListViewItem) content).getCategoryID(), ((ShopListViewItem) content).getItemId(), tempItem);
                }
            });

            // item type edit text
            view.findViewById(R.id.itemEditViewType).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final PopupMenu popUpTypeMenu = new PopupMenu(getContext(), v);
                    popUpTypeMenu.getMenuInflater().inflate(R.menu.pop_up_type_menu, popUpTypeMenu.getMenu());
                    popUpTypeMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            tempItem.setName(((ShopListViewItem) content).getName());
                            tempItem.setAmount(((ShopListViewItem) content).getAmount());
                            tempItem.setUnit(item.getTitle().toString());
                            ((EditText)v.findViewById(R.id.itemEditViewType)).setText(item.getTitle());
                            mainViewModel.updateItem(((ShopListViewItem) content).getCategoryID(), ((ShopListViewItem) content).getItemId(), tempItem);
                            return false;
                        }
                    });
                    popUpTypeMenu.show();
                }
            });
            ((EditText) view.findViewById(R.id.itemEditViewType)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    tempItem.setName(((ShopListViewItem) content).getName());
                    tempItem.setUnit(((EditText) v.findViewById(R.id.itemEditViewType)).getText().toString());
                    tempItem.setAmount(((ShopListViewItem) content).getAmount());
                    mainViewModel.updateItem(((ShopListViewItem) content).getCategoryID(), ((ShopListViewItem) content).getItemId(), tempItem);
                    return false;
                }
            });

            // item amount edit text
            ((TextView) view.findViewById(R.id.itemEditViewAmount)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    EditText editAmountText = (EditText) v.findViewById(R.id.itemEditViewAmount);
                    tempItem.setName(((ShopListViewItem) content).getName());
                    tempItem.setUnit(((ShopListViewItem) content).getUnit());
                    tempItem.setAmount(Double.parseDouble(editAmountText.getText().toString()));
                    mainViewModel.updateItem(((ShopListViewItem) content).getCategoryID(), ((ShopListViewItem) content).getItemId(), tempItem);
                    return false;
                }
            });
        }
        else if(content.getType().equals(ShopListViewContent.contentType.CATEGORY)){
            view = mInflater.inflate(R.layout.list_category_edit_view, parent, false);

            ((EditText)view.findViewById(R.id.CategoryEditViewName)).setText(((ShopListViewCategory) content).getName());
            ((EditText)view.findViewById(R.id.CategoryEditViewName)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    mainViewModel.updateCategoryName(((ShopListViewCategory) content).getCatId(), ((EditText) v.findViewById(R.id.CategoryEditViewName)).getText().toString());
                    return false;
                }
            });

            view.findViewById(R.id.categoryEditViewDelete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainViewModel.deleteCategory(((ShopListViewCategory) content).getCatId());
                }
            });
        }
        return view;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ShopListViewContent getItem(int position) {
        return list.get(position);
    }


}



