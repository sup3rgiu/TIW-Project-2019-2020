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
    private Integer nextGroupId = null;
    private Integer previousGroupId = null;

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
        return getFiveImages(album_id, null, null);
    }

    public List<Image> getImagesByAlbumAndGroupId(int albumid, Integer groupid) throws SQLException {
        return getFiveImages(albumid, null, groupid);
    }

    public List<Image> getImagesByAlbumAndImageId(int albumid, Integer imageid) throws SQLException {
        return getFiveImages(albumid, imageid, null);
    }

    private List<Image> getFiveImages(int albumid, Integer imageid, Integer groupid) throws SQLException {
        List<Image> images = new ArrayList<>();
        String query = "WITH\n" +
                "  cte AS (SELECT *, FLOOR((ROW_NUMBER() OVER(ORDER BY date DESC)-1)/5)+1 AS grpNmb FROM image WHERE album_id = ?),\n" +    // add a column with the group number. One group each 5 rows
                "  cte2 AS(SELECT count(DISTINCT grpNmb) as totalGroupsNumber FROM cte)\n" +                                                // add a column with the total number of groups
                "SELECT *\n" +
                "FROM cte, cte2\n" +
                "WHERE grpNmb = (\n" +
                "CASE\n" +
                "  WHEN ? IS NOT NULL\n" +                                // if groupid is not null, use it to select the 5 images
                "\t THEN ?\n" +
                "  WHEN ? IS NOT NULL\n" +                                // if imageid is not null, use it to select the 5 images
                "\t THEN (SELECT grpNmb FROM cte WHERE id = ?)\n" +        // find the group number of the image with the given imageid
                "  ELSE 1\n" +                                      // else get the 5 images in the first group
                "END\n" +
                ")\n" +
                "LIMIT 5";      // just to be sure, but should always be 5 or less
        boolean isFirst = true;
        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setInt(1, albumid);

            if (groupid != null) {
                pstatement.setInt(2, groupid);
                pstatement.setInt(3, groupid);
            } else {
                pstatement.setNull(2, java.sql.Types.INTEGER);
                pstatement.setNull(3, java.sql.Types.INTEGER);
            }

            if (imageid != null) {
                pstatement.setInt(4, imageid);
                pstatement.setInt(5, imageid);
            } else {
                pstatement.setNull(4, java.sql.Types.INTEGER);
                pstatement.setNull(5, java.sql.Types.INTEGER);
            }

            try (ResultSet result = pstatement.executeQuery()) {
                while (result.next()) {
                    Image image = new Image();
                    image.setId(result.getInt("id"));
                    image.setName(result.getString("name"));
                    image.setDescription(result.getString("description"));
                    image.setFilepath(result.getString("filepath"));
                    image.setDate(result.getDate("date"));
                    images.add(image);

                    if(isFirst) {
                        int resultGroupId = result.getInt("grpNmb");
                        int numberOfGroups = result.getInt("totalGroupsNumber");
                        if(resultGroupId < numberOfGroups)
                            nextGroupId = resultGroupId + 1;
                        if(resultGroupId > 1)
                            previousGroupId = resultGroupId - 1;
                        isFirst = false;
                    }
                }
            }
        }
        return images;
    }

    public Integer getNextGroupId() {
        return nextGroupId;
    }

    public Integer getPreviousGroupId() {
        return previousGroupId;
    }

}
