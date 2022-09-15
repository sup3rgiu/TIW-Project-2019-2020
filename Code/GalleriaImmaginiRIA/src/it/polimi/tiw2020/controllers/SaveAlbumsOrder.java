package it.polimi.tiw2020.controllers;

import com.google.gson.*;

import it.polimi.tiw2020.beans.User;
import it.polimi.tiw2020.dao.UserDAO;
import it.polimi.tiw2020.utils.ConnectionHandler;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/SaveAlbumsOrder")
public class SaveAlbumsOrder extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public SaveAlbumsOrder() {
        super();
    }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        List<Integer> orderedIdList = new ArrayList<>(); // will contain all the album IDs in the desired order
        JsonElement orderElem = JsonParser.parseReader(request.getReader()).getAsJsonObject().get("order"); // get JSON from request body

        // check if the JSON is well-formed and add each album_id to a List
        if(orderElem != null && orderElem.isJsonArray()) {
            JsonArray albumsIdOrder = orderElem.getAsJsonArray();
            albumsIdOrder.forEach( jsonElement -> {
                if(jsonElement.getAsJsonPrimitive().isNumber()) {
                    orderedIdList.add(jsonElement.getAsInt());
                }
            });
        }

        // add the desired albums' order in the db (and in session) for this user
        if(orderedIdList.size() > 0) {
            String jsonList = new Gson().toJson(orderedIdList);
            UserDAO userDAO = new UserDAO(connection);
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            try {
                userDAO.saveAlbumsOrderForUser(jsonList, user);
                user.setAlbumsSorting(orderedIdList);
            } catch (SQLException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Albums order can not be saved");
                return;
            }
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
