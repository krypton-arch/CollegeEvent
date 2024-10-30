# College Event Management

College Event Management is a web-based application that allows college students to RSVP to events, view their bookings, and manage their attendance. This project aims to streamline event management, making it easier for students to discover and sign up for events.

## Features

- **User Registration and Login**: Students can create accounts and log in to the portal.
- **Event Listings**: Displays upcoming events with details for students to RSVP.
- **My RSVPs**: Allows users to view, manage, and cancel their event bookings.

## Project Structure

The project consists of the following main components:

1. **Frontend (HTML/CSS/JavaScript)**: User interface for students to view and manage events.
2. **Backend (Java Servlet)**: Server-side code to handle user data, fetch events, and manage RSVP bookings.

### Frontend Files

- **`home.html`**: Home page displaying events and sign-up options.
- **`login.html`**: Login form for user authentication.
- **`register.html`**: Registration form for user initialization
- **`my-rsvps.html`**: Page for users to view and manage their RSVPs.
- **`styles.css`**: Main stylesheet providing a modern aesthetic for the user interface.

### Backend Files

- **`MyRSVPsServlet.java`**: Java servlet handling the RSVP functionalities, such as fetching RSVP data and canceling bookings for a user.
- **`LoginServlet.java`**: Java servlet handling the login functionalities
- **`RegisterServlet.java`**: Java servlet handleing the registration of users on the website
- **`BookingServlet.java`**: Java Servlet handleing the booking functionalities of a user

## Setup and Installation

### Prerequisites

- **Java Development Kit (JDK)** 8 or higher
- **Apache Tomcat** (or any compatible servlet container)
- **Git** (for cloning the repository)
- **A modern web browser**

### Installation Steps

1. **Clone the repository**:
    ```bash
    git clone https://github.com/krypton-arch/CollegeEvent.git
    cd college-event-management
    ```

2. **Set up the Servlet in Tomcat**:
    - Move the Java servlet files (like `MyRSVPsServlet.java`) to your servlet container's web application directory.
    - Compile the servlet and restart your Tomcat server to deploy the servlet.

3. **Set up the HTML/CSS/JavaScript files**:
    - Place the HTML files (e.g., `my-rsvps.html`, `login.html`, and `index.html`) in the `webapp` folder.
    - Place the CSS files (e.g `styles.css`) in the `css` folder within `webapp`.

4. **Database Setup** (Optional):
    - For a persistent RSVP system, configure a database (e.g., MySQL) and modify the servlet to handle database connections for storing and retrieving RSVPs.

5. **Access the Application**:
    - Open your web browser and navigate to the deployed URL (e.g., `http://localhost:8080/college-event-management`).

## Usage

### Registering and Logging In

1. **Register** for an account through `index.html` or `register.html`.
2. **Log In** to access the `College Event List` page, where you can view and book rsvps for the event.
3. 

### Viewing and Managing RSVPs

1. Access the `My RSVPs` page to see all your current bookings.
2. Each booking displays the event name, booking date, and a cancel button.
3. **Canceling a Booking**:
    - Click the **Cancel** button next to a booking. This action triggers the `cancelBooking` function in JavaScript, which communicates with `MyRSVPsServlet.java` to process the cancellation.

## Code Overview

### JavaScript

- **`my-rsvps.html`**:
    - Fetches RSVP data on page load and populates the table with each RSVP.
    - Allows users to cancel bookings via `cancelBooking` function, which updates the UI and removes the booking entry upon successful response.

### Java Servlet

- **`MyRSVPsServlet.java`**:
    - **GET** request: Returns a JSON list of the user’s RSVPs with event name and booking date.
    - **POST** request: Processes booking cancellations by accepting the booking ID as a parameter.

### CSS

- **`styles.css`**:
    - Defines the layout and styling for the entire application with a modern, user-friendly design.
    - Styling includes a maroon navbar, light-themed table, button styles, and responsive design to enhance user experience.

## Contribution Guidelines

1. **Fork** the repository and create a new branch for your feature.
2. Follow best practices for HTML, CSS, and Java code.
3. Test your changes thoroughly before creating a pull request.
4. Submit a detailed pull request explaining your changes.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

For further questions or feedback, please open an issue on GitHub or contact the project maintainer directly.

---

This project is intended for educational purposes as a sample college event management tool.
