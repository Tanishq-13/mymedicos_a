package com.medical.my_medicos.adapter.cme.items;

public class cmeitem2 {

    private int imageview;
    String doc_name;
    String hosp_name;
    String pos_nam,type;
    int image;
    String docname,docpos,doctitle,docpresenter,date,time,email,documentid,mode;

    public cmeitem2(String docname, String docpos, String date, String doctitle, String docpresenter,int imageview,String time,String email,String type,String documentid,String mode) {
        this.docname = docname;
        this.docpos = docpos;
        this.imageview = imageview;
        this.doctitle=doctitle;
        this.date=date;
        this.time=time;
        this.mode=mode;
        this.email=email;
        this.type=type;
        this.docpresenter=docpresenter;
        this.documentid=documentid;
    }
    public String getDocname() {
        return docname;
    }
    public String getDate() {
        return date;
    }
    public String getTime() {
        return time;
    }
    public String getEmail() {
        return email;
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
    public String getDocumentid() {
        return documentid;
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

    public String getDoc_name() {
        return doc_name;
    }

    public void setDoc_name(String doc_name) {
        this.doc_name = doc_name;
    }

    public String getHosp_name() {
        return hosp_name;
    }

    public void setHosp_name(String hosp_name) {
        this.hosp_name = hosp_name;
    }


}
