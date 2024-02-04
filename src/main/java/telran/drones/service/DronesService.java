package telran.drones.service;

import java.util.List;

import telran.drones.dto.*;

public interface DronesService {
	DroneDto registerDrone(DroneDto droneDto);
	DroneMedication loadDrone(DroneMedication droneMedication);
	List<String> checkMedicationItems(String droneNumber);
	List<String> checkAvailableDrones();
	int checkBatteryCapasity(String droneNumber);
	List<DroneItemsAmount> checkDroneLoadedItemAmounts();

}
