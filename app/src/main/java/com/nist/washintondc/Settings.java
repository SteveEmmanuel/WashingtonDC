package com.nist.washintondc;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.evernote.android.job.JobManager;

import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.nist.washintondc.InitializeDataForConsumption.initializeEmailList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Settings.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Settings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Settings extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    DaoSession daoSession;
    List<EmailRecipients> emails;
    ListView emailList ;
    EmailListViewAdapter adapter;
    Button timePicker;
    TextView timeView;

    public Settings() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Settings.
     */
    // TODO: Rename and change types and number of parameters
    public static Settings newInstance(String param1, String param2) {
        Settings fragment = new Settings();
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
                .setActionBarTitle("Settings");

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        timeView = view.findViewById(R.id.timeView);
        SharedPreferences emailPreferences = getActivity().getSharedPreferences("emailPreferences", MODE_PRIVATE);

        String emailTime = emailPreferences.getString("emailTime", null);

        timeView.setText(emailTime);

        daoSession = ((App) getActivity().getApplicationContext()).getDaoSession();
        emails = initializeEmailList(getActivity().getApplicationContext());
        emailList = (ListView) view.findViewById(R.id.emailList);
        adapter= new EmailListViewAdapter(getActivity().getApplicationContext(), emails);
        emailList.setAdapter(adapter);
        registerForContextMenu(emailList);

        final EditText newEmail = (EditText) view.findViewById(R.id.newEmail);
        Button addEmail = (Button) view.findViewById(R.id.addNewEmail);
        addEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailTobeAdded = newEmail.getText().toString();
                if (isEmailValid(emailTobeAdded)) {
                    Cursor cursor = daoSession.getDatabase().rawQuery(
                            "SELECT * from email_recipients where email=\'" + emailTobeAdded + "\'", new String[]{});
                    if (cursor.moveToFirst() && cursor.getCount() > 0) {
                        Toast.makeText(getActivity().getApplicationContext(), "Email already Exists", Toast.LENGTH_SHORT).show();
                    } else {
                        EmailRecipients email = new EmailRecipients();
                        email.setEmail(emailTobeAdded);
                        daoSession.getEmailRecipientsDao().insert(email);
                        emails.add(email);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getActivity().getApplicationContext(), "Email added succesfully", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Enter Valid Email", Toast.LENGTH_SHORT).show();
                }
            }
        });

        timePicker = (Button) view.findViewById(R.id.timePicker);
        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showTimePickerDialog(v);
            }
        });

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.email_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;

        switch (item.getItemId()) {
            case R.id.emailDelete:
                daoSession.getEmailRecipientsDao().delete(emails.get(position));
                emails.remove(position);
                adapter.notifyDataSetChanged();
                Toast.makeText(getActivity().getApplicationContext(), "email removed", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onSettingsFragmentInteraction(uri);
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
     * See the Android Training lesson <a href=z
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSettingsFragmentInteraction(Uri uri);
    }

    boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();

        newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
    }


    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        TextView timeView;
        View rootView;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
            timeView = rootView.findViewById(R.id.timeView);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            timeView.setText(hourOfDay+":"+minute);
            SharedPreferences.Editor editor = getActivity().getSharedPreferences("emailPreferences", MODE_PRIVATE).edit();
            editor.putString("emailTime", timeView.getText().toString());
            editor.commit();
            //setRepeatingAlarm(getActivity().getApplicationContext());
            JobManager.instance().cancelAllForTag(PrepareExcelForEmail.TAG);
            JobManager.instance().cancelAllForTag(SendExcelEmailJob.TAG);
            PrepareExcelForEmail.scheduleNextJob(getActivity().getApplicationContext(), true);

        }
    }
}