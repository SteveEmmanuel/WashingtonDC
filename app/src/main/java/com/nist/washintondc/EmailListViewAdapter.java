package com.nist.washintondc;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oldtrafford on 09/07/18.
 */


public class EmailListViewAdapter extends BaseAdapter {

    Context c;
    List<EmailRecipients> emails = new ArrayList<>();

    public EmailListViewAdapter(Context c, List emails){
        this.c=  c;
        this.emails = emails;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return emails.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return emails.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    private class ViewHolder{

    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        if(view==null)
        {
            view= LayoutInflater.from(c).inflate(R.layout.email_list_row, viewGroup,false);
        }

        TextView email = (TextView) view.findViewById(R.id.email);

        email.setText(emails.get(i).getEmail());
        if(i%2 == 0){ view.setBackgroundColor(Color.parseColor("#f7f7f7")); }
        return view;
    }

}