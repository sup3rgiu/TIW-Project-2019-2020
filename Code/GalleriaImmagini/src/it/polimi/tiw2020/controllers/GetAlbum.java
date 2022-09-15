package it.polimi.tiw2020.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw2020.beans.Comment;
import it.polimi.tiw2020.beans.Image;
import it.polimi.tiw2020.dao.CommentDAO;
import it.polimi.tiw2020.dao.ImageDAO;
import it.polimi.tiw2020.utils.ConnectionHandler;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;


@WebServlet("/GetAlbum")
public class GetAlbum extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;

    public GetAlbum() {
        super();
    }

    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");

        connection = ConnectionHandler.getConnection(getServletContext());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();

        // get and check params
        Integer albumid = getAlbumId(request, session);
        if(albumid == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
            return;
        }

        Integer imageid = getImageId(request, session);
        Integer groupid = getGroupId(request, session);

        // get images depending on the specified parameter
        ImageDAO imageDAO = new ImageDAO(connection);
        List<Image> images = new ArrayList<>();
        try {
            if(imageid == null && groupid == null) {
                images = imageDAO.getImagesByAlbumId(albumid); // nothing specified -> get first 5 images of the album
            } else {
                if(imageid != null)
                    images = imageDAO.getImagesByAlbumAndImageId(albumid, imageid); // imageid specified -> get the group of 5 images that contains this image
                else
                    images = imageDAO.getImagesByAlbumAndGroupId(albumid, groupid); // groupid specified -> get this group of 5 images
            }
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover images for this album");
            return;
        }

        // add details to image if needed
        boolean showImageDetails = Boolean.parseBoolean(request.getParameter("details"));
        List<Comment> comments = new ArrayList<>();
        Image detailedImage = null;
        if(imageid != null && showImageDetails) {
            try {
                CommentDAO commentDAO = new CommentDAO(connection);
                comments = commentDAO.getCommentsByImageId(imageid);
                detailedImage = images.stream().filter(image -> image.getId() == imageid).findFirst().orElse(null); // find the image that need details between those got above
            } catch (SQLException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover comments for this image");
                return;
            }
        }

        String path = "/WEB-INF/Album.html";
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        ctx.setVariable("images", images);
        ctx.setVariable("nextGroupId", imageDAO.getNextGroupId());
        ctx.setVariable("previousGroupId", imageDAO.getPreviousGroupId());
        ctx.setVariable("showImageDetails", showImageDetails);
        if(detailedImage != null) {
            ctx.setVariable("comments", comments);
            ctx.setVariable("detailedImage", detailedImage);
        }
        session.setAttribute("chosenAlbum", albumid);
        templateEngine.process(path, ctx, response.getWriter());
    }

    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* HELPERS */

    private Integer getAlbumId(HttpServletRequest request, HttpSession session) {
        String chosenAlbum = request.getParameter("albumid");
        Integer albumid = null;
        try {
            if(chosenAlbum != null) {
                albumid = Integer.parseInt(chosenAlbum);
            } else {
                albumid = (Integer) session.getAttribute("chosenAlbum");
            }

        } catch (NumberFormatException | NullPointerException e) {
        }

        return albumid;
    }

    private Integer getImageId(HttpServletRequest request, HttpSession session) {
        String chosenImage = request.getParameter("imageid");
        Integer imageid = null;
        try {
            if(chosenImage != null) {
                imageid = Integer.parseInt(chosenImage);
            } else {
                imageid = (Integer) session.getAttribute("chosenImage");
            }

        } catch (NumberFormatException | NullPointerException e) {
        }

        return imageid;
    }

    private Integer getGroupId(HttpServletRequest request, HttpSession session) {
        String group = request.getParameter("groupid");
        Integer groupid = null;
        try {
            if(group != null)
                groupid = Integer.parseInt(group);
        } catch (NumberFormatException | NullPointerException e) {
        }

        return groupid;
    }

}
