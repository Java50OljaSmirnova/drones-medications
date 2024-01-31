package telran.drones.dto;

import static telran.drones.api.DronesValidationErrorMessages.*;

import jakarta.validation.constraints.*;

public record DroneDto(
		@NotEmpty(message = MISSING_DRONE_NUMBER) @Size(max = MAX_CHARACTERS_SIZE_NUMBER , message = DRON_NUMBER_WRONG_LENGTH) String number,
		@NotNull(message = MISSING_DRONE_MODEL) ModelType modelType) {

}
