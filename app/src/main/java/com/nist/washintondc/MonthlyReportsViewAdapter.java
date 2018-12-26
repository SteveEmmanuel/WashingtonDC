package com.nist.washintondc;
/**
 * Created by oldtrafford on 01/07/18.
 */

        import java.text.DateFormat;
        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;

        import android.app.Activity;
        import android.content.Context;
        import android.graphics.Color;
        import android.os.Bundle;
        import android.support.v4.app.FragmentTransaction;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.TextView;

        import com.nist.washintondc.Orders;

public class MonthlyReportsViewAdapter extends BaseAdapter {

    Context c;
    ArrayList<ArrayList<String>> orders = new ArrayList<ArrayList<String>>();

    public MonthlyReportsViewAdapter(Context c, ArrayList orders){
        this.c=  c;
        this.orders = orders;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return orders.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return orders.get(position);
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
            view= LayoutInflater.from(c).inflate(R.layout.monthly_report_row, viewGroup,false);
        }

        TextView orderDate = (TextView) view.findViewById(R.id.monthlyReportDate);;
        TextView collection =(TextView) view.findViewById(R.id.monthlyReportCollection);

        orderDate.setText(orders.get(i).get(0));
        collection.setText(orders.get(i).get(1));
        if(i%2 == 0){ view.setBackgroundColor(Color.parseColor("#f7f7f7")); }
        return view;
    }

}