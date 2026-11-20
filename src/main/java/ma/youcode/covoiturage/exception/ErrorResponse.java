package ma.youcode.covoiturage.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Format standardisé des réponses d'erreur de l'API")
public record ErrorResponse(
        @Schema(description = "Horodatage de l'erreur", example = "2026-11-20T10:30:00Z")
        Instant timestamp,

        @Schema(description = "Code de statut HTTP", example = "404")
        int code,

        @Schema(description = "Nom du statut d'erreur", example = "NOT_FOUND")
        String type,

        @Schema(description = "Message explicatif détaillé de l'erreur", example = "Conducteur introuvable")
        String message,

        @Schema(description = "URI de la requête ayant provoqué l'erreur", example = "/api/conducteurs/99")
        String path
) {
}
