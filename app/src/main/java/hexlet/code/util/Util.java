package hexlet.code.util;

import hexlet.code.App;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Collectors;

public class Util {

    public static String readResourceFile(String fileName) throws IOException {
        InputStream inputStream = Optional.ofNullable(App.class.getClassLoader().getResourceAsStream(fileName))
                .orElseThrow(() -> new FileNotFoundException("Resource file not found: " + fileName));
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    public static String getParsedCreatedAt(Timestamp createdAt) {
        LocalDateTime parsedCreatedAt = createdAt.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return parsedCreatedAt.format(formatter);
    }
}
