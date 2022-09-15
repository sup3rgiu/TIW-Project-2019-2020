package it.polimi.tiw2020.dao;

import it.polimi.tiw2020.beans.Album;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlbumDAO {
    private Connection con;

    public AlbumDAO(Connection connection) {
        this.con = connection;
    }

    public List<Album> getAllAlbums() throws SQLException {
        List<Album> albums = new ArrayList<>();
        String query = "SELECT * FROM album ORDER BY date DESC";
        try (PreparedStatement pstatement = con.prepareStatement(query);) {
            try (ResultSet result = pstatement.executeQuery();) {
                while (result.next()) {
                    Album album = new Album();
                    album.setId(result.getInt("id"));
                    album.setName(result.getString("name"));
                    album.setDate(result.getDate("date"));
                    albums.add(album);
                }
            }
        }
        return albums;
    }

}
