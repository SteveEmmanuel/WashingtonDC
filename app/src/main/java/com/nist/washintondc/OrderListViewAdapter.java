package com.nist.washintondc;

/**
 * Created by oldtrafford on 05/06/18.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OrderListViewAdapter extends BaseAdapter{

    Context c;
    public List<Orders> orderHistory;
    Boolean extended;

    TextView orderListNo, orderListCustomerName, orderListCustomerCarName,
                orderListPackageName, orderListAmount, orderListDiscount;

    public OrderListViewAdapter(Context c, List<Orders> orderHistory, Boolean extended){
        this.c=  c;
        this.orderHistory = orderHistory;
        this.extended = extended;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return orderHistory.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return orderHistory.get(position);
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
            if(extended){
                view= LayoutInflater.from(c).inflate(R.layout.daily_report_order_row, viewGroup,false);
            }
            else {
                view = LayoutInflater.from(c).inflate(R.layout.order_row, viewGroup, false);
            }
        }
        if(extended){
            orderListNo = (TextView) view.findViewById(R.id.dailReportByOrderNo);
            orderListCustomerCarName=(TextView) view.findViewById(R.id.dailReportByOrderCarName);
            orderListPackageName = (TextView) view.findViewById(R.id.dailReportByOrderPackageName);
            orderListAmount = (TextView) view.findViewById(R.id.dailReportByOrderAmount);
            orderListDiscount = (TextView) view.findViewById(R.id.dailReportByOrderDiscount);

            orderListAmount.setText(orderHistory.get(i).getAmount().toString());
            orderListDiscount.setText(orderHistory.get(i).getDiscount().toString());
        }
        else {
            orderListNo = (TextView) view.findViewById(R.id.orderListNo);
            orderListCustomerName =(TextView) view.findViewById(R.id.orderListCustomerName);
            orderListCustomerCarName=(TextView) view.findViewById(R.id.orderListCustomerCarName);
            orderListPackageName = (TextView) view.findViewById(R.id.orderListPackageName);

            orderListCustomerName.setText(orderHistory.get(i).getCustomer().getName().toString());


        }
        orderListNo.setText(String.valueOf(i+1));
        orderListCustomerCarName.setText(orderHistory.get(i).getCustomer().getCarName().toString());
        orderListPackageName.setText(orderHistory.get(i).getPackages().getName().toString()+"-"+orderHistory.get(i).getCustomer().getCarType());

        view.setTag(new Long(orderHistory.get(i).getId()));
        if(i%2 == 0){ view.setBackgroundColor(Color.parseColor("#f7f7f7")); }
        return view;
    }

}