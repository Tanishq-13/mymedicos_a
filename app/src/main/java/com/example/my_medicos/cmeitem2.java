package com.example.my_medicos;

public class cmeitem2 {

    String doc_name;
    String hosp_name;

    String pos_name;

    int image;

    public int getImage() {
        return image;
    }

    public cmeitem2(String johnWick, String esiHospital, String peitiric, int img_6) {
    }

    public cmeitem2(String doc_name, String hosp_name, String pos_name) {
        this.doc_name = doc_name;
        this.hosp_name = hosp_name;
        this.pos_name = pos_name;
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

    public String getPos_name() {
        return pos_name;
    }

    public void setPos_name(String pos_name) {
        this.pos_name = pos_name;
    }
}
