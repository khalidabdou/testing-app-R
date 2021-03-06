package com.example.clientimadradio;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Tab1.OnFragmentInteractionListener, Tab2.OnFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener {
    public final static String stream = "http://rtstream.tanitweb.com/nationale"; //Todo :station  par defaut
    public static TextView txt_play;
    public static Context context;
    public static InterstitialAd interstitialAd;
    public static com.wang.avi.AVLoadingIndicatorView avi;
    public static int adsShow = 0;
    static ImageView play;
    static ImageView icon_play;
    static MediaPlayer mediaPlayer;
    static boolean started = false;
    static boolean prepared = false;
    static PlayTask PlayTask;
    db_manager dbase;
    ConsentSDK consentSDK;
    boolean search = false;
    private AdView mAdView;
    //volume
    private SeekBar volumeSeekbar = null;
    private AudioManager audioManager = null;

    //new player
    public static void play(String url, int img, String name) {
        prepared = false;
        play.setEnabled(false);
        avi.show();
        mediaPlayer.stop();
        PlayTask.cancel(true);
        mediaPlayer = new MediaPlayer();
        PlayTask = new PlayTask();
        txt_play.setText(name);
        PlayTask.execute(url);
        showadsMethod();
    }

    public static AdRequest showads(Context context) {
        AdRequest request;
        if (ConsentInformation.getInstance(context).isRequestLocationInEeaOrUnknown()) {
            if (ConsentInformation.getInstance(context).getConsentStatus() == ConsentStatus.PERSONALIZED) {
                request = new AdRequest.Builder().build();
            } else {
                Bundle extras = new Bundle();
                extras.putString("npa", "1");
                request = new AdRequest.Builder()
                        .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                        .build();
            }
        } else {
            request = new AdRequest.Builder().build();
        }
        return request;
    }

    //counter show ads
    public static void showadsMethod() {
        if (!interstitialAd.isLoaded()) {
            interstitialAd.loadAd(showads(context));
        }
        adsShow++;
        //Toast.makeText(context, String.valueOf(adsShow), Toast.LENGTH_SHORT).show();

        if (adsShow == 5 && interstitialAd.isLoaded()) {
            interstitialAd.show();


            adsShow = 10;
        } else if (adsShow % 10 == 0 && interstitialAd.isLoaded()) {
            //Toast.makeText(context, "show", Toast.LENGTH_SHORT).show();
            interstitialAd.show();


        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        context = this;
        avi = findViewById(R.id.aviMain);
        consentSDK = new ConsentSDK.Builder(this)
                .addPrivacyPolicy("http://www.mediafire.com/file/dmx2x432cm8m1lm/pry.txt/file") // Add your privacy policy url
                .addPublisherId("pub-4657176966074920") // Add your admob publisher id
                .build();
        consentSDK.checkConsent(new ConsentSDK.ConsentCallback() {
            @Override
            public void onResult(boolean isRequestLocationInEeaOrUnknown) {

            }
        });


        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        interstitialAd.loadAd(showads(this));


        //================
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(context, R.color.color1));
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


        //Toast.makeText(context, String.valueOf(Tab1.filterlist.size()), Toast.LENGTH_SHORT).show();

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


//// play par defaut
        ArrayList<class_itm> defalt=dbase.getsations();
        PlayTask.execute(defalt.get(0).url);
        ImageView imgp=findViewById(R.id.icon_play);
        imgp.setImageDrawable(getResources().getDrawable(Tab1.geticon(defalt.get(0).ids)));
        TextView txtplay=findViewById(R.id.txt_payed);
        txtplay.setText(defalt.get(0).name_station);


    }

    @Override
    public void onFragmentInteraction(Uri uri) {
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
                    showadsMethod();
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
            //todo : icon privacy
        if (id == R.id.Privacy) {
            // Handle the camera action

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com")); // todo : change it
            startActivity(browserIntent);
            //todo : icon gdpr
        } else if (id == R.id.GDPR) {
// To request the consent form to re-edit it for the users within EEA
            if (consentSDK.isUserLocationWithinEea(context)) {
                consentSDK = new ConsentSDK.Builder(context)
                        .addPrivacyPolicy("http://www.mediafire.com/file/dmx2x432cm8m1lm/pry.txt/file") // Add your privacy policy url
                        .addPublisherId("pub-4657176966074920") // Add your admob publisher id
                        .build();
                consentSDK.checkConsent(new ConsentSDK.ConsentCallback() {
                    @Override
                    public void onResult(boolean isRequestLocationInEeaOrUnknown) {
                        // Toast.makeText(context, String.valueOf(isRequestLocationInEeaOrUnknown), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
            //todo : icon more
        } else if (id == R.id.more_app) {
            openWebPage("http://play.google.com/store/apps/details?id=" + getPackageName());

                //todo : icon rate
        } else if (id == R.id.rate_app) {
            openWebPage("http://play.google.com/store/apps/details?id=" + getPackageName());

            //todo : icon share
        } else if (id == R.id.nav_share) {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                String shareMessage = "\nLet me recommend you this application\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch (Exception e) {
                //e.toString();
            }
            //todo : icon exit
        } else if (id == R.id.exit) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Confirm");
            builder.setMessage("Do you want to quit the application ?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing but close the dialog
                    mediaPlayer.stop();
                    PlayTask.cancel(true);
                    ActivityCompat.finishAffinity(MainActivity.this);
                    dialog.dismiss();


                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // Do nothing
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();

            alert.show();


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
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Tab1.filter(newText);


                return true;
            }
        });

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

    public void openWebPage(String url) {
        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(myIntent);
    }

    //play task
    public static class PlayTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String... strings) {

            try {
                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {

                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });

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
            avi.hide();


        }
    }

}
