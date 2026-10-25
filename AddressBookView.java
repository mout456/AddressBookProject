package application;

import java.util.List;

public interface AddressBookView {

    void setTitle(String title);

    void showEntries(List<Contact> contacts);

    void showError(String message);

    boolean askSaveChanges(String filePath);

    String askFileToOpen();

    String askFileToSave(String currentFilePath);
}