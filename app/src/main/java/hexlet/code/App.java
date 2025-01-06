package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.repository.BaseRepository;
import io.javalin.Javalin;

import hexlet.code.util.Util;

public class App {

    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(Util.getPort());
    }

    public static Javalin getApp() {

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(Util.getDatabaseUrl());

        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        BaseRepository.dataSource = dataSource;

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
        });

        app.get("/", ctx -> ctx.result("Hello World"));

        return app;
    }

}
