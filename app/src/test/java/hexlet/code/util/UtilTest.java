package hexlet.code.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {

    @Test
    void testReadExistFile() throws IOException {
        String expected = "Hello,\n World!";
        String actual = Util.readResourceFile("test-file.txt");

        assertEquals(expected, actual);
    }

    @Test
    void testReadEmptyFile() throws IOException {
        String expected = "";
        String actual = Util.readResourceFile("empty-file.txt");

        assertEquals(expected, actual);
    }

    @Test
    void testReadNonExistFile() throws IOException {
        String nonExistFile = "non-exist-file.txt";
        Exception exception = assertThrows(IOException.class, () -> {
            Util.readResourceFile(nonExistFile);
        });
        String expectedMessage = "Resource file not found: " + nonExistFile;

        assertEquals(expectedMessage, exception.getMessage());
    }
}
