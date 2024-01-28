package telran.drones.service;

import telran.drones.dto.DroneDto;
import telran.drones.dto.DroneMedication;

public interface DronesService {
	DroneDto registerDrone(DroneDto droneDto);
	DroneMedication loadDrone(DroneMedication droneMedication);

}
