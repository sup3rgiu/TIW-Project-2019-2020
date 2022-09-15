package it.polimi.tiw2020.beans;

import java.util.List;

public class User {

	private int id;
	private String username;
	private String email;
	private List<Integer> albumsSorting;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<Integer> getAlbumsSorting() {
		return albumsSorting;
	}

	public void setAlbumsSorting(List<Integer> albumsSorting) {
		this.albumsSorting = albumsSorting;
	}
}
