package it.polimi.tiw2020.controllers;

import it.polimi.tiw2020.beans.Album;
import it.polimi.tiw2020.beans.User;
import it.polimi.tiw2020.dao.AlbumDAO;
import it.polimi.tiw2020.utils.ConnectionHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/GetAlbumsList")
public class GetAlbumsList extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetAlbumsList() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		AlbumDAO albumDAO = new AlbumDAO(connection);
		List<Album> albums = new ArrayList<>();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");

		try {
			albums = albumDAO.getAllAlbums();
			if(user.getAlbumsSorting().size() > 0) { // if the user has a non-default sorting
				albums = sortAlbums(albums, user.getAlbumsSorting());
			}
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover albums");
			return;
		}

		Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
		String json = gson.toJson(albums);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private List<Album> sortAlbums(List<Album> albums, List<Integer> orderedIdList) {

		List<Album> orderedAlbums = albums.stream().filter( album -> orderedIdList.contains(album.getId())).collect(Collectors.toList()); // keep only the albums present in the orderedIdList
		orderedAlbums.sort(Comparator.comparing( album -> orderedIdList.indexOf(album.getId()))); // sort these albums depending on the orderedIdList
		albums.removeAll(orderedAlbums); // remove from all albums the ones we have just ordered
		orderedAlbums.addAll(albums); // add at the bottom of the orderedAlbums the remaining albums

		return orderedAlbums;
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
