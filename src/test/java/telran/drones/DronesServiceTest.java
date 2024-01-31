package telran.drones;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;


import telran.drones.dto.DroneDto;
import telran.drones.dto.ModelType;
import telran.drones.service.DronesService;
@SpringBootTest
class DronesServiceTest {
	private static final String DRONE_NUMBER = "number1";
	private static final ModelType DRONE_MODEL = ModelType.Heavyweight;
	DroneDto droneDto = new DroneDto(DRONE_NUMBER, DRONE_MODEL);
	@Autowired
	ApplicationContext ctx;
	DronesService dronesService;

	@Test
	void testRegisterDrone() {
		assertEquals(droneDto, dronesService.registerDrone(droneDto));
		assertThrowsExactly(IllegalStateException.class, () -> dronesService.registerDrone(droneDto));
		
	}

}
