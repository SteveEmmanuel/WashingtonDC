package com.nist.washintondc;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.greendao.query.QueryBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static com.nist.washintondc.InitializeDataForConsumption.getPendingOrderById;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BillDetails.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BillDetails#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BillDetails extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    EditText name;
    EditText registration, carName;
    EditText phone, email;
    TextView amount;
    EditText discount;
    TextView total;
    Button submit;
    EditText amountEdit;
    TextView packageName, duration;
    float initialAmount;

    private ThreadHandler myHandlerThread;
    private Handler handler = new Handler();

    NotificationManager notificationManager;
    NotificationCompat.Builder notificationBuilder;

    PendingOrders pendingOrder;

    public BillDetails() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BillDetails.
     */
    // TODO: Rename and change types and number of parameters
    public static BillDetails newInstance(String param1, String param2) {
        BillDetails fragment = new BillDetails();
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

        SharedPreferences timerNotifications = getContext().getApplicationContext().getSharedPreferences("timerNotifications", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = timerNotifications.edit();
        editor.putBoolean("submit_pending_order_id_"+getArguments().getString("pending_order_id"), true);
        editor.apply();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity())
                .setActionBarTitle("Complete the Order");
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_bill_details, container, false);

        final DaoSession daoSession = ((App) getActivity().getApplication()).getDaoSession();

        Bundle b = getArguments();

        pendingOrder = getPendingOrderById(getContext().getApplicationContext(),
                    Long.parseLong(getArguments().getString("pending_order_id")));


        name = view.findViewById(R.id.billName);
        registration = view.findViewById(R.id.billregistration);
        phone = view.findViewById(R.id.billPhone);
        email = view.findViewById(R.id.billEmail);
        carName = view.findViewById(R.id.billCarName);
        submit = view.findViewById(R.id.submit);
        discount  = view.findViewById(R.id.discount);
        amount = view.findViewById(R.id.amount);
        amountEdit = view.findViewById(R.id.amountEdit);
        total = view.findViewById(R.id.total);
        packageName = view.findViewById(R.id.billPackageName);
        duration = view.findViewById(R.id.billTime);
        /*if(pendingOrder == null){
            getActivity().finish();
        }*/
        name.setText(pendingOrder.getCustomer().getName());
        registration.setText(pendingOrder.getCustomer().getRegistration());
        phone.setText(pendingOrder.getCustomer().getPhone());
        email.setText(pendingOrder.getCustomer().getEmail());
        carName.setText(pendingOrder.getCustomer().getCarName());
        packageName.setText(pendingOrder.getPackages().getName()+" - "+pendingOrder.getCustomer().getCarType());

        String dateStart = pendingOrder.getStartTime();
        String dateStop = getArguments().getString("end_time");

        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);
            duration.setText(diffHours+"h: "+diffMinutes+"m: "+diffSeconds+"s");
        } catch (Exception e) {
            e.printStackTrace();
        }

        initialAmount = pendingOrder.getPackages().getPrice().getPriceByCarType(pendingOrder.getCustomer().getCarType());

        if(pendingOrder.getPackages().getType().toString().equals("detailing_package")){
            amount.setVisibility(View.GONE);
            amountEdit.setVisibility(View.VISIBLE);
            amountEdit.setText(String.valueOf(initialAmount));
        }
        else{
            amount.setVisibility(View.VISIBLE);
            amountEdit.setVisibility(View.GONE);
            amount.setText(String.valueOf(initialAmount));
        }
        total.setText(String.valueOf(initialAmount));

        amountEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                float amountFloat = 0f;
                float discountFloat = 0f;

                if (discount.getText().toString().matches("\\d+(?:\\.\\d+)?")) {
                    discountFloat = Float.parseFloat(discount.getText().toString());
                }

                if (s.toString().matches("\\d+(?:\\.\\d+)?")) {
                    amountFloat = Float.parseFloat(s.toString());
                    discount.setText(Float.toString(amountFloat));
                    amountFloat = amountFloat - discountFloat;
                    if (amountFloat < 0) {
                        amountFloat = 0f;
                    }
                }
                total.setText(String.valueOf(amountFloat));
            }
        });

        discount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                float amountFloat = 0f;
                float discount = 0f;

                if(pendingOrder.getPackages().getPrice().getPriceByCarType(pendingOrder.getCustomer().getCarType())==0f){
                    if(amountEdit.getText().toString().matches("\\d+(?:\\.\\d+)?")){
                        amountFloat = Float.parseFloat(amountEdit.getText().toString());
                    }
                }
                else{
                    if(amount.getText().toString().matches("\\d+(?:\\.\\d+)?")){
                        amountFloat = Float.parseFloat(amount.getText().toString());
                    }
                }

                if(s.toString().matches("\\d+(?:\\.\\d+)?")){
                    discount = Float.parseFloat(s.toString());
                    amountFloat = amountFloat - discount;
                    if (amountFloat < 0) {
                        amountFloat = 0f;
                    }
                }
                total.setText(String.valueOf(amountFloat));

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                if(pendingOrder.getPackages().getType().toString().equals("detailing_package")){
                    if( TextUtils.isEmpty(amountEdit.getText())){
                        amountEdit.setError( "Enter Amount!" );

                    }
                    else{
                        amount.setText(amountEdit.getText().toString());
                    }
                    }
                if(pendingOrder.getPackages().getType().toString().equals("detailing_package") && !TextUtils.isEmpty(amountEdit.getText()) ||
                        pendingOrder.getPackages().getType().toString().equals("wash_package")) {

                    final Orders order = new Orders();

                    DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
                    dateFormatter.setLenient(false);
                    Date today = new Date();
                    String date = dateFormatter.format(today);
                    String time = timeFormatter.format(today);

                    Customers customer = pendingOrder.getCustomer();
                    customer.setCarName(carName.getText().toString());
                    customer.setPhone(phone.getText().toString());
                    customer.setEmail(email.getText().toString());
                    customer.setRegistration(registration.getText().toString());
                    customer.setName(name.getText().toString());
                    daoSession.update(customer);

                    order.setCustomerId(customer.getCustomerId());
                    order.setCustomer(pendingOrder.getCustomer());
                    order.setPackageId(pendingOrder.getPackageId());
                    order.setPackages(pendingOrder.getPackages());
                    order.setAmount(Float.parseFloat(amount.getText().toString()));
                    if(discount.getText().toString().matches("\\d+(?:\\.\\d+)?")) {
                        order.setDiscount(Float.parseFloat(discount.getText().toString()));
                    }
                    else{
                        order.setDiscount(0f);
                    }
                    order.setDate(date);
                    order.setStartTime(pendingOrder.getStartTime());
                    order.setEndTime(getArguments().getString("end_time"));
                    daoSession.getOrdersDao().insert(order);

                    QueryBuilder queryBuilder = daoSession.getPendingOrdersDao().queryBuilder();
                    List<PendingOrders> pendingOrders = queryBuilder.list();
                    //pendingOrders.remove(pendingOrder);
                    daoSession.delete(pendingOrder);

                    SharedPreferences timerNotifications = getContext().getApplicationContext().getSharedPreferences("timerNotifications", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = timerNotifications.edit();
                    editor.remove("pending_order_id_"+pendingOrder.getId());
                    editor.remove("submit_pending_order_id_"+pendingOrder.getId());
                    editor.apply();

                    getActivity().finish();

                }
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onBillDetailsFragmentInteraction(uri);
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
        Log.v("destroyedd", "detached");
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
        void onBillDetailsFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("destroyedd", "destriyed");
        SharedPreferences timerNotifications = getContext().getApplicationContext().getSharedPreferences("timerNotifications", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = timerNotifications.edit();
        editor.remove("submit_pending_order_id_"+pendingOrder.getId());
        editor.apply();
    }
}
