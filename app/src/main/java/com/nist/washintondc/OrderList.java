package com.nist.washintondc;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.ListView;
import android.widget.Spinner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.nist.washintondc.InitializeDataForConsumption.initializeOrderList;
import static com.nist.washintondc.InitializeDataForConsumption.initializePendingOrderList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OrderList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OrderList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderList extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    List<Orders> orderHistory;
    List<PendingOrders> pendingOrders;
    ListView orderListView, pendingOrderListView;
    OrderListViewAdapter orderListAdapter;
    PendingOrderListViewAdapter pendingOrderListAdapter;

    Boolean allowRefresh = true;

    public OrderList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderList.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderList newInstance(String param1, String param2) {
        OrderList fragment = new OrderList();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((MainActivity) getActivity())
                .setActionBarTitle("Todays Orders");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);
        FloatingActionButton fab = view.findViewById(R.id.addOrder);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                PackageCategorySelector newFragment = new PackageCategorySelector();
                Bundle bundle = new Bundle();
                bundle.putString("package_fragment", "add");
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

    return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

        final View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        dateFormatter.setLenient(false);
        Date today = new Date();

        final String date = dateFormatter.format(today);

        if(menu!=null){menu.clear();}
        getActivity().getMenuInflater().inflate(R.menu.order_list_menu, menu);



        MenuItem spinnerItem = menu.findItem(R.id.orderListSpinner);
        spinnerItem.setVisible(true);


        Spinner orderListTypeSpinner = (Spinner) spinnerItem.getActionView();
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                R.array.orderListTypes, android.R.layout.simple_spinner_dropdown_item);


        spinnerAdapter.setDropDownViewResource(R.layout.dropdown_textview);
        orderListTypeSpinner.setAdapter(spinnerAdapter);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        int position = 0;
        position = prefs.getInt("order_list_position", 0);

        final SharedPreferences.Editor editor = prefs.edit();
        //dailyReportTypeSpinner.setSelection(0, false);
        orderListTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    editor.putInt("order_list_position", 0);
                    orderListView = rootView.findViewById(R.id.orderListView);

                    pendingOrderListView = rootView.findViewById(R.id.pendingOrderListView);

                    pendingOrders = initializePendingOrderList(getActivity().getApplicationContext(), date);
                    pendingOrderListView.setVisibility(View.VISIBLE);
                    orderListView.setVisibility(View.GONE);

                    pendingOrderListAdapter = new PendingOrderListViewAdapter(getContext().getApplicationContext(), pendingOrders);
                    if (pendingOrders.size() == 0) {
                        pendingOrderListView.setEmptyView(rootView.findViewById(R.id.pendingOrderList_Empty_list_item));
                        rootView.findViewById(R.id.OrderList_empty_list_item).setVisibility(View.GONE);
                        rootView.findViewById(R.id.heading).setVisibility(View.GONE);
                    } else {
                        rootView.findViewById(R.id.OrderList_empty_list_item).setVisibility(View.GONE);
                        rootView.findViewById(R.id.pendingOrderList_Empty_list_item).setVisibility(View.GONE);
                        rootView.findViewById(R.id.heading).setVisibility(View.VISIBLE);
                        pendingOrderListView.setAdapter(pendingOrderListAdapter);
                    }

                }else if (position == 1) {
                    editor.putInt("order_list_position", 1);
                    editor.apply();
                    orderListView = rootView.findViewById(R.id.orderListView);

                    pendingOrderListView = rootView.findViewById(R.id.pendingOrderListView);
                    orderHistory = initializeOrderList(getActivity().getApplicationContext(), date);

                    pendingOrderListView.setVisibility(View.GONE);
                    orderListView.setVisibility(View.VISIBLE);


                    orderListAdapter=new OrderListViewAdapter(getContext().getApplicationContext(), orderHistory, false);
                    if(orderHistory.size() == 0){
                        orderListView.setEmptyView(rootView.findViewById(R.id.OrderList_empty_list_item));
                        rootView.findViewById(R.id.heading).setVisibility(View.GONE);
                        rootView.findViewById(R.id.pendingOrderList_Empty_list_item).setVisibility(View.GONE);
                    }
                    else {
                        rootView.findViewById(R.id.OrderList_empty_list_item).setVisibility(View.GONE);
                        rootView.findViewById(R.id.pendingOrderList_Empty_list_item).setVisibility(View.GONE);
                        rootView.findViewById(R.id.OrderList_empty_list_item).setVisibility(View.GONE);
                        rootView.findViewById(R.id.heading).setVisibility(View.VISIBLE);
                        orderListView.setAdapter(orderListAdapter);
                    }

                    orderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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


                }
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //dailyReportTypeSpinner.setSelection(0, true);
            }


        });
        orderListTypeSpinner.setSelection(position, true);
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onOrderListFragmentInteraction(uri);
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
        void onOrderListFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume() {
        super.onResume();

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        dateFormatter.setLenient(false);
        Date today = new Date();

        final String date = dateFormatter.format(today);
        final View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        pendingOrderListView = rootView.findViewById(R.id.pendingOrderListView);

        pendingOrders = initializePendingOrderList(getActivity().getApplicationContext(), date);

        pendingOrderListAdapter = new PendingOrderListViewAdapter(getContext().getApplicationContext(), pendingOrders);
        pendingOrderListView.setAdapter(pendingOrderListAdapter);

        if (pendingOrders.size() == 0) {
            pendingOrderListView.setEmptyView(rootView.findViewById(R.id.pendingOrderList_Empty_list_item));
            rootView.findViewById(R.id.OrderList_empty_list_item).setVisibility(View.GONE);
            rootView.findViewById(R.id.heading).setVisibility(View.GONE);
        } else {
            rootView.findViewById(R.id.OrderList_empty_list_item).setVisibility(View.GONE);
            rootView.findViewById(R.id.pendingOrderList_Empty_list_item).setVisibility(View.GONE);
            rootView.findViewById(R.id.heading).setVisibility(View.VISIBLE);
            pendingOrderListView.setAdapter(pendingOrderListAdapter);
        }
        pendingOrderListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("destroyedd", "destriyed");
        SharedPreferences timerNotifications = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        SharedPreferences.Editor editor = timerNotifications.edit();
        editor.putInt("order_list_position", 0);
        editor.apply();
    }
}
