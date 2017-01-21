package com.zeeley;

import org.parceler.Parcel;

/**
 * Created by gannu on 15-09-2016.
 */
@Parcel
public class inviteInfo {
    String teamID, senderID, teamName, currentInterest;

    public inviteInfo() {
    }

    public inviteInfo(String teamID, String senderID, String teamName, String currentInterest) {
        this.teamID = teamID;
        this.senderID = senderID;
        this.teamName = teamName;
        this.currentInterest = currentInterest;
    }

    public String getTeamID() {
        return teamID;
    }

    public String getSenderID() {
        return senderID;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getCurrentInterest() {
        return currentInterest;
    }
}
