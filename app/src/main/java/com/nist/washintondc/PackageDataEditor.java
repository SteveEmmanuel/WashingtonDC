package com.nist.washintondc;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import javax.xml.datatype.Duration;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PackageList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PackageList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PackageDataEditor extends Fragment implements View.OnClickListener {
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
    TextView packageName, packageTime, packageInfo;
    Button hatchPriceEdit, suvPriceEdit, sedanPriceEdit;
    LinearLayout hatch, suv, sedan, timeEdit;
    Button selectBtn, updateBtn;

    private long selectedPackage;

    DaoSession daoSession;

    public PackageDataEditor() {
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
        setHasOptionsMenu(true);
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
                .setActionBarTitle("Edit Packages");

        Toast.makeText(getContext().getApplicationContext(),"Click on a field to Edit",Toast.LENGTH_LONG).show();

        View view = inflater.inflate(R.layout.fragment_package_list, container, false);
        view.findViewById(R.id.select).setVisibility(View.GONE);
        view.findViewById(R.id.selectEdit).setVisibility(View.VISIBLE);

        selectBtn = (Button) view.findViewById(R.id.select);
        selectBtn.setVisibility(View.GONE);
        updateBtn = (Button) view.findViewById(R.id.selectEdit);
        updateBtn.setVisibility(View.VISIBLE);

        Bundle arguments = getArguments();
        if (arguments != null) {
            String a = arguments.getString("selectedPackage");
        }
        selectBtn = (Button) view.findViewById(R.id.selectEdit);

        selectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                packages.get((int)selectedPackage).setInfo(packageInfo.getText().toString());
                packages.get((int)selectedPackage).setName(packageName.getText().toString());
                double timedouble = Double.parseDouble(packageTime.getText().toString())*60;
                String timeString = Double.toString(timedouble);
                int time = (int)Double.parseDouble(timeString);
                packages.get((int)selectedPackage).setTime(time);
                packages.get((int)selectedPackage).setSelected(packages.get((int)selectedPackage).getSelected());
                if(packages.get(0).getType().toString().equals("wash_package")){
                    packages.get((int)selectedPackage).getPrice().setHatch(Float.parseFloat(hatchPriceEdit.getText().toString()));
                    packages.get((int)selectedPackage).getPrice().setSedan(Float.parseFloat(sedanPriceEdit.getText().toString()));
                    packages.get((int)selectedPackage).getPrice().setSuv(Float.parseFloat(suvPriceEdit.getText().toString()));
                }
                daoSession.getPackagesDao().update(packages.get((int)selectedPackage));
                Toast.makeText(getActivity(), "Package has been updated.", Toast.LENGTH_SHORT).show();
            }
        });

        packageName = (TextView) view.findViewById(R.id.packageName);
        packageInfo = (TextView) view.findViewById(R.id.packageInfo);
        packageInfo.setMovementMethod(new ScrollingMovementMethod());
        packageTime= (TextView)view.findViewById(R.id.packageTime);


        hatchPriceEdit = (Button) view.findViewById(R.id.hatchPrice);
        suvPriceEdit = (Button) view.findViewById(R.id.suvPrice);
        sedanPriceEdit = (Button) view.findViewById(R.id.sedanPrice);

        hatch = (LinearLayout) view.findViewById(R.id.hatchCard);
        suv = (LinearLayout) view.findViewById(R.id.suvCard);
        sedan = (LinearLayout) view.findViewById(R.id.sedanCard);

        hatch.setOnClickListener(this);
        suv.setOnClickListener(this);
        sedan.setOnClickListener(this);
        packageTime.setOnClickListener(this);
        packageName.setOnClickListener(this);
        packageInfo.setOnClickListener(this);

        gridView= view.findViewById(R.id.gridview);

        adapter=new CustomGridViewAdapter(this.getContext(), packages);

        gridView.setAdapter(adapter);

        selectedPackage = 0L;
        packageName.setText(packages.get(0).getName());
        packageInfo.setText(packages.get(0).getInfo());
        Double time = (double)(packages.get(0).getTime())/60;
        packageTime.setText(time.toString());

        final LinearLayout prices = (LinearLayout) view.findViewById(R.id.prices);

        if(packages.get(0).getPrice().getHatch() == 0L || packages.get(0).getPrice().getSuv() == 0L ||
                packages.get(0).getPrice().getSedan() == 0L){
            hatchPriceEdit.setText(" ");
            suvPriceEdit.setText(" ");
            sedanPriceEdit.setText(" ");
        }
        else{
            hatchPriceEdit.setText(packages.get(0).getPrice().getHatch().toString());
            suvPriceEdit.setText(packages.get(0).getPrice().getSuv().toString());
            sedanPriceEdit.setText(packages.get(0).getPrice().getSedan().toString());

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
                    hatchPriceEdit.setText(" ");
                    suvPriceEdit.setText(" ");
                    sedanPriceEdit.setText(" ");
                }
                else{
                    hatchPriceEdit.setText(packages.get(i).getPrice().getHatch().toString());
                    suvPriceEdit.setText(packages.get(i).getPrice().getSuv().toString());
                    sedanPriceEdit.setText(packages.get(i).getPrice().getSedan().toString());
                }

                for (int j = 0; j< packages.size(); j++) {

                    packages.get(j).setSelected(false);

                }
                packages.get(i).setSelected(true);
                selectedPackage = i;
                adapter.notifyDataSetChanged();
            }
        });


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onPackageDataEditorFragmentInteraction(uri);
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
        void onPackageDataEditorFragmentInteraction(Uri uri);
    }

    private void initializeData(){

        Bundle arguments = getArguments();
        String type = arguments.getString("type");
        daoSession = ((App) getActivity().getApplication()).getDaoSession();
        packages = daoSession.getPackagesDao().queryBuilder()
                .where(PackagesDao.Properties.Type.eq(type)).list();
        packages.get(0).setSelected(true);
        for(int i=1;i<packages.size();i++) {
            packages.get(i).setSelected(false);
        }
    }

    @Override
    public void onClick(View view) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());
        final View mView = layoutInflaterAndroid.inflate(R.layout.package_edit_dialog, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getActivity());
        alertDialogBuilderUserInput.setView(mView);

        final EditText editDialogUserInput = (EditText) mView.findViewById(R.id.editDialogUserInput);
        switch (view.getId()){
            case R.id.sedanCard:
                editDialogUserInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editDialogUserInput.setText(sedanPriceEdit.getText());
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                if( TextUtils.isEmpty(editDialogUserInput.getText())){
                                    editDialogUserInput.setError( "Enter Amount!" );

                                }
                                else{
                                    sedanPriceEdit.setText(editDialogUserInput.getText());
                                }

                            }
                        }).setTitle("Enter Price for Sedan");
                break;

            case R.id.suvCard:
                editDialogUserInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editDialogUserInput.setText(suvPriceEdit.getText());
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                if( TextUtils.isEmpty(editDialogUserInput.getText())){
                                    editDialogUserInput.setError( "Enter Amount!" );

                                }
                                else{
                                    suvPriceEdit.setText(editDialogUserInput.getText());
                                }

                            }
                        }).setTitle("Enter Price for SUV");
                break;

            case R.id.hatchCard:
                editDialogUserInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editDialogUserInput.setText(hatchPriceEdit.getText());
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                if( TextUtils.isEmpty(editDialogUserInput.getText())){
                                    editDialogUserInput.setError( "Enter Amount!" );

                                }
                                else{
                                    hatchPriceEdit.setText(editDialogUserInput.getText());
                                }


                            }
                        }).setTitle("Enter Price for Hatch");
                break;

            case R.id.packageTime:
                editDialogUserInput.setHint("Enter Time");
                editDialogUserInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editDialogUserInput.setText(packageTime.getText());
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                if( TextUtils.isEmpty(editDialogUserInput.getText())){
                                    editDialogUserInput.setError( "Enter Amount!" );

                                }
                                else{
                                    packageTime.setText(editDialogUserInput.getText());
                                }


                            }
                        }).setTitle("Enter Time for completion");
                break;

            case R.id.packageName:
                editDialogUserInput.setHint("Enter Package Name");
                editDialogUserInput.setInputType(InputType.TYPE_CLASS_TEXT);
                editDialogUserInput.setText(packageName.getText());
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                if( TextUtils.isEmpty(editDialogUserInput.getText())){
                                    editDialogUserInput.setError( "Enter Amount!" );

                                }
                                else{
                                    packageName.setText(editDialogUserInput.getText());
                                }


                            }
                        }).setTitle("Enter Package Name");
                break;

            case R.id.packageInfo:
                editDialogUserInput.setHint("Enter Package Info");
                editDialogUserInput.setInputType(InputType.TYPE_CLASS_TEXT);
                editDialogUserInput.setText(packageInfo.getText());
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                if( TextUtils.isEmpty(editDialogUserInput.getText())){
                                    editDialogUserInput.setError( "Enter Amount!" );

                                }
                                else{
                                    packageName.setText(editDialogUserInput.getText());
                                }


                            }
                        }).setTitle("Enter Package Info");
                break;
        }

        alertDialogBuilderUserInput.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });
        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }
}
