package telran.drones.api;

public interface ValidationConstants {
	String MISSING_DRONE_NUMBER = "Missing drone number";
	int MAX_CHARACTERS_SIZE_NUMBER = 100;
	String WRONG_DRON_SERIAL_NUMBER = "Serial number size must by less than " + MAX_CHARACTERS_SIZE_NUMBER + " characters";
	String MISSING_MEDICATION_CODE = "Missing medication code";
	String MEDICATION_CODE_REGEXP = "^[A-Z0-9_]+$";
	String WRONG_MEDICATION_CODE = "Medication code must be allowed only upper case letters, underscore and numbers";

}
