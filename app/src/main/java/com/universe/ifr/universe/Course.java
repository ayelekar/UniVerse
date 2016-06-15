package com.universe.ifr.universe;

import java.io.Serializable;

/**
 * Created by Asim on 6/13/2016.
 */
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

    private void validate() {
        if (subject == null || catalog == null || title == null) {
            valid = false;
        } else {
            valid = true;
        }
    }
}
