package it.polimi.tiw2020.controllers;

import it.polimi.tiw2020.beans.User;
import it.polimi.tiw2020.dao.UserDAO;
import it.polimi.tiw2020.utils.ConnectionHandler;
import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Pattern;

@WebServlet("/Signup")
@MultipartConfig
public class Signup extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public Signup() {
        super();
    }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // obtain and escape params
        String usrn, email, pwd, confirm_pwd;
        usrn = StringEscapeUtils.escapeJava(request.getParameter("username"));
        email = StringEscapeUtils.escapeJava(request.getParameter("email"));
        pwd = StringEscapeUtils.escapeJava(request.getParameter("pwd"));
        confirm_pwd = StringEscapeUtils.escapeJava(request.getParameter("confirm_pwd"));

        // check params integrity
        if (usrn == null || email == null || pwd == null || confirm_pwd == null || usrn.isEmpty() || email.isEmpty() || pwd.isEmpty() || confirm_pwd.isEmpty() ) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Credentials must be not null");
            return;
        }

        if(!pwd.equals(confirm_pwd)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("Passwords don't match");;
            return;
        }

        final String email_regex = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        final Pattern pattern = Pattern.compile(email_regex);
        if(!pattern.matcher(email).matches()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("Email is not valid");;
            return;
        }


        // query db to check if this username or email is already in use
        UserDAO userDao = new UserDAO(connection);
        try {
            boolean emailExists = userDao.checkEmailExists(email);
            if(emailExists) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().println("Email already in use");;
                return;
            }

            boolean userExists = userDao.checkUserExists(usrn);
            if(userExists) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().println("Username already in use");;
                return;
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Internal server error, retry later");
            return;
        }

        // add user
        User user = null;
        try {
            user = userDao.addUser(usrn, email, pwd);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Internal server error, retry later");
            return;
        }

        // If the user has been created, add info to the session and go to home page, otherwise
        // return an error status code and message
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Something went wrong. Try again");
        } else {
            request.getSession().setAttribute("user", user);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(usrn);
        }
    }

    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
