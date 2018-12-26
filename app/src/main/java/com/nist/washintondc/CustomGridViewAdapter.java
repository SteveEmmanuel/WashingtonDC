package com.nist.washintondc;

/**
 * Created by oldtrafford on 03/06/18.
 */
//https://github.com/tutsplus/Android-CardViewRecyclerView/blob/master/ListsAndCards/app/src/main/java/com/hathy/listsandcards/RVAdapter.java
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CustomGridViewAdapter extends BaseAdapter{

    Context c;
    List<Packages> packages;

    CustomGridViewAdapter(Context c, List<Packages> packages){
        this.c = c;
        this.packages = packages;
    }

    @Override
    public int getCount() {
        return packages.size();
    }

    @Override
    public Object getItem(int i) {
        return packages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(c).inflate(R.layout.card_layout, viewGroup, false);


            TextView packageName = (TextView) view.findViewById(R.id.package_name);
            //CardView cardView = (CardView) view.findViewById(R.id.card_view);
            ImageView image = (ImageView) view.findViewById(R.id.packageImage);

            int id = c.getResources().getIdentifier(packages.get(i).getImage(), "drawable", c.getPackageName());
            image.setImageResource(id);

            packageName.setText(packages.get(i).getName());
            //PackageInfo.setText(packages.get(i).getInfo());
            View root = view.getRootView();
            if (packages.get(i).selected == true) {
                view.setBackgroundColor(Color.parseColor("#b1efe9"));
            } else {
                //view.setBackgroundColor(Color.parseColor("#E0E0EB"));
                view.setBackgroundColor(Color.TRANSPARENT);
            }
            return view;
        }
        else{
            if (packages.get(i).selected == true) {
                view.setBackgroundColor(Color.parseColor("#b1efe9"));
            } else {
                //view.setBackgroundColor(Color.parseColor("#E0E0EB"));
                view.setBackgroundColor(Color.TRANSPARENT);
            }
            return view;
        }
    }

}