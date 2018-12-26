package com.nist.washintondc;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PackageCategorySelector.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PackageCategorySelector#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PackageCategorySelector extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    CardView washPackages, detailingPackages;

    public PackageCategorySelector() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PackageCategorySelector.
     */
    // TODO: Rename and change types and number of parameters
    public static PackageCategorySelector newInstance(String param1, String param2) {
        PackageCategorySelector fragment = new PackageCategorySelector();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_package_category_selector, container, false);

        washPackages = view.findViewById(R.id.washPackages);
        detailingPackages = view.findViewById(R.id.detailingPackages);

        washPackages.setOnClickListener(this);
        detailingPackages.setOnClickListener(this);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onPackageCategorySelectorFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onPackageCategorySelectorFragmentInteraction(Uri uri);
    }

    @Override
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        view.setBackgroundColor(Color.parseColor("#b1efe9"));
        switch (view.getId()){
            case R.id.washPackages:
                bundle.putString("type", "wash_package");
                break;

            case R.id.detailingPackages:
                bundle.putString("type", "detailing_package");
                break;
        }

        Bundle arguments = getArguments();
        String type = arguments.getString("package_fragment");

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        switch (type){
            case "add":
                PackageList packageList = new PackageList();
                packageList.setArguments(bundle);

                transaction.replace(R.id.fragment, packageList);
                break;

            case "edit":
                PackageDataEditor packageEditor = new PackageDataEditor();
                packageEditor.setArguments(bundle);

                transaction.replace(R.id.fragment, packageEditor);
                break;
        }



        transaction.addToBackStack("packageCategorySelector");

        // Commit the transaction
        transaction.commit();
    }
}
