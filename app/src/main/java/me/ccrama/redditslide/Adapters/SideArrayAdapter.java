package me.ccrama.redditslide.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.ccrama.redditslide.Activities.OverviewBase;
import me.ccrama.redditslide.Activities.SubredditView;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.SantitizeField;
import me.ccrama.redditslide.Visuals.Pallete;


/**
 * Created by ccrama on 8/17/2015.
 */
public class SideArrayAdapter extends ArrayAdapter<String> {
    private final List<String> objects;
    private Filter filter;
    private ArrayList<String> fitems;

    public SideArrayAdapter(Context context, ArrayList<String> objects) {
        super(context, 0, objects);
        this.objects = new ArrayList<>(objects);
        filter = new SubFilter();
        fitems = new ArrayList<>(objects);
    }

    @Override
    public Filter getFilter() {

        if (filter == null) {
            filter = new SubFilter();
        }
        return filter;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.subforsublist, parent, false);
        }
        final TextView t =
                ((TextView) convertView.findViewById(R.id.name));
        t.setText(fitems.get(position));

        final String subreddit = SantitizeField.sanitizeString(fitems.get(position).replace(getContext().getString(R.string.search_goto) + " ", ""));
        convertView.findViewById(R.id.color).setBackgroundResource(R.drawable.circle);
        convertView.findViewById(R.id.color).getBackground().setColorFilter(Pallete.getColor(subreddit), PorterDuff.Mode.MULTIPLY);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fitems.get(position).startsWith(getContext().getString(R.string.search_goto) + " ")) {
                    Intent inte = new Intent(getContext(), SubredditView.class);
                    inte.putExtra("subreddit", subreddit);
                    ((Activity) getContext()).startActivityForResult(inte, 4);
                } else {
                    ((OverviewBase) getContext()).pager.setCurrentItem(((OverviewBase) getContext()).usedArray.indexOf(fitems.get(position)));
                    ((OverviewBase) getContext()).drawerLayout.closeDrawers();
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        return convertView;
    }

    private class SubFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            String prefix = constraint.toString().toLowerCase();

            if (prefix == null || prefix.length() == 0) {
                ArrayList<String> list = new ArrayList<>(objects);
                results.values = list;
                results.count = list.size();
            } else {
                final ArrayList<String> list = new ArrayList<>(objects);
                final ArrayList<String> nlist = new ArrayList<>();
                int count = list.size();

                for (int i = 0; i < count; i++) {
                    final String sub = list.get(i);

                    if (sub.contains(prefix)) {
                        nlist.add(sub);
                    }
                    results.values = nlist;
                    results.count = nlist.size();
                }
                nlist.add(getContext().getString(R.string.search_goto) + " " + prefix);

            }
            return results;
        }


        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            fitems = (ArrayList<String>) results.values;
            notifyDataSetChanged();
            clear();
            if (fitems != null) {
                int count = fitems.size();
                for (int i = 0; i < count; i++) {
                    add(fitems.get(i));
                    notifyDataSetInvalidated();
                }
            }
        }
    }
}
