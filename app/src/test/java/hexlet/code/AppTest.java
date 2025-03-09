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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

class AppTest {

    private static Path getFixturePath(String fileName) {
        return Path.of("src", "test", "resources", "fixtures", fileName)
                .toAbsolutePath().normalize();
    }

    private static String readFixture(String fileName) throws IOException {
        Path path = getFixturePath(fileName);
        return Files.readString(path).trim();
    }

    private static Javalin app; //static - иначе создается новый экз.-р для каждого теста
    private static MockWebServer mockServer;
    private static String testUrlName;

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

    @BeforeAll
    public static void beforeAll() throws IOException {
        mockServer = new MockWebServer();
        mockServer.enqueue(new MockResponse().setBody(readFixture("index.jte")));
        testUrlName = mockServer.url("/").toString();
//        mockServer.start();
    }

    @AfterAll
    public static void afterAll() throws IOException {
        mockServer.close();
    }


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

//     Почему при редиректе на главную старницу после неудачной попытки добавить урл в html не отрисовывается флэш?
//     такое исполнение теста не верно, почему?
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

    @Test
    public void testUrlCheckCreate() {
        JavalinTest.test(app, (server, client) -> {
            var url = new Url(testUrlName);
            url.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            UrlsRepository.save(url);
            var response = client.post(NamedRoutes.urlCheckPath(url.getId()));
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains(
                    "A Sample HTML Document (Test File)",
                    "A blank HTML document for testing purposes.",
                    "A Sample HTML Document with h1 tag (Test File)"
            );
        });
    }
}
