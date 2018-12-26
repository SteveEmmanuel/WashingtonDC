package com.nist.washintondc;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.nist.washintondc.InitializeDataForConsumption.initializeBalanceData;
import static com.nist.washintondc.InitializeDataForConsumption.initializeOrderList;
import static com.nist.washintondc.InitializeDataForConsumption.initializeReportByCategoryData;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DailyReports.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DailyReports#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyReports extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    List<Expenses>  expenses = new ArrayList<>();
    ArrayList<ArrayList<String>> packages = new ArrayList<ArrayList<String>>();
    List<Orders> orderHistory;

    ListView listView;
    DailyReportsListViewAdapter adapter;

    Spinner dailyReportTypeSpinner;

    Boolean menuInflated = false;

    String date;
    public DailyReports() {
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
    public static DailyReports newInstance(String param1, String param2) {
        DailyReports fragment = new DailyReports();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            //Create a new instance of DatePickerDialog and return it
            DatePickerDialog datePicker =  new DatePickerDialog(getActivity(), this, year, month, day);
            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());



            return datePicker;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            DailyReports newFragment = new DailyReports();
            Bundle bundle = new Bundle();
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            String date = new String();
            String monthDate = new String();
            monthDate = Integer.toString(month+1);
            if(month+1<10){
                monthDate = "0"+monthDate;
            }
            date = Integer.toString(year)+ '-' + monthDate+ '-' + Integer.toString(day);
            try{
                bundle.putString("date", dateFormatter.format(dateFormatter.parse(date)).toString());
            }
            catch (ParseException e){
                e.printStackTrace();
            }
            newFragment.setArguments(bundle);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            //transaction.detach(newFragment);
            //transaction.attach(newFragment);

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment, newFragment);

            //transaction.addToBackStack("dailyReports");

            //Integer count = getActivity().getSupportFragmentManager().getBackStackEntryCount();
            // Commit the transaction
            transaction.commit();
        }
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
        packages.clear();
        expenses.clear();

        final String today;
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        dateFormatter.setLenient(false);
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("date")){
            today = arguments.getString("date");
        }
        else{
            today = dateFormatter.format(new Date());
        }
        date = today;

        if(menu!=null){menu.clear();}
        getActivity().getMenuInflater().inflate(R.menu.daily_reports_menu, menu);

        final View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);

        MenuItem spinnerItem = menu.findItem(R.id.dailyReportSpinner);
        spinnerItem.setVisible(true);

        MenuItem downloadItem = menu.findItem(R.id.dailyReportExcelDownload);
        downloadItem.setVisible(true);

        dailyReportTypeSpinner = (Spinner) spinnerItem.getActionView();
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                R.array.dailyReportTypes, android.R.layout.simple_spinner_dropdown_item);


        spinnerAdapter.setDropDownViewResource(R.layout.dropdown_textview);
        dailyReportTypeSpinner.setAdapter(spinnerAdapter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        int position = 0;
        position = prefs.getInt("daily_report_position", 0);

        final SharedPreferences.Editor editor = prefs.edit();

        dailyReportTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    editor.putInt("daily_report_position", 0);

                    rootView.findViewById(R.id.dailyReportBalance_listView).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.dailyReportByCategory_listView).setVisibility(View.GONE);
                    rootView.findViewById(R.id.dailyReportByOrders_listView).setVisibility(View.GONE);

                    rootView.findViewById(R.id.dailyReportBalance_heading).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.dailyReportByCategory_heading).setVisibility(View.GONE);
                    rootView.findViewById(R.id.dailyReportByOrders_heading).setVisibility(View.GONE);

                    rootView.findViewById(R.id.dailyReport_addExpense).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.dailyReport_empty_list_item).setVisibility(View.GONE);

                    expenses = initializeBalanceData(getActivity().getApplication(), today);

                    listView = rootView.findViewById(R.id.dailyReportBalance_listView);

                    adapter = new DailyReportsListViewAdapter(getActivity(), expenses);
                    if (expenses.size() == 0) {
                        listView.setEmptyView(rootView.findViewById(R.id.dailyReport_empty_list_item));
                        rootView.findViewById(R.id.dailyReportBalance_heading).setVisibility(View.INVISIBLE);
                    } else {
                        listView.setAdapter(adapter);
                    }

                } else if (position == 1) {
                    editor.putInt("daily_report_position", 1);

                    rootView.findViewById(R.id.dailyReportBalance_listView).setVisibility(View.GONE);
                    rootView.findViewById(R.id.dailyReportByCategory_listView).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.dailyReportByOrders_listView).setVisibility(View.GONE);

                    rootView.findViewById(R.id.dailyReportBalance_heading).setVisibility(View.GONE);
                    rootView.findViewById(R.id.dailyReportByCategory_heading).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.dailyReportByOrders_heading).setVisibility(View.GONE);

                    rootView.findViewById(R.id.dailyReport_addExpense).setVisibility(View.GONE);
                    rootView.findViewById(R.id.dailyReport_empty_list_item).setVisibility(View.GONE);

                    listView = rootView.findViewById(R.id.dailyReportByCategory_listView);

                    packages = initializeReportByCategoryData(getActivity().getApplication(), today);

                    adapter = new DailyReportsListViewAdapter(getActivity(), packages);
                    if (packages.size() == 0) {
                        listView.setEmptyView(rootView.findViewById(R.id.dailyReport_empty_list_item));
                        rootView.findViewById(R.id.dailyReportByCategory_heading).setVisibility(View.INVISIBLE);
                    } else {
                        listView.setAdapter(adapter);
                    }
                } else if(position == 2){
                    editor.putInt("daily_report_position", 2);

                    rootView.findViewById(R.id.dailyReportBalance_listView).setVisibility(View.GONE);
                    rootView.findViewById(R.id.dailyReportByCategory_listView).setVisibility(View.GONE);
                    rootView.findViewById(R.id.dailyReportByOrders_listView).setVisibility(View.VISIBLE);

                    rootView.findViewById(R.id.dailyReportBalance_heading).setVisibility(View.GONE);
                    rootView.findViewById(R.id.dailyReportByCategory_heading).setVisibility(View.GONE);
                    rootView.findViewById(R.id.dailyReportByOrders_heading).setVisibility(View.VISIBLE);

                    rootView.findViewById(R.id.dailyReport_addExpense).setVisibility(View.GONE);
                    rootView.findViewById(R.id.dailyReport_empty_list_item).setVisibility(View.GONE);

                    listView = rootView.findViewById(R.id.dailyReportByOrders_listView);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position,
                                                long id) {
                            OrderDetails newFragment = new OrderDetails();
                            Bundle bundle = new Bundle();
                            bundle.putString("order_id", view.getTag().toString());
                            newFragment.setArguments(bundle);
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                            // Replace whatever is in the fragment_container view with this fragment,
                            // and add the transaction to the back stack so the user can navigate back
                            transaction.replace(R.id.fragment, newFragment);
                            transaction.addToBackStack("orderList");

                            // Commit the transaction
                            transaction.commit();
                        }
                    });
                    orderHistory = initializeOrderList(getActivity().getApplicationContext(), today);

                    OrderListViewAdapter adapter = new OrderListViewAdapter(getActivity(), orderHistory, true);
                    if (orderHistory.size() == 0) {
                        listView.setEmptyView(rootView.findViewById(R.id.dailyReport_empty_list_item));
                        rootView.findViewById(R.id.dailyReportByOrders_heading).setVisibility(View.INVISIBLE);
                    } else {
                        listView.setAdapter(adapter);
                    }

                }
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //dailyReportTypeSpinner.setSelection(0, true);
            }
        });
        dailyReportTypeSpinner.setSelection(position, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.calendar:
                showDatePickerDialog();
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
        Bundle arguments = getArguments();
        DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        dateFormatter.setLenient(false);
        String today;
        if (arguments != null && arguments.containsKey("date")){
            today = arguments.getString("date");
        }
        else{
            today = dateFormatter.format(new Date());
        }
        ((MainActivity) getActivity())
                .setActionBarTitle("Daily Report : " + today);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_daily_reports, container, false);
        FloatingActionButton fab = view.findViewById(R.id.dailyReport_addExpense);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                ExpenseDetails newFragment = new ExpenseDetails();

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("date", date);
                newFragment.setArguments(bundle);

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.fragment, newFragment);
                transaction.addToBackStack("dailyReports");

                // Commit the transaction
                transaction.commit();
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onDailyReportFragmentInteraction(uri);
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
        void onDailyReportFragmentInteraction(Uri uri);
    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    private void generateExcel(){
        Intent intent = new Intent(getContext(), ExcelGeneratorJobIntentService.class);

        intent.putExtra("type", 0);
        intent.putExtra("date", date);

        ExcelGeneratorJobIntentService.enqueueWork(getActivity().getApplicationContext(), intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("destroyedd", "destriyed");
        SharedPreferences timerNotifications = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        SharedPreferences.Editor editor = timerNotifications.edit();
        editor.putInt("daily_report_position", 0);
        editor.apply();
    }
}

