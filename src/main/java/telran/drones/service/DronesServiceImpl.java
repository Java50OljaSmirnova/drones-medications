package telran.drones.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.drones.api.PropertiesNames;
import telran.drones.dto.*;
import telran.drones.exceptions.*;
import telran.drones.model.*;
import telran.drones.projection.DroneNumber;
import telran.drones.projection.MedicationCode;
import telran.drones.repo.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DronesServiceImpl implements DronesService {
	final DroneRepo droneRepo;
	final DroneModelRepo droneModelRepo;
	final MedicationRepo medicationRepo;
	final EventLogRepo logRepo;
	@Value("${" + PropertiesNames.CAPACITY_THRESHOLD + ":25}")
	int capacityThreshold;
	
	@Override
	@Transactional(readOnly = false)
	public DroneDto registerDrone(DroneDto droneDto) {
		log.debug("service got drone DTO: {}", droneDto);
		if(droneRepo.existsById(droneDto.number())) {
			throw new DroneAlreadyExistException();
		}
		Drone drone = Drone.of(droneDto);
		DroneModel droneModel = droneModelRepo.findById(droneDto.modelType()).
				orElseThrow(() -> new ModelNotFoundException());
		drone.setModel(droneModel);
		droneRepo.save(drone);
		log.debug("drone {} has been saved", drone);
		return droneDto;
	}

	@Override
	@Transactional(readOnly = false)
	public DroneMedication loadDrone(DroneMedication droneMedication) {
		String droneNumber = droneMedication.droneNumber();
		String medicationCode = droneMedication.medicationCode();
		log.debug("received: droneNumber={}, medicationCode={}",droneNumber ,
				droneMedication.medicationCode());
		log.debug("capacity threshold is {}", capacityThreshold);
		Drone drone = droneRepo.findById(droneNumber).orElseThrow(() -> new DroneNotFoundException());
		log.debug("found drone: {}", drone);
		Medication medication = medicationRepo.findById(medicationCode)
				.orElseThrow(() -> new MedicationNotFoundException());
		log.debug("found medication: {}", medication);
		if (drone.getState() != State.IDLE) {
			throw new IllegalDroneStateException();
		}

		if (drone.getBatteryCapacity() < capacityThreshold) {
			throw new LowBatteryCapacityException();
		}
		if (drone.getModel().getWeight() < medication.getWeight()) {
			throw new IllegalMedicationWeightException();
		}
		drone.setState(State.LOADING);
		EventLog eventLog = new EventLog(LocalDateTime.now(), drone.getNumber(), drone.getState(),
				drone.getBatteryCapacity(), medicationCode);
		logRepo.save(eventLog);
		
		log.debug("saved log: {}", eventLog);

		return droneMedication;
	}

	@Override
	public List<String> checkMedicationItems(String droneNumber) {
		if(!droneRepo.existsById(droneNumber)) {
			throw new DroneNotFoundException();
		}
		List<MedicationCode> codes = logRepo.findByDroneNumberAndState(droneNumber, State.LOADING);
		List<String> res = codes.stream().map(MedicationCode::getMedicationCode).toList();
		log.debug("Loaded medication items on drone {} are {} ", droneNumber, res);
		return res;
	}

	@Override
	public List<String> checkAvailableDrones() {
		List<DroneNumber> numbers = droneRepo.findByStateAndBatteryCapacityGreaterThanEqual(State.IDLE, capacityThreshold);
		List<String> res= numbers.stream().map(DroneNumber::getNumber).toList();
		log.debug("Available drones are {}", res);
		return res;
	}

	@Override
	public int checkBatteryCapasity(String droneNumber) {
		Integer res = droneRepo.findBatteryCapacity(droneNumber);
		if(res == null) {
			throw new DroneNotFoundException();
		}
		log.debug("service: battery capasity of drone {} is {}", droneNumber, res);
		return res;
	}

	@Override
	public List<DroneItemsAmount> checkDroneLoadedItemAmounts() {
		List<DroneItemsAmount> res = logRepo.getItemsAmount();
		logDroneItemsAmount(res);
		return res;
	}
	
	private void logDroneItemsAmount(List<DroneItemsAmount> list) {
		list.forEach(dia -> log.trace("drone {} items amount {}", dia.getNumber(), dia.getAmount()));
	}

}
