package revisednoteapp.sayan.revisednoteapp;

/**
 * Created by banersay on 26-07-2016.
 */
public class Tasks {

    public String title;
    public String desc;
    public int id;
    public String date;

    public Tasks(){

    }


    public Tasks(int id, String title, String desc, String date){
        this.title = title;
        this.desc = desc;
        this.id = id;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
