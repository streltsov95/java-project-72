package hexlet.code.dto;

/**
 * Represents a base page with flash message support.
 *
 * <p>This class is designed for extension. Subclasses can extend this class
 * to provide additional functionality while maintaining the base flash message support.</p>
 */
public class BasePage {
    private String flash;
    private String flashType;

    /**
     * Gets the flash message.
     *
     * @return the flash message
     */
    public String getFlash() {
        return flash;
    }

    /**
     * Sets the flash message.
     *
     * @param flash the flash message to set
     */
    public void setFlash(String flash) {
        this.flash = flash;
    }

    /**
     * Gets the type of the flash message.
     *
     * @return the flash message type
     */
    public String getFlashType() {
        return flashType;
    }

    /**
     * Sets the type of the flash message.
     *
     * @param flashType the flash message type to set
     */
    public void setFlashType(String flashType) {
        this.flashType = flashType;
    }
}
