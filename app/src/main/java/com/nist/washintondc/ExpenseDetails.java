package com.nist.washintondc;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExpenseDetails.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExpenseDetails#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpenseDetails extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    Spinner spinner;
    Button expenseSubmit;
    TextView expenseParticulars;
    TextView expenseAmount;

    public ExpenseDetails() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExpenseDetails.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpenseDetails newInstance(String param1, String param2) {
        ExpenseDetails fragment = new ExpenseDetails();
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
        ((MainActivity) getActivity())
                .setActionBarTitle("Enter Expense Details");
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_expense, container, false);
        final DaoSession daoSession = ((App) getActivity().getApplication()).getDaoSession();
        spinner = (Spinner) view.findViewById(R.id.expenseType);
        expenseSubmit = (Button) view.findViewById(R.id.expenseSubmit);
        expenseParticulars = (TextView) view.findViewById(R.id.expenseParticulars);
        expenseAmount = (TextView) view.findViewById(R.id.expenseAmount);
        expenseSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                if( TextUtils.isEmpty(expenseParticulars.getText())){
                    expenseParticulars.setError( "Particulars is required!" );

                }
                else if( TextUtils.isEmpty(expenseAmount.getText())){
                    expenseAmount.setError( "Amount is required!" );

                }
                else {
                    DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
                    dateFormatter.setLenient(false);
                    String date;

                    Bundle arguments = getArguments();
                    if (arguments != null && arguments.containsKey("date")){
                        date = arguments.getString("date");
                    }
                    else{
                        date = dateFormatter.format(new Date());
                    }

                    Date today = new Date();
                    String time = timeFormatter.format(today);
                    Expenses expense = new Expenses();
                    expense.setAmount(expenseAmount.getText().toString());
                    expense.setDate(date);
                    expense.setTime(time);
                    expense.setParticulars(expenseParticulars.getText().toString());
                    expense.setType(spinner.getSelectedItem().toString().toLowerCase());

                    daoSession.getExpensesDao().insert(expense);
                    DailyReports newFragment = new DailyReports();
                    Bundle bundle = new Bundle();
                    bundle.putString("date", date);
                    newFragment.setArguments(bundle);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    // Replace whatever is in the fragment_container view with this fragment,
                    // and add the transaction to the back stack so the user can navigate back
                    transaction.replace(R.id.fragment, newFragment);

                    FragmentManager fm = getActivity().getSupportFragmentManager(); // or 'getSupportFragmentManager();'
                    fm.popBackStack();

                    transaction.commit();
                }
            }
        });
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.expenseTypes, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onExpenseDetailsFragmentInteraction(uri);
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
        void onExpenseDetailsFragmentInteraction(Uri uri);
    }
}
