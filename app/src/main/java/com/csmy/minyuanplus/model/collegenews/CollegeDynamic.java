package com.csmy.minyuanplus.model.collegenews;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 院部动态实体类
 * Created by Zero on 16/7/23.
 */
public class CollegeDynamic extends DataSupport implements Serializable{
    //新闻ID
    private int contentID;
    //新闻作者
    private String contentAuthor;
    //新闻标题
    private String contentTitle;
    //发表时间
    private String submitTime;

    @Override
    public String toString() {
        return "CollegeDynamic{" +
                "contentID=" + contentID +
                ", contentAuthor='" + contentAuthor + '\'' +
                ", contentTitle='" + contentTitle + '\'' +
                ", submitTime='" + submitTime + '\'' +
                '}';
    }

    public int getContentID() {
        return contentID;
    }

    public void setContentID(int contentID) {
        this.contentID = contentID;
    }

    public String getContentAuthor() {
        return contentAuthor;
    }

    public void setContentAuthor(String contentAuthor) {
        this.contentAuthor = contentAuthor;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public String getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(String submitTime) {
        this.submitTime = submitTime;
    }
}
