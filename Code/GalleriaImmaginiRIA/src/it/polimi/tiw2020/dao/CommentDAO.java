package it.polimi.tiw2020.dao;

import it.polimi.tiw2020.beans.Comment;
import it.polimi.tiw2020.beans.Image;
import it.polimi.tiw2020.beans.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {
    private Connection con;

    public CommentDAO(Connection connection) {
        this.con = connection;
    }

    public List<Comment> getCommentsByImageId(int imageid) throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String query = "SELECT idcomment, text, username FROM image_comments_view WHERE idimage = ? ORDER BY idcomment DESC";
        try (PreparedStatement pstatement = con.prepareStatement(query);) {
            pstatement.setInt(1, imageid);
            try (ResultSet result = pstatement.executeQuery()) {
                while(result.next()) {
                    Comment comment = new Comment();
                    comment.setId(result.getInt("idcomment"));
                    comment.setText(result.getString("text"));
                    comment.setUsername(result.getString("username"));
                    comments.add(comment);
                }
            }
        }
        return comments;
    }

    public int addComment(Comment comment, Image image, User user) throws SQLException {
        if (image == null || comment == null || user == null) {
            throw new NullPointerException();
        }
        String query = "INSERT into comment (text, image_id, user_id) VALUES(?, ?, ?)";
        try (PreparedStatement pstatement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            pstatement.setString(1, comment.getText());
            pstatement.setDouble(2, image.getId());
            pstatement.setDouble(3, user.getId());
            pstatement.executeUpdate();
            ResultSet generatedKeys = pstatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating comment failed, no ID obtained.");
            }
        }
    }
}
