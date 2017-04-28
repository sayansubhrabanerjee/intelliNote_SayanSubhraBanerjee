package revisednoteapp.sayan.revisednoteapp;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity /*implements ItemClickListener*/ {

    DatabaseHelper myDatabaseHelper;
    private List<Tasks> taskList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TasksAdapter tAdapter;
    String dateString;
    Calendar myCalendar;
    WeatherActivity weatherActivity;

    public AlarmManager alarmManager;
    static PendingIntent alarmIntent;
    public Intent alertIntent;

    FloatingActionButton fab;
    FloatingActionButton fab1;
    FloatingActionButton fab2;
    FloatingActionButton fab3;
    FloatingActionButton fab4;

    CoordinatorLayout rootLayout;
    LinearLayout mViewBottom;

    private ProgressDialog mSpinner;

    //Save the FAB's active status
    //false -> fab = close
    //true -> fab = open
    public boolean FAB_Status = false;

    //Animations
    Animation show_fab_1;
    Animation hide_fab_1;
    Animation show_fab_2;
    Animation hide_fab_2;
    Animation show_fab_3;
    Animation hide_fab_3;

    Animation hideSnack_fab_1;
    Animation hideSnack_fab_2;
    Animation hideSnack_fab_3;

    View view;
    FrameLayout frameLayout;

    MainActivity mainActivity;

    boolean doubleBackToExitPressedOnce = false;
    boolean bounceCoordinator = false;

    static ObjectAnimator bounceCoordinatorAnimY;

    //TextView textViewBlankScreen;

    public static int countDialogAppearance=0;

    public CoordinatorLayout  mDrawerCoordinatorRecylcer;
    public LinearLayout mLinearLayoutRecyclerView;

    int listItemPosition;

    final static int RQS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(((MainActivity.this).isFinishing()))
        {
            //stopAlarmSpinner();
            finish();
        }

        //Toast.makeText(getApplicationContext(),"Original",Toast.LENGTH_SHORT).show();

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        frameLayout.getBackground().setAlpha(0);

        /*if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("EXIT", false)) {
            finish();
        }*/

        //View parentLayout = findViewById(R.id.coordinatorLayout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable
                    (ContextCompat.getColor(this,R.color.colorDefault)));
            //getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back);
            //getSupportActionBar().setIcon(R.drawable.app_icon_tasks);
        }

        rootLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        mViewBottom = (LinearLayout)findViewById(R.id.viewBottom);
        mDrawerCoordinatorRecylcer = (CoordinatorLayout)findViewById(R.id.coordinatorLayoutRecyclerView);
        mLinearLayoutRecyclerView = (LinearLayout)findViewById(R.id.linearLayoutRecyclerView);

        //Floating Action Buttons
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab_1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab_2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab_3);

        //Animations
        show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_show);
        hide_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_hide);
        show_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_show);
        hide_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_hide);
        show_fab_3 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab3_show);
        hide_fab_3 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab3_hide);

        hideSnack_fab_1 = AnimationUtils.loadAnimation(getApplication(),R.anim.fab1_snack_hide);
        hideSnack_fab_2 = AnimationUtils.loadAnimation(getApplication(),R.anim.fab2_snack_hide);
        hideSnack_fab_3 = AnimationUtils.loadAnimation(getApplication(),R.anim.fab3_snack_hide);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!FAB_Status) {
                    //Display FAB menu
                    expandFAB();
                    FAB_Status = true;
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorFAB)));
                    //showAlertDialog(view);

                } else {
                    //Close FAB menu
                    hideFAB();
                    FAB_Status = false;
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorFABIndigo)));
                }
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplication(), "Floating Action Button 1", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(getApplicationContext(),MapsActivity.class));
                //overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
                //overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplication(), "Floating Action Button 2", Toast.LENGTH_SHORT).show();
                removeAllData(v);

            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplication(), "Floating Action Button 3", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(getApplicationContext(),WeatherActivity.class));
                //overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                Intent intent = new Intent(getApplicationContext(), WeatherActivity.class);
                startActivity(intent);
                //overridePendingTransition(R.anim.slide_up, R.anim.slide_down);

            }
        });


        myDatabaseHelper = new DatabaseHelper(this);
        myCalendar = Calendar.getInstance();
        weatherActivity = new WeatherActivity();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        tAdapter = new TasksAdapter(getApplicationContext(),taskList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(tAdapter);


        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (FAB_Status) {
                    hideFAB();
                    FAB_Status = false;
                }
                return false;
            }
        });

        //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //hide focus keyboard on startup of activity

        //resumeTaskList(rootLayout);



            prepareTasks(rootLayout);
         //first time the app launches we don't have any view initialized.
                                  // so we have to initialize with Co-ordinator Layout Id.
        bounceCoordinatorLayout();
        bounceFloatingButton();
        //showAlertDialog();

        /*if(!bounceCoordinator){
            bounceCoordinatorLayout();
        }
        if (bounceCoordinator){
            bounceCoordinatorAnimY.cancel();
        }*/
        deleteOlderData(rootLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bounceFloatingButton();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        bounceFloatingButton();
    }

    /*public void animateTaskShrinkRecyclerLinearLayout(final Context mContext){
        LinearLayout dialog   = (LinearLayout)findViewById(R.id.viewBottom);
        dialog.setVisibility(LinearLayout.INVISIBLE);
        Animation animation   =    AnimationUtils.loadAnimation(this, R.anim.shrink_linear_layout);
        animation.setDuration(3000);
        dialog.setAnimation(animation);
        dialog.animate();
        animation.start();

        *//*LinearLayout myNewLinearLayout = new LinearLayout(mContext);
        myNewLinearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView myNewTextView = new TextView(mContext);
        myNewTextView.setText("Hi");
        myNewLinearLayout.addView(myNewTextView);*//*


        *//*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        },3000);*//*

    }*/
    public void animateTaskGrowRecyclerLinearLayout(){
        LinearLayout dialog   = (LinearLayout)findViewById(R.id.viewBottom);
        dialog.setVisibility(LinearLayout.VISIBLE);
        Animation animation   =    AnimationUtils.loadAnimation(this, R.anim.grow_linear_layout);
        animation.setDuration(3000);
        dialog.setAnimation(animation);
        dialog.animate();
        animation.start();

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        },3000);*/

    }
    public void animateTaskGrowTodoPicture(){
        LinearLayout dialog   = (LinearLayout)findViewById(R.id.viewTop);
        dialog.setVisibility(LinearLayout.VISIBLE);
        Animation animation   =    AnimationUtils.loadAnimation(this, R.anim.grow_linear_layout);
        animation.setDuration(3000);
        dialog.setAnimation(animation);
        dialog.animate();
        animation.start();

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        },3000);*/

    }

    private void bounceFloatingButton(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator animY = ObjectAnimator.ofFloat(fab, "translationY", -500f, 0f);
                animY.setDuration(3000);//1sec
                animY.setInterpolator(new BounceInterpolator());
                //animY.setRepeatCount(3);
                animY.start();

            }
        },0);

        /*ObjectAnimator animY = ObjectAnimator.ofFloat(fab, "translationY", -500f, 0f);
        animY.setDuration(3000);//1sec
        animY.setInterpolator(new BounceInterpolator());
        //animY.setRepeatCount(3);
        animY.start();*/
    }

    private void bounceCoordinatorLayout(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
              bounceCoordinatorAnimY = ObjectAnimator.ofFloat(rootLayout, "translationY", -500f, 0f);
                bounceCoordinatorAnimY.setDuration(3000);//1sec
                bounceCoordinatorAnimY.setInterpolator(new BounceInterpolator());
                //animY.setRepeatCount(3);
                bounceCoordinatorAnimY.start();
                bounceCoordinator = true;

            }
        },0);
    }

    private void showAlertDialog(View view){
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        ContextThemeWrapper ctw = new ContextThemeWrapper(view.getContext(),R.style.Theme_Show_Dialog_Alert);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        builder.setTitle("Your guide is here");
        builder.setMessage("Hi");
        builder.show();
    }

    private void removeAllData(final View v){
        ContextThemeWrapper ctw = new ContextThemeWrapper(v.getContext(), R.style.Theme_Show_Dialog_Alert);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        if(!(taskList.isEmpty())){
            builder.setMessage("Do you really want to remove all items from your list?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog,  final int id) {

                            if(!((MainActivity.this).isFinishing()))
                            {
                                showSpinner(v);
                                //stopAlarmSpinner();

                            }

                            //stopAlarm();


                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
        else {
                hideSnackFAB();
                FAB_Status = false;
                Snackbar snackbar = Snackbar
                    .make(v, "You do not have any upcoming tasks!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .setDuration(1000);
                snackbar.getView().setBackgroundColor(Color.parseColor("#7F049E"));
                snackbar.show();

            /*if(!((MainActivity.this).isFinishing()))
            {

            }*/

                //Toast.makeText(getApplicationContext(),"You do not have any upcoming tasks!",Toast.LENGTH_SHORT).show();
        }

    }

    private void showSpinner(final View v) {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Clearing Task List");
        mSpinner.setMessage("Please wait...");
        mSpinner.setIcon(R.drawable.removetask);
        mSpinner.show();
        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                taskList.clear();
                tAdapter.notifyDataSetChanged();
                if(taskList.size()==0){
                    //Toast.makeText(v.getContext(),"Add some tasks to stay organized!",Toast.LENGTH_SHORT).show();
                    hideSnackFAB();
                    FAB_Status = false;
                    Snackbar snackbar = Snackbar
                            .make(v, "Add some tasks to stay organized!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
                            .setDuration(1000);
                    snackbar.getView().setBackgroundColor(Color.parseColor("#7F049E"));
                    snackbar.show();
                }
                mSpinner.cancel();
                if(!((MainActivity.this).isFinishing()))
                {
                    //stopAlarmSpinner();
                    stopAlarm();
                }

            }
        };

        myDatabaseHelper.deleteAllRecords();
        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 3000);
    }

    private void stopAlarmSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Cancelling Alarm Notifications");
        mSpinner.setMessage("Please wait...");
        mSpinner.setIcon(R.drawable.alaramclock);
        mSpinner.show();
        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                alarmManager.cancel(alarmIntent);
                alarmIntent = null;
                Log.d("Message:", "cancelling Alert Notifications...");
                mSpinner.cancel();
            }
        };
        //stopAlarm();
        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 3500);
    }



    public void deleteOlderData(View view){
        if (!(taskList.isEmpty())){
            Cursor cursor = myDatabaseHelper.selectOldRecords();
            if (!(cursor.getCount() == 0)){
                //myDatabaseHelper.deleteOldRecords();
                if(!((MainActivity.this).isFinishing()))
                {
                    showSpinnerForOlderRecordsRemoval(view,cursor);
                }
                //showSpinnerForOlderRecordsRemoval(view,cursor);
                //stopAlarm();
            }
            //Toast.makeText(getApplicationContext(),"Cleared older records", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSpinnerForOlderRecordsRemoval(final View v, Cursor cursor) {
        listItemPosition = cursor.getPosition();
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Flushing out the outdated tasks ");
        mSpinner.setMessage("It\'ll be gone next time you visit!");
        mSpinner.setIcon(R.drawable.removetask);
        mSpinner.show();
        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                //taskList.clear();
                //tAdapter.notifyDataSetChanged();
                //taskList.remove(listItemPosition);

                if(taskList.size()==0){
                    //Toast.makeText(v.getContext(),"Add some tasks to stay organized!",Toast.LENGTH_SHORT).show();
                    hideSnackFAB();
                    FAB_Status = false;
                    Snackbar snackbar = Snackbar
                            .make(v, "Add some tasks to stay organized!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
                            .setDuration(1000);
                    snackbar.getView().setBackgroundColor(Color.parseColor("#7F049E"));
                    snackbar.show();
                }
                mSpinner.cancel();
            }
        };

        while (cursor.moveToNext()){
            cursor = myDatabaseHelper.deleteOldRecords();
        }
        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 5000);
    }



    public void stopAlarm()
    {
        /*Toast.makeText(getApplicationContext(),"Stopped Alarm", Toast.LENGTH_SHORT).show();
        alertIntent = new Intent(this,AlarmReceiver.class);
        alertIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        alarmIntent = PendingIntent.getBroadcast(this, RQS, alertIntent, 0);
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);*/
        //alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime, PendingIntent.getBroadcast(this, 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        if(alarmIntent !=null ){
            stopAlarmSpinner();

        }
        else {
            //Toast.makeText(getApplicationContext(),"Null Alarm",Toast.LENGTH_SHORT).show();
            //hideSnackFAB();
            //FAB_Status = false;
            Snackbar snackbar = Snackbar
                    .make(rootLayout, "You don\'t have any pending alarms at this moment", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .setDuration(1000);
            snackbar.getView().setBackgroundColor(Color.parseColor("#7F049E"));
            snackbar.show();
        }



    }



    public void prepareTasks(final View v) {

        //animateTaskGrowTodoPicture();
        animateTaskGrowRecyclerLinearLayout();
        //changeBackgroundColorRecylcer();
        Cursor cursor = myDatabaseHelper.RetrieveData();
        if (cursor.getCount() == 0) {
            changeBackgroundColorRecylcer(v.getContext());
            if(countDialogAppearance == 0){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ContextThemeWrapper ctw = new ContextThemeWrapper(v.getContext(), R.style.Theme_Show_Dialog_Alert);
                        final AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
                        builder.setIcon(R.drawable.app_icon_tasks);
                        builder.setTitle("What is intelliNote ?");
                        builder.setMessage("  Add Some Tasks. \n  Get Notifications. \n  Know Your Exact Location. \n  Check Weather Condition. \n  Auto-Removes Outdated Tasks.")
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, final int id) {
                                        dialog.cancel();
                                    }
                                });
                        final AlertDialog alert = builder.create();
                        if(((MainActivity.this).isFinishing()))
                        {
                            //stopAlarmSpinner();
                            finish();
                            //prepareTasks(rootLayout);
                        }
                        else {
                            alert.show();
                            countDialogAppearance++;
                        }
                    }
                }, 3000);
            }
            /*else {
                Log.i("Tag","No Tasks");
            }*/

            //showMessage("Error","Not Found");
        }
        while (cursor.moveToNext()){
            Tasks tasks = new Tasks(cursor.getInt(0), cursor.getString(1),cursor.getString(2),cursor.getString(3));
            taskList.add(tasks);

            tAdapter.notifyDataSetChanged();
            // prepareMovieData();
            try {
                checkDates();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /*public int generateRandomColor() {
        // This is the base color which will be mixed with the generated one
        int baseColor = Color.WHITE;

        int baseRed = Color.red(baseColor);
        int baseGreen = Color.green(baseColor);
        int baseBlue = Color.blue(baseColor);

        Random mRandom = new Random();
        int red = (baseRed + mRandom.nextInt(256)) / 2;
        int green = (baseGreen + mRandom.nextInt(256)) / 2;
        int blue = (baseBlue + mRandom.nextInt(256)) / 2;

        changeBackgroundColorRecylcer(Color.rgb(red, green, blue));
        return Color.rgb(red, green, blue);
    }*/

    public void changeBackgroundColorRecylcer(final Context context){
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (context == null) {
                    return;
                }
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
//                        final Toast toast = Toast.makeText(
//                                getApplicationContext(), count + "",
//                                Toast.LENGTH_SHORT);
//                        toast.show();
                        int baseColor = Color.WHITE;

                        int baseRed = Color.red(baseColor);
                        int baseGreen = Color.green(baseColor);
                        int baseBlue = Color.blue(baseColor);

                        Random mRandom = new Random();
                        int red = (baseRed + mRandom.nextInt(256)) / 2;
                        int green = (baseGreen + mRandom.nextInt(256)) / 2;
                        int blue = (baseBlue + mRandom.nextInt(256)) / 2;
                        mViewBottom.setBackgroundColor(Color.rgb(red, green, blue));

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                //toast.cancel();
                            }
                        }, 2000);

                    }
                });
            }
        }, 0, 2000); //will pop up after every 10 secs
    }

    public void checkDates() throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String selectDate = sdf.format(myCalendar.getTime());
        /*Date inputDate = sdf.parse(selectDate);
        dateString = sdf.format(inputDate);*/
        //mEdDate.setText(dateString);

        Cursor cursor = myDatabaseHelper.RetrieveData();
        if (cursor.getCount() == 0) {
            //Toast.makeText(getApplicationContext(), "No Upcoming Tasks!", Toast.LENGTH_SHORT).show();
            //showMessage("Error","Not Found");
            Log.i("Tag","No Tasks");
        }
        StringBuffer buffer = new StringBuffer();
        /*Intent dateIntent = getIntent();
        String strDateIntent = dateIntent.getStringExtra("Intent_Date");*/
        while (cursor.moveToNext()) {
            if(cursor.getString(3).equals(selectDate)){
                buffer.append(cursor.getString(1));
                //buffer.append(cursor.getString(2) + "\n\n");
                buffer.append(" , ");
                notifying(buffer.toString());
                weatherActivity.notifyingWeather(getApplicationContext(),"Know more...","Updated Weather");
                startAlarm(buffer.toString());

                //startAlarm(buffer.toString());

                //Intent intent = getIntent();
                /*String temp = getIntent().getStringExtra("temperature");
                String desc = getIntent().getStringExtra("description");
                Log.i("Temp",String.valueOf(temp));
                Log.i("Desc",String.valueOf(desc));*/
                //weatherActivity.notifyingWeather(getApplicationContext(),getIntent().getStringExtra("temperature"),getIntent().getStringExtra("description"));



            }
            else
            {
                //Toast.makeText(getApplicationContext(),"No Not", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void notifying(String title){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, 1, intent, 0);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(this)
                /*.setContentTitle("New mail from " + "test@gmail.com")
                .setContentText("Subject")*/
                .setContentTitle("Tasks for today")
                .setContentText(title)
                /*.setDefaults(Notification.DEFAULT_SOUND)*/
                .setSmallIcon(R.drawable.task)
                .setColor(Color.parseColor("#C23707"))
                .setContentIntent(pIntent).build();
                /*.addAction(R.drawable.bell3, "Call", pIntent)
                .addAction(R.drawable.icon, "More", pIntent)
                .addAction(R.drawable.bell3, "And more", pIntent).build();*/
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(1, noti);
    }

    public void startAlarm(String title)
    {
        //Calendar calendar = new GregorianCalendar();
        //Long alertTime = calendar.getTimeInMillis()+5*1000; //used to get alarm after 5 seconds..

        alertIntent = new Intent(this,AlarmReceiver.class);
        alertIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        alertIntent.putExtra("intent_title",title);
        alarmIntent = PendingIntent.getBroadcast(this, RQS, alertIntent, 0);
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime, PendingIntent.getBroadcast(this, 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT)); // RINGS ONLY ONCE.. ONLY AT 1ST TIME..
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                AlarmManager.INTERVAL_HOUR,
                AlarmManager.INTERVAL_HOUR, alarmIntent); //RINGS AT EVERY 15 MINS INTERVAL.
    }

   /* public void startAlarm(String title){
        alertIntent = new Intent(this,AlarmReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putString("taskAlarmTitle",title);
        alertIntent.putExtras(bundle);
        alertIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        alarmIntent = PendingIntent.getBroadcast(this, 0, alertIntent, 0);
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime, PendingIntent.getBroadcast(this, 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT)); // RINGS ONLY ONCE.. ONLY AT 1ST TIME..
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmIntent); //RINGS AT EVERY 15 MINS INTERVAL.
    }*/

    /*@Override
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
        if (id == R.id.action_weather) {
            startActivity(new Intent(getApplicationContext(),WeatherActivity.class));
            return true;
        }

        if (id == R.id.action_location){
            startActivity(new Intent(getApplicationContext(),MapsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    private void expandFAB() {

        Animation startRotateAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_things);
        fab.startAnimation(startRotateAnimation);

        //frameLayout.getBackground().setAlpha(240);  // for giving layers
        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        layoutParams.rightMargin += (int) (fab1.getWidth() * 1.7);
        layoutParams.bottomMargin += (int) (fab1.getHeight() * 0.25);
        fab1.setLayoutParams(layoutParams);
        fab1.startAnimation(show_fab_1);
        fab1.setClickable(true);
        fab1.requestLayout();

        //Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams2.rightMargin += (int) (fab2.getWidth() * 1.5);
        layoutParams2.bottomMargin += (int) (fab2.getHeight() * 1.5);
        fab2.setLayoutParams(layoutParams2);
        fab2.startAnimation(show_fab_2);
        fab2.setClickable(true);
        fab2.requestLayout();

        //Floating Action Button 3
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        layoutParams3.rightMargin += (int) (fab3.getWidth() * 0.25);
        layoutParams3.bottomMargin += (int) (fab3.getHeight() * 1.7);
        fab3.setLayoutParams(layoutParams3);
        fab3.startAnimation(show_fab_3);
        fab3.setClickable(true);
        fab3.requestLayout();
    }


    private void hideFAB() {

        Animation startRotateAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_things);
        fab.startAnimation(startRotateAnimation);

        //frameLayout.getBackground().setAlpha(0);

        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        layoutParams.rightMargin -= (int) (fab1.getWidth() * 1.7);
        layoutParams.bottomMargin -= (int) (fab1.getHeight() * 0.25);
        fab1.setLayoutParams(layoutParams);
        fab1.startAnimation(hide_fab_1);
        fab1.setClickable(false);
        fab1.requestLayout();

        //Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams2.rightMargin -= (int) (fab2.getWidth() * 1.5);
        layoutParams2.bottomMargin -= (int) (fab2.getHeight() * 1.5);
        fab2.setLayoutParams(layoutParams2);
        fab2.startAnimation(hide_fab_2);
        fab2.setClickable(false);
        fab2.requestLayout();

        //Floating Action Button 3
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        layoutParams3.rightMargin -= (int) (fab3.getWidth() * 0.25);
        layoutParams3.bottomMargin -= (int) (fab3.getHeight() * 1.7);
        fab3.setLayoutParams(layoutParams3);
        fab3.startAnimation(hide_fab_3);
        fab3.setClickable(false);
        fab3.requestLayout();
    }

    public void hideSnackFAB() {

        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        layoutParams.rightMargin -= (int) (fab1.getWidth() * 1.7);
        layoutParams.bottomMargin -= (int) (fab1.getHeight() * 0.25);
        fab1.setLayoutParams(layoutParams);
        fab1.startAnimation(hideSnack_fab_1);
        fab1.setClickable(false);
        fab1.requestLayout();

        //Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams2.rightMargin -= (int) (fab2.getWidth() * 1.5);
        layoutParams2.bottomMargin -= (int) (fab2.getHeight() * 1.5);
        fab2.setLayoutParams(layoutParams2);
        fab2.startAnimation(hideSnack_fab_2);
        fab2.setClickable(false);
        fab2.requestLayout();

        //Floating Action Button 3
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        layoutParams3.rightMargin -= (int) (fab3.getWidth() * 0.25);
        layoutParams3.bottomMargin -= (int) (fab3.getHeight() * 1.7);
        fab3.setLayoutParams(layoutParams3);
        fab3.startAnimation(hideSnack_fab_3);
        fab3.setClickable(false);
        fab3.requestLayout();
    }

    /*public void startAlarm()
    {
        //Calendar calendar = new GregorianCalendar();
        //Long alertTime = calendar.getTimeInMillis()+5*1000; //used to get alarm after 5 seconds..

        alertIntent = new Intent(this,AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, alertIntent, 0);
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime, PendingIntent.getBroadcast(this, 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT)); // RINGS ONLY ONCE.. ONLY AT 1ST TIME..
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmIntent); //RINGS AT EVERY 15 MINS INTERVAL.
    }*/



    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        //overridePendingTransition(R.anim.rotate_out,R.anim.rotate_in);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
        }

        this.doubleBackToExitPressedOnce = true;
        //Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();


        /*hideSnackFAB();
        FAB_Status = false;*/

        if (FAB_Status) {
            hideFAB();
            FAB_Status = false;
        }

        Snackbar snackbar = Snackbar
                .make(rootLayout, "Press back again to exit intelliNote app", Snackbar.LENGTH_SHORT)
                .setAction("Action", null)
                .setDuration(1000);
        snackbar.getView().setBackgroundColor(Color.parseColor("#7F049E"));
        snackbar.show();


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
                //FAB_Status = true;
            }
        }, 2000);
        //super.onBackPressed();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
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
        if (id == R.id.action_stopAlarm) {
            //new DisplayToast().showToast(this,"Your location is: ");
            //startActivity(new Intent(getApplicationContext(),MapsActivity.class));

            if(!((MainActivity.this).isFinishing()))
            {
                //stopAlarmSpinner();
                stopAlarm();
            }

            //Toast.makeText(getApplicationContext(),"Hi",Toast.LENGTH_SHORT).show();

            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
