package team.diamond.kaizer.models;

public class ModelStoryFree {
    //используется то что добавляли при публикации    TrueStoryAdd
    // имненно это написано в child  в Firebase
    String name, StoryTitle, StoryText, StoryImage, StoryTime, profile_pic, keyFreeStory, customVer1, StoryLike, StoryView, inkognito_id, uid;

    public ModelStoryFree() {
    }

    public ModelStoryFree(String name, String storyTitle, String storyText, String storyImage, String storyTime, String profile_pic, String keyFreeStory, String customVer1, String storyLike, String storyView, String inkognito_id, String uid) {
        this.name = name;
        StoryTitle = storyTitle;
        StoryText = storyText;
        StoryImage = storyImage;
        StoryTime = storyTime;
        this.profile_pic = profile_pic;
        this.keyFreeStory = keyFreeStory;
        this.customVer1 = customVer1;
        StoryLike = storyLike;
        StoryView = storyView;
        this.inkognito_id = inkognito_id;
        this.uid = uid;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStoryTitle() {
        return StoryTitle;
    }

    public void setStoryTitle(String storyTitle) {
        StoryTitle = storyTitle;
    }

    public String getStoryText() {
        return StoryText;
    }

    public void setStoryText(String storyText) {
        StoryText = storyText;
    }

    public String getStoryImage() {
        return StoryImage;
    }

    public void setStoryImage(String storyImage) {
        StoryImage = storyImage;
    }

    public String getStoryTime() {
        return StoryTime;
    }

    public void setStoryTime(String storyTime) {
        StoryTime = storyTime;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getKeyFreeStory() {
        return keyFreeStory;
    }

    public void setKeyFreeStory(String keyFreeStory) {
        this.keyFreeStory = keyFreeStory;
    }

    public String getCustomVer1() {
        return customVer1;
    }

    public void setCustomVer1(String customVer1) {
        this.customVer1 = customVer1;
    }

    public String getStoryLike() {
        return StoryLike;
    }

    public void setStoryLike(String storyLike) {
        StoryLike = storyLike;
    }

    public String getStoryView() {
        return StoryView;
    }

    public void setStoryView(String storyView) {
        StoryView = storyView;
    }

    public String getInkognito_id() {
        return inkognito_id;
    }

    public void setInkognito_id(String inkognito_id) {
        this.inkognito_id = inkognito_id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}