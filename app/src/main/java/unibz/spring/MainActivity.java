package unibz.spring;


import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;



import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


import unibz.spring.model.DatabaseHandler;
import unibz.spring.model.Msg;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //The logic
    private  Controller logic = new Controller();
    private String originalText;
    private  String translatedText;
    private String revertedText;
    //Database handler
    private DatabaseHandler db;

    //morse sound playlist
    private Timer timer;
    private MediaPlayer mp;
    private ArrayList<Integer> playlist;
    private int i=0;

    //flashlight variables
    //boolean flashOn= false;
    private Camera cam ;
    private Fragment fragment ;
    private MainFragment mainFragment;

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

        //setting up main fragment
        mainFragment = new MainFragment();
        fragment = mainFragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();


//        TextView msgMorse = (TextView)mainFragment.getView().findViewById(R.id.morseMsg);
 //       System.out.println("FRAGMENT: outside " +msgMorse);



        //END NAVIGATION
        db = new DatabaseHandler(this);
        // Inserting Contacts
//        Log.d("Insert: ", "Inserting ..");
//        db.addMsg(new Msg("msg 1", "morse 1", "sound"));
//        db.addMsg(new Msg("msg 2 ", "morse 2", "flash"));
//        db.addMsg(new Msg("msg 3", "morse 3", "sound"));
//        db.addMsg(new Msg("msg 4qw", "morse 4", "flash21321"));

        // Reading all contacts
        Log.d("Reading: ", "Reading all contacts..");
        List<Msg> Msgs = db.getAllMsgs();

        for (int i = 0; i < Msgs.size(); i++) {
            System.out.println("Reading: " + Msgs.get(i).getMsg() +"-"+Msgs.get(i).getMorseMsg()+"-"+Msgs.get(i).getType());

        }


        //setting the text currently in the text field
     //   setTextMessage();



        //buttons
      //  Button sendSoundBotton=(Button)findViewById(R.id.sendSound);
       // Button sendFlashBotton=(Button)findViewById(R.id.sendFlash);


        //send flash morse msg
//        sendFlashBotton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                setTextMessage();
//                //send flash button pressed
//                boolean hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
//                System.out.println("OUTPUT: FLASH SUPPORTED ");
//                if(hasFlash)
//                {
//                    db.addMsg(new Msg(originalText, translatedText, "flash"));
//
//
//                    String[] symbols = translatedText.split("");
//                    for (int i = 0; i <symbols.length ; i++) {
//                        if (symbols[i].equals(".")) {
//                            try {
//                                flashTurnOn();
//                                Thread.sleep(200);
//                                flashTurnOff();
//                            } catch (InterruptedException e) {
//                                // TODO Auto-generated catch block
//                                e.printStackTrace();
//                            }
//                        } else if (symbols[i].equals("-")) {
//                            try {
//                                flashTurnOn();
//                                Thread.sleep(450);
//                                flashTurnOff();
//                            } catch (InterruptedException e) {
//                                // TODO Auto-generated catch block
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }
//
//            }
//        });
//
//        //send sound morse msg
//        sendSoundBotton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setTextMessage();
//                System.out.println("SOUND: about to play");
//                db.addMsg(new Msg(originalText, translatedText, "sound"));
//
//                playlist = logic.constructSoundMessage(translatedText);
//                i = 0;
//                mp = MediaPlayer.create(MainActivity.this, playlist.get(i));
//                mp.start();
//                timer = new Timer();
//                if (playlist.size() > 1) {
//                    playNext();
//                } else {
//                    System.out.println("OUTPUT: finished playing");
//
//                }
//
//
//            }
//        });
    }


    public void setTextMessage()
    {
        EditText txtname = (EditText)mainFragment.getView().findViewById(R.id.morseMsg);
        String textToSend      =  txtname.getText().toString();
        System.out.println("OUTPUT: textToSend "+textToSend);
        originalText =textToSend;

        //text to translate
        // String textToSend = "SOS";
        TextView translated = (TextView) mainFragment.getView().findViewById(R.id.translated);
        TextView reverted = (TextView) mainFragment.getView().findViewById(R.id.reverted);
        //TextView text = (TextView) findViewById(R.id.text);


       // text.setText("Example text to send:"+textToSend);
        System.out.println("OUTPUT: textToSend2 " + textToSend);

        //translating from text to morse
        translatedText = logic.codeTextToMorse(textToSend);
        translated.setText("From text to morse: " +translatedText);

        //translating from morse to text again
        revertedText = logic.decodeMorseToText(translatedText);
        reverted.setText("Translated from morse: " + revertedText);

    }
    public void flashTurnOn()
    {
        cam = Camera.open();
        Camera.Parameters p = cam.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        cam.setParameters(p);
        cam.startPreview();
    }

    public void flashTurnOff()
    {
        System.out.println("OUTPUT: FLASH OFF! ");
        cam.stopPreview();
        cam.release();

    }

    public void playNext() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mp.reset();
                mp = MediaPlayer.create(MainActivity.this, playlist.get(++i));
                mp.start();
                if (playlist.size() > i + 1) {
                    playNext();
                }
            }
        }, mp.getDuration());
    }

    @Override
    public void onDestroy() {
        if (mp.isPlaying())
            mp.stop();
        timer.cancel();
        super.onDestroy();
    }

    ///NAVIGATION
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
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        fragment = null;

        //new activity
        if (id == R.id.nav_sendMsg) {
            fragment = mainFragment;

        } else if (id == R.id.nav_history) {
            fragment = new HistoryFragment();

        } else if (id == R.id.nav_translate) {
            fragment = new TranslateFragment();

            System.out.println("NAVIGATION: NAV TRANSLATE");

        }

        if (fragment!= null)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            System.out.println("FRAGMENT: CHANGED! ");

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
