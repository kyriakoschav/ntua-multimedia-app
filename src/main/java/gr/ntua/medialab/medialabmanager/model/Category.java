package gr.ntua.medialab.medialabmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Η κλάση Category αναπαριστά μια κατηγορία εγγράφων στο σύστημα MediaLab.
 * Κάθε έγγραφο ανήκει υποχρεωτικά σε μία κατηγορία και οι χρήστες
 * αποκτούν δικαιώματα πρόσβασης με βάση αυτές τις κατηγορίες.
 * * @author [Το Όνομά Σου / MediaLab]
 * @version 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Category {
    private String name;

    /**
     * Προεπιλεγμένος, κενός κατασκευαστής (constructor).
     * Απαιτείται από τη βιβλιοθήκη Jackson για τη σωστή μετατροπή
     * των δεδομένων από και προς τη μορφή JSON.
     */
    public Category() {
    }

    /**
     * Κατασκευαστής που αρχικοποιεί την κατηγορία με ένα συγκεκριμένο όνομα.
     * * @param name Το όνομα της νέας κατηγορίας (π.χ. "Science", "History").
     */
    public Category(String name) {
        this.name = name;
    }

    /**
     * Επιστρέφει το όνομα της κατηγορίας.
     * * @return Το τρέχον όνομα της κατηγορίας ως String.
     */
    public String getName() {
        return name;
    }

    /**
     * Ενημερώνει το όνομα της κατηγορίας.
     * * @param name Το νέο όνομα που θα δοθεί στην κατηγορία.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Επιστρέφει την αναπαράσταση της κλάσης σε μορφή συμβολοσειράς (String).
     * Χρησιμοποιείται κυρίως από τα στοιχεία του JavaFX (όπως TableView ή ComboBox)
     * για την ορθή εμφάνιση του αντικειμένου στο γραφικό περιβάλλον.
     * * @return Το όνομα της κατηγορίας.
     */
    @Override
    public String toString() {
        return name;
    }
}