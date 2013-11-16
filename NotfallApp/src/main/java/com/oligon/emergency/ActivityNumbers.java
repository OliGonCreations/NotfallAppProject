package com.oligon.emergency;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class ActivityNumbers extends SherlockListActivity {

    private CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_numbers);
        List<String> strings = new ArrayList<String>();
        List<String> numbers = new ArrayList<String>();
        List<Drawable> images = new ArrayList<Drawable>();
        TypedArray array = this.getResources().obtainTypedArray(R.array.numbers_images);
        String[] array1 = this.getResources().getStringArray(R.array.numbers_title);
        String[] array2 = this.getResources().getStringArray(R.array.numbers_number);
        for (int i = 0; i < array.length(); i++) {
            images.add(array.getDrawable(i));
            strings.add(array1[i]);
            numbers.add(array2[i]);
        }
        adapter = new CustomAdapter(this, strings, images, numbers);
        setListAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_about:
                ActivityMain.showAboutDialog(this);
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, ActivitySettings.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Toast.makeText(this, "Bestätigen.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + adapter.getNumber(position).trim()));
        startActivity(intent);
        super.onListItemClick(l, v, position, id);
    }


    private static class CustomAdapter extends BaseAdapter {

        private Activity activity;
        private static LayoutInflater inflater = null;
        private List<String> strings, numbers;
        private List<Drawable> images;

        public CustomAdapter(Activity a, List<String> strings, List<Drawable> images, List<String> numbers) {
            activity = a;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.strings = strings;
            this.numbers = numbers;
            this.images = images;
        }

        public int getCount() {
            return strings.size();
        }

        public Object getItem(int position) {
            return strings.get(position);
        }

        public String getNumber(int position) {
            return numbers.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (convertView == null)
                view = inflater.inflate(R.layout.list_row, null);

            TextView text = (TextView) view.findViewById(R.id.list_text);
            TextView number = (TextView) view.findViewById(R.id.list_number);
            ImageView image = (ImageView) view.findViewById(R.id.list_image);
            text.setText(strings.get(position));
            number.setText(numbers.get(position));
            image.setImageDrawable(images.get(position));
            return view;
        }
    }
}
