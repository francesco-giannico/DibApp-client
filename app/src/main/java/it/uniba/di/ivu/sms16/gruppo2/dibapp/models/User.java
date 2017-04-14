package it.uniba.di.ivu.sms16.gruppo2.dibapp.models;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    public String name;
    public String photoUrl;
    public String email;
    public String phoneNumber;
    public String degreeCourse;
    public float avgRating;
    public int numRatings;
    public ArrayList<String> followingCourses;
    public ArrayList<String> interests;
    public GeoPosition geoPosition;

    public User() {

    }

    public User(String name, String email, String photoUrl) {
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;

    }


    public User(String name, String photoUrl, String email, String phoneNumber, String degreeCourse,
                float avgRating, int numRatings, ArrayList<String> followingCourses,
                GeoPosition geoPosition) {
        this.name = name;
        this.photoUrl = photoUrl;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.degreeCourse = degreeCourse;
        this.avgRating = avgRating;
        this.numRatings = numRatings;
        this.followingCourses = followingCourses;
        this.geoPosition = geoPosition;
    }

    public String obtainFormattedInterests() {
        String interestsString = "";
        if (interests != null) {
            for (String s : interests) {
                if (interests.indexOf(s) == interests.size() - 1) {
                    interestsString += s;
                } else {
                    interestsString += s + ", ";
                }
            }
        }

        return interestsString;
    }

    public String obtainFormattedCourses() {
        String coursesString = "";
        if (followingCourses != null) {
            for (String s : followingCourses) {
                if (followingCourses.indexOf(s) == followingCourses.size() - 1) {
                    coursesString += s;
                } else {
                    coursesString += s + ", ";
                }
            }
        }
        return coursesString;
    }
}