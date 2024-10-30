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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/user_management";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASS = "root";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (isInvalidInput(email, password)) {
            out.write(gson.toJson(new Response(false, "Email and password cannot be empty")));
            out.close();
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            log("MySQL JDBC Driver not found.", e);
            out.write(gson.toJson(new Response(false, "Internal server error")));
            out.close();
            return;
        }

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT id, password FROM users WHERE email = ?")) {

            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt("id");
                String storedPassword = resultSet.getString("password");

                if (password.equals(storedPassword)) {
                    HttpSession session = request.getSession();
                    session.setAttribute("userId", userId);
                    session.setAttribute("userEmail", email);
                    session.setMaxInactiveInterval(30 * 60);

                    out.write(gson.toJson(new Response(true, "Login successful")));
                } else {
                    out.write(gson.toJson(new Response(false, "Invalid email or password")));
                }
            } else {
                out.write(gson.toJson(new Response(false, "Invalid email or password")));
            }
            out.close();

        } catch (SQLException e) {
            log("Database error during login process.", e);
            out.write(gson.toJson(new Response(false, "Database error")));
            out.close();
        }
    }

    private boolean isInvalidInput(String email, String password) {
        return email == null || password == null || email.isEmpty() || password.isEmpty();
    }

    public static class Response {
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
