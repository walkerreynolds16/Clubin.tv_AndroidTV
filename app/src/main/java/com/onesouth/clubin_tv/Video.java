package com.onesouth.clubin_tv;


public class Video {
    private String videoTitle;
    private String videoId;
    private String channelName;
    private int duration;
    private int viewCount;
    private String memberName;

    public String getVideoTitle() { return videoTitle; }
    public String getVideoId() { return videoId; }
    public String getChannelName() { return channelName; }
    public String getMemberName() { return memberName; }
    public int getDuration() {return duration;}
    public int getViewCount() {return viewCount;}


    public void setDuration(int duration) {this.duration = duration;}
    public void setViewCount(int viewCount) {this.viewCount = viewCount;    }
    public void setVideoTitle (String videoTitle) { this.videoTitle = videoTitle; }
    public void setVideoId (String videoId) { this.videoId = videoId; }
    public void setChannelName (String channelName) { this.channelName = channelName; }
    public void setMemberName (String memberName) { this.memberName = memberName; }

    public String toString() {
        return String.format("videoTitle: %s, videoId: %s, channelName: %s, memberName: %s", videoTitle, videoId, channelName, memberName);
    }
}
