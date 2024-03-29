package telran.drones.controller;

import static telran.drones.api.DronesValidationErrorMessages.DRON_NUMBER_WRONG_LENGTH;
import static telran.drones.api.DronesValidationErrorMessages.MAX_CHARACTERS_SIZE_NUMBER;
import static telran.drones.api.DronesValidationErrorMessages.MISSING_DRONE_NUMBER;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.drones.api.UrlConstants;
import telran.drones.dto.DroneDto;
import telran.drones.dto.DroneItemsAmount;
import telran.drones.dto.DroneMedication;
import telran.drones.dto.EventLogDto;
import telran.drones.service.DronesService;
@RestController
@RequiredArgsConstructor
@Slf4j
public class DronesController {
	final DronesService dronesService;
	@PostMapping(UrlConstants.DRONES)
	DroneDto registerDrone(@RequestBody @Valid DroneDto droneDto) {
		log.debug("received: {}", droneDto);
		return dronesService.registerDrone(droneDto);
	}
	@PostMapping(UrlConstants.LOAD_DRONE)
	DroneMedication loadDrone(@RequestBody @Valid DroneMedication droneMedication) {
		log.debug("received: {}", droneMedication);
		return dronesService.loadDrone(droneMedication);
	}
	@GetMapping(UrlConstants.DRONE_MEDICATION_ITEMS + "{" + UrlConstants.DRONE_NUMBER + "}")
	List<String> checkMedicationItems(@PathVariable @NotEmpty(message = MISSING_DRONE_NUMBER) 
	@Size(max = MAX_CHARACTERS_SIZE_NUMBER , message = DRON_NUMBER_WRONG_LENGTH)String droneNumber){
		log.debug("checkMedicationItems controller for drone {}", droneNumber);
		return dronesService.checkMedicationItems(droneNumber);
	}
	@GetMapping(UrlConstants.AVAILABLE_DRONES)
	List<String> checkAvailableDrones(){
		log.debug("checkAvailableDrones controller");
		return dronesService.checkAvailableDrones();
	}
	@GetMapping(UrlConstants.DRONE_BATTERY_CAPASCITY + "{" + UrlConstants.DRONE_NUMBER + "}")
	int checkBatteryCapasity(@PathVariable @NotEmpty(message = MISSING_DRONE_NUMBER) 
	@Size(max = MAX_CHARACTERS_SIZE_NUMBER , message = DRON_NUMBER_WRONG_LENGTH)String droneNumber) {
		log.debug("checkBatteryCapasity controller for drone {}", droneNumber);
		return dronesService.checkBatteryCapasity(droneNumber);
	}
	@GetMapping(UrlConstants.DRONE_AMOUNT_ITEMS)
	List<DroneItemsAmount> checkDronesMedItems(){
		log.debug("checkDronesMedItems controller");
		return dronesService.checkDroneLoadedItemAmounts();
	}
	@GetMapping(UrlConstants.DRONE_HISTORY_LOGS + "{" + UrlConstants.DRONE_NUMBER + "}")
	List<EventLogDto> checkHistoryLogs(@PathVariable(UrlConstants.DRONE_NUMBER) String droneNumber) {
		log.debug("checkHistoryLogs controller for drone {}", droneNumber);
		return dronesService.checkHistoryLogs(droneNumber);

	}

}
