package it.uniba.di.ivu.sms16.gruppo2.dibapp.models;


public class Message {

    public String senderId;
    public String senderName;
    public String senderPhoto;
    public String message;
    public String date;
    public String hour;

    public Message() {

    }

    public Message(String senderId, String senderName, String senderPhoto, String message, String date, String hour) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.message = message;
        this.date = date;
        this.hour = hour;

        if (senderPhoto != null) {
            this.senderPhoto = senderPhoto;
        }
    }
}
