package gr.ntua.medialab.medialabmanager;

import gr.ntua.medialab.medialabmanager.model.User;
import gr.ntua.medialab.medialabmanager.service.JsonManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("MediaLab Login");
        stage.setScene(scene);
        stage.show();
    }
}
