package it.polimi.tiw2020.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.tiw2020.beans.Comment;
import it.polimi.tiw2020.beans.Image;
import it.polimi.tiw2020.dao.AlbumDAO;
import it.polimi.tiw2020.dao.CommentDAO;
import it.polimi.tiw2020.dao.ImageDAO;
import it.polimi.tiw2020.utils.ConnectionHandler;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@WebServlet("/GetAlbum")
public class GetAlbum extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public GetAlbum() {
        super();
    }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // get and check params
        Integer albumid = null;
        try {
            albumid = Integer.parseInt(request.getParameter("albumid"));
        } catch (NumberFormatException | NullPointerException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Incorrect param values");
            return;
        }

        // get images
        ImageDAO imageDAO = new ImageDAO(connection);
        List<Image> images = new ArrayList<>();
        try {
            images = imageDAO.getImagesByAlbumId(albumid);
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR , "Not possible to recover images for this album");
            return;
        }

        // get comments and add them to the images
        CommentDAO commentDAO = new CommentDAO(connection);
        for (Image image : images) {
            List<Comment> comments = new ArrayList<>();
            try {
                comments = commentDAO.getCommentsByImageId(image.getId());
            } catch (SQLException e) {
            }
            image.setCommentsList(comments);
        }

        Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
        String json = gson.toJson(images);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }

    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
