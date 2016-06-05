package com.mahindracomviva.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mahindracomviva.adapters.AudioListAdapter;
import com.mahindracomviva.adapters.ImagesListAdapter;
import com.mahindracomviva.adapters.VideosListAdapter;
import com.mahindracomviva.exploreit.R;
import com.mahindracomviva.utilities.GeneralOperationsNew;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView1, recyclerView2, recyclerView3;
    private VideosListAdapter videosListAdapter;
    private ImagesListAdapter imagesListAdapter;
    private AudioListAdapter audioListAdapter;
    GeneralOperationsNew generalOperationsNew;
    LinearLayout ll_layout1;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView1 = (RecyclerView) findViewById(R.id.recycler_view1);
        recyclerView2 = (RecyclerView) findViewById(R.id.recycler_view2);
        recyclerView3 = (RecyclerView) findViewById(R.id.recycler_view3);
        ll_layout1 = (LinearLayout) findViewById(R.id.ll_layout1);

        generalOperationsNew = new GeneralOperationsNew();

        try {

            if(generalOperationsNew.isInternetAvailable(this)) {
                videosListAdapter = new VideosListAdapter(this, generalOperationsNew);
                recyclerView1.setAdapter(videosListAdapter);
                recyclerView1.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager_Videos = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                recyclerView1.setLayoutManager(linearLayoutManager_Videos);
                recyclerView1.setItemAnimator(new DefaultItemAnimator());

                imagesListAdapter = new ImagesListAdapter(this, generalOperationsNew);
                recyclerView2.setAdapter(imagesListAdapter);
                recyclerView2.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager_Images = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                recyclerView2.setLayoutManager(linearLayoutManager_Images);
                recyclerView2.setItemAnimator(new DefaultItemAnimator());

                audioListAdapter = new AudioListAdapter(this, generalOperationsNew);
                recyclerView3.setAdapter(audioListAdapter);
                recyclerView3.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager_Audios = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                recyclerView3.setLayoutManager(linearLayoutManager_Audios);
                recyclerView3.setItemAnimator(new DefaultItemAnimator());
            }else{
                ll_layout1.setVisibility(View.GONE);
                Toast.makeText(this, "Internet is not available", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        askForPermissions();

    }

    private void askForPermissions() {

        int hasWriteStoragePermission = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasWriteStoragePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showMessageOKCancel("You need to allow access to write storage for saving the downloads",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void refreshActivity(){
        Intent refreshIntent = new Intent(this, MainActivity.class);
        startActivity(refreshIntent);
        finish();
    }
}
