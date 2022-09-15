package it.polimi.tiw2020.dao;

import com.google.gson.Gson;
import it.polimi.tiw2020.beans.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserDAO {
	private Connection con;

	public UserDAO(Connection connection) {
		this.con = connection;
	}

	public User checkCredentials(String username, String pwd) throws SQLException {
		String query = "SELECT id, username, email, albumsOrder FROM user WHERE username = ? AND password =?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, username);
			pstatement.setString(2, pwd);
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					User user = new User();
					user.setId(result.getInt("id"));
					user.setUsername(result.getString("username"));
					user.setEmail(result.getString("email"));
					user.setAlbumsSorting(populateAlbumsOrder(result.getString("albumsOrder")));
					return user;
				}
			}
		}
	}

	private List<Integer> populateAlbumsOrder(String jsonOrder) {
		if(jsonOrder == null) {
			return new ArrayList<>();
		}
		int[] ids = new Gson().fromJson(jsonOrder, int[].class); // JSON string to int[]
		List<Integer> albumsOrder = Arrays.stream(ids).boxed().collect(Collectors.toList()); // int[] to List<Integer>
		return albumsOrder;
	}

	public boolean checkUserExists(String username) throws  SQLException {
		String query = "SELECT id FROM user WHERE username = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setString(1, username);
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst()) // no results, username does not exist
					return false;
				else {
					return true;
				}
			}
		}
	}

	public boolean checkEmailExists(String email) throws  SQLException {
		String query = "SELECT id FROM user WHERE email = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setString(1, email);
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst()) // no results, email does not exist
					return false;
				else {
					return true;
				}
			}
		}
	}

	public User addUser(String username, String email, String pwd) throws SQLException {
		String query = "INSERT INTO user (username, email, password) VALUES (?, ?, ?)";
		try (PreparedStatement pstatement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			pstatement.setString(1, username);
			pstatement.setString(2, email);
			pstatement.setString(3, pwd);
			pstatement.executeUpdate();
			ResultSet generatedKeys = pstatement.getGeneratedKeys();
			if (generatedKeys.next()) {
				User user = new User();
				user.setId(generatedKeys.getInt(1));
				user.setUsername(username);
				user.setEmail(email);
				user.setAlbumsSorting(new ArrayList<>());
				return user;
			} else {
				throw new SQLException("Creating comment failed, no ID obtained.");
			}
		}
	}

	public void saveAlbumsOrderForUser(String jsonOrder, User user) throws SQLException {
		String query = "UPDATE user SET albumsOrder = ? WHERE id = ?;";
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setString(1, jsonOrder);
			pstatement.setInt(2, user.getId());
			pstatement.executeUpdate();
		}
	}
}
