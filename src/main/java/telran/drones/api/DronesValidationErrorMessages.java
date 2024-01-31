package telran.drones.api;

public interface DronesValidationErrorMessages {
	String MISSING_DRONE_NUMBER = "Missing drone number";
	int MAX_CHARACTERS_SIZE_NUMBER = 100;
	String DRON_NUMBER_WRONG_LENGTH = "Length of drone number cannot be greater than " + MAX_CHARACTERS_SIZE_NUMBER + " characters";
	String MISSING_MEDICATION_CODE = "Missing medication code";
	String MISSING_DRONE_MODEL = "Missing drone model";
	String WRONG_MEDICATION_CODE = "Wrong medication code";

}
