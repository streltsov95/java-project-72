package hexlet.code;

import io.javalin.Javalin;

public class App {
    public static Javalin getApp() {

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
        });

        app.get("/", ctx -> ctx.result("Hello World"));

        return app;
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.valueOf(port);
    }

    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(getPort());
    }
}
