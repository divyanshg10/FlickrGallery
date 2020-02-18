package com.dcodestar.flickrbrowser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GetFlickrJsonData.OnDataAvailable, RecyclerViewClickListener.OnItemClick {
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private FlickrRecyclerViewAdapter flickrRecyclerViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView=findViewById(R.id.recylcer_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewClickListener recyclerViewClickListener=new RecyclerViewClickListener(this,this,recyclerView);
        flickrRecyclerViewAdapter=new FlickrRecyclerViewAdapter(new ArrayList<Photo>(),this);
        recyclerView.setAdapter(flickrRecyclerViewAdapter);
        recyclerView.addOnItemTouchListener(recyclerViewClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String query=sharedPreferences.getString("query","");
        Log.d(TAG, "onResume: "+query);
        GetFlickrJsonData getFlickrJsonData=new GetFlickrJsonData("https://www.flickr.com/services/feeds/photos_public.gne","en-us",true,this);
//        getFlickrJsonData.executeOnSameThread("android, noughat");
        getFlickrJsonData.execute(query);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id==R.id.activity_search){
            Intent intent=new Intent(this,SearchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDataAvailable(List<Photo> data, DownloadStatus status) {
        if(status==DownloadStatus.OK){
           flickrRecyclerViewAdapter.loadNewData(data);
        }else{
            Log.e(TAG, "onDataAvailable: failed with status"+status);
        }
    }

    @Override
    public void onSingleClick(View v, int position) {
//        Toast.makeText(this,"photo single tapped",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLongClick(View v, int position) {
//        Toast.makeText(this,"photo long tapped",Toast.LENGTH_SHORT).show();
        Photo photo=flickrRecyclerViewAdapter.getPhoto(position);
        if(photo!=null) {
            Intent i = new Intent(this, PhotoDetailActivity.class);
            i.putExtra("photo", photo);
            startActivity(i);
        }
    }
}
