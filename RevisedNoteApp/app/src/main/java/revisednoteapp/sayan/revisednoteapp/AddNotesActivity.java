package revisednoteapp.sayan.revisednoteapp;

import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by banersay on 20-07-2016.
 */
public class AddNotesActivity extends AppCompatActivity {

    DatabaseHelper myDatabaseHelper;
    EditText mTaskTitle;
    EditText mTaskDesc;
    EditText mTaskDate;
    Button mbtnSaveTask;
    Button mbtnShowTask;
    Calendar myCalendar;
    String dateString;
    TextView mTvCurrentDate;
    DatePickerDialog datePickerDialog;
    SimpleDateFormat sdf;
    private ProgressDialog mSpinner;
    LinearLayout mParentLinearLayoutAddTask;

    public AlarmManager alarmManager;
    public PendingIntent alarmIntent;
    public Intent alertIntent;

    String strTaskDate;

    boolean clickedBackButton = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable
                    (ContextCompat.getColor(this,R.color.colorDefault)));
            //getSupportActionBar().setHomeAsUpIndicator(R.mipmap.custombackarrow);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mParentLinearLayoutAddTask = (LinearLayout)findViewById(R.id.parentLinearLayoutAddTask);

        //changeBackgroundColorRecylcer();
        myCalendar = Calendar.getInstance();
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            datePickerDialog = new DatePickerDialog(this);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 10000);
        }*/

        myDatabaseHelper = new DatabaseHelper(this);
        mTaskTitle = (EditText)findViewById(R.id.edTaskTitle);
        mTaskDesc = (EditText)findViewById(R.id.edTaskDesc);
        mTaskDate = (EditText)findViewById(R.id.edTaskDate);
        mbtnSaveTask = (Button)findViewById(R.id.btnSaveTask);

        mTaskTitle.clearFocus();
        mTaskDesc.clearFocus();
        mTaskDate.clearFocus();

        mTvCurrentDate = new TextView(this);

        mbtnSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    saveTask(view);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                try {
                    updateLabel();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

        };

        mTaskDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*view = getApplicationContext().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }*/
                new DatePickerDialog(AddNotesActivity.this,date,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //hide focus keyboard on startup of activity
        animateTaskGrowLinearLayout();
        bounceStartButton();
    }

    public void changeBackgroundColorRecylcer(){
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (getApplicationContext() == null) {
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
                        mParentLinearLayoutAddTask.setBackgroundColor(Color.rgb(red, green, blue));

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                //toast.cancel();
                            }
                        }, 1000);

                    }
                });
            }
        }, 0, 1000); //will pop up after every 10 secs
    }

    public void animateTaskGrowLinearLayout(){
        LinearLayout dialog   = (LinearLayout)findViewById(R.id.addTaskLinearLayout);
        dialog.setVisibility(LinearLayout.VISIBLE);
        Animation animation   =    AnimationUtils.loadAnimation(this, R.anim.grow_linear_layout);
        animation.setDuration(1500);
        dialog.setAnimation(animation);
        dialog.animate();
        animation.start();
    }

    public void animateTaskShrinkLinearLayout(){
        LinearLayout dialog   = (LinearLayout)findViewById(R.id.addTaskLinearLayout);
        dialog.setVisibility(LinearLayout.INVISIBLE);
        Animation animation   =    AnimationUtils.loadAnimation(this, R.anim.shrink_linear_layout);
        animation.setDuration(1500);
        dialog.setAnimation(animation);
        dialog.animate();
        animation.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intentDate = new Intent(getApplicationContext(),MainActivity.class);
                intentDate.putExtra("Intent_Date",strTaskDate);
                startActivity(intentDate);
                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        },1500);

    }

    private void bounceStartButton(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator animY = ObjectAnimator.ofFloat(mbtnSaveTask, "translationY", -500f, 0f);
                animY.setDuration(3000);
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

    private void updateLabel() throws ParseException {

        String myFormat = "yyyy-MM-dd";
        sdf = new SimpleDateFormat(myFormat, Locale.US);

        String selectDate = sdf.format(myCalendar.getTime());
        Date inputDate = sdf.parse(selectDate);
        dateString = sdf.format(inputDate); //store this to sqlite
        mTaskDate.setText(selectDate);


        //mTvCurrentDate.setText(strCurrentDate);
        //Toast.makeText(getApplicationContext(),selectDate,Toast.LENGTH_SHORT).show();
    }

    public void saveTask(View view) throws ParseException {

         String strTaskTitle = mTaskTitle.getText().toString().trim();
         String strTaskDesc = mTaskDesc.getText().toString().trim();
         strTaskDate = mTaskDate.getText().toString().trim();

        if (!(strTaskTitle.isEmpty() || strTaskDesc.isEmpty() || strTaskDate.isEmpty())) {

            String myFormat = "yyyy-MM-dd";
            sdf = new SimpleDateFormat(myFormat, Locale.US);
            sdf.setLenient(false);

            String selectDate = sdf.format(myCalendar.getTime());
            Date inputDate = sdf.parse(selectDate);  //choosing date from Calendar

            Date currentDate = new Date(System.currentTimeMillis()); //today's current date
            String strCurrentDate = sdf.format(currentDate);
            Date cDate = sdf.parse(strCurrentDate);

            if (inputDate.before(cDate)){
                //Toast.makeText(getApplicationContext(),"Outdated!",Toast.LENGTH_SHORT).show();
                Snackbar snackbar = Snackbar
                        .make(view, "Outdated!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null);
                snackbar.getView().setBackgroundColor(Color.parseColor("#7F049E"));
                snackbar.show();
                mTaskDate.setText("");
            }

            if(cDate.equals(inputDate) || inputDate.after(cDate)){
                //Toast.makeText(getApplicationContext(),"Same Date!",Toast.LENGTH_SHORT).show();
                myDatabaseHelper.insertIntoTable(strTaskTitle, strTaskDesc, strTaskDate);
                if(!((AddNotesActivity.this).isFinishing()))
                {
                    showSpinner(view);
                }

                //Toast.makeText(getApplicationContext(), "Task Added!", Toast.LENGTH_SHORT).show();
                /*Snackbar snackbar = Snackbar
                        .make(view, "Task Added!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null);
                snackbar.getView().setBackgroundColor(Color.parseColor("#7F049E"));
                snackbar.show();*/
            }
        }
        else if(strTaskTitle.isEmpty()) {
            //Toast.makeText(getApplicationContext(), "Please give a Task Title!", Toast.LENGTH_SHORT).show();
            Snackbar snackbar = Snackbar
                    .make(view, "Please give a Task Title!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null);
            snackbar.getView().setBackgroundColor(Color.parseColor("#7F049E"));
            snackbar.show();
            mTaskTitle.requestFocus();
        }
        else if(strTaskDesc.isEmpty()) {
            //Toast.makeText(getApplicationContext(), "Please describe your task!", Toast.LENGTH_SHORT).show();
            Snackbar snackbar = Snackbar
                    .make(view, "Please describe your task!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null);
            snackbar.getView().setBackgroundColor(Color.parseColor("#7F049E"));
            snackbar.show();
            mTaskDesc.requestFocus();
        }
        else if( mTaskDate.getText().toString().isEmpty()) {
            //Toast.makeText(getApplicationContext(), "Please select a date when your task needs to be done!", Toast.LENGTH_SHORT).show();
            Snackbar snackbar = Snackbar
                    .make(view, "Please select a date when your task needs to be done!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null);
            snackbar.getView().setBackgroundColor(Color.parseColor("#7F049E"));
            snackbar.show();
            mTaskDate.requestFocus();
        }

    }

    private void showSpinner(final View v) {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Adding Task to your List");
        mSpinner.setMessage("Please wait...");
        mSpinner.setIcon(R.mipmap.addlist);
        mSpinner.show();
        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {


                    //Toast.makeText(v.getContext(),"Add some tasks to stay organized!",Toast.LENGTH_SHORT).show();
                    Snackbar snackbar = Snackbar
                            .make(v, "Task Added!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
                    snackbar.getView().setBackgroundColor(Color.parseColor("#7F049E"));
                    snackbar.show();
                mSpinner.cancel();
            }
        };
        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 1500);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Toast.makeText(getApplicationContext(),"Back button clicked", Toast.LENGTH_SHORT).show();
                if(!clickedBackButton){
                    clickedBackButton = true;
                    animateTaskShrinkLinearLayout();
                }
                else{
                    if(!(getSupportActionBar()==null)){
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    }

                }

                //bounceStartButton();
                //backToHome();
                break;
        }
        return true;
    }
}
