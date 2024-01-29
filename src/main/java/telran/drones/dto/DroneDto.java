package telran.drones.dto;

import static telran.drones.api.ValidationConstants.*;

import jakarta.validation.constraints.*;

public record DroneDto(
		@NotEmpty(message = MISSING_DRONE_NUMBER) @Size(max = MAX_CHARACTERS_SIZE_NUMBER , message = WRONG_DRON_SERIAL_NUMBER) String number,
		ModelType modelType) {

}
