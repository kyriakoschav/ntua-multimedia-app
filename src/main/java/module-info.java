module gr.ntua.medialab.medialabmanager {
    requires javafx.controls;
    requires javafx.fxml;

    // ΠΡΟΣΘΕΣΕ ΑΥΤΕΣ ΤΙΣ 2 ΓΡΑΜΜΕΣ:
    requires com.fasterxml.jackson.databind; // <--- Για να δουλέψει ο JsonManager
    requires com.fasterxml.jackson.core;     // <--- Για να φύγουν τα κόκκινα

    opens gr.ntua.medialab.medialabmanager to javafx.fxml;
    exports gr.ntua.medialab.medialabmanager;

    // ΠΡΟΣΘΕΣΕ ΚΑΙ ΑΥΤΟ (ΣΗΜΑΝΤΙΚΟ):
    // Επιτρέπει στο Jackson να "δει" τα δεδομένα σου (User, Document)
    exports gr.ntua.medialab.medialabmanager.model;
    opens gr.ntua.medialab.medialabmanager.model to com.fasterxml.jackson.databind;

    // --- Η ΓΡΑΜΜΗ ΠΟΥ ΣΟΥ ΛΕΙΠΕΙ (ΓΙΑ ΤΟ LOGIN) ---
    // Αυτό επιτρέπει στο JavaFX να μπει στον φάκελο controller
    exports gr.ntua.medialab.medialabmanager.controller;
    opens gr.ntua.medialab.medialabmanager.controller to javafx.fxml;
}