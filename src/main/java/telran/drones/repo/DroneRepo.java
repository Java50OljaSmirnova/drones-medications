package telran.drones.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import telran.drones.dto.State;
import telran.drones.model.Drone;
import telran.drones.projection.DroneNumber;

public interface DroneRepo extends JpaRepository<Drone, String> {
	List<DroneNumber> findByStateAndBatteryCapacityGreaterThanEqual (State state, int capasityThreshold);
	@Query("select batteryCapacity from Drone where number=:droneNumber")
	Integer findBatteryCapacity(String droneNumber);

}
