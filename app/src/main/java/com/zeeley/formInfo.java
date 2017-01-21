package com.zeeley;

/**
 * Created by gannu on 03-10-2016.
 */
public class formInfo {
    String url,teamId,senderId;

    public formInfo(String url, String teamId, String senderId) {

        this.url = url;
        this.teamId = teamId;
        this.senderId = senderId;
    }

    public String getUrl() {
        return url;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getSenderId() {
        return senderId;
    }
}
