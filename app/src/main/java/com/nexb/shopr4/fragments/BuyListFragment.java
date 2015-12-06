package com.nexb.shopr4.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.nexb.shopr4.FireBaseController;
import com.nexb.shopr4.IMainViewModel;
import com.nexb.shopr4.MainViewModel;
import com.nexb.shopr4.R;
import com.nexb.shopr4.View.ShopListBuyViewAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BuyListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BuyListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuyListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private IMainViewModel mainViewModel;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BuyListFragment newInstance(String param1, String param2) {
        BuyListFragment fragment = new BuyListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public BuyListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_buy_list, container, false);
        ((TextView)v.findViewById(R.id.listNameBuy)).setText(FireBaseController.getI().getActiveShopListName());

        ListView listView = (ListView) v.findViewById(R.id.buyfragmentlistview);

        ShopListBuyViewAdapter adaptor = new ShopListBuyViewAdapter(getActivity(), android.R.layout.simple_list_item_1, mainViewModel.getShopListViewContents());
        listView.setAdapter(adaptor);
        mainViewModel.setShoplistAdaptor(adaptor);
        // Inflate the layout for this fragment
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setMainViewModel(IMainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
