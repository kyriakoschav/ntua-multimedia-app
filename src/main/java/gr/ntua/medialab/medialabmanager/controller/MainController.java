package gr.ntua.medialab.medialabmanager.controller;

import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import gr.ntua.medialab.medialabmanager.model.Category;
import gr.ntua.medialab.medialabmanager.model.Document;
import gr.ntua.medialab.medialabmanager.model.User;
import gr.ntua.medialab.medialabmanager.service.JsonManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javafx.scene.control.CheckBox;

public class MainController {


    @FXML private Label totalCategoriesLabel;
    @FXML private Label totalDocsLabel;
    @FXML private Label myDocsLabel;
    @FXML private Label usernameLabel;
    @FXML private Label roleLabel;


    @FXML private TabPane mainTabPane;
    @FXML private Tab usersTab;
    @FXML private Tab categoriesTab;
    @FXML private Tab documentsTab;


    @FXML private TextField txtSearchQuery;
    @FXML private TableView<Document> searchTable;
    @FXML private TableColumn<Document, String> colSearchTitle;
    @FXML private TableColumn<Document, String> colSearchAuthor;
    @FXML private TableColumn<Document, String> colSearchCategory;
    @FXML private TableColumn<Document, String> colSearchDate;
    @FXML private TableColumn<Document, String> colSearchVersion; // <--- ΑΥΤΟ ΠΡΟΣΘΕΤΕΙΣ

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, String> colFullname;
    @FXML private TextField txtNewUsername;
    @FXML private TextField txtNewPassword;
    @FXML private TextField txtNewFullname;
    @FXML private ComboBox<String> cmbNewRole;
    @FXML private TextField txtNewUserCategories;


    @FXML private TableView<Category> categoriesTable;
    @FXML private TableColumn<Category, String> colCategoryName;
    @FXML private TextField txtNewCategoryName;


    @FXML private TableView<Document> docsTable;
    @FXML private TableColumn<Document, String> colDocTitle;
    @FXML private TableColumn<Document, String> colDocCategory;
    @FXML private TextField txtDocTitle;
    @FXML private TextField txtDocCategory;
    @FXML private TextArea txtDocContent;
    @FXML private Label lblVersionInfo;
    @FXML private ToggleButton btnFollow;
    @FXML private ComboBox<Document.Version> cmbVersions;
    @FXML private HBox versionBox;
    @FXML private CheckBox chkShowFollowed;


    private User loggedInUser;

    // Η "Μνήμη" της εφαρμογής (Global Lists)
    private List<User> globalUsers = new ArrayList<>();
    private List<Category> globalCategories = new ArrayList<>();
    private List<Document> globalDocs = new ArrayList<>();
    private static boolean hookAdded = false;

    private ObservableList<User> usersListObservable = FXCollections.observableArrayList();
    private ObservableList<Category> categoriesListObservable = FXCollections.observableArrayList();
    private ObservableList<Document> docsListObservable = FXCollections.observableArrayList();
    private ObservableList<Document> searchListObservable = FXCollections.observableArrayList();



    @FXML
    public void initialize() {
        // Setup Users Table
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colFullname.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        usersTable.setItems(usersListObservable);

        cmbNewRole.getItems().addAll("admin", "author", "user");

        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                txtNewUsername.setText(newSel.getUsername());
                txtNewPassword.setText(newSel.getPassword());
                txtNewFullname.setText(newSel.getFullName());
                cmbNewRole.setValue(newSel.getRole());


                if (newSel.getAssignedCategories() != null) {
                    txtNewUserCategories.setText(String.join(", ", newSel.getAssignedCategories()));
                } else {
                    txtNewUserCategories.setText("");
                }
            }
        });

        colCategoryName.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoriesTable.setItems(categoriesListObservable);


        colDocTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDocCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        docsTable.setItems(docsListObservable);


        colSearchTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colSearchAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colSearchCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colSearchDate.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        colSearchVersion.setCellValueFactory(cellData -> {
            Document doc = cellData.getValue();
            if (doc.getCurrentVersion() != null) {
                return new javafx.beans.property.SimpleStringProperty("v" + doc.getCurrentVersion().getVersionCode());
            } else {
                return new javafx.beans.property.SimpleStringProperty("-");
            }
        });
        searchTable.setItems(searchListObservable);


        docsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showDocumentDetails(newSelection);
            }
        });

        cmbVersions.setConverter(new StringConverter<Document.Version>() {
            @Override
            public String toString(Document.Version v) {
                if (v == null) return "";
                return "v" + v.getVersionCode() + " (" + v.getDate() + ")";
            }
            @Override
            public Document.Version fromString(String string) { return null; }
        });

        cmbVersions.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                txtDocContent.setText(newV.getText());
                lblVersionInfo.setText("Προβολή: Έκδοση " + newV.getVersionCode());
                txtDocContent.setEditable(false);
            }
        });


        JsonManager manager = new JsonManager();
        globalUsers = manager.loadUsers();
        globalCategories = manager.loadCategories();
        globalDocs = manager.loadDocuments();


        if (!hookAdded) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Εντοπίστηκε τερματισμός. Αποθήκευση των δεδομένων...");
                saveEverythingToJson();
            }));
            hookAdded = true;
        }
    }


    // LOGIN SETUP

    public void setLoggedInUser(User user) {

        for (User u : globalUsers) {
            if (u.getUsername().equals(user.getUsername())) {
                this.loggedInUser = u;
                break;
            }
        }

        usernameLabel.setText(loggedInUser.getFullName());
        roleLabel.setText(loggedInUser.getRole().toUpperCase());

        loadDocsTable();

        if (!loggedInUser.getRole().equalsIgnoreCase("admin")) {
            mainTabPane.getTabs().remove(usersTab);
            mainTabPane.getTabs().remove(categoriesTab);
        } else {
            loadUsersTable();
            loadCategoriesTable();
        }
        refreshStats();
        showNotifications();
    }


    // SEARCH LOGIC

    @FXML
    private void searchDocuments() {
        String query = txtSearchQuery.getText().toLowerCase().trim();
        searchListObservable.clear();

        if (query.isEmpty()) {
            searchListObservable.addAll(docsListObservable);
            return;
        }

        for (Document doc : docsListObservable) {
            boolean matchesTitle = doc.getTitle().toLowerCase().contains(query);
            boolean matchesAuthor = doc.getAuthor().toLowerCase().contains(query);
            boolean matchesCategory = doc.getCategory().toLowerCase().contains(query);

            if (matchesTitle || matchesAuthor || matchesCategory) {
                searchListObservable.add(doc);
            }
        }
    }


    // USERS LOGIC

    private void loadUsersTable() {
        usersListObservable.setAll(globalUsers);
    }

    @FXML
    private void addUser() {
        String u = txtNewUsername.getText();
        String p = txtNewPassword.getText();
        String f = txtNewFullname.getText();

        String r = cmbNewRole.getValue() == null ? "" : cmbNewRole.getValue();
        String catsStr = txtNewUserCategories.getText();

        if (u.isEmpty() || p.isEmpty() || r.isEmpty() || catsStr.isEmpty()) {
            showAlert("Σφάλμα", "Συμπληρώστε όλα τα πεδία (και τις κατηγορίες)!");
            return;
        }

        List<String> validCategories = new ArrayList<>();
        for (Category c : globalCategories) {
            validCategories.add(c.getName().toLowerCase());
        }

        List<String> assignedCats = new ArrayList<>();
        String[] parts = catsStr.split(",");

        for (String part : parts) {
            String catName = part.trim().toLowerCase();
            if (!validCategories.contains(catName)) {
                showAlert("Σφάλμα", "Η κατηγορία '" + part.trim() + "' δεν υπάρχει!");
                return;
            }
            assignedCats.add(catName);
        }

        User newUser = new User(u, p, r, f, assignedCats);
        globalUsers.add(newUser);
        usersListObservable.add(newUser);

        txtNewUsername.clear(); txtNewPassword.clear(); txtNewFullname.clear();
        cmbNewRole.setValue(null); txtNewUserCategories.clear();
        showAlert("Επιτυχία", "Ο χρήστης προστέθηκε επιτυχώς!");
    }

    @FXML
    private void deleteUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) return;
        if (selectedUser.getUsername().equals(loggedInUser.getUsername())) {
            showAlert("Σφάλμα", "Δεν μπορείτε να διαγράψετε τον εαυτό σας!"); return;
        }

        usersListObservable.remove(selectedUser);
        globalUsers.remove(selectedUser);
    }

    @FXML
    private void editUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Προσοχή", "Επιλέξτε πρώτα έναν χρήστη από τον πίνακα!");
            return;
        }

        String u = txtNewUsername.getText().trim();
        String p = txtNewPassword.getText().trim();
        String f = txtNewFullname.getText().trim();
        // Διαβάζουμε την επιλογή από το Dropdown
        String r = cmbNewRole.getValue() == null ? "" : cmbNewRole.getValue();
        String catsStr = txtNewUserCategories.getText().trim();

        if (u.isEmpty() || p.isEmpty() || r.isEmpty() || f.isEmpty()) {
            showAlert("Σφάλμα", "Συμπληρώστε όλα τα βασικά πεδία!");
            return;
        }


        List<String> validCategories = new ArrayList<>();
        for (Category c : globalCategories) {
            validCategories.add(c.getName().toLowerCase());
        }

        List<String> assignedCats = new ArrayList<>();
        if (!catsStr.isEmpty()) {
            String[] parts = catsStr.split(",");
            for (String part : parts) {
                String catName = part.trim().toLowerCase();
                if (!validCategories.contains(catName)) {
                    showAlert("Σφάλμα", "Η κατηγορία '" + part.trim() + "' δεν υπάρχει!");
                    return;
                }
                assignedCats.add(catName);
            }
        }


        selectedUser.setUsername(u);
        selectedUser.setPassword(p);
        selectedUser.setFullName(f);
        selectedUser.setRole(r);
        selectedUser.setAssignedCategories(assignedCats);

        usersTable.refresh();

        txtNewUsername.clear(); txtNewPassword.clear(); txtNewFullname.clear();
        cmbNewRole.setValue(null); txtNewUserCategories.clear();
        usersTable.getSelectionModel().clearSelection();

        showAlert("Επιτυχία", "Τα στοιχεία του χρήστη τροποποιήθηκαν!");
    }

    // CATEGORIES LOGIC

    private void loadCategoriesTable() {
        categoriesListObservable.setAll(globalCategories);
        totalCategoriesLabel.setText(String.valueOf(globalCategories.size()));
    }

    @FXML
    private void addCategory() {
        String name = txtNewCategoryName.getText();
        if (name.isEmpty()) return;
        for (Category c : globalCategories) {
            if (c.getName().equalsIgnoreCase(name)) {
                showAlert("Σφάλμα", "Η κατηγορία υπάρχει ήδη!"); return;
            }
        }
        Category newCat = new Category(name);
        categoriesListObservable.add(newCat);
        globalCategories.add(newCat);

        txtNewCategoryName.clear();
        totalCategoriesLabel.setText(String.valueOf(globalCategories.size()));
    }

    @FXML
    private void editCategory() {
        Category selected = categoriesTable.getSelectionModel().getSelectedItem();
        String newName = txtNewCategoryName.getText().trim();

        if (selected == null || newName.isEmpty()) return;

        for (Category c : globalCategories) {
            if (c.getName().equalsIgnoreCase(newName) && c != selected) {
                showAlert("Σφάλμα", "Υπάρχει ήδη κατηγορία με αυτό το όνομα!");
                return;
            }
        }

        String oldName = selected.getName().toLowerCase();
        String finalNewName = newName.toLowerCase();


        selected.setName(newName);
        categoriesTable.refresh();


        for (Document doc : globalDocs) {
            if (doc.getCategory().toLowerCase().equals(oldName)) {
                doc.setCategory(finalNewName);
            }
        }
        docsTable.refresh();


        for (User u : globalUsers) {
            List<String> assigned = u.getAssignedCategories();
            if (assigned != null && assigned.contains(oldName)) {
                assigned.remove(oldName);
                if (!assigned.contains(finalNewName)) {
                    assigned.add(finalNewName);
                }
            }
        }

        txtNewCategoryName.clear();
        showAlert("Επιτυχία", "Η κατηγορία, τα έγγραφά της και τα δικαιώματα ενημερώθηκαν!");
    }

    @FXML
    private void deleteCategory() {
        Category selected = categoriesTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        String catName = selected.getName().toLowerCase();


        categoriesListObservable.remove(selected);
        globalCategories.remove(selected);
        totalCategoriesLabel.setText(String.valueOf(globalCategories.size()));


        List<Document> docsToRemove = new ArrayList<>();
        List<String> deletedDocIds = new ArrayList<>();

        for (Document doc : globalDocs) {
            if (doc.getCategory().toLowerCase().equals(catName)) {
                docsToRemove.add(doc);
                deletedDocIds.add(String.valueOf(doc.getId()));
            }
        }

        globalDocs.removeAll(docsToRemove);
        docsListObservable.removeAll(docsToRemove);



        if (!deletedDocIds.isEmpty()) {
            for (User u : globalUsers) {
                if (u.getFollowedDocs() != null) {

                    for (String docId : deletedDocIds) {
                        u.getFollowedDocs().remove(docId);
                    }
                }
            }
        }

        refreshStats();
        showAlert("Επιτυχία", "Η κατηγορία και τα έγγραφά της διαγράφηκαν!");
    }

    // DOCUMENTS LOGIC

    @FXML
    private void loadDocsTable() {
        docsListObservable.clear();

        for (Document doc : globalDocs) {
            boolean hasAccess = false;

            if (loggedInUser.getRole().equals("admin")) {
                hasAccess = true;
            } else {
                List<String> myCats = loggedInUser.getAssignedCategories();
                if (myCats != null && myCats.contains(doc.getCategory().toLowerCase())) {
                    hasAccess = true;
                }
            }

            if (hasAccess) {
                if (chkShowFollowed != null && chkShowFollowed.isSelected()) {

                    if (loggedInUser.getFollowedDocs() != null && loggedInUser.getFollowedDocs().containsKey(String.valueOf(doc.getId()))) {
                        docsListObservable.add(doc);
                    }
                } else {
                    docsListObservable.add(doc);
                }
            }
        }
        totalDocsLabel.setText(String.valueOf(globalDocs.size()));
    }

    private void showDocumentDetails(Document doc) {
        txtDocTitle.setText(doc.getTitle());
        txtDocCategory.setText(doc.getCategory());


        if (loggedInUser.getFollowedDocs() != null && loggedInUser.getFollowedDocs().containsKey(String.valueOf(doc.getId()))) {
            btnFollow.setSelected(true);
            btnFollow.setText("★ Following");
        } else {
            btnFollow.setSelected(false);
            btnFollow.setText("☆ Follow");
        }

        cmbVersions.getItems().clear();

        if (doc.getCurrentVersion() != null) {
            cmbVersions.getItems().add(doc.getCurrentVersion());
            cmbVersions.getSelectionModel().select(doc.getCurrentVersion());
            txtDocContent.setText(doc.getCurrentVersion().getText());
            txtDocContent.setEditable(true);
        }

        if (!loggedInUser.getRole().equals("user") && doc.getPreviousVersions() != null) {
            cmbVersions.getItems().addAll(doc.getPreviousVersions());
        }

        boolean canSeeVersions = !loggedInUser.getRole().equals("user");
        versionBox.setVisible(canSeeVersions);
        versionBox.setManaged(canSeeVersions);
    }

    @FXML
    private void createNewDoc() {
        docsTable.getSelectionModel().clearSelection();
        txtDocTitle.clear(); txtDocCategory.clear(); txtDocContent.clear();
        lblVersionInfo.setText("Δημιουργία Νέου...");
        txtDocTitle.requestFocus();
    }

    @FXML
    private void saveDocument() {
        if (loggedInUser.getRole().equals("user")) {
            showAlert("Απαγορεύεται", "Οι απλοί χρήστες δεν μπορούν να δημιουργήσουν έγγραφα.");
            return;
        }

        String title = txtDocTitle.getText().trim();
        String cat = txtDocCategory.getText().trim().toLowerCase();
        String text = txtDocContent.getText();

        if (title.isEmpty() || cat.isEmpty()) {
            showAlert("Σφάλμα", "Συμπληρώστε Τίτλο και Κατηγορία.");
            return;
        }

        boolean categoryExists = false;
        for (Category c : globalCategories) {
            if (c.getName().toLowerCase().equals(cat)) {
                categoryExists = true;
                break;
            }
        }

        if (!categoryExists) {
            showAlert("Σφάλμα", "Η κατηγορία '" + cat + "' δεν υπάρχει!");
            return;
        }

        if (loggedInUser.getRole().equals("author")) {
            if (!loggedInUser.getAssignedCategories().contains(cat)) {
                showAlert("Απαγορεύεται", "Δεν έχετε δικαίωμα στην κατηγορία '" + cat + "'.");
                return;
            }
        }

        Document selectedDoc = docsTable.getSelectionModel().getSelectedItem();

        if (selectedDoc == null) {
            Document newDoc = new Document();
            newDoc.setId((int) (System.currentTimeMillis() / 1000));
            newDoc.setTitle(title);
            newDoc.setCategory(cat);
            newDoc.setAuthor(loggedInUser.getUsername());
            newDoc.setCreationDate(LocalDate.now().toString());
            newDoc.setCurrentVersion(new Document.Version(1, text, LocalDate.now().toString()));

            docsListObservable.add(newDoc);
            globalDocs.add(newDoc);

        } else {
            if (!selectedDoc.getTitle().equals(title) || !selectedDoc.getCategory().toLowerCase().equals(cat)) {
                showAlert("Προσοχή", "Τίτλος και Κατηγορία δεν αλλάζουν.\nΑποθηκεύτηκε μόνο το κείμενο.");
                txtDocTitle.setText(selectedDoc.getTitle());
                txtDocCategory.setText(selectedDoc.getCategory());
            }

            Document.Version oldVersion = selectedDoc.getCurrentVersion();
            selectedDoc.getPreviousVersions().add(0, oldVersion);

            if (selectedDoc.getPreviousVersions().size() > 2) {
                selectedDoc.getPreviousVersions().remove(selectedDoc.getPreviousVersions().size() - 1);
            }

            selectedDoc.setCurrentVersion(new Document.Version(oldVersion.getVersionCode() + 1, text, LocalDate.now().toString()));
        }

        docsTable.refresh();
        refreshStats();
        showAlert("Επιτυχία", "Το έγγραφο αποθηκεύτηκε στη μνήμη!");
    }

    @FXML
    private void deleteDocument() {
        if (loggedInUser.getRole().equals("user")) return;

        Document selected = docsTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        String docId = String.valueOf(selected.getId());

        docsListObservable.remove(selected);
        globalDocs.remove(selected);

        for (User u : globalUsers) {
            if (u.getFollowedDocs() != null) {
                u.getFollowedDocs().remove(docId);
            }
        }

        refreshStats();
        createNewDoc();
        showAlert("Επιτυχία", "Το έγγραφο διαγράφηκε!");
    }

    @FXML
    private void toggleFollow() {
        Document selectedDoc = docsTable.getSelectionModel().getSelectedItem();
        if (selectedDoc == null) return;

        String docId = String.valueOf(selectedDoc.getId());
        if (loggedInUser.getFollowedDocs() == null) loggedInUser.setFollowedDocs(new HashMap<>());

        Map<String, Integer> followed = loggedInUser.getFollowedDocs();

        if (btnFollow.isSelected()) {
            if (!followed.containsKey(docId)) {
                // Αποθηκεύουμε το ID και την ΤΡΕΧΟΥΣΑ έκδοση τη στιγμή που πατάει follow
                int currentVer = selectedDoc.getCurrentVersion() != null ? selectedDoc.getCurrentVersion().getVersionCode() : 1;
                followed.put(docId, currentVer);
                btnFollow.setText("★ Following");
            }
        } else {
            followed.remove(docId);
            btnFollow.setText("☆ Follow");
        }
        refreshStats();
    }


    // HELPERS & SAVE TO JSON

    private void refreshStats() {
        totalDocsLabel.setText(String.valueOf(globalDocs.size()));
        totalCategoriesLabel.setText(String.valueOf(globalCategories.size()));
        if (loggedInUser.getFollowedDocs() == null) {
            loggedInUser.setFollowedDocs(new HashMap<>());
        }

        Map<String, Integer> validFollows = new HashMap<>();
        for (Map.Entry<String, Integer> entry : loggedInUser.getFollowedDocs().entrySet()) {
            for (Document d : globalDocs) {
                if (String.valueOf(d.getId()).equals(entry.getKey())) {
                    validFollows.put(entry.getKey(), entry.getValue());
                    break;
                }
            }
        }
        loggedInUser.setFollowedDocs(validFollows);
        myDocsLabel.setText(String.valueOf(loggedInUser.getFollowedDocs().size()));
    }
    private void showNotifications() {
        if (loggedInUser.getFollowedDocs() == null || loggedInUser.getFollowedDocs().isEmpty()) return;

        StringBuilder msg = new StringBuilder("Έγγραφα που έχουν ανανεωθεί:\n\n");
        boolean hasUpdates = false;

        for (Document doc : globalDocs) {
            String docId = String.valueOf(doc.getId());


            if (loggedInUser.getFollowedDocs().containsKey(docId)) {
                int lastSeenVersion = loggedInUser.getFollowedDocs().get(docId);
                int currentVersion = doc.getCurrentVersion() != null ? doc.getCurrentVersion().getVersionCode() : 1;


                if (currentVersion > lastSeenVersion) {
                    hasUpdates = true;
                    msg.append("📄 ").append(doc.getTitle())
                            .append(" (Νέα Έκδοση: ").append(currentVersion).append(")\n");


                    loggedInUser.getFollowedDocs().put(docId, currentVersion);
                }
            }
        }

        if (hasUpdates) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Νέες Ενημερώσεις");
            alert.setHeaderText("Υπάρχουν αλλαγές, " + loggedInUser.getFullName() + "!");
            alert.setContentText(msg.toString());
            alert.showAndWait();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(content);
        alert.showAndWait();
    }


    private void saveEverythingToJson() {
        JsonManager manager = new JsonManager();
        manager.saveUsers(globalUsers);
        manager.saveCategories(globalCategories);
        manager.saveDocuments(globalDocs);
        System.out.println("Τερματισμός: Τα δεδομένα αποθηκεύτηκαν στα JSON!");
    }
}