package gr.ntua.medialab.medialabmanager.controller;

import gr.ntua.medialab.medialabmanager.model.User;
import gr.ntua.medialab.medialabmanager.service.JsonManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    protected void onLoginButtonClick(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();


        JsonManager manager = new JsonManager();
        List<User> users = manager.loadUsers();

        boolean found = false;
        User foundUser = null;


        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                found = true;
                foundUser = u;
                break;
            }
        }

        if (found) {

            errorLabel.setText("Επιτυχία!");
            openMainWindow(event, foundUser);
        } else {

            errorLabel.setText("Λάθος όνομα χρήστη ή κωδικός!");
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }


    private void openMainWindow(ActionEvent event, User user) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gr/ntua/medialab/medialabmanager/main.fxml"));
            Scene mainScene = new Scene(loader.load());


            MainController controller = loader.getController();
            controller.setLoggedInUser(user);


            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();


            window.setScene(mainScene);
            window.setTitle("MediaLab Document Manager - Dashboard");
            window.centerOnScreen();


            window.setOnCloseRequest(closeEvent -> {
                System.out.println("Η εφαρμογή κλείνει. Αποθήκευση δεδομένων...");
                JsonManager saveManager = new JsonManager();

                System.out.println("Η αποθήκευση ολοκληρώθηκε.");
            });

            window.show();

        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Σφάλμα κατά τη φόρτωση της εφαρμογής.");
        }
    }
}
