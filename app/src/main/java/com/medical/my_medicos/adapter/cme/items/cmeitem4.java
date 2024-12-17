package com.medical.my_medicos.adapter.cme.items;
public class cmeitem4 {

    String docname,docpos,doctitle,docpresenter,date,time,email,type,documentid,mode;
    int imageview;



    public cmeitem4(String docname, String docpos, String date, String doctitle, String docpresenter,int imageview,String time,String email,String type,String documentid,String mode) {
        this.docname = docname;
        this.docpos = docpos;
//        this.imageview = imageview;
        this.doctitle=doctitle;
        this.email=email;
        this.type=type;
        this.mode=mode;
        this.date=date;
        this.time=time;
        this.docpresenter=docpresenter;
        this.documentid=documentid;
    }

    public String getDocname() {
        return docname;
    }
    public String getEmail() {
        return email;
    }

    public String getDate() {
        return date;
    }
    public String getDocumentid() {
        return documentid;
    }
    public String getTime() {
        return time;
    }
    public String getType() {
        return type;
    }
    public String getMode() {
        return mode;
    }

    public void setDocname(String docname) {
        this.docname = docname;
    }

    public String getDocpos() {
        return docpos;
    }

    public void setDocpos(String docpos) {
        this.docpos = docpos;
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

    public String getDocpresenter() {
        return docpresenter;
    }

    public void setDocpresenter(String docpresenter) {
        this.docpresenter = docpresenter;
    }


    public void setImage(int imageview) {
        this.imageview = imageview;
    }
}
