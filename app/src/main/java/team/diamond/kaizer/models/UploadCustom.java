package team.diamond.kaizer.models;

import com.google.firebase.database.Exclude;

public class UploadCustom {
    private  String mName;
    private  String mImageUrl;
    private String mKey;

    public UploadCustom(){
        //empty constructor needed
    }

    public UploadCustom(String name, String imageUrl){
        if (name.trim().equals("")){
            name = " ";
        }

        mName = name;
        mImageUrl = imageUrl;

    }

    public String getName(){
        return mName;
    }

    public void setName(String name){
        mName = name;
    }  //  где оно используетися ?

    public String getImageUrl(){
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl){
        mImageUrl = imageUrl;
    }

    @Exclude
    public String getKey(){
        return mKey;
    }

    @Exclude
    public void setKey(String key)    {
        mKey = key;
    }

}
