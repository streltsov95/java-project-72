package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.repository.BaseRepository;
import io.javalin.Javalin;

import hexlet.code.util.Util;

import java.io.IOException;
import java.sql.SQLException;

public class App {

    public static void main(String[] args) throws IOException, SQLException {
        Javalin app = getApp();
        app.start(Util.getPort());
    }

    public static Javalin getApp() throws IOException, SQLException {

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(Util.getDatabaseUrl());

        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        BaseRepository.dataSource = dataSource;

        var initDatabaseSql = Util.readResourceFile("schema.sql");
        try (var connection = dataSource.getConnection();
                var statement = connection.createStatement()) {
            statement.execute(initDatabaseSql);
        }

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
        });

        app.get("/", ctx -> ctx.result("Hello World"));

        return app;
    }

}
