package com.nist.washintondc;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.nist.rackmonthpicker.RackMonthPicker;
import com.nist.rackmonthpicker.listener.DateMonthDialogListener;
import com.nist.rackmonthpicker.listener.OnCancelMonthDialogListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static com.nist.washintondc.InitializeDataForConsumption.initializeMonthlyReportsData;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MonthlyReports.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MonthlyReports#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MonthlyReports extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ArrayList<ArrayList<String>> orders = new ArrayList<ArrayList<String>>();

    ListView listView;
    MonthlyReportsViewAdapter adapter;

    Boolean menuInflated = false;

    String date;

    public MonthlyReports() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DailyReports.
     */
    // TODO: Rename and change types and number of parameters
    public static MonthlyReports newInstance(String param1, String param2) {
        MonthlyReports fragment = new MonthlyReports();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {


        if(menu!=null){menu.clear();}
        getActivity().getMenuInflater().inflate(R.menu.daily_reports_menu, menu);

        MenuItem spinnerItem = menu.findItem(R.id.dailyReportSpinner);
        if(spinnerItem!=null){spinnerItem.setVisible(false);}

        MenuItem downloadItem = menu.findItem(R.id.dailyReportExcelDownload);
        if(downloadItem!=null){downloadItem.setVisible(true);}


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.calendar:
                showDatePickerDialog(getActivity());
                break;
            case R.id.dailyReportExcelDownload:
                Toast.makeText(getActivity().getApplicationContext(), "Report will be generated in the background.", Toast.LENGTH_SHORT).show();
                generateExcel();
                break;
        }
        return true;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity())
                .setActionBarTitle("Monthly Report");

        View view = inflater.inflate(R.layout.fragment_monthly_reports, container, false);

        Bundle arguments = getArguments();
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        dateFormatter.setLenient(false);
        if (arguments != null && arguments.containsKey("month")) {
            date = arguments.getString("month");

        } else {
            date = dateFormatter.format(new Date());
        }
        if (orders.size() == 0) {
            orders = initializeMonthlyReportsData(getActivity().getApplicationContext(), date);
        }
        listView= view.findViewById(R.id.monthlyReport_listView);

        adapter=new MonthlyReportsViewAdapter(this.getActivity(), orders);
        if(orders.size()==0){
            listView.setEmptyView(view.findViewById(R.id.monthlyReport_empty_list_item));
            view.findViewById(R.id.monthlyReport_heading).setVisibility(View.INVISIBLE);
        }
        else {
            listView.setAdapter(adapter);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

                ArrayList<String> temp = new ArrayList<String>();
                temp = (ArrayList)listView.getItemAtPosition(position);

                DailyReports newFragment = new DailyReports();

                Bundle bundle = new Bundle();

                bundle.putString("date", temp.get(0));

                newFragment.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, newFragment);
                transaction.addToBackStack("monthlyReports");
                // Commit the transaction
                transaction.commit();
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onMonthlyRepotsFragmentInteraction(uri);
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
        void onMonthlyRepotsFragmentInteraction(Uri uri);
    }


    public void showDatePickerDialog(Context context) {
        new RackMonthPicker(context)
                .setLocale(Locale.ENGLISH)
                .setPositiveButton(new DateMonthDialogListener() {
                    @Override
                    public void onDateMonth(int month, int startDate, int endDate, int year, String monthLabel) {
                        MonthlyReports newFragment = new MonthlyReports();
                        Bundle bundle = new Bundle();

                        DateFormat dateFormatterInput = new SimpleDateFormat("yyyyMM");
                        dateFormatterInput.setLenient(false);
                        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                        dateFormatter.setLenient(false);
                        if(month < 10){

                            date = Integer.toString(year)+ "0" + Integer.toString(month);
                        }
                        else{
                            date = Integer.toString(year)+Integer.toString(month);
                        }

                        try {
                            date = dateFormatter.format(dateFormatterInput.parse(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        bundle.putString("month", date);

                        newFragment.setArguments(bundle);
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment, newFragment);

                        transaction.commit();
                    }
                })
                .setNegativeButton(new OnCancelMonthDialogListener() {
                    @Override
                    public void onCancel(AlertDialog dialog) {
                    }
                }).show();
    }




    private void generateExcel(){
        Intent intent = new Intent(getContext(), ExcelGeneratorJobIntentService.class);

        intent.putExtra("type", 1);
        intent.putExtra("date", date);

        ExcelGeneratorJobIntentService.enqueueWork(getActivity().getApplicationContext(), intent);
    }

}

