package com.oligon.emergency;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.oligon.emergency.model.DatabaseHandler;

public class ActivityNumbers extends SherlockListActivity {

    private static SimpleCursorAdapter mAdapter;
    private static Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_numbers);

        mAdapter = new SimpleCursorAdapter(this, R.layout.list_row, mCursor, new String[]{DatabaseHandler.KEY_TEL_TITLE, DatabaseHandler.KEY_TEL_NUMBER, DatabaseHandler.KEY_TEL_IMG}, new int[]{R.id.list_text, R.id.list_number, R.id.list_image}, 0);
        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (columnIndex == 3) {
                    ((ImageView) view.findViewById(R.id.list_image)).setImageDrawable(getResources().getDrawable(cursor.getInt(3)));
                    return true;
                }
                return false;
            }
        });
        setListAdapter(mAdapter);
        updateContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_main_reduced, menu);
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
        Toast.makeText(this, R.string.confirm, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        mCursor.moveToPosition(position);
        intent.setData(Uri.parse("tel:" + mCursor.getString(2).trim()));
        startActivity(intent);
        super.onListItemClick(l, v, position, id);
    }

    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mAdapter.changeCursor(mCursor);
            mAdapter.notifyDataSetChanged();
        }
    };

    private void updateContent() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mCursor = ActivityMain.db.getAllNumbers();
                handler.sendEmptyMessage(0);
            }
        }).start();
    }
}
