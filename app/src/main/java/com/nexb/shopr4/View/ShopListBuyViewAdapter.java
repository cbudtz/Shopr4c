package com.nexb.shopr4.View;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nexb.shopr4.DpToPx;
import com.nexb.shopr4.FireBaseController;
import com.nexb.shopr4.Interfaces.IMainViewModel;
import com.nexb.shopr4.R;
import com.nexb.shopr4.dataModel.ListItem;

import java.util.ArrayList;

/**
 * Created by mac on 19/11/15.
 */
public class ShopListBuyViewAdapter extends ArrayAdapter {


    private ArrayList<ShopListViewContent> list;
    LayoutInflater mInflater;
    IMainViewModel mainViewModel;


    public ShopListBuyViewAdapter(Context context, int resource, IMainViewModel mainViewModel) {
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

    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ShopListViewContent content = getItem(position);
        final ListItem tempItem = new ListItem();
        View view = null;
        final boolean[] states = {false};



        if(content.getType().equals(ShopListViewContent.contentType.ITEM)){
            view = mInflater.inflate(R.layout.list_item_buy_view, null);
            tempItem.setName(((ShopListViewItem) content).getName());
            tempItem.setUnit(((ShopListViewItem) content).getUnit());
            tempItem.setAmount(((ShopListViewItem) content).getAmount());
            tempItem.setState(((ShopListViewItem) content).getState());

            //add listViewItem
            ((TextView)view.findViewById(R.id.itemBuyViewName)).setText(
                    ((ShopListViewItem) content).getAmount() + " " +
                            ((ShopListViewItem) content).getUnit() + " " +
                            ((ShopListViewItem) content).getName()
            );

            if(((ShopListViewItem) content).getState().equals(ListItem.ListItemState.FOUND)){
                view.setBackgroundColor(Color.argb(90, 0, 200, 0));
                view.setPadding(DpToPx.dpToPx(0), DpToPx.dpToPx(0), DpToPx.dpToPx(0), DpToPx.dpToPx(0));
            }
            else if(((ShopListViewItem) content).getState().equals(ListItem.ListItemState.NOT_FOUND)){
                view.setBackgroundColor(Color.argb(90,200,0,0));
                view.setPadding(DpToPx.dpToPx(0), DpToPx.dpToPx(0), DpToPx.dpToPx(0), DpToPx.dpToPx(0));
            }


            final View finalView = view;
            view.findViewById(R.id.itemBuyViewGotItem).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (((ShopListViewItem) content).getState().equals(ListItem.ListItemState.FOUND)) {
                        tempItem.setState(ListItem.ListItemState.DEFAULT);
                        mainViewModel.updateItem(((ShopListViewItem) content).getCategoryID(), ((ShopListViewItem) content).getItemId(), tempItem);
                    } else {
                        tempItem.setState(ListItem.ListItemState.FOUND);
                        mainViewModel.updateItem(((ShopListViewItem) content).getCategoryID(), ((ShopListViewItem) content).getItemId(), tempItem);
                    }
                }
            });
            view.findViewById(R.id.itemBuyViewDontGotItem).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((ShopListViewItem) content).getState().equals(ListItem.ListItemState.NOT_FOUND)) {
                        tempItem.setState(ListItem.ListItemState.DEFAULT);
                        mainViewModel.updateItem(((ShopListViewItem) content).getCategoryID(), ((ShopListViewItem) content).getItemId(), tempItem);
                    } else {
                        tempItem.setState(ListItem.ListItemState.NOT_FOUND);
                        mainViewModel.updateItem(((ShopListViewItem) content).getCategoryID(), ((ShopListViewItem) content).getItemId(), tempItem);
                    }

                }
            });

        }
        else if(content.getType().equals(ShopListViewContent.contentType.CATEGORY)){
            view = mInflater.inflate(R.layout.list_category_buy_view, null);
            ((TextView)view.findViewById(R.id.categoryBuyViewNitle)).setText(((ShopListViewCategory) content).getName());
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



