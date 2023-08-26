package com.example.my_medicos;

public class publicationitem {

    String publicationdate,publicationtimings,publicationdr;

    public publicationitem() {
    }

    public publicationitem(String publicationdate, String publicationtimings, String publicationdr) {
        this.publicationdate = publicationdate;
        this.publicationtimings = publicationtimings;
        this.publicationdr = publicationdr;
    }


    public String getPublicationdate() {
        return publicationdate;
    }

    public void setPublicationdate(String publicationdate) {
        this.publicationdate = publicationdate;
    }

    public String getPublicationtimings() {
        return publicationtimings;
    }

    public void setPublicationtimings(String publicationtimings) {
        this.publicationtimings = publicationtimings;
    }

    public String getPublicationdr() {
        return publicationdr;
    }

    public void setPublicationdr(String publicationdr) {
        this.publicationdr = publicationdr;
    }
}
