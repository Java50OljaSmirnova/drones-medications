package telran.drones.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import telran.drones.dto.ModelType;
import telran.drones.model.DroneModel;

public interface DroneModelRepo extends JpaRepository<DroneModel, ModelType> {

}
