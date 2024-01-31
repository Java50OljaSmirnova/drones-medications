package telran.drones.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.drones.api.UrlConstants;
import telran.drones.dto.DroneDto;
import telran.drones.dto.DroneMedication;
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

}