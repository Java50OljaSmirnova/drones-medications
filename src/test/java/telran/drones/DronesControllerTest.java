package telran.drones;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static telran.drones.api.ValidationConstants.*;
import static telran.drones.api.ServiceExceptionMessages.*;
import telran.drones.dto.*;
import telran.drones.exceptions.NotFoundException;
import telran.drones.service.DronesService;
@WebMvcTest
class DronesControllerTest {
	private static final String DRONE_NUMBER = "111-11-11";
	private static final String MEDICATION_CODE = "MED_COD1";
	private static final String WRONG_DRONE_NUMBER = fillNumber();
	private static final String MEDICATION_WRONG_CODE = "med-cod";
	@MockBean
	DronesService dronesService;
	@Autowired
	MockMvc mockMvc;
	
	DroneDto droneDto = new DroneDto(DRONE_NUMBER, ModelType.Middleweight);
	DroneDto droneWrongNumberSize = new DroneDto(WRONG_DRONE_NUMBER, ModelType.Middleweight);
	DroneMedication droneMedication = new DroneMedication(DRONE_NUMBER, MEDICATION_CODE);
	DroneMedication droneMedicationWrongCode = new DroneMedication(DRONE_NUMBER, MEDICATION_WRONG_CODE);
	
	@Autowired
	ObjectMapper mapper;
	
	private static String fillNumber() {
		String res = "";
		for(int i = 0; i < 101; i++) {
			res += "d";
		}
		return res;
	}

	@Test
	void testRegisterDrone() throws Exception {
		when(dronesService.registerDrone(droneDto)).thenReturn(droneDto);
		String jsonDroneDto = mapper.writeValueAsString(droneDto);
		String actualJSON = mockMvc.perform(post("http://localhost:8080/drones").contentType(MediaType.APPLICATION_JSON)
				.content(jsonDroneDto)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(jsonDroneDto, actualJSON);
	}
	
	@Test
	void testLoadDrone() throws Exception {
		when(dronesService.loadDrone(droneMedication)).thenReturn(droneMedication);
		String jsonDroneMed = mapper.writeValueAsString(droneMedication);
		String actualJSON = mockMvc.perform(post("http://localhost:8080/drones/load").contentType(MediaType.APPLICATION_JSON)
				.content(jsonDroneMed)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(jsonDroneMed, actualJSON);
	}
	
	@Test
	void testRegisterDroneAlredyExists() throws Exception {
		when(dronesService.registerDrone(droneDto)).thenThrow(new IllegalStateException(DRONE_ALREADY_EXISTS));
		String jsonDroneDto = mapper.writeValueAsString(droneDto);
		String response = mockMvc.perform(post("http://localhost:8080/drones").contentType(MediaType.APPLICATION_JSON)
				.content(jsonDroneDto)).andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertEquals(DRONE_ALREADY_EXISTS, response);
	}
	@Test
	void testLoadDroneNotFoundDataDrone() throws Exception {
		when(dronesService.loadDrone(droneMedication)).thenThrow(new NotFoundException(DRONE_NOT_FOUND));
		String jsonData = mapper.writeValueAsString(droneMedication);
		String response = mockMvc.perform(post("http://localhost:8080/drones/load").contentType(MediaType.APPLICATION_JSON)
				.content(jsonData)).andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();
		assertEquals(DRONE_NOT_FOUND, response);
	}
	@Test
	void testLoadDroneNotFoundDataMedication() throws Exception {
		when(dronesService.loadDrone(droneMedication)).thenThrow(new NotFoundException(MEDICATION_NOT_FOUND));
		String jsonData = mapper.writeValueAsString(droneMedication);
		String response = mockMvc.perform(post("http://localhost:8080/drones/load").contentType(MediaType.APPLICATION_JSON)
				.content(jsonData)).andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();
		assertEquals(MEDICATION_NOT_FOUND, response);
	}
	
	/**
	 * @throws Exception ****************************************************************************************/
	/* Alternative flows - Validation exceptions handling *************************/
	
	@Test
	void testRegisterDroneWrongNumberSize () throws Exception {
		wrongDroneDataRequest(droneWrongNumberSize, WRONG_DRON_SERIAL_NUMBER);
	}
	@Test
	void testLoadDroneWrongMedicationCode() throws Exception {
		wrongMedicationDataRequest(droneMedicationWrongCode, WRONG_MEDICATION_CODE);
	}

	private void wrongDroneDataRequest(Object objectWrongData, String expectedMessage) throws Exception {
		String jsonData = mapper.writeValueAsString(objectWrongData);
		String response = mockMvc.perform(post("http://localhost:8080/drones").contentType(MediaType.APPLICATION_JSON)
				.content(jsonData)).andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertEquals(expectedMessage, response);
			
	}
	private void wrongMedicationDataRequest(Object objectWrongData, String expectedMessage) throws Exception {
		String jsonData = mapper.writeValueAsString(objectWrongData);
		String response = mockMvc.perform(post("http://localhost:8080/drones/load").contentType(MediaType.APPLICATION_JSON)
				.content(jsonData)).andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertEquals(expectedMessage, response);
			
	}

}
