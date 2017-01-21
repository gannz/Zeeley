package com.zeeley;

/**
 * Created by gannu on 01-09-2016.
 */
public class unsendMessage {
    String message, pk;

    public unsendMessage(String pk, String message) {
        this.message = message;
        this.pk = pk;
    }

    public String getMessage() {
        return message;
    }

    public String getPk() {
        return pk;
    }
}
