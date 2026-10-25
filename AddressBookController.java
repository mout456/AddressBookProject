package application;

public class AddressBookController {

    // ---------------------------------------------------------
    // REFERENCES
    // ---------------------------------------------------------

    private AddressBookView view;
    private AddressBook addressBook;
    private FileSystem fileSystem;

    // ---------------------------------------------------------
    // CONSTRUCTOR
    // ---------------------------------------------------------

    public AddressBookController(AddressBookView view) {

        this.view = view;
        this.fileSystem = new FileSystem();
        this.addressBook = new AddressBook();

        // Show initial empty address book
        view.setTitle("Untitled - Address Book");
        view.showEntries(addressBook.getContacts());
    }

    // ---------------------------------------------------------
    // NEW ADDRESS BOOK
    // ---------------------------------------------------------

    public void newAddressBook() {

        // Check for unsaved changes
        if (addressBook.isModified()) {

            boolean proceed =
                    view.askSaveChanges(
                            addressBook.getFilePath()
                    );

            if (!proceed) {
                return;
            }
        }

        // Create new empty address book
        addressBook = new AddressBook();

        // Update view
        view.showEntries(addressBook.getContacts());
        view.setTitle("Untitled - Address Book");
    }

    // ---------------------------------------------------------
    // ADD CONTACT
    // ---------------------------------------------------------

    public void addContact(Contact contact) {

        if (contact == null) {
            return;
        }

        addressBook.addContact(contact);

        // Update GUI
        view.showEntries(addressBook.getContacts());
    }

    // ---------------------------------------------------------
    // REMOVE CONTACT
    // ---------------------------------------------------------

    public void removeContact(Contact contact) {

        if (contact == null) {
            return;
        }

        addressBook.removeContact(contact);

        // Update GUI
        view.showEntries(addressBook.getContacts());
    }

    // ---------------------------------------------------------
    // OPEN
    // ---------------------------------------------------------

    public void open(String filePath) {

        if (filePath == null || filePath.isEmpty()) {
            return;
        }

        // Check for unsaved changes
        if (addressBook.isModified()) {

            boolean proceed =
                    view.askSaveChanges(
                            addressBook.getFilePath()
                    );

            if (!proceed) {
                return;
            }
        }

        // Check whether file exists
        if (!fileSystem.exists(filePath)) {

            view.showError(
                    "File does not exist."
            );

            return;
        }

        // Read address book
        AddressBook loadedBook =
                fileSystem.read(filePath);

        if (loadedBook == null) {

            view.showError(
                    "Unable to open the file."
            );

            return;
        }

        // Replace current address book
        addressBook = loadedBook;

        // Update GUI
        view.showEntries(
                addressBook.getContacts()
        );

        view.setTitle(
                filePath + " - Address Book"
        );
    }

    // ---------------------------------------------------------
    // SAVE
    // ---------------------------------------------------------

    public boolean save() {

        // If there is no file associated with the book,
        // Save should behave like Save As.
        if (addressBook.getFilePath() == null) {

            String filePath =
                    view.askFileToSave(null);

            if (filePath == null) {
                return false;
            }

            return saveAs(filePath);
        }

        // Write to existing file
        boolean success =
                fileSystem.write(
                        addressBook.getFilePath(),
                        addressBook
                );

        if (success) {

            addressBook.markAsSaved();

            view.setTitle(
                    addressBook.getFilePath()
                            + " - Address Book"
            );

            return true;
        }

        view.showError(
                "Unable to save the address book."
        );

        return false;
    }

    // ---------------------------------------------------------
    // SAVE AS
    // ---------------------------------------------------------

    public boolean saveAs(String filePath) {

        if (filePath == null || filePath.isEmpty()) {
        	
            return false;
        }

        boolean success =
                fileSystem.write(
                        filePath,
                        addressBook
                );

        if (success) {

            // Store new file path
            addressBook.setFilePath(filePath);

            // No longer modified
            addressBook.markAsSaved();

            // Update window title
            view.setTitle(
                    filePath + " - Address Book"
            );

            return true;
        }

        view.showError(
                "Unable to save the address book."
        );

        return false;
    }

    // ---------------------------------------------------------
    // CLOSE
    // ---------------------------------------------------------

    public boolean close() {

        // Check for unsaved changes
        if (addressBook.isModified()) {

            boolean proceed =
                    view.askSaveChanges(
                            addressBook.getFilePath()
                    );

            if (!proceed) {
                return false;
            }
        }

        return true;
    }
}