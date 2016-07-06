package com.universe.ifr.universe;

import java.io.Serializable;

//Course object that holds the course information
public class Course implements Serializable {
    int id;
    String subject;
    String catalog;
    String title;
    boolean valid;

    public Course(int id, String subject, String catalog, String title) {
        this.id = id;
        this.subject = subject;
        this.catalog = catalog;
        this.title = title;

        validate();
    }

    //check to see if the object is valid
    private void validate() {
        if (subject == null || catalog == null || title == null) {
            valid = false;
        } else {
            valid = true;
        }
    }
}
