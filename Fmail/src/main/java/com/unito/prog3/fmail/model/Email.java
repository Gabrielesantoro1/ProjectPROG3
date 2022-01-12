package com.unito.prog3.fmail.model;
import java.io.Serializable;
import java.util.Date;

public class Email implements Serializable {
    private int id;
    private String from;
    private String to;
    private String object;
    private String text;
    private Date date;

    public Email(){}

    /**
     *   This constructor is used by Client because it doesn't know the current id counter.
     */
    public Email(String from, String to, String object, String text) {
        this.from = from;
        this.to = to;
        this.object = object;
        this.text = text;
        this.date = new Date();
    }

    /**
     *  This constructor is used by Server because it knows the current id counter.
     */
    public Email(int id, String from, String to, String object, String text, Date date) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.object = object;
        this.text = text;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "\n              Email{" +
                "id=" + id +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", object='" + object + '\'' +
                ", text='" + text + '\'' +
                ", date=" + date +
                '}';
    }
}
