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
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/myRSVPs")
public class MyRSVPsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/user_management";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASS = "root";

    @Override
    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL JDBC driver once
        } catch (ClassNotFoundException e) {
            log("MySQL JDBC Driver not found", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");

        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession(false);
            String userEmail = (session != null) ? (String) session.getAttribute("userEmail") : null;

            if (userEmail == null) {
                out.write(new Gson().toJson(new ErrorResponse("User not logged in")));
                return;
            }

            String userName = getUserName(userEmail);
            if (userName == null) {
                out.write(new Gson().toJson(new ErrorResponse("User not found")));
                return;
            }

            List<Booking> bookings = getUserBookings(userEmail);
            out.write(new Gson().toJson(new UserRSVPsResponse(userName, bookings)));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");

        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession(false);
            String userEmail = (session != null) ? (String) session.getAttribute("userEmail") : null;

            if (userEmail == null) {
                out.write(new Gson().toJson(new ErrorResponse("User not logged in")));
                return;
            }

            String bookingIdParam = request.getParameter("bookingId");
            try {
                int bookingId = Integer.parseInt(bookingIdParam);
                boolean isCancelled = cancelBooking(bookingId, userEmail);
                out.write(new Gson().toJson(isCancelled ? 
                    new SuccessResponse("Booking cancelled successfully") : 
                    new ErrorResponse("Failed to cancel booking")));
            } catch (NumberFormatException e) {
                out.write(new Gson().toJson(new ErrorResponse("Invalid booking ID")));
            }
        }
    }

    private String getUserName(String email) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT name FROM users WHERE email = ?")) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("name");
            }
        } catch (SQLException e) {
            log("Database error retrieving user name", e);
        }
        return null;
    }

    private List<Booking> getUserBookings(String email) {
        List<Booking> bookings = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT id, event_name, booking_date FROM bookings WHERE user_email = ?")) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                bookings.add(new Booking(
                        resultSet.getInt("id"),
                        resultSet.getString("event_name"),
                        resultSet.getString("booking_date")));
            }
        } catch (SQLException e) {
            log("Database error retrieving bookings", e);
        }
        return bookings;
    }

    private boolean cancelBooking(int bookingId, String userEmail) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "DELETE FROM bookings WHERE id = ? AND user_email = ?")) {
            preparedStatement.setInt(1, bookingId);
            preparedStatement.setString(2, userEmail);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            log("Database error cancelling booking", e);
        }
        return false;
    }

    private static class UserRSVPsResponse {
        private final String name;
        private final List<Booking> bookings;

        public UserRSVPsResponse(String name, List<Booking> bookings) {
            this.name = name;
            this.bookings = bookings;
        }
    }

    private static class Booking {
        private final int id;
        private final String eventName;
        private final String bookingDate;

        public Booking(int id, String eventName, String bookingDate) {
            this.id = id;
            this.eventName = eventName;
            this.bookingDate = bookingDate;
        }
    }

    private static class ErrorResponse {
        private final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }
    }

    private static class SuccessResponse {
        private final String message;

        public SuccessResponse(String message) {
            this.message = message;
        }
    }
}
