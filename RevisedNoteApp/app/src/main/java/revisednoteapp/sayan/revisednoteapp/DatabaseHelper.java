package revisednoteapp.sayan.revisednoteapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by banersay on 26-07-2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    SQLiteDatabase sqLiteDatabase;

    public static final String DATABASE_NAME = "MyNoteAppDatabase";
    public static final String TABLE_NAME = "MyNewTaskDetailsTable";
    public static final String TASK_POSITION = "TaskPos";

    public static final String TASK_TITLE = "TaskTitle";
    public static final String TASK_DESC = "TaskDesc";
    public static final String TASK_DATE = "TaskDate";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME,null,1);
        sqLiteDatabase = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL( " create table " + TABLE_NAME + " ( ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , TaskTitle Text , TaskDesc Text , TaskDate Text ) " );
        //System.out.println("-----------"+TABLE_NAME+" TABLE CREATED AGAIN "+"------------");
        Log.v(TABLE_NAME," is created");
        Log.i(TABLE_NAME," is created now");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL( " drop table if exists " + TABLE_NAME );
        //onCreate(sqLiteDatabase);

    }

    public boolean insertIntoTable (String taskTitle, String taskDesc, String taskDate){
        sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(TASK_TITLE,taskTitle);
        contentValues.put(TASK_DESC,taskDesc);
        contentValues.put(TASK_DATE,taskDate);

        long result = sqLiteDatabase.insert(TABLE_NAME,null,contentValues);
        if(result == -1 )
            return false;
        else
            return true;
    }

    public Cursor RetrieveData(){
        sqLiteDatabase = this.getWritableDatabase();
        Cursor myCursor = sqLiteDatabase.rawQuery( " select * from " + TABLE_NAME , null);
        return myCursor;
    }

    public boolean deleteAllRecords()
    {
        sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete(TABLE_NAME,null, null) > 0;
    }
    public boolean deleteSpecifRecords(int sPos)
    {
        sqLiteDatabase = this.getWritableDatabase();
        long delResult = sqLiteDatabase.delete(TABLE_NAME, " ID = "+sPos ,null) ;
        if(delResult == -1 )
            return false;
        else
            return true;
    }

    public Cursor rowIdData(){
        sqLiteDatabase = this.getWritableDatabase();
        Cursor myCursor = sqLiteDatabase.rawQuery( " select rowid from " + TABLE_NAME , null);
        return myCursor;
    }

    public Cursor selectOldRecords(){
        sqLiteDatabase = this.getWritableDatabase();
        Cursor myCursor = sqLiteDatabase.rawQuery("SELECT * FROM MyNewTaskDetailsTable WHERE TaskDate <= date('now','-1 day')",null);
        //String sql = "DELETE FROM MyNewTaskDetailsTable WHERE TaskDate <= date('now','-1 day')";
        return myCursor;
    }

    public Cursor deleteOldRecords(){
        sqLiteDatabase = this.getWritableDatabase();
        Cursor myCursor = sqLiteDatabase.rawQuery("DELETE FROM MyNewTaskDetailsTable WHERE TaskDate <= date('now','-1 day')",null);
        //String sql = "DELETE FROM MyNewTaskDetailsTable WHERE TaskDate <= date('now','-1 day')";
        return myCursor;
    }

}
