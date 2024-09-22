package HospitalManagementSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Doctors {
    private Connection connection;

    // Constructor to initialize connection
    public Doctors(Connection connection) {
        this.connection = connection;
    }

    // Main method for testing
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/HOSPITAL";
        String username = "root";
        String password = "******";

        Connection connection = null;
        Scanner scanner = new Scanner(System.in);

        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to the database successfully!");

            Doctors doctorsManager = new Doctors(connection);

            boolean running = true;
            while (running) {
                System.out.println("Choose an action:");
                System.out.println("1. View Doctors");
                System.out.println("2. Check Doctor by ID");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        doctorsManager.viewDoctors();
                        break;
                    case 2:
                        System.out.print("Enter doctor ID to check: ");
                        int doctorId = scanner.nextInt();
                        doctorsManager.getDoctorById(doctorId);
                        break;
                    case 3:
                        running = false;
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                scanner.close();
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public void viewDoctors() {
        String query = "SELECT * FROM doctors";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            System.out.println("+------------+--------------------+------------------+----------------+---------------------+--------------------+-----------------------+");
            System.out.println("| Doctor Id  | First Name         | Last Name       | Specialty      | Contact Number      | Email              | Years of Experience   |");
            System.out.println("+------------+--------------------+------------------+----------------+---------------------+--------------------+-----------------------+");

            while (resultSet.next()) {
                // Print doctor details
                System.out.printf("| %-10d | %-18s | %-16s | %-14s | %-18s | %-18s | %-21d |\n",
                        resultSet.getInt("doctor_id"), resultSet.getString("first_name"),
                        resultSet.getString("last_name"), resultSet.getString("specialty"),
                        resultSet.getString("contact_number"), resultSet.getString("email"),
                        resultSet.getInt("years_of_experience"));
                System.out.println("+------------+--------------------+------------------+----------------+---------------------+--------------------+-----------------------+");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving doctors: " + e.getMessage());
        }
    }

    public boolean getDoctorById(int id) {
        String query = "SELECT * FROM doctors WHERE doctor_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                // Print doctor details
                System.out.printf("Doctor ID: %d, Name: %s %s, Specialty: %s, Contact: %s, Email: %s, Years of Experience: %d%n",
                        resultSet.getInt("doctor_id"), resultSet.getString("first_name"),
                        resultSet.getString("last_name"), resultSet.getString("specialty"),
                        resultSet.getString("contact_number"), resultSet.getString("email"),
                        resultSet.getInt("years_of_experience"));
                return true;
            } else {
                System.out.println("Doctor not found.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error checking doctor: " + e.getMessage());
            return false;
        }
    }

    public void viewDoctorAppointmentHistory(int doctorId) {
        String query = "SELECT a.appointment_date, a.appointment_time, p.first_name, p.last_name " +
                "FROM appointments a JOIN patients p ON a.patient_id = p.patient_id " +
                "WHERE a.doctor_id = ? ORDER BY a.appointment_date DESC";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, doctorId);
            ResultSet resultSet = preparedStatement.executeQuery();

            System.out.println("+---------------------+-----------------+------------------+");
            System.out.println("| Appointment Date    | Appointment Time| Patient Name     |");
            System.out.println("+---------------------+-----------------+------------------+");

            while (resultSet.next()) {
                String appointmentDate = resultSet.getString("appointment_date");
                String appointmentTime = resultSet.getString("appointment_time");
                String patientName = resultSet.getString("first_name") + " " + resultSet.getString("last_name");

                System.out.printf("| %-19s | %-15s | %-16s |\n", appointmentDate, appointmentTime, patientName);
                System.out.println("+---------------------+-----------------+------------------+");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving appointment history: " + e.getMessage());
        }
    }
}
