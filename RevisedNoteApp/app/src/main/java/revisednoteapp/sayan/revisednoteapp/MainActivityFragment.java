package revisednoteapp.sayan.revisednoteapp;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    FloatingActionButton mplusButton;
    private static boolean isFabOpen = false;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View myRootView = inflater.inflate(R.layout.fragment_main, container, false);

        mplusButton = (FloatingActionButton)myRootView.findViewById(R.id.plusButton);
        mplusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                animateFAB();

                /*Animation startRotateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_things);
                mplusButton.startAnimation(startRotateAnimation);*/

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        /*Intent i=new Intent(SearxhJobs.this,JobsTypes.class);
                        startActivity(i);*/

                        Intent intent = new Intent(getActivity(), AddNotesActivity.class);
                        Bundle bundle = ActivityOptions.makeCustomAnimation(getActivity(),R.anim.slide_enter,R.anim.slide_exit).toBundle();
                        startActivity(intent,bundle);
                        getActivity().finish();
                    }
                }, 1400);
                /*Intent intent = new Intent(getActivity(), AddNotesActivity.class);
                Bundle bundle = ActivityOptions.makeCustomAnimation(getActivity(),R.anim.slide_enter,R.anim.slide_exit).toBundle();
                startActivity(intent,bundle);*/

                //getActivity().overridePendingTransition(R.anim.slide_enter, R.anim.slide_in);
            }
        });

        return myRootView;

    }

    public void animateFAB(){

        Animation mRotateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_plus);
        mplusButton.startAnimation(mRotateAnimation);
        /*if(isFabOpen){
            Animation endRotateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_things_done);
            mplusButton.startAnimation(endRotateAnimation);
            isFabOpen = false;
            Log.d("Sayan", "close");

        } else {

            Animation startRotateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_things_start);
            mplusButton.startAnimation(startRotateAnimation);
            isFabOpen = true;
            Log.d("Sayan","open");

        }*/
    }

    @Override
    public void onResume() {
        super.onResume();
        Animation mRotateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_plus_onresume);
        mplusButton.startAnimation(mRotateAnimation);
    }
}
