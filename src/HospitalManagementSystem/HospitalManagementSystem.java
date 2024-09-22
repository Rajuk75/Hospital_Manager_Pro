package HospitalManagementSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/HOSPITAL"; // Database URL
    private static final String username = "root"; // Database username
    private static final String password = "*******"; // Database password

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load the MySQL JDBC driver
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return; // Exit if the driver isn't found
        }

        Scanner scanner = new Scanner(System.in); // Scanner for user input
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(url, username, password); // Establish database connection
            Patient patient = new Patient(connection, scanner); // Create Patient instance
            Doctors doctors = new Doctors(connection); // Create Doctors instance

            // Code edited by: Raju Kumar, BTech_ECE, Greater Noida

            while (true) {
                printStyledBox("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. View Doctor Appointment History");
                System.out.println("6. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        patient.addPatient();
                        break;
                    case 2:
                        patient.viewPatients();
                        break;
                    case 3:
                        doctors.viewDoctors();
                        break;
                    case 4:
                        bookAppointment(patient, doctors, connection, scanner);
                        break;
                    case 5:
                        System.out.print("Enter Doctor Id to view appointment history: ");
                        int doctorId = scanner.nextInt();
                        doctors.viewDoctorAppointmentHistory(doctorId);
                        break;
                    case 6:
                        System.out.println("THANK YOU FOR USING HOSPITAL MANAGEMENT SYSTEM!!");
                        return; // Exit the program
                    default:
                        System.out.println("Enter valid choice!!!");
                        break;
                }
            }
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage()); // Error message
        } finally {
            try {
                if (connection != null) {
                    connection.close(); // Close the connection if it's not null
                }
                scanner.close(); // Close the scanner
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage()); // Error message during closing
            }
        }
    }

    // Method to book an appointment
    public static void bookAppointment(Patient patient, Doctors doctors, Connection connection, Scanner scanner) {
        System.out.print("Enter Patient Id: ");
        int patientId = scanner.nextInt();
        System.out.print("Enter Doctor Id: ");
        int doctorId = scanner.nextInt();
        System.out.print("Enter appointment date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();
        System.out.print("Enter appointment time (HH:MM:SS): "); // New input for time
        String appointmentTime = scanner.next();

        // Check if patient and doctor exist
        if (patient.getPatientById(patientId) && doctors.getDoctorById(doctorId)) {
            if (checkDoctorAvailability(doctorId, appointmentDate, connection)) {
                String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date, appointment_time) VALUES(?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery)) {
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);
                    preparedStatement.setString(4, appointmentTime); // Set the appointment time
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Appointment Booked!");
                    } else {
                        System.out.println("Failed to Book Appointment!");
                    }
                } catch (SQLException e) {
                    System.out.println("Error booking appointment: " + e.getMessage());
                }
            } else {
                System.out.println("Doctor not available on this date!!");
            }
        } else {
            System.out.println("Either doctor or patient doesn't exist!!!");
        }
    }

    // Method to check doctor availability
    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection) {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count == 0; // Return true if no appointments exist for that doctor on that date
            }
        } catch (SQLException e) {
            System.out.println("Error checking doctor availability: " + e.getMessage());
        }
        return false; // Default return value if an error occurs
    }

    // Method to print a styled box
    public static void printStyledBox(String message) {
        int length = message.length() + 4;
        System.out.println("+" + "-".repeat(length) + "+");
        System.out.println("|  " + message + "  |");
        System.out.println("+" + "-".repeat(length) + "+");
    }
}
