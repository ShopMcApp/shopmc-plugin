package app.shopmc.plugin.resource;

public class ResourceLoaderException extends Exception {

    private final Reason reason;

    public ResourceLoaderException(final Reason reason, final Throwable cause) {
        super(cause);
        this.reason = reason;
    }

    public Reason getReason() {
        return this.reason;
    }

    public enum Reason {
        DIRECTORY_NOT_CREATED("Nie udało się utworzyć folderu pluginu"),
        DEFAULT_FILE_NOT_SAVED("Nie udało się zapisać domyślnego pliku %s"),
        FILE_NOT_LOADED("Nie udało się otworzyć pliku %s");

        private final String message;

        Reason(final String message) {
            this.message = message;
        }

        public String getMessage(final String fileName) {
            return String.format(this.message, fileName);
        }
    }
}