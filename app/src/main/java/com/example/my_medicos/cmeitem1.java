package com.example.my_medicos;

    public class cmeitem1 {

        String docname,docpos;
        int image;

    public cmeitem1() {
    }

    public cmeitem1(String docname, String docpos, int image) {
        this.docname = docname;
        this.docpos = docpos;
        this.image = image;
    }

    public String getDocname() {
        return docname;
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
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
