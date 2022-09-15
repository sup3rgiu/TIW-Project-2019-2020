package it.polimi.tiw2020.dao;

import it.polimi.tiw2020.beans.Image;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ImageDAO {
    private Connection con;

    public ImageDAO(Connection connection) {
        this.con = connection;
    }

    public Image findImageById(int imageid) throws SQLException {
        Image image = null;

        String query = "SELECT * FROM image WHERE id = ?";
        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setInt(1, imageid);
            try (ResultSet result = pstatement.executeQuery();) {
                if (result.next()) {
                    image = new Image();
                    image.setId(result.getInt("id"));
                    image.setName(result.getString("name"));
                    image.setDescription(result.getString("description"));
                    image.setFilepath(result.getString("filepath"));
                    image.setDate(result.getDate("date"));
                }
            }
        }
        return image;
    }

    public List<Image> getImagesByAlbumId(int album_id) throws SQLException {
        List<Image> images = new ArrayList<>();
        String query = "SELECT * FROM image WHERE album_id = ? ORDER BY date DESC";
        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setInt(1, album_id);
            try (ResultSet result = pstatement.executeQuery()) {
                while (result.next()) {
                    Image image = new Image();
                    image.setId(result.getInt("id"));
                    image.setName(result.getString("name"));
                    image.setDescription(result.getString("description"));
                    image.setFilepath(result.getString("filepath"));
                    image.setDate(result.getDate("date"));
                    images.add(image);
                }
            }
        }
        return images;
    }

}
