package com.example.anyiqu.calendar;

import java.io.Serializable;

public class Event implements Serializable {
    public String Date;
    public String Title;
    public String Location;
    public String Startime;
    public String Endtime;
    public Long Remindtime;

    public Event(String Date, String Title,String Location,String Startime, String Endtime,long Remindtime) {
        this.Date = Date;
        this.Title = Title;
        this.Location = Location;
        this.Startime = Startime;
        this.Endtime = Endtime;
        this.Remindtime = Remindtime;
    }
    public String getDate(){
        return this.Date;
    }
    public String getTitle(){
        return this.Title;
    }
    public String getLocation(){
        return this.Location;
    }
    public String getStartime(){
        return this.Startime;
    }
    public String getEndtime(){
        return this.Endtime;
    }
    public Long getRemindtime(){
        return this.Remindtime;
    }
}
