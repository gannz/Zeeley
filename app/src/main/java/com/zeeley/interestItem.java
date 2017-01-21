package com.zeeley;

/**
 * Created by gannu on 01-08-2016.
 */
public class interestItem {
    public String itemName;
    public int image;
    public interestItem(String itemName,int image){
        this.image=image;
        this.itemName=itemName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
