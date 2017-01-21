package com.zeeley;

/**
 * Created by gannu on 05-08-2016.
 */
public class notificObj {
    String name, distance;
    int profilePic, interest, id;
    public notificObj(String name,String distance,int profilePic,int interest,int id){
        this.name=name;
        this.distance=distance;
        this.profilePic=profilePic;
        this.interest=interest;
        this.id=id;

    }

    public String getName() {
        return name;
    }

    public String getDistance() {
        return distance;
    }

    public int getProfilePic() {
        return profilePic;
    }

    public int getInterest() {
        return interest;
    }

    public int getId() {
        return id;
    }
}
