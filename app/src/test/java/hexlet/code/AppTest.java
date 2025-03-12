package hexlet.code;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlChecksRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class AppTest {
    private static Javalin app; //static - иначе создается новый экз.-р для каждого теста
    private static MockWebServer mockServer;
    private static String testUrlName;

    private static Path getFixturePath(String fileName) {
        return Path.of("src", "test", "resources", "fixtures", fileName)
                .toAbsolutePath().normalize();
    }

    private static String readFixture(String fileName) throws IOException {
        Path path = getFixturePath(fileName);
        return Files.readString(path).trim();
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
            var url = UrlsRepository.findUrlByName("https://www.example.com:5654");

            assertThat(response.code()).isEqualTo(200);
            assertTrue(url.isPresent());
            assertThat(response.body().string()).contains(url.get().getName());
        });
    }

//     JavalinTest не хранит данные сессии, поэтому с его помощью не получается проверить флеш сообщения.
//     Можно сделать отдельный тест и там использовать что-нибудь типа Unirest, слать реальные запросы в приложение
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
            var url = UrlsRepository.findUrlByName("https://www.example.com:5654");
            response = client.get(NamedRoutes.urlPath(url.get().getId()));

            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains(url.get().getName());
        });
    }

    @Test
    public void testUrlCheckCreate() {
        JavalinTest.test(app, (server, client) -> {
            var url = new Url(testUrlName);
            UrlsRepository.save(url);
            var response = client.post(NamedRoutes.urlCheckPath(url.getId()));
            var actualCheck = UrlChecksRepository.getEntities(url.getId());
            var expectedCheck = List.copyOf(actualCheck);

            assertThat(response.code()).isEqualTo(200);
            assertEquals(expectedCheck, actualCheck);
            assertThat(response.body().string()).contains(
                    "A Sample HTML Document (Test File)",
                    "A blank HTML document for testing purposes.",
                    "A Sample HTML Document with h1 tag (Test File)"
            );
        });
    }
}
