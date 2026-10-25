package application;

import java.util.ArrayList;
import java.util.List;

public class AddressBook {

    private List<Contact> contacts;
    private String filePath;
    private boolean modified;

    public AddressBook() {
        contacts = new ArrayList<>();
        filePath = null;
        modified = false;
    }

    public void addContact(Contact contact) {
        contacts.add(contact);
        modified = true;
    }

    public void removeContact(Contact contact) {
        contacts.remove(contact);
        modified = true;
    }

    public void updateContact(Contact oldContact, Contact newContact) {
        int index = contacts.indexOf(oldContact);

        if (index != -1) {
            contacts.set(index, newContact);
            modified = true;
        }
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public boolean isModified() {
        return modified;
    }

    public void markAsSaved() {
        modified = false;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}