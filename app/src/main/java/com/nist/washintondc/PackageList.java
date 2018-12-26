package com.nist.washintondc;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PackageList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PackageList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PackageList extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Context mContext;

    List<Packages> packages;
    GridView gridView;
    CustomGridViewAdapter adapter;
    TextView packageName, packagePrice, packageInfo, packageTime;
    Button selectBtn, updateBtn;

    Button hatchPrice, suvPrice, sedanPrice;

    LinearLayout priceCard, hatchCard, sedanCard, suvCard;

    private long selectedPackage;

    public PackageList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PackageList.
     */
    // TODO: Rename and change types and number of parameters
    public static PackageList newInstance(String param1, String param2) {
        PackageList fragment = new PackageList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity().getApplicationContext();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        initializeData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity())
                .setActionBarTitle("Select Package");

        View view = inflater.inflate(R.layout.fragment_package_list, container, false);

        selectBtn = (Button) view.findViewById(R.id.select);
        selectBtn.setVisibility(View.VISIBLE);
        updateBtn = (Button) view.findViewById(R.id.selectEdit);
        updateBtn.setVisibility(View.GONE);

        selectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CustomerDetails newFragment = new CustomerDetails();
                Bundle bundle = new Bundle();
                bundle.putLong("selectedPackage", selectedPackage);
                bundle.putString("carType", priceCard.getTag().toString().toLowerCase());
                newFragment.setArguments(bundle);

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.fragment, newFragment);
                transaction.addToBackStack("packageList");

                // Commit the transaction
                transaction.commit();
            }
        });

        packageName = (TextView)view.findViewById(R.id.packageName);
        packagePrice = (TextView)view.findViewById(R.id.packagePrice);
        packageInfo = (TextView)view.findViewById(R.id.packageInfo);
        packageInfo.setMovementMethod(new ScrollingMovementMethod());
        packageTime= (TextView)view.findViewById(R.id.packageTime);

        hatchPrice = (Button) view.findViewById(R.id.hatchPrice);
        suvPrice = (Button) view.findViewById(R.id.suvPrice);
        sedanPrice = (Button) view.findViewById(R.id.sedanPrice);

        hatchCard = (LinearLayout) view.findViewById(R.id.hatchCard);
        sedanCard = (LinearLayout) view.findViewById(R.id.sedanCard);
        suvCard = (LinearLayout) view.findViewById(R.id.suvCard);

        hatchCard.setOnClickListener(this);
        sedanCard.setOnClickListener(this);
        suvCard.setOnClickListener(this);

        priceCard = hatchCard;
        hatchCard.setBackgroundColor(Color.parseColor("#b1efe9"));


        gridView= view.findViewById(R.id.gridview);

        adapter=new CustomGridViewAdapter(this.getContext(), packages);

        gridView.setAdapter(adapter);

        selectedPackage = packages.get(0).getId();
        packageName.setText(packages.get(0).getName());
        packageInfo.setText(packages.get(0).getInfo());
        Double time = (double)(packages.get(0).getTime())/60;
        time = Double.parseDouble(String.format("%.3f", time));
        packageTime.setText(time.toString());
        final LinearLayout prices = (LinearLayout) view.findViewById(R.id.prices);

        if(packages.get(0).getPrice().getHatch() == 0L || packages.get(0).getPrice().getSuv() == 0L ||
                packages.get(0).getPrice().getSedan() == 0L){
            hatchPrice.setText(" ");
            suvPrice.setText(" ");
            sedanPrice.setText(" ");
        }
        else{
            hatchPrice.setText(packages.get(0).getPrice().getHatch().toString());
            suvPrice.setText(packages.get(0).getPrice().getSuv().toString());
            sedanPrice.setText(packages.get(0).getPrice().getSedan().toString());
        }


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                packageName.setText(packages.get(i).getName());
                packageInfo.setText(packages.get(i).getInfo());
                Double time = (double)(packages.get(i).getTime())/60;
                packageTime.setText(time.toString());

                if(packages.get(i).getPrice().getHatch() == 0L || packages.get(i).getPrice().getSuv() == 0L ||
                        packages.get(i).getPrice().getSedan() == 0L){
                    hatchPrice.setText(" ");
                    suvPrice.setText(" ");
                    sedanPrice.setText(" ");
                }
                else{
                    hatchPrice.setText(packages.get(i).getPrice().getHatch().toString());
                    suvPrice.setText(packages.get(i).getPrice().getSuv().toString());
                    sedanPrice.setText(packages.get(i).getPrice().getSedan().toString());
                }

                for (int j = 0; j< packages.size(); j++) {

                    packages.get(j).setSelected(false);

                }
                packages.get(i).setSelected(true);
                selectedPackage = packages.get(i).getId();
                adapter.notifyDataSetChanged();
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onPackageListFragmentInteraction(uri);
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
        void onPackageListFragmentInteraction(Uri uri);
    }

    private void initializeData(){
        Bundle arguments = getArguments();
        String type = arguments.getString("type");
        DaoSession daoSession = ((App) getActivity().getApplication()).getDaoSession();
        packages = daoSession.getPackagesDao().queryBuilder()
                .where(PackagesDao.Properties.Type.eq(type)).list();
        packages.get(0).setSelected(true);
        for(int i=1;i<packages.size();i++) {
            packages.get(i).setSelected(false);
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.hatchCard:
                setFocus(priceCard, hatchCard);
                break;

            case R.id.sedanCard :
                setFocus(priceCard, sedanCard);
                break;

            case R.id.suvCard :
                setFocus(priceCard, suvCard);
                break;

        }
    }

    private void setFocus(LinearLayout priceCard, LinearLayout cardFocus){

        priceCard.setBackgroundColor(Color.TRANSPARENT);
        cardFocus.setBackgroundColor(Color.parseColor("#b1efe9"));

        this.priceCard = cardFocus;
    }

}
