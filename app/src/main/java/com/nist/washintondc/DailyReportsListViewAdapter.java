package com.nist.washintondc;

/**
 * Created by oldtrafford on 28/06/18.
 */

        import java.nio.channels.FileLock;
        import java.util.ArrayList;
        import java.util.List;

        import android.content.Context;
        import android.graphics.Color;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.TextView;

public class DailyReportsListViewAdapter extends BaseAdapter {

    Context c;
    public List<Expenses> expenses;
    ArrayList<ArrayList<String>> packages = new ArrayList<ArrayList<String>>();
    int type; // decides the view(balance or report by category)

    public DailyReportsListViewAdapter(Context c, List list){
        this.c=  c;
        this.expenses = list;

    }

    public DailyReportsListViewAdapter(Context c, ArrayList list){
        this.c=  c;
        this.packages = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if(expenses!=null){
            return expenses.size();
        }
        else if(packages!=null){
            return packages.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return expenses.get(position);
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

        if (expenses!=null) {
            if (view == null) {
                view = LayoutInflater.from(c).inflate(R.layout.expense_row, viewGroup, false);
            }

            TextView expenseListNo = (TextView) view.findViewById(R.id.expenseListNo);
            ;
            TextView expenseListParticulars = (TextView) view.findViewById(R.id.expenseListParticulars);
            TextView expenseListCredit = (TextView) view.findViewById(R.id.expenseListCredit);
            TextView expenseListDebit = (TextView) view.findViewById(R.id.expenseListDebit);

            expenseListNo.setText(String.valueOf(i + 1));
            expenseListParticulars.setText(expenses.get(i).getParticulars());
            if (expenses.get(i).getType().equals("credit")) {
                expenseListCredit.setText(expenses.get(i).getAmount());
                expenseListDebit.setText("");
            } else if (expenses.get(i).getType().equals("debit")) {
                expenseListDebit.setText(expenses.get(i).getAmount());
                expenseListCredit.setText("");
            } else if (expenses.get(i).getType().equals("end")) {
                expenseListDebit.setText(expenses.get(i).getAmount());
                expenseListCredit.setText(expenses.get(i).getAmount());
            }
            expenseListParticulars.setText(expenses.get(i).getParticulars());
            if (i % 2 == 0) {
                view.setBackgroundColor(Color.parseColor("#f7f7f7"));
            }
            return view;
        }
        else if(packages!=null){
            if (view == null) {
                view = LayoutInflater.from(c).inflate(R.layout.daily_reports_by_category_row, viewGroup, false);
            }

            TextView packageName = (TextView) view.findViewById(R.id.dailyReportsByCategoryPackageName);
            TextView count = (TextView) view.findViewById(R.id.dailyReportsByCategoryCount);
            TextView packageAmount = (TextView) view.findViewById(R.id.dailyReportsByCategoryAmount);


            packageName.setText(packages.get(i).get(0));
            count.setText(packages.get(i).get(1));
            packageAmount.setText(packages.get(i).get(2));
            //count.setText(packages.get(i));

            if (i % 2 == 0) {
                view.setBackgroundColor(Color.parseColor("#f7f7f7"));
            }
            return view;

        }
        return null;
    }
}