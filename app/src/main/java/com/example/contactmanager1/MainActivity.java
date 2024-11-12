package com.example.contactmanager1;




import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText contactNameInput, contactNumberInput;
    private Button addContactButton, searchContactButton, deleteLastContactButton;
    private TextView stackContacts;

    private ContactStack contactStack; // Stack to manage contacts

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        contactNameInput = findViewById(R.id.contactNameInput);
        contactNumberInput = findViewById(R.id.contactNumberInput);
        addContactButton = findViewById(R.id.addContactButton);
        searchContactButton = findViewById(R.id.searchContactButton);
        deleteLastContactButton = findViewById(R.id.deleteLastContactButton);
        stackContacts = findViewById(R.id.stackContacts);

        // Initialize the stack
        contactStack = new ContactStack();

        // Set up button click listeners
        addContactButton.setOnClickListener(v -> addContact());
        searchContactButton.setOnClickListener(v -> searchContact());
        deleteLastContactButton.setOnClickListener(v -> deleteLastContact());
    }

    private void addContact() {
        String name = contactNameInput.getText().toString().trim();
        String number = contactNumberInput.getText().toString().trim();

        if (name.isEmpty() || number.isEmpty()) {
            Toast.makeText(this, "Please enter both name and number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check for duplicate name
        if (contactStack.isDuplicateName(name)) {
            Toast.makeText(this, "Name already exists. Please enter a different name.", Toast.LENGTH_SHORT).show();
            return;
        }

        contactStack.push(new Contact(name, number));
        updateStackDisplay();

        contactNameInput.setText("");
        contactNumberInput.setText("");
        Toast.makeText(this, "Contact Added", Toast.LENGTH_SHORT).show();
    }

    private void searchContact() {
        String name = contactNameInput.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Enter a name to search", Toast.LENGTH_SHORT).show();
            return;
        }

        String result = contactStack.searchByName(name);
        if (!result.isEmpty()) {
            Toast.makeText(this, "Contacts Found:\n" + result, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Contact Not Found", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteLastContact() {
        if (contactStack.isEmpty()) {
            Toast.makeText(this, "No contacts to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        contactStack.pop(); // Remove the last inserted (top) contact
        updateStackDisplay();
        Toast.makeText(this, "Last Contact Deleted", Toast.LENGTH_SHORT).show();
    }

    private void updateStackDisplay() {
        StringBuilder stackText = new StringBuilder();
        ContactNode current = contactStack.top;
        while (current != null) {
            stackText.insert(0, current.contact.getName() + " - " + current.contact.getNumber() + "\n"); // Display in first-in order
            current = current.next;
        }
        stackContacts.setText(stackText.toString());
    }

    // Contact Class
    static class Contact {
        private String name;
        private String number;

        public Contact(String name, String number) {
            this.name = name;
            this.number = number;
        }

        public String getName() { return name; }
        public String getNumber() { return number; }
    }

    // Node class for singly linked list
    private static class ContactNode {
        Contact contact;
        ContactNode next;

        ContactNode(Contact contact) {
            this.contact = contact;
            this.next = null;
        }
    }

    // Custom stack implementation using singly linked list
    private static class ContactStack {
        private ContactNode top;

        public void push(Contact contact) {
            ContactNode newNode = new ContactNode(contact);
            newNode.next = top;
            top = newNode;
        }

        public void pop() {
            if (top != null) {
                top = top.next; // Remove top element
            }
        }

        public boolean isEmpty() {
            return top == null;
        }

        // Check for duplicate names in the stack
        public boolean isDuplicateName(String name) {
            ContactNode current = top;
            while (current != null) {
                if (current.contact.getName().equalsIgnoreCase(name)) {
                    return true;
                }
                current = current.next;
            }
            return false;
        }

        // Search contacts by name, allowing partial matches
        public String searchByName(String name) {
            StringBuilder foundContacts = new StringBuilder();
            ContactNode current = top;
            while (current != null) {
                if (current.contact.getName().toLowerCase().contains(name.toLowerCase())) {
                    foundContacts.append(current.contact.getName()).append(" - ").append(current.contact.getNumber()).append("\n");
                }
                current = current.next;
            }
            return foundContacts.toString();
        }
    }
}
