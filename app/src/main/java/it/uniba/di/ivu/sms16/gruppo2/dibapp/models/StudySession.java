package it.uniba.di.ivu.sms16.gruppo2.dibapp.models;

import java.io.Serializable;

public class StudySession implements Serializable {
    private static final long serialVersionUID = 1L;

    public String id;
    public String title;
    public String type;
    public String description;
    public String degreeCourse;
    public String course;
    public GeoPosition geoPosition;
    public String date;
    public String hourStart;
    public String hourEnd;
    public String organizer;
    public int numParticipants;

    public StudySession() {

    }

    public StudySession(String type, String title, String organizer) {
        this.type = type;
        this.title = title;
        this.organizer = organizer;
    }

    public StudySession(String title, String type, String description, String degreeCourse,
                        String course, GeoPosition geoPosition, String date, String hourStart,
                        String hourEnd, String organizer, int numParticipants) {

        this.title = title;
        this.type = type;
        this.description = description;
        this.degreeCourse = degreeCourse;
        this.course = course;
        this.geoPosition = geoPosition;
        this.date = date;
        this.hourStart = hourStart;
        this.hourEnd = hourEnd;
        this.organizer = organizer;
        this.numParticipants = numParticipants;
    }
}