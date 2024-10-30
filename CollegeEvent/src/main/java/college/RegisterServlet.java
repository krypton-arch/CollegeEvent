package college;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // JDBC connection parameters
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/user_management";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASS = "root";

    // SQL query for inserting a new user
    private static final String INSERT_USER_SQL = "INSERT INTO users(name, email, password) VALUES(?,?,?)";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Set response type to HTML
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Retrieve input data from form
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Simple input validation
        if (name == null || email == null || password == null || 
            name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            out.println("<h3>Error: All fields are required!</h3>");
            return;
        }

        // Load MySQL JDBC Driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");  // Load MySQL JDBC driver
        } catch (ClassNotFoundException e) {
            out.println("<h3>Error: MySQL JDBC Driver not found.</h3>");
            e.printStackTrace();
            return;
        }

        // Database connection and user insertion
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL)) {

            // Set parameters for the SQL statement
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);

            // Execute update and check the result
            int row = preparedStatement.executeUpdate();
            if (row > 0) {
                // Redirect to login.html after successful registration
                response.sendRedirect("login.html");
            } else {
                out.println("<h3>Error: Registration failed. Please try again.</h3>");
            }

        } catch (SQLException e) {
            // Log specific SQL error details
            out.println("<h3>Error: Unable to connect to the database.</h3>");
            out.println("<p>SQL State: " + e.getSQLState() + "</p>");
            out.println("<p>Error Code: " + e.getErrorCode() + "</p>");
            out.println("<p>Message: " + e.getMessage() + "</p>");
            e.printStackTrace();
        }
    }
}
