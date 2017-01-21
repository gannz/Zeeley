package com.zeeley;

/**
 * Created by gannu on 01-08-2016.
 */
public class item {
    String title;
    boolean isSection;
    int image;
    int id;
    public item(String title,boolean isSection,int id){
        this.title=title;
        this.isSection=isSection;
        this.id=id;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSection() {
        return isSection;
    }

    public void setIsSection(boolean isSection) {
        this.isSection = isSection;
    }
}
