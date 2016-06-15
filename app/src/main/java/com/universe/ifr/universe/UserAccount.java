package com.universe.ifr.universe;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Asim on 6/13/2016.
 */
public class UserAccount implements Serializable {

    public String username;
    public String email;
    public String password;

    public String term;
    public int termNumber;
    public String faculty;
    public String program;

    public boolean validAccount;

    ArrayList<ArrayList<Course>> coursesTaken;

    private static UserAccount ourInstance = new UserAccount();

    public static UserAccount getInstance() {
        return ourInstance;
    }

    private UserAccount() {
        term = "1A";
        termNumber = 1;
        faculty = "";
        program = "";
        coursesTaken = new ArrayList<ArrayList<Course>>();
        coursesTaken.add(new ArrayList<Course>());
    }

    public void setTerm(String termName, int termNum) {
        if (termNum < termNumber) {
            for (int i=termNumber-1; i>=termNum; i--) {
                coursesTaken.remove(i);
            }
        } else {
            for (int i=0; i<(termNum - termNumber); i++) {
                coursesTaken.add(new ArrayList<Course>());
            }
        }
        term = termName;
        termNumber = termNum;
        validAccount = true;
    }

    public void reset() {
        username = null;
        email = null;
        password = null;
        term = "1A";
        termNumber = 1;
        faculty = "";
        program = "";
        validAccount = false;
        coursesTaken.clear();
        coursesTaken.add(new ArrayList<Course>());
    }

    public void writeToFile(Context c) {
        String FILENAME = UserAccount.getInstance().username + "_data";
        try {
            FileOutputStream fos = c.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(this);
            os.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void loadData(Context c, String user) {
        try {
            FileInputStream fis = c.openFileInput(user + "_data");
            ObjectInputStream is = new ObjectInputStream(fis);
            ourInstance = (UserAccount) is.readObject();
            is.close();
            fis.close();
        } catch (Exception e) {
            System.out.println("Data file not found...");
            e.printStackTrace();
        }
    }
}
