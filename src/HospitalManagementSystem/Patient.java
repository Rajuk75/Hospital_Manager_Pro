package HospitalManagementSystem;

import java.sql.Connection; // Import the Connection class for database connections
import java.sql.DriverManager; // Import for establishing a connection
import java.sql.PreparedStatement; // Import for executing SQL statements
import java.sql.ResultSet; // Import for handling result sets from queries
import java.sql.SQLException; // Import for handling SQL exceptions
import java.util.Scanner; // Import for reading user input

public class Patient {
    private Connection connection; // Database connection
    private Scanner scanner; // Scanner for user input

    // Constructor to initialize connection and scanner
    public Patient(Connection connection, Scanner scanner) {
        this.connection = connection; // Set the connection
        this.scanner = scanner; // Set the scanner
    }

    // Main method
    public static void main(String[] args) {
        // JDBC URL, username, and password for MySQL database
        String url = "jdbc:mysql://localhost:3306/HOSPITAL"; // Change if needed
        String user = "root"; // Your MySQL username
        String password = "*******"; // Your MySQL password

        Connection connection = null; // Initialize the connection to null
        Scanner scanner = new Scanner(System.in); // Create scanner for user input

        try {
            // Establish the connection
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database successfully!"); // Success message

            Patient patientManager = new Patient(connection, scanner); // Create an instance of Patient

            boolean running = true; // Control variable for the menu loop
            while (running) { // Loop until the user chooses to exit
                // Display menu options
                System.out.println("Choose an action:");
                System.out.println("+---------------------------------------+");
                System.out.println("| 1. Add Patient                       |");
                System.out.println("| 2. View Patients                     |");
                System.out.println("| 3. Check Patient                     |");
                System.out.println("| 4. Remove Patient                    |");
                System.out.println("| 5. Exit                              |");
                System.out.println("+---------------------------------------+");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt(); // Read user choice
                scanner.nextLine(); // Consume newline

                // Switch statement to handle user choices
                switch (choice) {
                    case 1: // Add patient
                        patientManager.addPatient();
                        break;
                    case 2: // View patients
                        patientManager.viewPatients();
                        break;
                    case 3: // Check a specific patient
                        patientManager.checkPatient();
                        break;
                    case 4: // Remove a patient
                        patientManager.removePatient();
                        break;
                    case 5: // Exit
                        running = false; // Set running to false to exit loop
                        System.out.println("Exiting..."); // Exit message
                        break;
                    default: // Invalid choice
                        System.out.println("Invalid choice. Please try again."); // Error message
                }
            }
        } catch (SQLException e) { // Catch SQL exceptions
            System.out.println("Database connection error: " + e.getMessage()); // Error message
        } finally {
            try {
                if (connection != null) {
                    connection.close(); // Close the connection if it's not null
                }
                scanner.close(); // Close the scanner
            } catch (SQLException e) { // Catch SQL exceptions during closing
                System.out.println("Error closing connection: " + e.getMessage()); // Error message
            }
        }
    }

    // Method to add a patient
    public void addPatient() {
        // Prompt for first name
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine(); // Read first name
        // Prompt for last name
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine(); // Read last name
        // Prompt for date of birth
        System.out.print("Enter date of birth (YYYY-MM-DD): ");
        String dateOfBirth = scanner.nextLine(); // Read date of birth
        // Prompt for gender
        System.out.print("Enter gender (Male, Female, Other): ");
        String gender = scanner.nextLine(); // Read gender
        // Prompt for contact number
        System.out.print("Enter contact number: ");
        String contactNumber = scanner.nextLine(); // Read contact number
        // Prompt for email
        System.out.print("Enter email: ");
        String email = scanner.nextLine(); // Read email
        // Prompt for address
        System.out.print("Enter address: ");
        String address = scanner.nextLine(); // Read address

        // SQL query to insert a new patient
        String query = "INSERT INTO patients (first_name, last_name, date_of_birth, gender, contact_number, email, address) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) { // Prepare the statement
            preparedStatement.setString(1, firstName); // Set first name
            preparedStatement.setString(2, lastName); // Set last name
            preparedStatement.setString(3, dateOfBirth); // Set date of birth
            preparedStatement.setString(4, gender); // Set gender
            preparedStatement.setString(5, contactNumber); // Set contact number
            preparedStatement.setString(6, email); // Set email
            preparedStatement.setString(7, address); // Set address
            preparedStatement.executeUpdate(); // Execute the update
            System.out.println("Patient added successfully!"); // Success message
        } catch (SQLException e) { // Catch SQL exceptions
            System.out.println("Error adding patient: " + e.getMessage()); // Error message
        }
    }

    // Method to view all patients
    public void viewPatients() {
        // SQL query to select all patients
        String query = "SELECT * FROM patients";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query); // Prepare the statement
             ResultSet resultSet = preparedStatement.executeQuery()) { // Execute query and get results

            // Print header for patient details
            System.out.println("+------------+--------------------+------------------+------------------+----------------+--------------------+--------------------+------------------------------+");
            System.out.println("| Patient Id | First Name         | Last Name        | Date of Birth    | Gender         | Contact Number      | Email              | Address                      |");
            System.out.println("+------------+--------------------+------------------+------------------+----------------+--------------------+--------------------+------------------------------+");

            // Loop through results
            while (resultSet.next()) {
                int patientId = resultSet.getInt("patient_id"); // Get patient ID
                String firstName = resultSet.getString("first_name"); // Get first name
                String lastName = resultSet.getString("last_name"); // Get last name
                String dateOfBirth = resultSet.getString("date_of_birth"); // Get date of birth
                String gender = resultSet.getString("gender"); // Get gender
                String contactNumber = resultSet.getString("contact_number"); // Get contact number
                String email = resultSet.getString("email"); // Get email
                String address = resultSet.getString("address"); // Get address

                // Print patient details
                System.out.printf("| %-10d | %-18s | %-16s | %-16s | %-14s | %-18s | %-18s | %-28s |\n",
                        patientId, firstName, lastName, dateOfBirth, gender, contactNumber, email, address);
                System.out.println("+------------+--------------------+------------------+------------------+----------------+--------------------+--------------------+------------------------------+");
            }
        } catch (SQLException e) { // Catch SQL exceptions
            System.out.println("Error retrieving patients: " + e.getMessage()); // Error message
        }
    }

    // Method to check a patient by ID
    public void checkPatient() {
        // Prompt for patient ID
        System.out.print("Enter patient ID to check: ");
        int patientId = scanner.nextInt(); // Read patient ID
        scanner.nextLine(); // Consume newline

        // SQL query to select a patient by ID
        String query = "SELECT * FROM patients WHERE patient_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) { // Prepare the statement
            preparedStatement.setInt(1, patientId); // Set patient ID
            ResultSet resultSet = preparedStatement.executeQuery(); // Execute query

            if (resultSet.next()) { // If a patient is found
                String firstName = resultSet.getString("first_name"); // Get first name
                String lastName = resultSet.getString("last_name"); // Get last name
                String dateOfBirth = resultSet.getString("date_of_birth"); // Get date of birth
                String gender = resultSet.getString("gender"); // Get gender
                String contactNumber = resultSet.getString("contact_number"); // Get contact number
                String email = resultSet.getString("email"); // Get email
                String address = resultSet.getString("address"); // Get address

                // Print patient details
                System.out.printf("ID: %d, Name: %s %s, DOB: %s, Gender: %s, Contact: %s, Email: %s, Address: %s%n",
                        patientId, firstName, lastName, dateOfBirth, gender, contactNumber, email, address);
            } else { // If no patient is found
                System.out.println("Patient not found."); // Message
            }
        } catch (SQLException e) { // Catch SQL exceptions
            System.out.println("Error checking patient: " + e.getMessage()); // Error message
        }
    }

    // Method to remove a patient by ID
    public void removePatient() {
        // Prompt for patient ID
        System.out.print("Enter patient ID to remove: ");
        int patientId = scanner.nextInt(); // Read patient ID
        scanner.nextLine(); // Consume newline

        // SQL query to delete a patient by ID
        String query = "DELETE FROM patients WHERE patient_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) { // Prepare the statement
            preparedStatement.setInt(1, patientId); // Set patient ID
            int rowsAffected = preparedStatement.executeUpdate(); // Execute update

            if (rowsAffected > 0) { // If a patient was removed
                System.out.println("Patient removed successfully!"); // Success message
            } else { // If no patient is found
                System.out.println("Patient not found."); // Message
            }
        } catch (SQLException e) { // Catch SQL exceptions
            System.out.println("Error removing patient: " + e.getMessage()); // Error message
        }
    }


    // Method to check if a patient exists by ID (returns boolean)
    public boolean getPatientById(int patientId) {
        String query = "SELECT * FROM patients WHERE patient_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, patientId);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // Return true if a patient is found
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if no patient is found or an error occurs
    }
}
