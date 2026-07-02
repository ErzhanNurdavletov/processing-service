package kg.bakaibank.processingservice.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response of existence of account")
public record AccountExistsResponse(

    @Schema(description = "does account exist/boolean")
    boolean accountExists
) {
}
