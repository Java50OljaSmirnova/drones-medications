package telran.drones.dto;

import static telran.drones.api.DronesValidationErrorMessages.*;
import static telran.drones.api.ConstraintConstants.*;

import jakarta.validation.constraints.*;

public record DroneMedication(@NotEmpty(message = MISSING_DRONE_NUMBER) 
@Size(max = MAX_CHARACTERS_SIZE_NUMBER , message = DRON_NUMBER_WRONG_LENGTH)String droneNumber, 
@NotEmpty(message = MISSING_MEDICATION_CODE) @Pattern(regexp = MEDICATION_CODE_REGEXP, message = WRONG_MEDICATION_CODE) String medicationCode) {

}
