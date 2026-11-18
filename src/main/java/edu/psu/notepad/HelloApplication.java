package edu.psu.notepad;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.print.PageOrientation;
import javafx.print.PageLayout;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.scene.control.ChoiceBox;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class HelloApplication extends Application {

    private File currentFile = null;
    private Paper selectedPaper = Paper.NA_LETTER;
    private PageOrientation selectedOrientation = PageOrientation.PORTRAIT;

    @Override
    public void start(Stage stage) throws IOException {

        // border pane
        var root = new BorderPane();

        // make text area
        var textArea = new TextArea();

        // set text area in center of border pane
        root.setCenter(textArea);

        // menu bar
        var menuBar = new MenuBar();
        menuBar.setStyle("-fx-background-color: darkgrey;");
        var menu1 = new Menu("File");
        var menu2 = new Menu("Edit");
        var menu3 = new Menu("Format");
        var menu4 = new Menu("View");
        var menu5 = new Menu("Help");
        menuBar.getMenus().addAll(menu1, menu2, menu3, menu4, menu5);
        root.setTop(menuBar);


        // File menu items
        var newItem = new MenuItem("New");
        newItem.setOnAction(e->{
            textArea.clear();
            currentFile = null;
        });
        var openItem = new MenuItem("Open...");
        openItem.setOnAction(e->{
            var fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(stage);
            if (file != null){
                try {
                    String content = Files.readString(file.toPath());
                    textArea.setText(content);
                    currentFile = file;
                } catch(IOException ex){
                    ex.printStackTrace();
                }
            }
        });

        var saveItem = new MenuItem("Save");
        saveItem.setOnAction(e->{
            if (currentFile != null){
                try{
                    Files.writeString(currentFile.toPath(), textArea.getText());
                } catch(IOException ex){
                    ex.printStackTrace();
                }
            }
        });
        var saveAsItem = new MenuItem("Save As...");
        saveAsItem.setOnAction(e->{
            var fileChooser = new FileChooser();
            File file = fileChooser.showSaveDialog(stage);
            if (file != null){
                try{
                    currentFile = file;
                    Files.writeString(currentFile.toPath(), textArea.getText());
                } catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        });

        var pageSetupItem = new MenuItem("Page Setup...");
        pageSetupItem.setOnAction(e->{
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Page Setup");
            dialog.setHeaderText("Configure Page Settings");
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);


            var paperChoice = new ChoiceBox<Paper>();
            paperChoice.getItems().addAll(
                    Paper.NA_LETTER,
                    Paper.LEGAL,
                    Paper.A4
            );
            paperChoice.setValue(selectedPaper);

            var orientationChoice = new ChoiceBox<PageOrientation>();
            orientationChoice.getItems().addAll(
                    PageOrientation.PORTRAIT,
                    PageOrientation.LANDSCAPE
            );
            orientationChoice.setValue(selectedOrientation);

            var layout = new VBox(10);

            layout.getChildren().addAll(
                    new Label("Paper Size:"),
                    paperChoice,
                    new Label("Orientation:"),
                    orientationChoice
            );

            dialog.getDialogPane().setContent(layout);

            dialog.setResultConverter(button->{
                if (button == ButtonType.OK){
                    selectedPaper = paperChoice.getValue();
                    selectedOrientation = orientationChoice.getValue();
                }
                return button;
            });

            dialog.showAndWait();
        });

        var printItem = new MenuItem("Print");
        printItem.setOnAction(e->{
            PrinterJob job = PrinterJob.createPrinterJob();

            if (job != null && job.showPrintDialog(stage)){
                PageLayout pageLayout = job.getPrinter().createPageLayout(
                        selectedPaper,
                        selectedOrientation,
                        Printer.MarginType.DEFAULT
                );
                boolean success = job.printPage(pageLayout, textArea);

                if (success){
                    job.endJob();
                }
            }
        });

        var exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e->{
            stage.close();
        });
        menu1.getItems().addAll(newItem,  openItem, saveItem, saveAsItem,
                pageSetupItem, printItem, exitItem);


        // Edit menu items
        var undoItem = new MenuItem("Undo");
        undoItem.setOnAction(e->{
            textArea.undo();
        });
        var redoItem = new MenuItem("Redo");
        redoItem.setOnAction(e->{
            textArea.redo();
        });
        var cutItem = new MenuItem("Cut");
        cutItem.setOnAction(e->{
            textArea.cut();
        });
        var copyItem = new MenuItem("Copy");
        copyItem.setOnAction(e->{
            textArea.copy();
        });
        var pasteItem = new MenuItem("Paste");
        pasteItem.setOnAction(e->{
            textArea.paste();
        });
        var deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(e->{
            textArea.deleteText(textArea.getSelection());
        });
        var selectAllItem = new MenuItem("Select All");
        selectAllItem.setOnAction(e->{
            textArea.selectAll();
        });
        menu2.getItems().addAll(undoItem, redoItem, cutItem, copyItem, pasteItem,
                deleteItem, selectAllItem);

        // Format menu items
        var wordWrapItem = new CheckMenuItem("Word Wrap");
        textArea.setWrapText(true);
        wordWrapItem.setOnAction(e->{
            textArea.setWrapText(wordWrapItem.isSelected());
        });

        var fontItem = new MenuItem("Font");
        fontItem.setOnAction(e->{
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Font");
            dialog.setHeaderText("Font settings");
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            var fontChoice = new ChoiceBox<String>();
            fontChoice.getItems().addAll(Font.getFamilies());
            fontChoice.setValue(textArea.getFont().getFamily());

            var sizeField = new TextField(
                    String.valueOf(textArea.getFont().getSize())
            );

            var boldCheck = new CheckBox("Bold");
            boldCheck.setSelected(textArea.getFont().getStyle().contains("Bold"));
            var italicCheck = new CheckBox("Italic");
            italicCheck.setSelected(textArea.getFont().getStyle().contains("Italic"));

            var layout = new VBox(10);
            layout.getChildren().addAll(
                    new Label("Font Family:"),
                    fontChoice,
                    new Label("Font Size:"),
                    sizeField,
                    boldCheck,
                    italicCheck,
                    new Label("Note: Italic may not be available for all fonts")
                    );
            dialog.getDialogPane().setContent(layout);

            dialog.setResultConverter(button->{
                if (button == ButtonType.OK){
                    double size = Double.parseDouble(sizeField.getText());

                    FontWeight weight = FontWeight.NORMAL;
                    if (boldCheck.isSelected()){
                        weight = FontWeight.BOLD;
                    }

                    FontPosture posture = FontPosture.REGULAR;
                    if (italicCheck.isSelected()){
                        posture = FontPosture.ITALIC;
                    }

                    Font newFont = Font.font(fontChoice.getValue(), weight, posture, size);


                    textArea.setFont(newFont);
                }
                return button;
            });

            dialog.showAndWait();
        });

        menu3.getItems().addAll(wordWrapItem, fontItem);

        // view menu items
        var zoomInItem = new MenuItem("Zoom In");
        zoomInItem.setOnAction(e->{
            Font currentFont = textArea.getFont();
            FontWeight weight = FontWeight.NORMAL;
            if (currentFont.getStyle().contains("Bold")){
                weight = FontWeight.BOLD;
            }
            FontPosture posture = FontPosture.REGULAR;
            if (currentFont.getStyle().contains("Italic")){
                posture = FontPosture.ITALIC;
            }
            textArea.setFont(Font.font(
                    currentFont.getFamily(),
                    weight,
                    posture,
                    currentFont.getSize() + 2
            ));
        });

        var zoomOutItem = new MenuItem("Zoom Out");
        zoomOutItem.setOnAction(e->{
            Font currentFont = textArea.getFont();
            FontWeight weight = FontWeight.NORMAL;
            if (currentFont.getStyle().contains("Bold")){
                weight = FontWeight.BOLD;
            }
            FontPosture posture = FontPosture.REGULAR;
            if (currentFont.getStyle().contains("Italic")){
                posture = FontPosture.ITALIC;
            }
            textArea.setFont(Font.font(
                    currentFont.getFamily(),
                    weight,
                    posture,
                    currentFont.getSize() - 2
            ));
        });
        menu4.getItems().addAll(zoomInItem, zoomOutItem);

        // Help menu items
        var aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e->{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("About Notepad");
            alert.setContentText("this is a notepad");
            alert.showAndWait();

        });

        var helpItem = new MenuItem("Help");
        helpItem.setOnAction(e->{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Help");
            alert.setHeaderText("How to use notepad");
            alert.setContentText("This is self explanatory");
            alert.showAndWait();
        });
        menu5.getItems().addAll(aboutItem, helpItem);


        // make scene
        var scene = new Scene(root);
        stage.setHeight(700);
        stage.setWidth(700);
        stage.setTitle("Notepad");
        stage.setScene(scene);
        stage.show();
    }
}
