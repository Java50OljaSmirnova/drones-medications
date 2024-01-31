package telran.drones.api;

public interface ServiceExceptionMessages {
	String DRONE_NOT_FOUND = "Drone not found";
	String DRONE_ALREADY_EXISTS = "Drone already exists";
	String MEDICATION_NOT_FOUND = "Medication not found";
	String NOT_IDLE_STATE = "Loading may be done only in IDLE state";
	String MODEL_NOT_FOUND = "Modelnot found";
	String LOW_BATTERY_CAPACITY = "Too low battery capacity";
	String WEIGHT_LIMIT_VIOLATION = "Dron's weight limit less than medication weight";
	

}
