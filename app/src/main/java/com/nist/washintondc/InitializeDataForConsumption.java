package com.nist.washintondc;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * Created by oldtrafford on 06/07/18.
 */

public class InitializeDataForConsumption {

    public static ArrayList<ArrayList<String>> initializeReportByCategoryData(Context context, String today){
        ArrayList<ArrayList<String>> packages = new ArrayList<ArrayList<String>>();
        DaoSession daoSession = ((App) context).getDaoSession();
        Cursor cursor = daoSession.getDatabase().rawQuery(
                "SELECT Packages.name, count(*), sum(amount-discount) from Orders join Packages on Orders.PACKAGE_ID=Packages._id where date=\'"+today+"\' group by Orders.PACKAGE_ID", new String []{});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ArrayList<String> p = new ArrayList<String>();
            p.add(cursor.getString(0));
            p.add(cursor.getString(1));
            p.add(cursor.getString(2));//add the item
            packages.add(p);
            cursor.moveToNext();
        }
        return packages;
    }


    public static List<Expenses> initializeBalanceData(Context context, String today){
        List<Expenses> expenses = new ArrayList<>();
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
        dateFormatter.setLenient(false);
        Calendar cal = Calendar.getInstance();
        try{
            cal.setTime(dateFormatter.parse(today));
        }
        catch (ParseException e){
            e.printStackTrace();
        }

        cal.add(Calendar.DATE, -1);
        String yesterday = dateFormatter.format(cal.getTime());
        cal.add(Calendar.DATE, -1);
        String dayBeforeYesterday = dateFormatter.format(cal.getTime());
        String time = timeFormatter.format(new Date());


        DaoSession daoSession = ((App) context).getDaoSession();

        Cursor cursor = daoSession.getDatabase().rawQuery("SELECT SUM(amount-discount) from Orders where date=\'"+dayBeforeYesterday+"\'", new String []{});
        cursor.moveToFirst();
        Float orderSumDayBeforeYesterday = cursor.getFloat(0);
        cursor = daoSession.getDatabase().rawQuery("SELECT SUM(amount) from Expenses where date='"+dayBeforeYesterday+"\' and type='credit'", new String []{});
        cursor.moveToFirst();
        Float creditDayBeforeYesterday = cursor.getFloat(0);
        cursor = daoSession.getDatabase().rawQuery("SELECT SUM(amount) from Expenses where date='"+dayBeforeYesterday+"\' and type='debit'", new String []{});
        cursor.moveToFirst();
        Float debitDayBeforeYesterday = cursor.getFloat(0);


        cursor = daoSession.getDatabase().rawQuery("SELECT SUM(amount-discount) from Orders where date=\'"+yesterday+"\'", new String []{});
        cursor.moveToFirst();
        Float orderSumYesterday = cursor.getFloat(0);
        cursor = daoSession.getDatabase().rawQuery("SELECT SUM(amount) from Expenses where date='"+yesterday+"\' and type='credit'", new String []{});
        cursor.moveToFirst();
        Float creditYesterday = cursor.getFloat(0);
        cursor = daoSession.getDatabase().rawQuery("SELECT SUM(amount) from Expenses where date='"+yesterday+"\' and type='debit'", new String []{});
        cursor.moveToFirst();
        Float debitYesterday = cursor.getFloat(0);

        cursor = daoSession.getDatabase().rawQuery("SELECT SUM(amount-discount) from Orders where date='"+today+"\'", new String []{});
        cursor.moveToFirst();
        Long orderSum = cursor.getLong(0);
        cursor = daoSession.getDatabase().rawQuery("SELECT SUM(amount) from Expenses where date='" + today + "\' and type='credit'", new String []{});
        cursor.moveToFirst();
        Long credit = cursor.getLong(0);
        cursor = daoSession.getDatabase().rawQuery("SELECT SUM(amount) from Expenses where date='" + today + "\' and type='debit'", new String []{});
        cursor.moveToFirst();
        Long debit = cursor.getLong(0);

        QueryBuilder queryBuilder = daoSession.getExpensesDao().queryBuilder()
                .where(ExpensesDao.Properties.Date.eq(today));
        expenses = queryBuilder.list();

        Float closingBalanceDayBeforeYesterday = orderSumDayBeforeYesterday+creditDayBeforeYesterday-debitDayBeforeYesterday;

        Float openingBalance = closingBalanceDayBeforeYesterday + orderSumYesterday+creditYesterday-debitYesterday;
        Float closingBalance = openingBalance+orderSum+credit-debit;
        Float total = (openingBalance+orderSum+credit);
        expenses.add(0, new Expenses(expenses.size()+1L, yesterday, time,  "Opening Balance", openingBalance.toString(), "credit"));
        expenses.add(1, new Expenses(expenses.size()+1L, today, time,  "Collection", orderSum.toString(), "credit"));
        expenses.add(expenses.size(), new Expenses(expenses.size()+1L, today, time,  "Closing Balance", closingBalance.toString(), "debit"));
        expenses.add(expenses.size(), new Expenses(expenses.size()+1L, today, time,  "", total.toString(), "end"));
        return expenses;
    }

    public static List<EmailRecipients> initializeEmailList(Context context){

        List<EmailRecipients> emails = new ArrayList<>();

        DaoSession daoSession = ((App) context).getDaoSession();
        QueryBuilder queryBuilder = daoSession.getEmailRecipientsDao().queryBuilder();
        emails = queryBuilder.list();

        return emails;
    }

    public static List<Orders> initializeOrderList(Context context, String date){
        DaoSession daoSession = ((App) context).getDaoSession();
        QueryBuilder queryBuilder = daoSession.getOrdersDao().queryBuilder()
                .where(OrdersDao.Properties.Date.eq(date)).orderDesc(OrdersDao.Properties.Id);
        List<Orders> orderHistory = queryBuilder.list();
        return orderHistory;
    }

    public static List<PendingOrders> initializePendingOrderList(Context context, String date){
        DaoSession daoSession = ((App) context).getDaoSession();
        QueryBuilder queryBuilder = daoSession.getPendingOrdersDao().queryBuilder()
                .where(PendingOrdersDao.Properties.Date.eq(date)).orderDesc(PendingOrdersDao.Properties.Id);
        List<PendingOrders> pendingOrders = queryBuilder.list();
        return pendingOrders;
    }

    public static PendingOrders getPendingOrderById(Context context, Long id){
        DaoSession daoSession = ((App) context).getDaoSession();
        QueryBuilder queryBuilder = daoSession.getPendingOrdersDao().queryBuilder()
                .where(PendingOrdersDao.Properties.Id.eq(id));
        List<PendingOrders> pendingOrders = queryBuilder.list();
        return pendingOrders.get(0);
    }

    public static Orders initializeOrderDetails(Context context, Long orderId){
        DaoSession daoSession = ((App) context).getDaoSession();
        QueryBuilder queryBuilder = daoSession.getOrdersDao().queryBuilder()
                .where(OrdersDao.Properties.Id.eq(orderId));
        List<Orders> order = queryBuilder.list();
        return order.get(0);
    }


    public static ArrayList<ArrayList<String>>  initializeMonthlyReportsData(Context context, String month) {

        ArrayList<ArrayList<String>> orders = new ArrayList<ArrayList<String>>();

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        dateFormatter.setLenient(false);

        Calendar cal = Calendar.getInstance();

        try {
            cal.setTime(dateFormatter.parse(month));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        String MonthFirstDay = dateFormatter.format(cal.getTime());
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        String MonthLastDay = dateFormatter.format(cal.getTime());

        DaoSession daoSession = ((App) context).getDaoSession();


        Cursor cursor = daoSession.getDatabase().rawQuery("Select date, sum(amount-discount), sum(amount), sum(discount) from orders where date>=\'" +
                MonthFirstDay + "\' and date<=\'" + MonthLastDay + "\' group by date", new String[]{});

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            ArrayList<String> order = new ArrayList<String>();
            order.add(cursor.getString(0));
            order.add(cursor.getString(1));
            order.add(cursor.getString(2));
            order.add(cursor.getString(3));//add the item
            orders.add(order);
            cursor.moveToNext();
        }

        return orders;
    }





    public static File generateDailyReportExcelSheet(Context context, String today){
        File file;
        List<Expenses>  expenses = new ArrayList<>();
        ArrayList<ArrayList<String>> packages = new ArrayList<ArrayList<String>>();
        List<Orders> orderHistory;

        expenses = initializeBalanceData(context.getApplicationContext(), today);
        packages = initializeReportByCategoryData(context.getApplicationContext(), today);
        orderHistory = initializeOrderList(context.getApplicationContext(), today);
        ExcelWriter test = new ExcelWriter();
        String Fnamexls = "daily_report-" + today + ".xls";

        File sdCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        File directory = new File(sdCard.getAbsolutePath() + "/WashingtonDC");
        directory.mkdirs();

        file = new File(directory, Fnamexls);
        test.setOutputFile(file);

        try {
            test.write();
        } catch (WriteException w) {

        } catch (IOException io) {
        }
        WorkbookSettings wbSettings;
        wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "EN"));

        WritableWorkbook workbook;
        try {
            workbook = Workbook.createWorkbook(file, wbSettings);

            WritableSheet sheet_1 = workbook.createSheet("Balance Sheet", 0);
            WritableSheet sheet_2 = workbook.createSheet("Sales By Category", 1);
            WritableSheet sheet_3 = workbook.createSheet("Orders", 2);

            Label label = new Label(0, 0, "Particulars");
            Label label1 = new Label(1, 0, "Debit");
            Label label0 = new Label(2, 0, "Credit");

            try {

                //SHEET 1
                sheet_1.addCell(label);
                sheet_1.addCell(label1);
                sheet_1.addCell(label0);
                for (int j = 0; j < expenses.size(); j++) {
                    sheet_1.addCell(new Label(0, j + 1, expenses.get(j).getParticulars()));
                    if (expenses.get(j).getType() == "credit") {
                        sheet_1.addCell(new Label(1, j + 1, expenses.get(j).getAmount()));
                    } else {
                        sheet_1.addCell(new Label(2, j + 1, expenses.get(j).getAmount()));
                    }
                }

                //SHEET 2
                sheet_2.addCell(new Label(0, 0, "Sl No."));
                sheet_2.addCell(new Label(1, 0, "Package Name"));
                sheet_2.addCell(new Label(2, 0, "Sales"));
                for (int j = 0; j < packages.size(); j++) {
                    sheet_2.addCell(new Label(0, j + 1, Integer.toString(j + 1)));
                    sheet_2.addCell(new Label(1, j + 1, packages.get(j).get(0)));
                    sheet_2.addCell(new Label(2, j + 1, packages.get(j).get(1)));
                }

                // SHEET 3
                sheet_3.addCell(new Label(0, 0, "Sl No."));
                sheet_3.addCell(new Label(1, 0, "Customer Name"));
                sheet_3.addCell(new Label(2, 0, "Vehicle Name"));
                sheet_3.addCell(new Label(3, 0, "Package"));
                sheet_3.addCell(new Label(4, 0, "Package Price"));
                sheet_3.addCell(new Label(5, 0, "discount"));
                sheet_3.addCell(new Label(6, 0, "Total"));
                sheet_3.addCell(new Label(7, 0, "Time Taken"));
                for (int j = 0; j < orderHistory.size(); j++) {
                    sheet_3.addCell(new Label(0, j+1, Integer.toString(j)));
                    sheet_3.addCell(new Label(1, j+1, orderHistory.get(j).getCustomer().getName()));
                    sheet_3.addCell(new Label(2, j+1, orderHistory.get(j).getCustomer().getCarName()));
                    sheet_3.addCell(new Label(3, j+1, orderHistory.get(j).getPackages().getName()+"-"+ orderHistory.get(j).getCustomer().getCarType()));
                    sheet_3.addCell(new Label(4, j+1, Float.toString(orderHistory.get(j).getAmount())));
                    sheet_3.addCell(new Label(5, j+1, Float.toString(orderHistory.get(j).getDiscount())));
                    Float total = orderHistory.get(j).getAmount()- orderHistory.get(j).getDiscount();
                    sheet_3.addCell(new Label(6, j+1, Float.toString(total)));

                    String dateStart = orderHistory.get(j).getStartTime();
                    String dateStop = orderHistory.get(j).getEndTime();

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
                        sheet_3.addCell(new Label(7, j+1, diffHours+"h: "+diffMinutes+"m: "+diffSeconds+"s"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (RowsExceededException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }
            workbook.write();
            try {
                workbook.close();
            } catch (WriteException e) {

                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    public static File generateMonthlyReportExcelSheet(Context context, String today){
        File file;

        ArrayList<ArrayList<String>> revenue = new ArrayList<ArrayList<String>>();
        //ArrayList<ArrayList<ArrayList<String>>> packages = new ArrayList<>();

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        dateFormatter.setLenient(false);
        Calendar cal = Calendar.getInstance();
        Calendar calLast = Calendar.getInstance();

        try {
            cal.setTime(dateFormatter.parse(today));
            calLast.setTime(dateFormatter.parse(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DAY_OF_MONTH)); //set to first day of month
        calLast.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        String MonthFirstDay = dateFormatter.format(cal.getTime());
        String MonthLastDay = dateFormatter.format(calLast.getTime());

        DaoSession daoSession = ((App) context).getDaoSession();
        QueryBuilder queryBuilder = daoSession.getPackagesDao().queryBuilder();
        List<Packages> packages = queryBuilder.list();

        revenue = initializeMonthlyReportsData(context.getApplicationContext(), today);


        ExcelWriter test = new ExcelWriter();
        String Fnamexls = "monthly_report-" + today + ".xls";

        File sdCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        File directory = new File(sdCard.getAbsolutePath() + "/WashingtonDC");
        directory.mkdirs();

        file = new File(directory, Fnamexls);
        test.setOutputFile(file);

        try {
            test.write();
        } catch (WriteException w) {

        } catch (IOException io) {
        }

        WorkbookSettings wbSettings;
        wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "EN"));

        WritableWorkbook workbook;
        try {
            workbook = Workbook.createWorkbook(file, wbSettings);

            WritableSheet sheet_1 = workbook.createSheet("Daily Report", 0);
            WritableSheet sheet_2 = workbook.createSheet("Detailed Report", 1);
            Cursor cursor;

            try {

                //SHEET 1
                sheet_1.addCell(new Label(0, 0, "Date"));
                sheet_1.addCell(new Label(1, 0, "Amount"));
                sheet_1.addCell(new Label(2, 0, "Discount"));
                sheet_1.addCell(new Label(3, 0, "Collection"));
                sheet_1.addCell(new Label(4, 0, "Debit"));
                sheet_1.addCell(new Label(5, 0, "Credit"));

                for (int j = 0; j < revenue.size(); j++) {

                    sheet_1.addCell(new Label(0, j + 1, revenue.get(j).get(0)));
                    sheet_1.addCell(new Label(1, j + 1, revenue.get(j).get(2) == null ? "0" : revenue.get(j).get(2)));
                    sheet_1.addCell(new Label(2, j + 1, revenue.get(j).get(3) == null ? "0" : revenue.get(j).get(3)));
                    sheet_1.addCell(new Label(3, j + 1, revenue.get(j).get(1) == null ? "0" : revenue.get(j).get(1)));
                    cursor = daoSession.getDatabase().rawQuery("SELECT SUM(amount) from Expenses where date='" + revenue.get(j).get(0) + "\' and type='credit'", new String []{});
                    cursor.moveToFirst();
                    sheet_1.addCell(new Label(4, j + 1, cursor.getString(0) == null ? "0" : cursor.getString(0)));
                    cursor = daoSession.getDatabase().rawQuery("SELECT SUM(amount) from Expenses where date='" + revenue.get(j).get(0) + "\' and type='debit'", new String []{});
                    cursor.moveToFirst();
                    sheet_1.addCell(new Label(5, j + 1, cursor.getString(0) == null ? "0" : cursor.getString(0)));


                }

                cursor = daoSession.getDatabase().rawQuery("Select sum(amount-discount), sum(amount), sum(discount) from orders where date>=\'" +
                        MonthFirstDay + "\' and date<=\'" + MonthLastDay + "\'", new String[]{});
                cursor.moveToFirst();
                sheet_1.addCell(new Label(1, revenue.size() + 2, cursor.getString(1)));
                sheet_1.addCell(new Label(2, revenue.size() + 2, cursor.getString(2)));
                sheet_1.addCell(new Label(3, revenue.size() + 2, cursor.getString(0)));

                //SHEET 2
                sheet_2.addCell(new Label(0, 0, "Date"));
                int i = 1;
                for(Packages p : packages){
                    sheet_2.addCell(new Label(i, 0, p.getName()));
                    sheet_2.addCell(new Label(i+1, 0, "Amount"));
                    i = i + 2;
                }

                int row = 0;
                int col;

                while(cal.before(calLast)) {
                    row++;
                    col = 0;

                    String date = dateFormatter.format(cal.getTime());

                    sheet_2.addCell(new Label(col++, row, date));

                    for (Packages p : packages) {
                        cursor = daoSession.getDatabase().rawQuery(
                                "SELECT count(*), sum(amount-discount) from Orders join Packages on " +
                                        "Orders.PACKAGE_ID=Packages._id where date=\'" + date + "\' and Orders.PACKAGE_ID=" + p.getId(), new String[]{});

                        cursor.moveToFirst();

                        String temp;
                        temp = cursor.getString(0);
                        sheet_2.addCell(new Label(col++, row, temp));
                        temp = cursor.getString(1);
                        sheet_2.addCell(new Label(col++, row, temp));
                    }


                    cal.add(Calendar.DATE, 1);
                }

                int n = 1;
                for(Packages p : packages){
                    cursor = daoSession.getDatabase().rawQuery(
                            "SELECT count(*), sum(amount-discount) from Orders join Packages on " +
                                    "Orders.PACKAGE_ID=Packages._id where date>=\'" +
                                    MonthFirstDay + "\' and date<=\'" + MonthLastDay + "\' and Orders.PACKAGE_ID="+p.getId(), new String[]{});

                    cursor.moveToFirst();
                    sheet_2.addCell(new Label(n, row+1, cursor.getString(0)));
                    sheet_2.addCell(new Label(n+1, row+1, cursor.getString(1)));
                    n = n + 2;
                }

            } catch (RowsExceededException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }
            workbook.write();
            try {
                workbook.close();
            } catch (WriteException e) {

                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
