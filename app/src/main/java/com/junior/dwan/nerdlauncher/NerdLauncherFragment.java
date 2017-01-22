package com.junior.dwan.nerdlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Might on 15.11.2016.
 */

public class NerdLauncherFragment extends ListFragment {
    public static final String TAG="NerdLauncherFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent startUpIntent=new Intent(Intent.ACTION_MAIN);
        startUpIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        final PackageManager pm=getActivity().getPackageManager();
        List<ResolveInfo> activities =pm.queryIntentActivities(startUpIntent,0);

        Log.i(TAG, "I've found " + activities.size() + " activities.");

        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo a, ResolveInfo b) {
                PackageManager pm=getActivity().getPackageManager();

                return String.CASE_INSENSITIVE_ORDER.compare(a.loadLabel(pm).toString(),b.loadLabel(pm).toString());
            }
        });
        ArrayAdapter<ResolveInfo> adapter=new ArrayAdapter<ResolveInfo>(getActivity(),
                android.R.layout.simple_list_item_1,activities){
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v=super.getView(position,convertView,parent);
                // В документации сказано, что simple_list_item_1
                // является TextView; преобразуем для задания текстового значения.
                TextView textView=(TextView)v;
                ResolveInfo ri=getItem(position);
                textView.setText(ri.loadLabel(pm));

                return v;
            }
        };
        setListAdapter(adapter);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ResolveInfo resolveInfo=(ResolveInfo)l.getAdapter().getItem(position);
        ActivityInfo activityInfo=resolveInfo.activityInfo;

        if(activityInfo ==null){
            return;
        }

        Intent i=new Intent(Intent.ACTION_MAIN);
        i.setClassName(activityInfo.applicationInfo.packageName,activityInfo.name);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}
