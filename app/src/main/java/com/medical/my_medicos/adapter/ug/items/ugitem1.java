package com.medical.my_medicos.adapter.ug.items;

public class ugitem1 {

    String docname,doctitle,docdesciption,date,docpos,pdf,docphone,docdownloads;
    int imageview;



    public ugitem1(String docname, String docpos, String pdf, String doctitle, String docdesciption,String date, String docphone, String docdownloads) {
        this.docname = docname;
        this.doctitle=doctitle;
        this.docphone=doctitle;
        this.docdownloads = docdownloads;
        this.date=date;
        this.docpos=docpos;
        this.pdf=pdf;
        this.docdesciption=docdesciption;
    }
    public String getDocname() {
        return docname;
    }
    public String getPdf() {
        return pdf;
    }

    public void setDocname(String docname) {
        this.docname = docname;
    }

    public String getDocdescripiton() {
        return docdesciption;
    }
    public String getdate() {
        return date;
    }
    public int getImage() {
        return imageview;
    }

    public String getDoctitle() {
        return doctitle;
    }

    public void setDoctitle(String doctitle) {
        this.doctitle = doctitle;
    }

    public String getDocphone() {
        return docphone;
    }

    public void setDocphone(String docphone) {
        this.docphone = docphone;
    }

    public String getDocdownloads() {
        return docdownloads;
    }

    public void setDocdownloads(String docdownloads) {
        this.docdownloads = docdownloads;
    }

    public void setImage(int imageview) {
        this.imageview = imageview;
    }
}
