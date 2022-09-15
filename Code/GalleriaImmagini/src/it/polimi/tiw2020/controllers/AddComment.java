package it.polimi.tiw2020.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw2020.beans.Comment;
import it.polimi.tiw2020.beans.Image;
import it.polimi.tiw2020.beans.User;
import it.polimi.tiw2020.dao.CommentDAO;
import it.polimi.tiw2020.dao.ImageDAO;
import it.polimi.tiw2020.utils.ConnectionHandler;
import org.apache.commons.lang.StringUtils;

@WebServlet("/AddComment")
public class AddComment extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public AddComment() {
        super();
    }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();

        Integer albumid = (Integer) session.getAttribute("chosenAlbum");
        if(albumid == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No album chosen");
            return;
        }

        // get and check params
        Integer imageId = null;
        Comment comment = null;
        try {
            imageId = Integer.parseInt(request.getParameter("imageid"));
            String commentText = request.getParameter("comment");
            if(StringUtils.isBlank(commentText)) {
                return; // do not add empty comment
            }
            comment = new Comment();
            comment.setText(commentText.trim());
        } catch (NumberFormatException | NullPointerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
            return;
        }

        // Add comment
        User user = (User) session.getAttribute("user");
        CommentDAO commentDAO = new CommentDAO(connection);
        ImageDAO imageDAO = new ImageDAO(connection);
        try {
            Image image = imageDAO.findImageById(imageId);
            if (image == null) {
                response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Image not found");
                return;
            }
            commentDAO.addComment(comment, image, user);
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Commment can not be added");
            return;
        }

        // Return view
        String ctxpath = getServletContext().getContextPath();
        String path = ctxpath + "/GetAlbum?albumid=" + albumid + "&imageid=" + imageId + "&details=true";
        response.sendRedirect(path);

    }

    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
