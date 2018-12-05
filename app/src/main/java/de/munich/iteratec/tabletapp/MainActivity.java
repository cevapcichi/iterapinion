package de.munich.iteratec.tabletapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.ahmadrosid.svgloader.SvgLoader;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference database;
    private Question question;
    private Timer timer;

    int countAnswer1Text, countAnswer2Text, countAnswer3Text, countAnswer4Text,
            countAnswer1Image, countAnswer2Image, countAnswer3Image, countAnswer4Image,
            countRating1, countRating2, countRating3, countRating4, countRating5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("OnCreate started", "true");

        setRandomBackground();
        startEventListener();
        refreshQuestions();

    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
        timer.purge();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshQuestions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer.purge();
    }

    private void startEventListener() {

        Log.i("startEvenListener", "started");
        database = FirebaseDatabase.getInstance().getReference();

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("onDataChange", "started");

                for (DataSnapshot singleDataSnapshot:dataSnapshot.getChildren()) {
                    String key = singleDataSnapshot.getKey();
                    Log.i("Datasnapshot Key", key);

                    final long currentDate = Calendar.getInstance().getTimeInMillis();
                    long dateBegin = singleDataSnapshot.child("begin").getValue(Long.class);
                    Log.i("DateBegin", "" + dateBegin);
                    Log.i("Datasnapshot", singleDataSnapshot.toString());
                    long dateEnd = singleDataSnapshot.child("end").getValue(Long.class);
                    if ((currentDate > dateBegin) && (currentDate < dateEnd)) {
                        Log.i("current date vergleich", "started");

                        String questionType = singleDataSnapshot.child("Fragentyp").getValue(String.class);
                        if (questionType.equals("Antworten als Text") || (questionType.equals("Antworten als Icons"))) {
                            question = singleDataSnapshot.getValue(Question.class);
                        } else if (questionType.equals("Bewertung")) {
                            question = singleDataSnapshot.getValue(Question.class);
                        }
                        //Log.i("Added question", question.getQuestionType());
                        setQuestions(question);
                    } else {
                        question = null;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
            /*@Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final long currentDate = Calendar.getInstance().getTimeInMillis();


                long dateBegin = dataSnapshot.child("Von").getValue(Long.class);
                Log.i("DateBegin", "" + dateBegin);

                Log.i("Datasnapshot", dataSnapshot.toString());

                long dateEnd = dataSnapshot.child("Bis").getValue(Long.class);

                if ((currentDate > dateBegin) && (currentDate < dateEnd)) {

                    String questionType = dataSnapshot.child("Fragentyp").getValue(String.class);
                    if (questionType.equals("Antworten als Text") || (questionType.equals("Antworten als Icons"))) {
                        question = new Question(dataSnapshot.getKey().toString(),
                                dataSnapshot.child("Antwort 1").getValue(String.class),
                                dataSnapshot.child("Antwort 2").getValue(String.class),
                                dataSnapshot.child("Antwort 3").getValue(String.class),
                                dataSnapshot.child("Antwort 4").getValue(String.class),
                                dataSnapshot.child("Fragentyp").getValue(String.class));
                    } else if (questionType.equals("Bewertung")) {
                        question = new Question(dataSnapshot.getKey().toString(),
                                dataSnapshot.child("Rating 1").getValue(String.class),
                                dataSnapshot.child("Rating 2").getValue(String.class),
                                dataSnapshot.child("Rating 3").getValue(String.class),
                                dataSnapshot.child("Rating 4").getValue(String.class),
                                dataSnapshot.child("Rating 5").getValue(String.class),
                                dataSnapshot.child("Fragentyp").getValue(String.class));
                    }

                    Log.i("Added question", question.getQuestionType());
                    setQuestions(question);
                } else {
                    question = null;
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }



    private void refreshQuestions() {

        Date timerStart = new GregorianCalendar(2018 + 1900, 11, 10, 0, 1).getTime();
        int oneWeekPeriod = 604800000;

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                Log.i("Refresh Questions", "aufgerufen");
                startEventListener();
            }
        }, 60000, 60000);

    }

    private void setQuestions(Question addedQuestion) {
        Log.i("Set Questions", "aufgerufen");

        final Question question = addedQuestion;
        //question being displayed
        TextView questionView = (TextView) findViewById(R.id.question);
        questionView.setText(question.getQuestion());

        //Standard output when no questions left
        TextView noQuestions = (TextView) findViewById(R.id.noQuestions);

        //answers of Text layout
        TextView answer1Text = (TextView) findViewById(R.id.answer1Text);
        TextView answer2Text = (TextView) findViewById(R.id.answer2Text);
        TextView answer3Text = (TextView) findViewById(R.id.answer3Text);
        TextView answer4Text = (TextView) findViewById(R.id.answer4Text);

        //answers of Icon layout
        ImageView answer1Image = (ImageView) findViewById(R.id.answer1Image);
        ImageView answer2Image = (ImageView) findViewById(R.id.answer2Image);
        ImageView answer3Image = (ImageView) findViewById(R.id.answer3Image);
        ImageView answer4Image = (ImageView) findViewById(R.id.answer4Image);

        //SmileyRating of Rating layout
        SmileRating rating = (SmileRating) findViewById(R.id.smileyRating);

        ViewFlipper viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);

        switch (question.getQuestionType()) {
            case "Antworten als Text":
                viewFlipper.setDisplayedChild(0);
                answer1Text.setText(question.getAnswer1());
                answer2Text.setText(question.getAnswer2());
                answer3Text.setText(question.getAnswer3());
                answer4Text.setText(question.getAnswer4());

                answer1Text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        countAnswer1Text++;
                        database.child(question.getQuestion()).child("Antwort 1 Clicks").setValue(countAnswer1Text);
                        showThankYouMessage();
                    }
                });
                answer2Text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        countAnswer2Text++;
                        database.child(question.getQuestion()).child("Antwort 2 Clicks").setValue(countAnswer2Text);
                        showThankYouMessage();
                    }
                });
                answer3Text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        countAnswer3Text++;
                        database.child(question.getQuestion()).child("Antwort 3 Clicks").setValue(countAnswer3Text);
                        showThankYouMessage();
                    }
                });
                answer4Text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        countAnswer4Text++;
                        database.child(question.getQuestion()).child("Antwort 4 Clicks").setValue(countAnswer4Text);
                        showThankYouMessage();
                    }
                });

                break;

            case "Antworten als Icons":
                Log.i("Fragentyp", question.getQuestionType());

                viewFlipper.setDisplayedChild(1);
                SvgLoader.pluck().with(MainActivity.this).load(question.getAnswer1(), answer1Image);
                SvgLoader.pluck().with(MainActivity.this).load(question.getAnswer2(), answer2Image);
                SvgLoader.pluck().with(MainActivity.this).load(question.getAnswer3(), answer3Image);
                SvgLoader.pluck().with(MainActivity.this).load(question.getAnswer4(), answer4Image);

                answer1Image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        countAnswer1Image++;
                        database.child(question.getQuestion()).child("Antwort 1 Clicks").setValue(countAnswer1Image);
                        showThankYouMessage();
                    }
                });
                answer2Image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        countAnswer2Image++;
                        database.child(question.getQuestion()).child("Antwort 2 Clicks").setValue(countAnswer2Image);
                        showThankYouMessage();
                    }
                });
                answer3Image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        countAnswer3Image++;
                        database.child(question.getQuestion()).child("Antwort 3 Clicks").setValue(countAnswer3Image);
                        showThankYouMessage();
                    }
                });
                answer4Image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        countAnswer4Image++;
                        database.child(question.getQuestion()).child("Antwort 4 Clicks").setValue(countAnswer4Image);
                        showThankYouMessage();
                    }
                });
                break;

            case "Bewertung":
                viewFlipper.setDisplayedChild(2);

                rating.setNameForSmile(BaseRating.TERRIBLE, question.getRating1());
                rating.setNameForSmile(BaseRating.BAD, question.getRating2());
                rating.setNameForSmile(BaseRating.OKAY, question.getRating3());
                rating.setNameForSmile(BaseRating.GOOD, question.getRating4());
                rating.setNameForSmile(BaseRating.GREAT, question.getRating5());
                rating.setSelectedSmile(BaseRating.OKAY);

                rating.setOnRatingSelectedListener(new SmileRating.OnRatingSelectedListener() {
                    @Override
                    public void onRatingSelected(int level, boolean reselected) {
                        switch (level) {
                            case 1:
                                countRating1++;
                                database.child(question.getQuestion()).child("Rating 1 Clicks").setValue(countRating1);
                                showThankYouMessage();
                                break;
                            case 2:
                                countRating2++;
                                database.child(question.getQuestion()).child("Rating 2 Clicks").setValue(countRating2);
                                showThankYouMessage();
                                break;
                            case 3:
                                countRating3++;
                                database.child(question.getQuestion()).child("Rating 3 Clicks").setValue(countRating3);
                                showThankYouMessage();
                                break;
                            case 4:
                                countRating4++;
                                database.child(question.getQuestion()).child("Rating 4 Clicks").setValue(countRating4);
                                showThankYouMessage();
                                break;
                            case 5:
                                countRating5++;
                                database.child(question.getQuestion()).child("Rating 5 Clicks").setValue(countRating5);
                                showThankYouMessage();
                                break;
                        }
                    }
                });
                break;
        }
        countAnswer1Text = countAnswer2Text = countAnswer3Text = countAnswer4Text =
                countAnswer1Image = countAnswer2Image = countAnswer3Image = countAnswer4Image =
                        countRating1 = countRating2 = countRating3 = countRating4 = countRating5 = 0;

        //if no questions left, display the following
        if (question == null) {
            viewFlipper.setDisplayedChild(3);
            noQuestions.setText("Leider gibt es momentan keine Fragen, komm später wieder!");
            questionView.setText("");
        }
    }

    public void showThankYouMessage() {

        //build AlertDialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(this).setCancelable(true).setTitle("Danke für deine Teilnahme!").setMessage("Komm nächste Woche wieder für neue Fragen \uD83C\uDF89");


        //set AlertDialog and make it not focusable when created to prevent action bar and navigation bar showing
        final AlertDialog alert = dialog.create();
        alert.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        alert.show();
        //clear flags 'not focusable'
        alert.getWindow().getDecorView().setSystemUiVisibility(this.getWindow().getDecorView().getSystemUiVisibility());
        alert.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);


        //close AlertDialog
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (alert.isShowing()) {
                    alert.dismiss();
                }
            }
        };

        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });

        //delay closing AlertDialog with delay
        handler.postDelayed(runnable, 2500);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    public void setRandomBackground() {
        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        int[] androidColors = getResources().getIntArray(R.array.androidcolors);
        int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
        mainLayout.setBackgroundColor(randomAndroidColor);
    }
}

