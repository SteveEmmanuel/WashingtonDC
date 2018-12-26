package com.nist.washintondc;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CustomerDetails.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CustomerDetails#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerDetails extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //Spinner spinner;
    Button nextButton;
    TextView name;
    TextView registration, carName;
    TextView phone, email;
    DaoSession daoSession;
    public CustomerDetails() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomerDetails.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerDetails newInstance(String param1, String param2) {
        CustomerDetails fragment = new CustomerDetails();
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
        ((MainActivity) getActivity())
                .setActionBarTitle("Enter Customer Details");
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_customer_details, container, false);
        //spinner = (Spinner) view.findViewById(R.id.customerCarType);
        nextButton = (Button) view.findViewById(R.id.customerSubmit);
        name = (TextView) view.findViewById(R.id.customerName);
        registration = (TextView) view.findViewById(R.id.customerRegistration);
        phone = (TextView) view.findViewById(R.id.customerPhoneNumber);
        carName = (TextView) view.findViewById(R.id.customerCarName);
        email = (TextView) view.findViewById(R.id.customerEmail);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Boolean error = false;
                if( TextUtils.isEmpty(name.getText())){
                    name.setError( "Your name is required!" );
                    error = setError();
                }
                if( TextUtils.isEmpty(registration.getText())){
                    registration.setError( "Registration number is required!" );
                    error = setError();
                }
                if( TextUtils.isEmpty(phone.getText())){
                    phone.setError( "Phone number is required!" );
                    error = setError();

                }
                if(!Patterns.PHONE.matcher(phone.getText()).matches()){
                    phone.setError( "Enter Valid Phone Number!" );
                    error = setError();
                }
                if( TextUtils.isEmpty(email.getText())){
                    email.setError( "Email is required!" );
                    error = setError();

                }
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()){
                    email.setError( "Enter Valid Email!" );
                    error = setError();
                }
                if(!error) {
                    daoSession = ((App) getActivity().getApplication()).getDaoSession();

                    Customers customer = new Customers();
                    customer.setName(name.getText().toString());
                    customer.setCarName(carName.getText().toString());
                    customer.setCarType(getArguments().getString("carType"));
                    customer.setEmail(email.getText().toString());
                    customer.setPhone(phone.getText().toString());
                    customer.setRegistration(registration.getText().toString());

                    long customerId = daoSession.getCustomersDao().insert(customer);

                    PendingOrders pendingOrder = new PendingOrders();

                    DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
                    dateFormatter.setLenient(false);
                    Date today = new Date();
                    String date = dateFormatter.format(today);
                    String time = timeFormatter.format(today);

                    pendingOrder.setCustomerId(customerId);
                    pendingOrder.setPackageId(getArguments().getLong("selectedPackage"));

                    pendingOrder.setDate(date);
                    pendingOrder.setStartTime(time);

                    daoSession.getPendingOrdersDao().insert(pendingOrder);


                    OrderList newFragment = new OrderList();

                    Bundle bundle = new Bundle();
                    bundle.putLong("pendingOrderId", pendingOrder.getId());
                    newFragment.setArguments(bundle);

                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                    // Replace whatever is in the fragment_container view with this fragment,
                    // and add the transaction to the back stack so the user can navigate back
                    transaction.replace(R.id.fragment, newFragment);

                    FragmentManager fm = getFragmentManager(); // or 'getSupportFragmentManager();'
                    int count = fm.getBackStackEntryCount();
                    for(int i = 0; i < count; i++) {
                        fm.popBackStack();
                    }
                    // Commit the transaction
                    transaction.commit();
                }
            }
        });
        /* Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.carTypes, android.R.layout.simple_spinner_item);
         Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         Apply the adapter to the spinner
        spinner.setAdapter(adapter);*/

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onCustomerDetailsFragmentInteraction(uri);
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
        void onCustomerDetailsFragmentInteraction(Uri uri);
    }
    public Boolean setError(){
        return true;
    }
}
