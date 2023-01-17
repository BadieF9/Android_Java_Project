package com.example.badiefarzandiassignment2.data.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Story {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String title;
    private String description;
    private String readingTime;
    private String age;
    private long userId;
    private String publishedDate;
    private boolean isFavorite;
    private String photoPath;

    public Story(long id, String title, String description, String age, long userId, String publishedDate, String photoPath, boolean isFavorite) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.age = age;
        float readingTime = (float) this.description.split(" ").length / 200;
        int readingTimeMin = (int) readingTime;
        float readingTimeSec = (readingTime - readingTimeMin) * 60;
        this.readingTime = readingTimeMin + "m, " + (int) readingTimeSec + "s";
        this.userId = userId;
        this.isFavorite = isFavorite;
        this.photoPath = photoPath;
        this.publishedDate = publishedDate;
    }

    @Ignore
    public Story(String title, String description, String age, long userId, String publishedDate, String photoPath, boolean isFavorite) {
        this.title = title;
        this.description = description;
        this.age = age;
        float readingTime = (float) this.description.split(" ").length / 200;
        int readingTimeMin = (int) readingTime;
        float readingTimeSec = (readingTime - readingTimeMin) * 60;
        this.readingTime = readingTimeMin + "m, " + (int) readingTimeSec + "s";
        this.userId = userId;
        this.isFavorite = isFavorite;
        this.photoPath = photoPath;
        this.publishedDate = publishedDate;
    }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getReadingTime() {
        return readingTime;
    }

    public void setReadingTime(String publishedDate) {
        this.readingTime = publishedDate;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) { this.userId = userId; }

    public boolean getIsFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    @Override
    public String toString() {
        return "Story{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", readingTime='" + readingTime + '\'' +
                ", age='" + age + '\'' +
                ", userId=" + userId +
                ", publishedDate='" + publishedDate + '\'' +
                ", photoPath='" + photoPath + '\'' +
                ", isFavorite=" + isFavorite +
                '}';
    }
}
