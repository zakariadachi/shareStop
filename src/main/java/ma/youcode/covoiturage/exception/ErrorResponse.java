package ma.youcode.covoiturage.exception;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,
        int code,
        String type,
        String message,
        String path
) {
}
