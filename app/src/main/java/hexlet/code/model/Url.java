package hexlet.code.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public final class Url {
    private int id;
    private String name;
    private Timestamp createdAt;
    private List<UrlCheck> urlChecks = new ArrayList<>();

    public Url(String name) {
        this.name = name;
    }

    public Optional<UrlCheck> getLatestUrlCheck() {
        if (!getUrlChecks().isEmpty()) {
            return getUrlChecks().stream()
                    .sorted(Comparator.comparing(UrlCheck::getCreatedAt))
                    .reduce((e1, e2) -> e2);
        }
        return Optional.empty();
    }

    public static String getParsedCreatedAt(Timestamp createdAt) {
        LocalDateTime parsedCreatedAt = createdAt.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return parsedCreatedAt.format(formatter);
    }
}
