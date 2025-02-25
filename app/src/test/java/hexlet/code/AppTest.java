package hexlet.code;

import static org.assertj.core.api.Assertions.assertThat;

import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.model.Url;
import hexlet.code.repository.BaseRepository;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

class AppTest {

//     Подсказки по поиску в чате:
//      1. выполняем запрос на сервис (app)
//      2. Проверяем, что в ответ вернулось то, что мы ожидали
//      3. Проверяем, что не только ответ совпадает, но и действительно в базе изменения применились. Сделать это можно обратившись к бд напрямую, а не через наш сервис
//
//      UrlTest.testIndex
//      UrlTest.testShow
//      UrlTest.testCreate
//
//      UrlCheckTest.testCreate
//
//      Для интеграционных тестов характерны запросы в БД(H2/Derby)
//      Тесты на заглушках(моках) - модульные unit тесты


//    private static Path getFixturePath(String fileName) {
//        return Path.of("src", "test", "resources", "fixtures", fileName)
//                .toAbsolutePath().normalize();
//    }
//
//    private static String readFixture(String fileName) throws IOException {
//        Path path = getFixturePath(fileName);
//        return Files.readString(path).trim();
//    }

    private Javalin app;

    private static Map<String, Object> getUrlByName(HikariDataSource dataSource, String url) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        String sql = "SELECT * FROM urls WHERE name = ?";
        try (var connection = dataSource.getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, url);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                result.put("id", resultSet.getInt("id"));
                result.put("name", resultSet.getString("name"));
            }

            return result;
        }
    }


//    @BeforeAll
//    public static void beforeAll() throws IOException {
//        try (MockWebServer mockServer = new MockWebServer()) {
//            mockServer.enqueue(new MockResponse().setBody(readFixture("index.jte")));
//            mockServer.enqueue(new MockResponse().setBody(readFixture("urls/index.jte")));
//            mockServer.enqueue(new MockResponse().setBody(readFixture("index-bad-flash.jte")));
//            mockServer.enqueue(new MockResponse().setBody(readFixture("urls/show.jte")));
//            mockServer.start();
//        }
//
//    }


    @BeforeEach
    public final void beforeEach() throws SQLException, IOException {
        app = App.getApp();
    }

    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.rootPath());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Бесплатно проверяйте сайты");
        });
    }

    @Test
    public void testCreateValidUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://www.example.com:5654/adad/asdasdd/gdfg/12";
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            var existingUrl = getUrlByName(BaseRepository.dataSource, "https://www.example.com:5654");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains(existingUrl.get("name").toString());
        });
    }

//     Почему при редиректе на главную старницу после неудачной попытки добавить урл в html не отрисовывается флэш,
//     следовательно такое исполнение теста не работает?

//    @Test
//    public void testCreateNonValidUrl() {
//        JavalinTest.test(app, (server, client) -> {
//            var requestBody = "url=sdfscv";
//            var response = client.post(NamedRoutes.urlsPath(), requestBody);
//            assertThat(response.code()).isEqualTo(200);
//            assertThat(response.body().string()).contains("Некорректный URL");
//        });
//    }


    @Test
    public void testShow() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://www.example.com:5654/adad/asdasdd/gdfg/12";
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            var existingUrl = getUrlByName(BaseRepository.dataSource, "https://www.example.com:5654");
            response = client.get(NamedRoutes.urlPath(existingUrl.get("id").toString()));
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains(existingUrl.get("name").toString());
        });
    }
}