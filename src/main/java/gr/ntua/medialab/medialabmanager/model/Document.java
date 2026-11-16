package gr.ntua.medialab.medialabmanager.model;

import java.util.List;
import java.util.ArrayList;

public class Document {
    private int id;
    private String title;
    private String author; // Το username του συγγραφέα
    private String category;
    private String creationDate;

    // Η τρέχουσα έκδοση
    private Version currentVersion;

    // Λίστα με τις παλιές εκδόσεις
    private List<Version> previousVersions = new ArrayList<>();


    public Document() {}


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCreationDate() { return creationDate; }
    public void setCreationDate(String creationDate) { this.creationDate = creationDate; }

    public Version getCurrentVersion() { return currentVersion; }
    public void setCurrentVersion(Version currentVersion) { this.currentVersion = currentVersion; }

    public List<Version> getPreviousVersions() { return previousVersions; }
    public void setPreviousVersions(List<Version> previousVersions) { this.previousVersions = previousVersions; }


    public static class Version {
        private int versionCode;
        private String text;
        private String date;

        public Version() {} // Κενός κατασκευαστής

        public Version(int versionCode, String text, String date) {
            this.versionCode = versionCode;
            this.text = text;
            this.date = date;
        }

        public int getVersionCode() { return versionCode; }
        public void setVersionCode(int versionCode) { this.versionCode = versionCode; }

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
    }
}
