package com.nexb.shopr4.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.nexb.shopr4.FireBaseController;
import com.nexb.shopr4.IMainViewModel;
import com.nexb.shopr4.MainViewModel;
import com.nexb.shopr4.R;
import com.nexb.shopr4.View.ShopListEditViewAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditListFragment extends Fragment {
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
    public static EditListFragment newInstance(String param1, String param2) {
        EditListFragment fragment = new EditListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public EditListFragment() {

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
        View v = inflater.inflate(R.layout.fragment_edit_list, container, false);

        ListView listView = (ListView) v.findViewById(R.id.editfragmentlistview);

        ShopListEditViewAdapter adaptor = new ShopListEditViewAdapter(getActivity(), android.R.layout.simple_list_item_1, mainViewModel);
        listView.setAdapter(adaptor);
        mainViewModel.setShoplistAdaptor(adaptor);
        mainViewModel.addTitleTextView(((EditText) v.findViewById(R.id.listNameEdit)));

        ((EditText)v.findViewById(R.id.listNameEdit)).setText(FireBaseController.getI().getActiveShopListName());
        ((EditText) v.findViewById(R.id.listNameEdit)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                mainViewModel.setActiveShopListName(((EditText) v.findViewById(R.id.listNameEdit)).getText().toString());
                //FireBaseController.getI().setActiveShopListName(((EditText) v.findViewById(R.id.listNameEdit)).getText().toString());
                return false;
            }
        });

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
