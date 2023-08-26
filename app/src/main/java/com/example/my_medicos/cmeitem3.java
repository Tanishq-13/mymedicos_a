package com.example.my_medicos;

public class cmeitem3 {

    String date;
    String time;
    String cmetitle;

    String drname;

    public int getImage() {
        return image;
    }

    int image;

    public cmeitem3(String s, String s1, String abcdefgh, String johnWick, int img_6) {
    }

    public cmeitem3(String date, String time, String cmetitle, String drname) {
        this.date = date;
        this.time = time;
        this.cmetitle = cmetitle;
        this.drname = drname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCmetitle() {
        return cmetitle;
    }

    public void setCmetitle(String cmetitle) {
        this.cmetitle = cmetitle;
    }

    public String getDrname() {
        return drname;
    }

    public void setDrname(String drname) {
        this.drname = drname;
    }
}
