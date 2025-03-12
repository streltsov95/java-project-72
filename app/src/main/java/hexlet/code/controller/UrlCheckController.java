package hexlet.code.controller;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.sql.SQLException;

public class UrlCheckController {
    public static void check(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Integer.class).get();
        Url url = UrlsRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Url with id = " + id + " not found"));
        HttpResponse<String> response;
        try {
            response = Unirest.get(url.getName()).asString();
        } catch (UnirestException e) {
            ctx.sessionAttribute("flash", "Некорректный адрес");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect(NamedRoutes.urlPath(id));
            return;
        }
        var statusCode = response.getStatus();
        var body = response.getBody();
        Document parsedBody = Jsoup.parse(body);
        String title = parsedBody.title();
        String h1 = parsedBody.select("h1").text();
        String description = parsedBody.select("meta[name=description]").attr("content");
        UrlCheck urlCheck = new UrlCheck(statusCode, title, h1, description, id);
        UrlChecksRepository.save(urlCheck);
        ctx.sessionAttribute("flash", "Страница успешно проверена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect(NamedRoutes.urlPath(id));
    }
}
