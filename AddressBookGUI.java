package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class AddressBookGUI extends Application implements AddressBookView {

    // ---------------------------------------------------------
    // CONTROLLER
    // ---------------------------------------------------------

    private AddressBookController controller;

    // ---------------------------------------------------------
    // JAVAFX COMPONENTS
    // ---------------------------------------------------------

    private Stage stage;

    private TableView<Contact> contactTable;

    private final ObservableList<Contact> contactList =
            FXCollections.observableArrayList();

    // ---------------------------------------------------------
    // START APPLICATION
    // ---------------------------------------------------------

    @Override
    public void start(Stage primaryStage) {

        this.stage = primaryStage;

        // Create controller
        controller = new AddressBookController(this);

        // Create contact table
        createContactTable();

        // Create menu bar
        MenuBar menuBar = createMenuBar();

        // Create button bar
        HBox buttonBar = createButtonBar();

        // Main layout
        BorderPane root = new BorderPane();

        root.setTop(menuBar);
        root.setCenter(contactTable);
        root.setBottom(buttonBar);

        BorderPane.setMargin(buttonBar, new Insets(10));
        BorderPane.setAlignment(buttonBar, Pos.CENTER);

        // Scene
        Scene scene = new Scene(root, 700, 450);
        scene.getStylesheets().add(
        	    getClass().getResource("application.css").toExternalForm()
        	);

        stage.setTitle("Untitled - Address Book");
        stage.setScene(scene);

        // Handle window close
        stage.setOnCloseRequest(event -> {

            if (!controller.close()) {
                event.consume();
            }
        });

        stage.show();
    }

    // ---------------------------------------------------------
    // MAIN METHOD
    // ---------------------------------------------------------

    public static void main(String[] args) {
        launch(args);
    }

    // ---------------------------------------------------------
    // CONTACT TABLE
    // ---------------------------------------------------------

    private void createContactTable() {

        contactTable = new TableView<>();

        // Name column
        TableColumn<Contact, String> nameColumn =
                new TableColumn<>("Name");

        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );

        // Phone column
        TableColumn<Contact, String> phoneColumn =
                new TableColumn<>("Phone");

        phoneColumn.setCellValueFactory(
                new PropertyValueFactory<>("phone")
        );

        // Email column
        TableColumn<Contact, String> emailColumn =
                new TableColumn<>("Email");

        emailColumn.setCellValueFactory(
                new PropertyValueFactory<>("email")
        );

        // Add columns
        contactTable.getColumns().addAll(
                nameColumn,
                phoneColumn,
                emailColumn
        );

        // Set data
        contactTable.setItems(contactList);

        // Message when table is empty
        contactTable.setPlaceholder(
                new Label("No contacts in this address book.")
        );

        // Column widths
        nameColumn.setPrefWidth(200);
        phoneColumn.setPrefWidth(200);
        emailColumn.setPrefWidth(250);
    }

    // ---------------------------------------------------------
    // MENU BAR
    // ---------------------------------------------------------

    private MenuBar createMenuBar() {

        MenuBar menuBar = new MenuBar();

        // File menu
        Menu fileMenu = new Menu("File");

        MenuItem newItem = new MenuItem("New");
        MenuItem openItem = new MenuItem("Open");
        MenuItem saveItem = new MenuItem("Save");
        MenuItem saveAsItem = new MenuItem("Save As");
        MenuItem closeItem = new MenuItem("Close");
        MenuItem quitItem = new MenuItem("Quit");

        // -----------------------------------------------------
        // NEW
        // -----------------------------------------------------

        newItem.setOnAction(event -> {
            controller.newAddressBook();
        });

        // -----------------------------------------------------
        // OPEN
        // -----------------------------------------------------

        openItem.setOnAction(event -> {

            String filePath = askFileToOpen();

            if (filePath != null) {
                controller.open(filePath);
            }
        });

        // -----------------------------------------------------
        // SAVE
        // -----------------------------------------------------

        saveItem.setOnAction(event -> {
            controller.save();
        });

        // -----------------------------------------------------
        // SAVE AS
        // -----------------------------------------------------

        saveAsItem.setOnAction(event -> {

            String filePath = askFileToSave(null);

            if (filePath != null) {
                controller.saveAs(filePath);
            }
        });

        // -----------------------------------------------------
        // CLOSE
        // -----------------------------------------------------

        closeItem.setOnAction(event -> {
            controller.close();
        });

        // -----------------------------------------------------
        // QUIT
        // -----------------------------------------------------

        quitItem.setOnAction(event -> {

            if (controller.close()) {
                stage.close();
            }
        });

        // Add menu items
        fileMenu.getItems().addAll(
                newItem,
                openItem,
                new SeparatorMenuItem(),
                saveItem,
                saveAsItem,
                new SeparatorMenuItem(),
                closeItem,
                quitItem
        );

        menuBar.getMenus().add(fileMenu);

        return menuBar;
    }

    // ---------------------------------------------------------
    // BUTTON BAR
    // ---------------------------------------------------------

    private HBox createButtonBar() {

        Button addButton =
                new Button("Add Contact");

        Button removeButton =
                new Button("Remove Contact");

        // Add contact
        addButton.setOnAction(event -> {
            addContact();
        });

        // Remove contact
        removeButton.setOnAction(event -> {
            removeContact();
        });

        HBox buttonBar = new HBox(
                30,
                addButton,
                removeButton
        );
        buttonBar.setAlignment(Pos.CENTER);
        return buttonBar;
    }

    // ---------------------------------------------------------
    // ADD CONTACT
    // ---------------------------------------------------------

    private void addContact() {

        Dialog<Contact> dialog = new Dialog<>();

        dialog.setTitle("Add Contact");
        dialog.setHeaderText(
                "Enter contact information"
        );

        // Add button
        ButtonType addButtonType =
                new ButtonType(
                        "Add",
                        ButtonBar.ButtonData.OK_DONE
                );

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        addButtonType,
                        ButtonType.CANCEL
                );

        // Input fields
        TextField nameField =
                new TextField();

        TextField phoneField =
                new TextField();

        TextField emailField =
                new TextField();

        nameField.setPromptText("Name");
        phoneField.setPromptText("Phone");
        emailField.setPromptText("Email");

        // Fields layout
        VBox fields = new VBox(
                10,
                new Label("Name:"),
                nameField,
                new Label("Phone:"),
                phoneField,
                new Label("Email:"),
                emailField
        );

        fields.setPadding(
                new Insets(10)
        );

        dialog.getDialogPane()
                .setContent(fields);

        // Convert dialog result into Contact
        dialog.setResultConverter(button -> {

            if (button == addButtonType) {

                return new Contact(
                        nameField.getText(),
                        phoneField.getText(),
                        emailField.getText()
                );
            }

            return null;
        });

        // Show dialog
        dialog.showAndWait()
                .ifPresent(contact -> {

                    controller.addContact(contact);
                });
    }

    // ---------------------------------------------------------
    // REMOVE CONTACT
    // ---------------------------------------------------------

    private void removeContact() {

        Contact selectedContact =
                contactTable.getSelectionModel().getSelectedItem();

        // No contact selected
        if (selectedContact == null) {

            showError(
                    "Please select a contact to remove."
            );

            return;
        }

        // Confirmation dialog
        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                "Remove Contact"
        );

        confirmation.setHeaderText(
                "Remove selected contact?"
        );

        confirmation.setContentText(
                selectedContact.getName()
        );

        confirmation.showAndWait()
                .ifPresent(response -> {

                    if (response == ButtonType.OK) {

                        controller.removeContact(
                                selectedContact
                        );
                    }
                });
    }

    // ---------------------------------------------------------
    // VIEW INTERFACE METHODS
    // ---------------------------------------------------------

    @Override
    public void setTitle(String title) {

        stage.setTitle(title);
    }

    // ---------------------------------------------------------
    // SHOW ENTRIES
    // ---------------------------------------------------------

    @Override
    public void showEntries(List<Contact> contacts) {

        contactList.clear();

        if (contacts != null) {

            contactList.addAll(contacts);
        }
    }

    // ---------------------------------------------------------
    // SHOW ERROR
    // ---------------------------------------------------------

    @Override
    public void showError(String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle("Error");

        alert.setHeaderText(null);

        alert.setContentText(message);

        alert.showAndWait();
    }

    // ---------------------------------------------------------
    // ASK SAVE CHANGES
    // ---------------------------------------------------------

    @Override
    public boolean askSaveChanges(
            String filePath
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        alert.setTitle(
                "Unsaved Changes"
        );

        String name =
                filePath == null
                        ? "Untitled"
                        : filePath;

        alert.setHeaderText(
                "The address book has unsaved changes."
        );

        alert.setContentText(
                "Do you want to save changes to "
                        + name
                        + "?"
        );

        // Save button
        ButtonType saveButton =
                new ButtonType("Save");

        // Don't Save button
        ButtonType discardButton =
                new ButtonType(
                        "Don't Save",
                        ButtonBar.ButtonData.NO
                );

        // Cancel button
        ButtonType cancelButton =
                new ButtonType(
                        "Cancel",
                        ButtonBar.ButtonData.CANCEL_CLOSE
                );

        alert.getButtonTypes().setAll(
                saveButton,
                discardButton,
                cancelButton
        );

        ButtonType result =
                alert.showAndWait()
                        .orElse(cancelButton);

        // Save
        if (result == saveButton) {

            return controller.save();
        }

        // Don't Save
        if (result == discardButton) {

            return true;
        }

        // Cancel
        return false;
    }

    // ---------------------------------------------------------
    // ASK FILE TO OPEN
    // ---------------------------------------------------------

    @Override
    public String askFileToOpen() {

        FileChooser fileChooser =
                new FileChooser();

        fileChooser.setTitle(
                "Open Address Book"
        );

        fileChooser.getExtensionFilters()
                .add(
                        new FileChooser.ExtensionFilter(
                                "Text Files",
                                "*.txt"
                        )
                );

        File file =
                fileChooser.showOpenDialog(stage);

        if (file != null) {

            return file.getAbsolutePath();
        }

        return null;
    }

    // ---------------------------------------------------------
    // ASK FILE TO SAVE
    // ---------------------------------------------------------

    @Override
    public String askFileToSave(
            String currentFilePath
    ) {

        FileChooser fileChooser =
                new FileChooser();

        fileChooser.setTitle(
                "Save Address Book"
        );

        fileChooser.getExtensionFilters()
                .add(
                        new FileChooser.ExtensionFilter(
                                "Text Files",
                                "*.txt"
                        )
                );

        // Use existing file name
        if (currentFilePath != null) {

            File currentFile =
                    new File(currentFilePath);

            fileChooser.setInitialFileName(
                    currentFile.getName()
            );
        }

        File file =
                fileChooser.showSaveDialog(stage);

        if (file != null) {

            return file.getAbsolutePath();
        }

        return null;
    }
}