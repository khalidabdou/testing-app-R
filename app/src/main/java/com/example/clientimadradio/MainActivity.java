package com.example.clientimadradio;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements Tab1.OnFragmentInteractionListener, Tab2.OnFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener {
    public final static String stream = "http://rtstream.tanitweb.com/nationale";
    public static TextView txt_play;
    static ImageView play;
    static ImageView icon_play;
    static MediaPlayer mediaPlayer;
    static boolean started = false;
    static boolean prepared = false;
    static PlayTask PlayTask;
    Context context;
    db_manager dbase;

    //volume
    private SeekBar volumeSeekbar = null;
    private AudioManager audioManager = null;

    //new player
    public static void play(String url, int img, String name) {
        play.setEnabled(false);
        mediaPlayer.stop();
        PlayTask.cancel(true);
        mediaPlayer = new MediaPlayer();
        PlayTask = new PlayTask();
        PlayTask.execute(url);
        MainActivity.txt_play.setText(name);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        context = this;

        //================
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });  */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //===================
        txt_play = findViewById(R.id.txt_payed);
        icon_play = findViewById(R.id.icon_play);

        dbase = new db_manager(context);

        initControls();

        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.addTab(tabLayout.newTab().setText("FAVORITES"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                TextView txt_empty = findViewById(R.id.txt_empty);
                //Toast.makeText(context,tab.getText(),Toast.LENGTH_LONG).show();
                if (Tab1.listF.size() == 0 && tab.getText().equals("FAVORITES")) {
                    //Toast.makeText(context,"empty",Toast.LENGTH_LONG).show();

                    txt_empty.setVisibility(View.VISIBLE);
                } else txt_empty.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        //button play
        play = findViewById(R.id.bt_play);
        play.setEnabled(false);
        // play.setText("Loading..");
        mediaPlayer = new MediaPlayer();
        PlayTask = new PlayTask();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (started) {
                    mediaPlayer.pause();
                    started = false;
                    play.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
                } else {
                    mediaPlayer.start();
                    started = true;
                    play.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_24dp));
                }
            }
        });


        PlayTask.execute(stream);


    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    public void clc_play(View view) {
    }

    @Override
    public void onBackPressed() {
        mediaPlayer.stop();
        PlayTask.cancel(true);
        ActivityCompat.finishAffinity(MainActivity.this);

        super.onBackPressed();
    }

    //controler volume
    private void initControls() {
        try {
            volumeSeekbar = (SeekBar) findViewById(R.id.seekBar);
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volumeSeekbar.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));


            volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_share) {
            Toast.makeText(context, "share", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_slideshow) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class PlayTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {

            try {

                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.prepare();
                mediaPlayer.start();
                prepared = true;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return prepared;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            play.setEnabled(true);


        }
    }
}
