package com.nist.washintondc;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import static com.nist.washintondc.InitializeDataForConsumption.getPendingOrderById;

public class PendingOrderListViewAdapter extends BaseAdapter {

    Context c;
    List<PendingOrders> pendingOrders;
    Button startButton, finishButton;

    TextView pendingOrderListNo, pendingOrderListCustomerName, pendingOrderListCustomerCarName, pendingOrderListPackageName;

    private ThreadHandler myHandlerThread;

    NotificationManager notificationManager;
    NotificationCompat.Builder notificationBuilder;

    public PendingOrderListViewAdapter(Context c, List<PendingOrders> pendingOrders){
        this.c=  c;
        this.pendingOrders = pendingOrders;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return pendingOrders.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return pendingOrders.get(position);
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
            view = LayoutInflater.from(c).inflate(R.layout.pending_order_row, viewGroup, false);
        }

        pendingOrderListNo = (TextView) view.findViewById(R.id.pendingOrderListNo);
        pendingOrderListCustomerName =(TextView) view.findViewById(R.id.pendingOrderListCustomerName);
        pendingOrderListCustomerCarName=(TextView) view.findViewById(R.id.pendingOrderListCustomerCarName);
        pendingOrderListPackageName = (TextView) view.findViewById(R.id.pendingOrderListPackageName);

        startButton = (Button) view.findViewById(R.id.startTask);
        finishButton = (Button) view.findViewById(R.id.finishTask);

        pendingOrderListCustomerName.setText(pendingOrders.get(i).getCustomer().getName().toString());


        pendingOrderListNo.setText(String.valueOf(i+1));
        pendingOrderListCustomerCarName.setText(pendingOrders.get(i).getCustomer().getCarName().toString());
        pendingOrderListPackageName.setText(pendingOrders.get(i).getPackages().getName().toString()+"-"+ pendingOrders.get(i).getCustomer().getCarType());

        if(i%2 == 0){ view.setBackgroundColor(Color.parseColor("#f7f7f7")); }


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.v("tagg",v.getTag().toString());

                v.setEnabled(false);
                Button finishButton = (Button)v.getRootView().findViewWithTag("finishButton_"+v.getTag().toString().split("_")[1]);
                finishButton.setEnabled(true);
                final PendingOrders pendingOrder = getPendingOrderById(v.getContext().getApplicationContext(),
                        Long.parseLong(v.getTag().toString().split("_")[1]));

                DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                DateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
                dateFormatter.setLenient(false);
                Date date = new Date();
                final String dateTime = dateFormatter.format(date);
                String startTime = timeFormatter.format(date);
                SharedPreferences timerNotifications = v.getContext().getApplicationContext().getSharedPreferences("timerNotifications", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = timerNotifications.edit();
                editor.putString("pending_order_id_"+pendingOrder.getId(), dateTime);
                editor.apply();


                myHandlerThread = new ThreadHandler("TimerThread");


                Intent intent= new Intent(v.getContext().getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("pending_order_id",pendingOrder.getId().toString());
                intent.putExtra("fragment_to_load","BillDetails");
                pendingOrder.setStartTime(startTime);
                DaoSession daoSession = ((App) v.getContext().getApplicationContext()).getDaoSession();
                daoSession.update(pendingOrder);

                int requestCode = (int) System.currentTimeMillis();
                requestCode = requestCode + pendingOrder.getId().intValue();
                intent.setAction(Integer.toString(requestCode));
                PendingIntent resultPendingIntent = PendingIntent.getActivity(v.getContext().getApplicationContext(), requestCode, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                notificationManager = (NotificationManager) v.getContext().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                String NOTIFICATION_CHANNEL_ID = "my_channel_id_"+pendingOrder.getId();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);

                    // Configure the notification channel.
                    notificationChannel.setDescription("Timer for order");
                    notificationManager.createNotificationChannel(notificationChannel);
                }


                notificationBuilder = new NotificationCompat.Builder(v.getContext().getApplicationContext(), NOTIFICATION_CHANNEL_ID);

                notificationBuilder.setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setContentTitle(pendingOrder.getCustomer().getName()+"-"+pendingOrder.getCustomer().getCarName())
                        .setContentText("Timer - ")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setTicker("WashingtonDC")
                        //   .addAction(R.drawable.ic_launcher_background, "Finish", resultPendingIntent)
                        .setOnlyAlertOnce(true)
                        .setOngoing(true);

                final Runnable myRunnable = new Runnable() {

                    @Override
                    public void run() {
                        while(true) {
                            Log.v("CountDownTimer", " "+i);
                            SharedPreferences timerNotifications = v.getContext().getApplicationContext().getSharedPreferences("timerNotifications", Context.MODE_PRIVATE);

                            if(timerNotifications.contains("pending_order_id_"+pendingOrder.getId())) {

                                DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                dateFormatter.setLenient(false);
                                Date date = new Date();
                                String dateTime = dateFormatter.format(date);
                                String startTime = timerNotifications.getString("pending_order_id_"+pendingOrder.getId(),dateTime);
                                Date startDate = new Date();;
                                try {
                                    startDate = dateFormatter.parse(startTime);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                long diff = date.getTime() - startDate.getTime();
                                long packageTime = pendingOrder.getPackages().getTime();

                                long seconds = diff / 1000;
                                long minutes = seconds / 60;
                                long hours = minutes / 60;
                                long days = hours / 24;

                                Intent intent= new Intent(v.getContext().getApplicationContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("pending_order_id",pendingOrder.getId().toString());
                                intent.putExtra("fragment_to_load","BillDetails");
                                int requestCode = (int) System.currentTimeMillis();
                                requestCode = requestCode + pendingOrder.getId().intValue();
                                intent.setAction(Integer.toString(requestCode));
                                PendingIntent resultPendingIntent = PendingIntent.getActivity(v.getContext().getApplicationContext(), requestCode, intent,
                                        PendingIntent.FLAG_UPDATE_CURRENT);
                                notificationBuilder.mActions.clear();
                                notificationBuilder.setContentTitle(pendingOrder.getCustomer().getName()+"-"+pendingOrder.getCustomer().getCarName());
                                if (seconds >= packageTime && seconds <= packageTime+1) {
                                    notificationBuilder.setOnlyAlertOnce(false);
                                    notificationBuilder.setContentText("Alloted time Over."+ hours%24 + ":" + minutes%60 + ":" + seconds%60);
                                    notificationManager.notify(/*notification id*/pendingOrder.getId().intValue(), notificationBuilder.build());
                                    notificationBuilder.setOnlyAlertOnce(true);
                                }
                                else if (seconds >= packageTime){
                                    notificationBuilder.setContentText("Alloted time Over."+ hours%24 + ":" + minutes%60 + ":" + seconds%60);
                                }
                                else{
                                    notificationBuilder.setContentText("Timer - " + hours%24 + ":" + minutes%60 + ":" + seconds%60);
                                }
                                if(timerNotifications.contains("submit_pending_order_id_"+pendingOrder.getId())) {
                                    notificationBuilder.mActions.clear();
                                }
                                else{
                                    notificationBuilder.addAction(R.drawable.ic_launcher_background, "Finish", resultPendingIntent);
                                }
                                notificationManager.notify(/*notification id*/pendingOrder.getId().intValue(), notificationBuilder.build());
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }
                            else{
                                myHandlerThread.interrupt();
                                myHandlerThread.quit();
                                myHandlerThread.getId();
                                notificationManager.cancel(pendingOrder.getId().intValue());
                                return;
                            }
                        }

                    }
                };

                myHandlerThread.start();
                myHandlerThread.prepareHandler();
                myHandlerThread.postTask(myRunnable);


            }
        });
        startButton.setTag(new String("startButton_"+pendingOrders.get(i).getId()));

        SharedPreferences timerNotifications = view.getContext().getApplicationContext().getSharedPreferences("timerNotifications", Context.MODE_PRIVATE);

        if(timerNotifications.contains("pending_order_id_"+pendingOrders.get(i).getId())) {
            startButton.setEnabled(false);
            finishButton.setEnabled(true);
        }
        else{
            startButton.setEnabled(true);
            finishButton.setEnabled(false);
        }

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                final PendingOrders pendingOrder = getPendingOrderById(v.getContext().getApplicationContext(),
                        Long.parseLong(v.getTag().toString().split("_")[1]));

                Intent intent= new Intent(v.getContext().getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("pending_order_id",pendingOrder.getId().toString());
                intent.putExtra("fragment_to_load","BillDetails");
                intent.putExtra("origin","OrderList");
                v.getContext().getApplicationContext().startActivity(intent);
                SharedPreferences timerNotifications = v.getContext().getApplicationContext().getSharedPreferences("timerNotifications", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = timerNotifications.edit();
                editor.remove("submit_pending_order_id_"+pendingOrder.getId());
                editor.apply();
            }
        });
        finishButton.setTag(new String("finishButton_"+pendingOrders.get(i).getId()));
        return view;
    }

}