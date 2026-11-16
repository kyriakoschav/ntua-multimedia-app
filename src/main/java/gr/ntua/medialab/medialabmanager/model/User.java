package gr.ntua.medialab.medialabmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String username;
    private String password;
    private String role; // "admin", "author", "user"
    private String fullName;


    private Map<String, Integer> followedDocs;
    private List<String> assignedCategories;

    public User() {
        this.followedDocs = new HashMap<>();
        this.assignedCategories = new ArrayList<>();
    }

    public User(String username, String password, String role, String fullName, List<String> assignedCategories) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.assignedCategories = assignedCategories != null ? assignedCategories : new ArrayList<>();
        this.followedDocs = new HashMap<>();
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Map<String, Integer> getFollowedDocs() { return followedDocs; }
    public void setFollowedDocs(Map<String, Integer> followedDocs) { this.followedDocs = followedDocs; }

    public List<String> getAssignedCategories() { return assignedCategories; }
    public void setAssignedCategories(List<String> assignedCategories) { this.assignedCategories = assignedCategories; }
}