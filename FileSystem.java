package application;

import java.io.*;
import java.util.ArrayList;

public class FileSystem {

    public boolean write(String filePath, AddressBook book) {

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {

            for (Contact contact : book.getContacts()) {
                writer.println(
                    contact.getName() + "," +
                    contact.getPhone() + "," +
                    contact.getEmail()
                );
            }

            return true;

        } catch (IOException e) {
            return false;
        }
    }

    public AddressBook read(String filePath) {

        AddressBook book = new AddressBook();

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(filePath))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");

                if (data.length == 3) {
                    Contact contact = new Contact(
                        data[0],
                        data[1],
                        data[2]
                    );

                    book.addContact(contact);
                }
            }

            book.setFilePath(filePath);
            book.markAsSaved();

            return book;

        } catch (IOException e) {
            return null;
        }
    }

    public boolean exists(String filePath) {
        return new File(filePath).exists();
    }
}