module edu.psu.notepad {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens edu.psu.notepad to javafx.fxml;
    exports edu.psu.notepad;
}