package telran.drones;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import telran.drones.dto.*;
import telran.drones.exceptions.DroneAlreadyExistException;
import telran.drones.exceptions.DroneNotFoundException;
import telran.drones.exceptions.IllegalDroneStateException;
import telran.drones.exceptions.MedicationNotFoundException;
import telran.drones.model.Drone;
import telran.drones.model.EventLog;
import telran.drones.repo.*;
import telran.drones.service.DronesService;
@SpringBootTest
@Sql(scripts = "classpath:test_data.sql")
class DronesServiceTest {
	private static final String DRONE1 = "Drone-1";
	private static final String DRONE2 = "Drone-2";
	private static final String MED1 = "MED_1";
	private static final String DRONE3 = "Drone-3";
	private static final String SERVICE_TEST = "Service: ";
	private static final String DRONE4 = "Drone-4";
	private static final String MED2 = "MED_2";
	
	@Autowired
	DroneRepo droneRepo;
	@Autowired
	EventLogRepo logRepo;
	@Autowired
	DronesService dronesService;
	
	DroneDto droneDto = new DroneDto(DRONE4, ModelType.Cruiserweight);
	DroneDto drone1 = new DroneDto(DRONE1, ModelType.Middleweight);
	DroneMedication droneMedication1 = new DroneMedication(DRONE1, MED1);
	DroneMedication droneMedication2 = new DroneMedication(DRONE2, MED2);	

	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.REGISTER_DRONE_NORMAL)
	void registerDrone_normal() {
		assertEquals(droneDto, dronesService.registerDrone(droneDto));
		assertTrue(droneRepo.existsById(DRONE4));
		
	}
	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.REGISTER_DRONE_ALREADY_EXISTS)
	void registerDrone_alreadyExist_exception() {
		assertThrowsExactly(DroneAlreadyExistException.class, () -> dronesService.registerDrone(drone1));
	}
	
	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.LOAD_DRONE_NORMAL)
	void loadDrone_normal() {
		dronesService.loadDrone(droneMedication1);
		List<EventLog> logs = logRepo.findAll();
		assertEquals(1, logs.size());
		EventLog loadingLog = logs.get(0);
		String droneNumber = loadingLog.getDroneNumber();
		State state = loadingLog.getState();
		String medicationCode = loadingLog.getMedicationCode();
		assertEquals(DRONE1, droneNumber);
		assertEquals(State.LOADING, state);
		assertEquals(MED1, medicationCode);
		Drone drone = droneRepo.findById(droneNumber).orElseThrow();
		assertEquals(State.LOADING, drone.getState());
		
	}
	
	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.LOAD_DRONE_NOT_FOUND)
	void loadDrone_notFoudDrone_exception() {
		assertThrowsExactly(DroneNotFoundException.class, () -> 
		          dronesService.loadDrone(new DroneMedication(DRONE4, MED1)));
	}
	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.LOAD_DRONE_NOT_MATCHING_STATE)
	void loadDrone_WrongState_exception() {
		assertThrowsExactly(IllegalDroneStateException.class, () -> 
		          dronesService.loadDrone(new DroneMedication(DRONE3, MED1)));
	}
	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.LOAD_DRONE_MEDICATION_NOT_FOUND)
	void loadDrone_notFoudMedication_exception() {
		assertThrowsExactly(MedicationNotFoundException.class, () -> 
		          dronesService.loadDrone(new DroneMedication(DRONE1, "KUKU")));
	}
	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.CHECK_MED_ITEMS_NORMAL)
	void checkMedItems_mormal() {
		dronesService.loadDrone(droneMedication1);
		List<String> medItemsExpected = List.of(MED1);
		List<String> medItemsActual = dronesService.checkMedicationItems(DRONE1);
		assertIterableEquals(medItemsExpected, medItemsActual);
		assertTrue(dronesService.checkMedicationItems(DRONE2).isEmpty());
	}
	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.CHECK_MED_ITEMS_DRONE_NOT_FOUND)
	void chackMedItems_notFoudDrone_exception() {
		assertThrowsExactly(DroneNotFoundException.class, () -> 
		          dronesService.checkMedicationItems(DRONE4));
	}
	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.AVAILABLE_DRONES)
	void availableDrones_checkForLoading() {
		List<String> availableExpected = List.of(DRONE1);
		List<String> availableActual = dronesService.checkAvailableDrones();
		assertIterableEquals(availableExpected, availableActual);
		dronesService.loadDrone(droneMedication1);
		assertTrue(dronesService.checkAvailableDrones().isEmpty());
	}
	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.CHECK_BATTERY_LEVEL_NORMAL)
	void batteryDrone_chackLevel_normal() {
		assertEquals(100, dronesService.checkBatteryCapasity(DRONE1));
	}
	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.CHECK_BATTERY_LEVEL_DRONE_NOT_FOUND)
	void batteryDrone_chackLevel_droneNotFound_exception() {
		assertThrowsExactly(DroneNotFoundException.class, () -> dronesService.checkBatteryCapasity(DRONE4));
	}
	@Test
	@Sql(scripts = "classpath:test_idle.data.sql")
	@DisplayName(SERVICE_TEST + TestDisplayNames.CHECK_DRONES_ITEMS_AMOUNT)
	void droneLoaded_checkItemsAmount() {
		dronesService.loadDrone(droneMedication1);
		dronesService.loadDrone(droneMedication2);
		Map<String, Long> resultMap =
				dronesService.checkDroneLoadedItemAmounts().stream()
				.collect(Collectors.toMap(da -> da.getNumber(), da -> da.getAmount()));
		assertEquals(3, resultMap.size());
		assertEquals(1, resultMap.get(DRONE1));
		assertEquals(1, resultMap.get(DRONE2));
		assertEquals(0, resultMap.get(DRONE3));
	}





			
	

}
