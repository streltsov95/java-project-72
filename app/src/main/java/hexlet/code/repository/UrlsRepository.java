package hexlet.code.repository;

import hexlet.code.model.Url;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlsRepository extends BaseRepository {

    public static void save (Url url) throws SQLException {
        String sql = "INSERT INTO urls (name, createdAt) VALUES (?, ?)";
        try (var connection = dataSource.getConnection();
                var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, url.getName());
            preparedStatement.setTimestamp(2, url.getCreatedAt());
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                url.setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }

    public static List<Url> getEntities() throws SQLException{
        String sql = "SELECT * FROM urls";
        try (var connection = dataSource.getConnection();
                var preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Url> entities = new ArrayList<>();
            while (resultSet.next()) {
                var id = resultSet.getInt("id");
                var name = resultSet.getString("name");
                var createdAt = resultSet.getTimestamp("createdAt");
                var url = new Url(name);
                url.setId(id);
                url.setCreatedAt(createdAt);
                entities.add(url);
            }
            return entities;
        }
    }

    public static boolean existsByName(String name) throws SQLException{
        return getEntities().stream()
                .anyMatch(url -> url.getName().equals(name));
    }

    public static Optional<Url> find(int id) throws SQLException {
        String sql = "SELECT * FROM urls WHERE id = ?";
        try (var connection = dataSource.getConnection();
                var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                var name = resultSet.getString("name");
                var createdAt = resultSet.getTimestamp("createdAt");
                var url = new Url(name);
                url.setId(id);
                url.setCreatedAt(createdAt);
                return Optional.of(url);
            }
            return Optional.empty();
        }
    }
}
