package college;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@WebServlet("/bookEvent")
public class BookingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // JDBC connection parameters
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/user_management";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASS = "root";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        // Set response type to JSON
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        // Retrieve the event name from request
        String event = request.getParameter("event");
        
        // Validate input
        if (event == null || event.trim().isEmpty()) {
            out.write(gson.toJson(new Response(false, "Event name cannot be empty")));
            return;
        }

        // Get the user's ID from the session (assuming you stored user ID in the session)
        HttpSession session = request.getSession(false);
        if (session == null) {
            out.write(gson.toJson(new Response(false, "Session does not exist")));
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            out.write(gson.toJson(new Response(false, "User ID not found in session")));
            return;
        }

        String userEmail = null;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");  // Load MySQL JDBC driver
        } catch (ClassNotFoundException e) {
            log("MySQL JDBC Driver not found.", e);
            out.write(gson.toJson(new Response(false, "Internal server error")));
            return;
        }

        // Database connection and user email retrieval
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
             PreparedStatement emailStatement = connection.prepareStatement(
                     "SELECT email FROM users WHERE id = ?")) {

            // Set parameters for the SQL statement
            emailStatement.setInt(1, userId);

            // Execute query and get the result
            try (ResultSet resultSet = emailStatement.executeQuery()) {
                if (resultSet.next()) {
                    userEmail = resultSet.getString("email");
                } else {
                    out.write(gson.toJson(new Response(false, "User email not found")));
                    return;
                }
            }

            // Insert booking into the bookings table
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO bookings (event_name, user_email, booking_date) VALUES (?, ?, ?)")) {

                // Set parameters for the SQL statement
                preparedStatement.setString(1, event);
                preparedStatement.setString(2, userEmail);
                preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis())); // Current timestamp

                // Execute update and check the result
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    out.write(gson.toJson(new Response(true, "Booking successful")));
                } else {
                    out.write(gson.toJson(new Response(false, "Failed to book the event")));
                }
            }

        } catch (SQLException e) {
            // Handle SQL errors
            e.printStackTrace();
            out.write(gson.toJson(new Response(false, "Database error")));
        }
    }

    // Response class for structured JSON output
    private static class Response {
        private final boolean success;
        private final String message;

        public Response(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}
