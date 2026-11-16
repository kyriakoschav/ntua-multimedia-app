package gr.ntua.medialab.medialabmanager.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.ntua.medialab.medialabmanager.model.Category;
import gr.ntua.medialab.medialabmanager.model.Document;
import gr.ntua.medialab.medialabmanager.model.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonManager {


    private static final String USERS_FILE_PATH = "medialab/users.json";
    private static final String DOCS_FILE_PATH = "medialab/documents.json";
    private static final String CATS_FILE_PATH = "medialab/categories.json"; // <--- ΝΕΟ ΑΡΧΕΙΟ

    private ObjectMapper mapper = new ObjectMapper();



    public List<User> loadUsers() {
        try {
            File file = new File(USERS_FILE_PATH);
            if (!file.exists()) return new ArrayList<>();
            return mapper.readValue(file, new TypeReference<List<User>>(){});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void saveUsers(List<User> users) {
        try {
            mapper.writeValue(new File(USERS_FILE_PATH), users);
        } catch (IOException e) { e.printStackTrace(); }
    }



    public List<Category> loadCategories() {
        try {
            File file = new File(CATS_FILE_PATH);
            if (!file.exists()) return new ArrayList<>();
            return mapper.readValue(file, new TypeReference<List<Category>>(){});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void saveCategories(List<Category> categories) {
        try {
            mapper.writeValue(new File(CATS_FILE_PATH), categories);
        } catch (IOException e) { e.printStackTrace(); }
    }



    public List<Document> loadDocuments() {
        try {
            File file = new File(DOCS_FILE_PATH);
            if (!file.exists()) return new ArrayList<>();
            return mapper.readValue(file, new TypeReference<List<Document>>(){});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void saveDocuments(List<Document> documents) {
        try {
            mapper.writeValue(new File(DOCS_FILE_PATH), documents);
        } catch (IOException e) { e.printStackTrace(); }
    }
}