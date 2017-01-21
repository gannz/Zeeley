package com.zeeley;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by gannu on 25-08-2016.
 */
public class setChatUserSettings {
    public static boolean updateData=false;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    public static final String privacy_url = "http://zeeley.com/android_privacy";

    public static boolean setCanInvite(boolean canInvite,dataobject user,Context context,boolean fromBlockedlist) {
        String pk=user.getId();
        try {
            URL url = new URL(privacy_url + (canInvite?"?unblock_invite=":"?block_invite=") + pk);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                settingsObj settings=user.getSettings();
                settings.setCanInvite(canInvite);
                if(fromBlockedlist){
                    updateData=true;
                }
                sharedPreferences = context.getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
                String list = sharedPreferences.getString(constants.BLOCKED_CONTACTS, constants.DEFAULT);
                ArrayList<blockedItem> blockedItems = new ArrayList<blockedItem>();
                Gson gson = new Gson();
                if (canInvite) {// user exist in blockedlist.. so remove him
                    if (!list.equals(constants.DEFAULT)) {
                        blockedItem[] items = gson.fromJson(list, blockedItem[].class);
                        blockedItems.addAll(Arrays.asList(items));
                        for(blockedItem item:blockedItems){
                            if(item.getId()==Integer.parseInt(user.getId())){
                                ArrayList<String> blockedFrom = item.getBlockedFrom();
                                if( blockedFrom.remove(constants.INVITATION)){
                                    editor=sharedPreferences.edit();
                                    editor.remove(constants.BLOCKED_CONTACTS);
                                    editor.putString(constants.BLOCKED_CONTACTS,gson.toJson(blockedItems));
                                    editor.apply();
                                }

                                break;
                            }
                        }
                    }
                }
                else{// user may or maynot exist
                    if (list.equals(constants.DEFAULT)) {
                        blockedItem item1 = new blockedItem(user.getName(), /*user.getProfile_pic()*/null,Integer.parseInt(user.getId()));
                        ArrayList<String> blockedFrom = new ArrayList<>();
                        blockedFrom.add(constants.INVITATION);
                        item1.setBlockedFrom(blockedFrom);
                        blockedItems.add(item1);
                        String json = gson.toJson(blockedItems);
                        editor.putString(constants.BLOCKED_CONTACTS, json);
                        editor.apply();
                    }
                    else {
                        blockedItem[] items = gson.fromJson(list, blockedItem[].class);
                        blockedItems.clear();
                        blockedItems.addAll(Arrays.asList(items));
                        boolean found = false;
                        for (blockedItem item : blockedItems) {
                            if (item.getId() == Integer.parseInt(user.getId())) {
                                found = true;
                                ArrayList<String> blockedFrom = item.getBlockedFrom();
                                if(!blockedFrom.contains(constants.INVITATION))
                                    blockedFrom.add(constants.INVITATION);
                                break;
                            }
                        }
                        if (!found) {
                            blockedItem item1 = new blockedItem(user.getName(), null, Integer.parseInt(user.getId()));
                            ArrayList<String> blockedFrom = new ArrayList<>();
                            blockedFrom.add(constants.INVITATION);
                            item1.setBlockedFrom(blockedFrom);
                            blockedItems.add(item1);
                        }
                        editor.remove(constants.BLOCKED_CONTACTS);
                        editor.putString(constants.BLOCKED_CONTACTS, gson.toJson(blockedItems));
                        editor.apply();
                    }
                }
                return true;

            }else{
                Toast.makeText(context,connection.getResponseMessage(),Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public static boolean setCanMessage(boolean canMessage,dataobject user,Context context,boolean fromBlockedlist) {
        String pk=user.getId();
        try {
            URL url = new URL(privacy_url + (canMessage?"?unblock_msg=":"?block_msg=") + pk);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                settingsObj settings=user.getSettings();
                settings.setCanMessage(canMessage);
                if(fromBlockedlist){
                    updateData=true;
                }
                sharedPreferences = context.getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
                String list = sharedPreferences.getString(constants.BLOCKED_CONTACTS, constants.DEFAULT);
                ArrayList<blockedItem> blockedItems = new ArrayList<blockedItem>();
                Gson gson = new Gson();
                if (canMessage) {// user exist in blockedlist.. so remove him
                    if (!list.equals(constants.DEFAULT)) {
                        blockedItem[] items = gson.fromJson(list, blockedItem[].class);
                        blockedItems.addAll(Arrays.asList(items));
                        for(blockedItem item:blockedItems){
                            if(item.getId()==Integer.parseInt(user.getId())){
                                ArrayList<String> blockedFrom = item.getBlockedFrom();
                                if( blockedFrom.remove(constants.MESSAGING)){
                                    editor=sharedPreferences.edit();
                                    editor.remove(constants.BLOCKED_CONTACTS);
                                    editor.putString(constants.BLOCKED_CONTACTS,gson.toJson(blockedItems));
                                    editor.apply();
                                }

                                break;
                            }
                        }
                    }
                }
                else{// user may or maynot exist
                    if (list.equals(constants.DEFAULT)) {
                        blockedItem item1 = new blockedItem(user.getName(), /*user.getProfile_pic()*/null,Integer.parseInt(user.getId()));
                        ArrayList<String> blockedFrom = new ArrayList<>();
                        blockedFrom.add(constants.MESSAGING);
                        item1.setBlockedFrom(blockedFrom);
                        blockedItems.add(item1);
                        String json = gson.toJson(blockedItems);
                        editor.putString(constants.BLOCKED_CONTACTS, json);
                        editor.apply();
                    }
                    else {
                        blockedItem[] items = gson.fromJson(list, blockedItem[].class);
                        blockedItems.clear();
                        blockedItems.addAll(Arrays.asList(items));
                        boolean found = false;
                        for (blockedItem item : blockedItems) {
                            if (item.getId() == Integer.parseInt(user.getId())) {
                                found = true;
                                ArrayList<String> blockedFrom = item.getBlockedFrom();
                                if(!blockedFrom.contains(constants.MESSAGING))
                                    blockedFrom.add(constants.MESSAGING);
                                break;
                            }
                        }
                        if (!found) {
                            blockedItem item1 = new blockedItem(user.getName(), null, Integer.parseInt(user.getId()));
                            ArrayList<String> blockedFrom = new ArrayList<>();
                            blockedFrom.add(constants.MESSAGING);
                            item1.setBlockedFrom(blockedFrom);
                            blockedItems.add(item1);
                        }
                        editor.remove(constants.BLOCKED_CONTACTS);
                        editor.putString(constants.BLOCKED_CONTACTS, gson.toJson(blockedItems));
                        editor.apply();
                    }
                }
                return true;
            }else{
                Toast.makeText(context,connection.getResponseMessage(),Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    public static boolean setCanViewProfile(boolean canViewProfile,dataobject user,Context context,boolean fromBlockedlist) {
        String pk=user.getId();
        try {
            URL url = new URL(privacy_url + (canViewProfile?"?unblock_prof=":"?block_prof=") + pk);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                settingsObj settings=user.getSettings();
               settings.setCanViewProfile(canViewProfile);
                if(fromBlockedlist){
                    updateData=true;
                }
                sharedPreferences = context.getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
                String list = sharedPreferences.getString(constants.BLOCKED_CONTACTS, constants.DEFAULT);
                ArrayList<blockedItem> blockedItems = new ArrayList<blockedItem>();
                Gson gson = new Gson();
                if (canViewProfile) {// user exist in blockedlist.. so remove him
                    if (!list.equals(constants.DEFAULT)) {
                        blockedItem[] items = gson.fromJson(list, blockedItem[].class);
                        blockedItems.addAll(Arrays.asList(items));
                        for(blockedItem item:blockedItems){
                            if(item.getId()==Integer.parseInt(user.getId())){
                                ArrayList<String> blockedFrom = item.getBlockedFrom();
                                if( blockedFrom.remove(constants.VIEWINNG_PROFILE)){
                                    editor=sharedPreferences.edit();
                                    editor.remove(constants.BLOCKED_CONTACTS);
                                    editor.putString(constants.BLOCKED_CONTACTS,gson.toJson(blockedItems));
                                    editor.apply();
                                }

                                break;
                            }
                        }
                    }
                }
                else{// user may or maynot exist
                    if (list.equals(constants.DEFAULT)) {
                        blockedItem item1 = new blockedItem(user.getName(),null,Integer.parseInt(user.getId()));
                        ArrayList<String> blockedFrom = new ArrayList<>();
                        blockedFrom.add(constants.VIEWINNG_PROFILE);
                        item1.setBlockedFrom(blockedFrom);
                        blockedItems.add(item1);
                        String json = gson.toJson(blockedItems);
                        editor.putString(constants.BLOCKED_CONTACTS, json);
                        editor.apply();
                    }
                    else {
                        blockedItem[] items = gson.fromJson(list, blockedItem[].class);
                        blockedItems.clear();
                        blockedItems.addAll(Arrays.asList(items));
                        boolean found = false;
                        for (blockedItem item : blockedItems) {
                            if (item.getId() == Integer.parseInt(user.getId())) {
                                found = true;
                                ArrayList<String> blockedFrom = item.getBlockedFrom();
                                if(!blockedFrom.contains(constants.VIEWINNG_PROFILE))
                                    blockedFrom.add(constants.VIEWINNG_PROFILE);
                                break;
                            }
                        }
                        if (!found) {
                            blockedItem item1 = new blockedItem(user.getName(), null, Integer.parseInt(user.getId()));
                            ArrayList<String> blockedFrom = new ArrayList<>();
                            blockedFrom.add(constants.VIEWINNG_PROFILE);
                            item1.setBlockedFrom(blockedFrom);
                            blockedItems.add(item1);
                        }
                        editor.remove(constants.BLOCKED_CONTACTS);
                        editor.putString(constants.BLOCKED_CONTACTS, gson.toJson(blockedItems));
                        editor.apply();
                    }
                }
                return true;
            }else{
                Toast.makeText(context,connection.getResponseMessage(),Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public static boolean setCanAccessLocation(boolean canAccessLocation,dataobject user,Context context,boolean fromBlockedlist) {
        String pk=user.getId();
        try {
            URL url = new URL(privacy_url + (canAccessLocation?"?unblock_location=":"?block_location=") + pk);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                settingsObj settings=user.getSettings();
                settings.setCanAccessLocation(canAccessLocation);
                if(fromBlockedlist){
                    updateData=true;
                }
                sharedPreferences = context.getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
                String list = sharedPreferences.getString(constants.BLOCKED_CONTACTS, constants.DEFAULT);
                ArrayList<blockedItem> blockedItems = new ArrayList<blockedItem>();
                Gson gson = new Gson();
                if (canAccessLocation) {// user exist in blockedlist.. so remove him
                    if (!list.equals(constants.DEFAULT)) {
                        blockedItem[] items = gson.fromJson(list, blockedItem[].class);
                        blockedItems.addAll(Arrays.asList(items));
                        for(blockedItem item:blockedItems){
                            if(item.getId()==Integer.parseInt(user.getId())){
                                ArrayList<String> blockedFrom = item.getBlockedFrom();
                                if( blockedFrom.remove(constants.Accesing_location)){
                                    editor=sharedPreferences.edit();
                                    editor.remove(constants.BLOCKED_CONTACTS);
                                    editor.putString(constants.BLOCKED_CONTACTS,gson.toJson(blockedItems));
                                    editor.apply();
                                }

                                break;
                            }
                        }
                    }
                }
                else{// user may or maynot exist
                    if (list.equals(constants.DEFAULT)) {
                        blockedItem item1 = new blockedItem(user.getName(), null,Integer.parseInt(user.getId()));
                        ArrayList<String> blockedFrom = new ArrayList<>();
                        blockedFrom.add(constants.Accesing_location);
                        item1.setBlockedFrom(blockedFrom);
                        blockedItems.add(item1);
                        String json = gson.toJson(blockedItems);
                        editor.putString(constants.BLOCKED_CONTACTS, json);
                        editor.apply();
                    }
                    else {
                        blockedItem[] items = gson.fromJson(list, blockedItem[].class);
                        blockedItems.clear();
                        blockedItems.addAll(Arrays.asList(items));
                        boolean found = false;
                        for (blockedItem item : blockedItems) {
                            if (item.getId() == Integer.parseInt(user.getId())) {
                                found = true;
                                ArrayList<String> blockedFrom = item.getBlockedFrom();
                                if(!blockedFrom.contains(constants.Accesing_location))
                                    blockedFrom.add(constants.Accesing_location);
                                break;
                            }
                        }
                        if (!found) {
                            blockedItem item1 = new blockedItem(user.getName(), null, Integer.parseInt(user.getId()));
                            ArrayList<String> blockedFrom = new ArrayList<>();
                            blockedFrom.add(constants.Accesing_location);
                            item1.setBlockedFrom(blockedFrom);
                            blockedItems.add(item1);
                        }
                        editor.remove(constants.BLOCKED_CONTACTS);
                        editor.putString(constants.BLOCKED_CONTACTS, gson.toJson(blockedItems));
                        editor.apply();
                    }
                }
                return true;
            }else{
                Toast.makeText(context,connection.getResponseMessage(),Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return false;
    }


}
