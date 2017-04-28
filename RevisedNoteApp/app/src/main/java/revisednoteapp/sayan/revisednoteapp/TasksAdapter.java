package revisednoteapp.sayan.revisednoteapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by banersay on 26-07-2016.
 */
public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.MyViewHolder> {

    DatabaseHelper myDatabaseHelper;
    public List<Tasks> tasksList;
    public TasksAdapter tasksAdapter = this;
    Context context;
    private ItemClickListener clickListener;
    private ProgressDialog mSpinner;
    MainActivity contextMainActivity;
    SimpleDateFormat sdf;
    public AlarmManager alarmManager;
    public PendingIntent alarmIntent;
    public Intent alertIntent;

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView title;
        public TextView desc;
        public TextView date;
        public ImageButton remove;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            desc = (TextView)itemView.findViewById(R.id.desc);
            date = (TextView)itemView.findViewById(R.id.date);
            remove = (ImageButton) itemView.findViewById(R.id.remove);
            //itemView.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //Log.i("Clicked: ",getLayoutPosition());
            /*int position = (int) view.getTag();
            tasksAdapter.notifyItemRemoved(position);*/

            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
            //Toast.makeText(context, String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
            Log.i("Clicked Position",String.valueOf(getAdapterPosition()));
            /*tasksList.remove(getAdapterPosition());
            notifyDataSetChanged();*/
        }
    }

    public TasksAdapter(Context applicationContext, List<Tasks> tasksList) {
        this.context = applicationContext;
        this.tasksList = tasksList;

    }

    @Override
    public TasksAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tasks_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TasksAdapter.MyViewHolder holder, final int position)  {
        final Tasks tasks = tasksList.get(position);
        holder.title.setText(tasks.getTitle());
        holder.desc.setText(tasks.getDesc());
        holder.date.setText(tasks.getDate());
        holder.remove.setTag(tasks.getId());

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rowId = (int) view.getTag();
                confirmRemoval(rowId,view.getContext(),position);
                //delete(rowId);

                //tasksAdapter.notifyItemRemoved(position);
                /*tasksList.remove(tasksList.get(position));
                tasksAdapter.notifyItemRemoved(position);*/
                //tasksList.get(position);
                //view.setOnClickListener(this);
            }
        });
    }

    private void confirmRemoval(final int rowId, final Context context, final int position) {
        ContextThemeWrapper ctw = new ContextThemeWrapper(context, R.style.Theme_Show_Dialog_Alert);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        builder.setMessage("Do you really want to remove it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,  final int id) {
                        showSpinner(context,rowId,position);
                        delete(rowId);
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

    public interface OnRecyclerItemClickListener {

        void onRecyclerItemClick(String data);
    }

    private void delete(int rowId) {
        String strPosition = String.valueOf(rowId);
        myDatabaseHelper = new DatabaseHelper(context);
        myDatabaseHelper.deleteSpecifRecords(rowId);
        Log.i("Database Postion:",strPosition);
        //Toast.makeText(context,"Removed",Toast.LENGTH_SHORT).show();
    }
    private void showSpinner(final Context context, final int rowId, final int position) {
        mSpinner = new ProgressDialog(context);
        mSpinner.setTitle("Removing");
        mSpinner.setMessage("Please wait...");
        mSpinner.setIcon(R.drawable.removetask);
        mSpinner.show();
        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                //Toast.makeText(context,"deleted",Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
                tasksList.remove(position);
                if(tasksList.size()==0){
                    //Toast.makeText(context,"Add some tasks to stay organized!",Toast.LENGTH_SHORT).show();
                    Log.i("List Position: ",String.valueOf(position));
                }
                //Log.i("List Position: ",String.valueOf(position));
                mSpinner.cancel();

            }
        };
        delete(rowId);
        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 3000);
    }


    /*public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(context, String.valueOf(getLayoutPosition()), Toast.LENGTH_SHORT).show();
        }
    }
*/

    @Override
    public int getItemCount() {
        return tasksList.size();
    }
}
