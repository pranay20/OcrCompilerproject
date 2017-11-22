package com.ocrcomp.myapp.ocrcompiler;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,ShowImage.OnShowImageListener,ShowText.OnShowTextListener {

    //Fragment objects
    ShowImage showImage;
    ShowText showText;
    FinalFragment finalFragment;
//Objects
    static TessBaseAPI mTess;
    ClipboardManager clipboard;
    //UI objects



//variables
    String Ocrresult="";
    String datapath = "";
    String language = "eng";
    String finalcode="";
    boolean f=false;

    int REQUEST_TAKE_PHOTO = 1;
    public static final int REQUEST_GALLERY = 2;


    String pasteData = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        datapath = getFilesDir()+ "/tesseract/";
        Toast.makeText(MainActivity.this,datapath,Toast.LENGTH_LONG).show();
        mTess = new TessBaseAPI();
        checkFile(new File(datapath + "tessdata/"));
        mTess.init(datapath, language);


        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);


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
            Toast.makeText(MainActivity.this, "Camera", Toast.LENGTH_SHORT).show();
            showImage=ShowImage.newInstance(REQUEST_TAKE_PHOTO);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main,showImage).commit();

        } else if (id == R.id.nav_gallery) {

            Toast.makeText(MainActivity.this, "Gallery", Toast.LENGTH_SHORT).show();
            showImage=ShowImage.newInstance(REQUEST_GALLERY);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main,showImage).commit();

        } else if (id == R.id.nav_importtext) {

            Toast.makeText(MainActivity.this, "Import Text", Toast.LENGTH_SHORT).show();
            ClipData.Item citem = clipboard.getPrimaryClip().getItemAt(0);

// Gets the clipboard as text.
            pasteData =""+ citem.getText();
            showText=ShowText.newInstance(pasteData);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main,showText).commit();


        } else if (id == R.id.nav_help) {
            Toast.makeText(MainActivity.this, "Help", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_share) {
            Toast.makeText(MainActivity.this, "Share", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_send) {
            Toast.makeText(MainActivity.this, "Send", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //required methods...

    private void checkFile(File dir) {
        if (!dir.exists()&& dir.mkdirs()){
            copyFiles();
        }
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);

            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }

    private void copyFiles() {
        try {
            String filepath = datapath + "/tessdata/eng.traineddata";
            AssetManager assetManager = getAssets();

            InputStream instream = null;
            try {
                instream = assetManager.open("tessdata/eng.traineddata");
            } catch (IOException e) {
                e.printStackTrace();
            }
            OutputStream outstream = new FileOutputStream(filepath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }



            outstream.flush();
            outstream.close();
            instream.close();

            File file = new File(filepath);
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //...



    //Fragment Listeners
    @Override
    public void onShowImageInteraction(String data) {
        Ocrresult=data;
        Toast.makeText(this, Ocrresult, Toast.LENGTH_SHORT).show();
        getSupportFragmentManager().beginTransaction().remove(showImage).commit();
        showText=ShowText.newInstance(Ocrresult);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main,showText).commit();


    }



    @Override
    public void onShowTextInteraction(String data) {
        getSupportFragmentManager().beginTransaction().remove(showText).commit();
        finalcode=data;
        finalFragment=FinalFragment.newInstance(finalcode);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main,finalFragment).commit();

    }
    ////



}
