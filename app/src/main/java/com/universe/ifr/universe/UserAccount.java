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
    public int facultyNumber;
    public String program;
    public int programNumber;

    public boolean validAccount;

    ArrayList<ArrayList<Course>> coursesTaken;

    //Private constructor for the singleton class
    private static UserAccount ourInstance = new UserAccount();
    //Get the instance
    public static UserAccount getInstance() {
        return ourInstance;
    }

    private UserAccount() {
        coursesTaken = new ArrayList<>();
        reset();
    }

    //Set term related details
    public void setTerm(String termName, int termNum) {
        term = termName;
        termNumber = termNum;
        validAccount = true;
    }

    //Reset the singleton if logged out
    public void reset() {
        username = null;
        email = null;
        password = null;
        term = "1A";
        termNumber = 1;
        faculty = "";
        facultyNumber = 1;
        program = "";
        programNumber = 1;
        validAccount = false;
        coursesTaken.clear();
        for (int i=0; i<10; i++) {
            coursesTaken.add(new ArrayList<Course>());
        }
    }

    //Write the data to the file so it can be retrieved
    public void writeToFile(Context c) {
        String FILENAME = username + "_data";
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

    //Load data from file
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
