package it.polimi.tiw2020.dao;

import it.polimi.tiw2020.beans.Comment;
import it.polimi.tiw2020.beans.Image;
import it.polimi.tiw2020.beans.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public void addComment(Comment comment, Image image, User user) throws SQLException {
        if (image == null || comment == null || user == null) {
            return;
        }
        String query = "INSERT into comment (text, image_id, user_id) VALUES(?, ?, ?)";
        // Delimit the transaction explicitly, not to leave the db in inconsistent state
        con.setAutoCommit(false);
        try (PreparedStatement pstatement = con.prepareStatement(query);) {
            pstatement.setString(1, comment.getText());
            pstatement.setDouble(2, image.getId());
            pstatement.setDouble(3, user.getId());
            pstatement.executeUpdate(); // 1st update
            con.commit();
        } catch (SQLException e) {
            con.rollback(); // if update 1 fails, roll back all work
            throw e;
        }
    }
}
