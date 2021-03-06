package com.example.randybiglow.tropical_fruits_app;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    FruitDatabaseHelper dbHelper;
    ListView listView;
    CursorAdapter cursorAdapter;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Database instantiated here to allow for a default view.
        dbHelper = FruitDatabaseHelper.getInstance(MainActivity.this);
        cursor = dbHelper.getFruitsList();
//        cursorAdapter = new CursorAdapter(MainActivity.this,cursor,0) {
//
//            @Override
//            public View newView(Context context, Cursor cursor, ViewGroup parent) {
//                return LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1,parent,false);
//            }
//
//            @Override
//            public void bindView(View view, Context context, Cursor cursor) {
//                TextView textView = (TextView) findViewById(android.R.id.text1);
//
//                textView.setText(cursor.getString(cursor.getColumnIndex(FruitDatabaseHelper.COL_COMMON_NAME)));
//            }
//        };

        //Gives the main menu a default list.
        listView = (ListView)findViewById(R.id.mainListView);

        //Allows the content on the list to be clickable.
        handleIntent(getIntent());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, Details.class);
                Cursor fruitCursor = (Cursor) parent.getAdapter().getItem(position);
                intent.putExtra("id", fruitCursor.getInt(fruitCursor.getColumnIndex(FruitDatabaseHelper.COL_ID)));
                startActivity(intent);
            }
        });

        new Handler().post(new Runnable() {

            @Override
            public void run() {
                cursorAdapter = new CustomCursorAdapter(MainActivity.this, cursor,0);

                listView.setAdapter(cursorAdapter);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options, menu);

        //Creates a SearchView at the top of the app with default search icon.
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    //Allows users to search by Name, Region, and Season of Fruits.
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Cursor mCursor = dbHelper.searchFruits(query);
            listView = (ListView)findViewById(R.id.mainListView);
            cursorAdapter.swapCursor(mCursor);
        }
//          else {
//            cursorAdapter.swapCursor(cursor);
//        }
    }
}