package unibz.spring;

/**
 * Created by jetzt on 1/26/16.
 */
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import unibz.spring.model.DatabaseHandler;
import unibz.spring.model.Msg;

public class MainFragment extends Fragment {

    private TextView mTextView;
    private RelativeLayout view=null;


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


    public MainFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view =  (RelativeLayout)inflater.inflate(R.layout.fragment_main,
                container, false);
        TextView morseMsg = (TextView) view.findViewById(R.id.morseMsg);
        System.out.println("FRAGMENT: inside 2 "+morseMsg.getText());


        Button sendFlashBotton=(Button)view.findViewById(R.id.sendFlash);
        Button sendSoundBotton=(Button)view.findViewById(R.id.sendSound);

        System.out.println("FRAGMENT: inside 3 "+morseMsg.getText());
        setTextMessage();


        //send flash morse msg
        sendFlashBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setTextMessage();
                //send flash button pressed
                boolean hasFlash = getActivity().getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
                System.out.println("FRAGMENT: FLASH SUPPORTED ");

                if (hasFlash) {
//                    db.addMsg(new Msg(originalText, translatedText, "flash"));


                    String[] symbols = translatedText.split("");
                    for (int i = 0; i < symbols.length; i++) {
                        if (symbols[i].equals(".")) {
                            try {
                                flashTurnOn();
                                Thread.sleep(200);
                                flashTurnOff();
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        } else if (symbols[i].equals("-")) {
                            try {
                                flashTurnOn();
                                Thread.sleep(450);
                                flashTurnOff();
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                }

            }
        });

        //send sound morse msg
        sendSoundBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextMessage();
                System.out.println("SOUND: about to play");
          //      db.addMsg(new Msg(originalText, translatedText, "sound"));

                playlist = logic.constructSoundMessage(translatedText);
                i = 0;
                mp = MediaPlayer.create(getActivity(), playlist.get(i));
                mp.start();
                timer = new Timer();
                if (playlist.size() > 1) {
                    playNext();
                } else {
                    System.out.println("OUTPUT: finished playing");

                }


            }
        });

        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);



    }


    public void setTextMessage()
    {
        EditText txtname = (EditText)view.findViewById(R.id.morseMsg);
        String textToSend      =  txtname.getText().toString();
        System.out.println("FRAGMENT: textToSend "+textToSend);
        originalText =textToSend;

        //text to translate
        // String textToSend = "SOS";
        TextView translated = (TextView) view.findViewById(R.id.translated);
        TextView reverted = (TextView) view.findViewById(R.id.reverted);
        //TextView text = (TextView) findViewById(R.id.text);


        // text.setText("Example text to send:"+textToSend);
        System.out.println("FRAGMENT: textToSend2 " + textToSend);

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
                mp = MediaPlayer.create(getActivity(), playlist.get(++i));
                mp.start();
                if (playlist.size() > i + 1) {
                    playNext();
                }
            }
        }, mp.getDuration());
    }

    @Override
    public void onDestroy() {
//        if (mp.isPlaying())
//            mp.stop();
//        timer.cancel();
        super.onDestroy();
    }

    public String getTextFromMorseMsg(){
        String result ="not setted";
        if(view != null) {
            TextView morseMsg = (TextView) view.findViewById(R.id.morseMsg);
            result= ""+morseMsg.getText();
        }
        else{

        }
        return result;

    }

    public View getView(){
        return view;
    }


}
