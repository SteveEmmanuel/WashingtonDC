package com.nist.washintondc;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.greendao.query.QueryBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.Duration;

import static com.nist.washintondc.InitializeDataForConsumption.initializeOrderDetails;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OrderDetails.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OrderDetails#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderDetails extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    Orders order;
    EditText orderDetailCustomerName, orderDetailCustomerRegistration, orderDetailCustomerEmail, orderDetailCustomerPhone,
            orderDetailCustomerCarName, orderDetailDiscount, orderDetailAmountEdit;
    TextView orderDetailPackageName, orderDetailAmount, orderDetailTotal, orderDetailTime;
    Button update, delete;
    float initialAmount;
    public OrderDetails() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderDetails newInstance(String param1, String param2) {
        OrderDetails fragment = new OrderDetails();
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
        }

        /*orderId = getArguments().getLong("orderId");
        order = initializeOrderDetails(getContext().getApplicationContext(), orderId);*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity())
                .setActionBarTitle("Order Detail");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_detail, container, false);


        orderDetailCustomerName = (EditText) view.findViewById(R.id.orderDetailCustomerName);
        orderDetailCustomerRegistration = (EditText) view.findViewById(R.id.orderDetailCustomerRegistration);
        orderDetailCustomerEmail = (EditText) view.findViewById(R.id.orderDetailCustomerEmail);
        orderDetailCustomerPhone = (EditText) view.findViewById(R.id.orderDetailCustomerPhone);
        orderDetailCustomerCarName = (EditText) view.findViewById(R.id.orderDetailCustomerCarName);
        orderDetailDiscount = (EditText) view.findViewById(R.id.orderDetailDiscount);
        orderDetailAmountEdit = (EditText) view.findViewById(R.id.orderDetailAmountEdit);

        orderDetailPackageName = (TextView) view.findViewById(R.id.orderDetailPackageName);
        orderDetailTotal = (TextView) view.findViewById(R.id.orderDetailTotal);
        orderDetailAmount = (TextView) view.findViewById(R.id.orderDetailAmount);
        orderDetailTime= (TextView) view.findViewById(R.id.orderDetailTime);


        update = (Button) view.findViewById(R.id.update);
        delete= (Button) view.findViewById(R.id.delete);


        final DaoSession daoSession = ((App) getActivity().getApplication()).getDaoSession();
        QueryBuilder queryBuilder = daoSession.getOrdersDao().queryBuilder()
                .where(OrdersDao.Properties.Id.eq(getArguments().getString("order_id")));
        final List<Orders> orders = queryBuilder.list();
        order = orders.get(0);

        orderDetailCustomerName.setText(order.getCustomer().getName());
        orderDetailCustomerCarName.setText(order.getCustomer().getCarName());
        orderDetailCustomerRegistration.setText(order.getCustomer().getEmail());
        orderDetailCustomerEmail.setText(order.getCustomer().getEmail());
        orderDetailCustomerPhone.setText(order.getCustomer().getPhone());
        orderDetailAmount.setText(order.getAmount().toString());
        orderDetailDiscount.setText(order.getDiscount().toString());

        double total = (order.getAmount() - order.getDiscount());
        orderDetailTotal.setText(String.valueOf(total));

        initialAmount = order.getPackages().getPrice().getPriceByCarType(order.getCustomer().getCarType());

        if(order.getPackages().getPrice().getPriceByCarType(order.getCustomer().getCarType())==0f){
            orderDetailAmount.setVisibility(View.GONE);
            orderDetailAmountEdit.setVisibility(View.VISIBLE);
            orderDetailAmountEdit.setText(order.getAmount().toString());
        }
        else{
            orderDetailAmount.setVisibility(View.VISIBLE);
            orderDetailAmountEdit.setVisibility(View.GONE);
            //orderDetailAmount.setText(String.valueOf(initialAmount));
        }

        orderDetailPackageName.setText(order.getPackages().getName()+" - "+order.getCustomer().getCarType());

        String dateStart = order.getStartTime();
        String dateStop = order.getEndTime();

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
            orderDetailTime.setText(diffHours+"h: "+diffMinutes+"m: "+diffSeconds+"s");
        } catch (Exception e) {
            e.printStackTrace();
        }


        orderDetailDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                float amount = 0f;
                float discount = 0f;

                if(order.getPackages().getPrice().getPriceByCarType(order.getCustomer().getCarType())==0f){
                   if(orderDetailAmountEdit.getText().toString().matches("\\d+(?:\\.\\d+)?")){
                       amount = Float.parseFloat(orderDetailAmountEdit.getText().toString());
                   }
                }
                else{
                    if(orderDetailAmount.getText().toString().matches("\\d+(?:\\.\\d+)?")){
                        amount = Float.parseFloat(orderDetailAmount.getText().toString());
                    }
                }

                if(s.toString().matches("\\d+(?:\\.\\d+)?")){
                    discount = Float.parseFloat(s.toString());
                    amount = amount - discount;
                    if (amount < 0) {
                        amount = 0f;
                    }
                }
                orderDetailTotal.setText(String.valueOf(amount));
            }
        });

        orderDetailAmountEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                float amount = 0f;
                float discount = 0f;

                if(orderDetailDiscount.getText().toString().matches("\\d+(?:\\.\\d+)?")){
                    discount = Float.parseFloat(orderDetailDiscount.getText().toString());
                }

                if(s.toString().matches("\\d+(?:\\.\\d+)?")){
                    amount = Float.parseFloat(s.toString());
                    orderDetailAmount.setText(Float.toString(amount));
                    amount = amount - discount;
                    if(amount < 0){
                        amount = 0f;
                     }
                    }
                orderDetailTotal.setText(String.valueOf(amount));
                }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean error = false;
                if( TextUtils.isEmpty(orderDetailCustomerName.getText())){
                    orderDetailCustomerName.setError( "Your name is required!" );
                    error = setError();
                }
                if( TextUtils.isEmpty(orderDetailCustomerRegistration.getText())){
                    orderDetailCustomerRegistration.setError( "Registration number is required!" );
                    error = setError();
                }
                if( TextUtils.isEmpty(orderDetailCustomerPhone.getText())){
                    orderDetailCustomerPhone.setError( "Phone number is required!" );
                    error = setError();

                }
                if(!Patterns.PHONE.matcher(orderDetailCustomerPhone.getText()).matches()){
                    orderDetailCustomerPhone.setError( "Enter Valid Phone Number!" );
                    error = setError();
                }
                if( TextUtils.isEmpty(orderDetailCustomerEmail.getText())){
                    orderDetailCustomerEmail.setError( "Email is required!" );
                    error = setError();

                }
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(orderDetailCustomerEmail.getText()).matches()){
                    orderDetailCustomerEmail.setError( "Enter Valid Email!" );
                    error = setError();
                }
                if(order.getPackages().getPrice().getPriceByCarType(order.getCustomer().getCarType())==0f){
                    if( TextUtils.isEmpty(orderDetailAmountEdit.getText())){
                        orderDetailAmountEdit.setError( "Enter Amount!" );
                        error = setError();
                    }
                }
                if(!error) {
                    order.getCustomer().setName(orderDetailCustomerName.getText().toString());
                    order.getCustomer().setRegistration(orderDetailCustomerRegistration.getText().toString());
                    order.getCustomer().setCarName(orderDetailCustomerCarName.getText().toString());
                    order.getCustomer().setEmail(orderDetailCustomerEmail.getText().toString());
                    order.getCustomer().setPhone(orderDetailCustomerPhone.getText().toString());

                    order.setDiscount(Float.parseFloat(orderDetailDiscount.getText().toString()));
                    order.setAmount(Float.parseFloat(orderDetailAmount.getText().toString()));

                    daoSession.getOrdersDao().update(order);
                    Toast.makeText(getContext().getApplicationContext(),
                            "Order Updated", Toast.LENGTH_SHORT).show();;
                    orderDetailPackageName.setText(order.getPackages().getName() + " - " + order.getCustomer().getCarType());
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getActivity());
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {

                                dialogBox.cancel();
                            }
                        })
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                daoSession.delete(order.getCustomer());
                                daoSession.delete(order);
                                Toast.makeText(getContext().getApplicationContext(),
                                        "Order Deleted", Toast.LENGTH_SHORT).show();;
                                OrderList newFragment = new OrderList();

                                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                                FragmentManager fragmentManager = getFragmentManager();
                                fragmentManager.popBackStackImmediate();
                            }
                        }).setTitle("Are you sure?");
                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();

            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onOrderDetailFragmentInteraction(uri);
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
        void onOrderDetailFragmentInteraction(Uri uri);
    }

    public Boolean setError(){
        return true;
    }
}
