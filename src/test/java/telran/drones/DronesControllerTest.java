package telran.drones;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static telran.drones.api.ServiceExceptionMessages.*;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import telran.drones.api.DronesValidationErrorMessages;
import telran.drones.api.UrlConstants;
import telran.drones.dto.*;
import telran.drones.exceptions.DroneAlreadyExistException;
import telran.drones.exceptions.DroneNotFoundException;
import telran.drones.exceptions.IllegalDroneStateException;
import telran.drones.exceptions.IllegalMedicationWeightException;
import telran.drones.exceptions.LowBatteryCapacityException;
import telran.drones.exceptions.MedicationNotFoundException;
import telran.exceptions.controller.DronesExceptionsController;
import telran.drones.service.DronesService;
record DroneDtoWrongEnum(String number, String modelType) {

}
@WebMvcTest
class DronesControllerTest {
	private static final String DRONE_NUMBER = "111-11-11";
	private static final String MEDICATION_CODE = "MED_COD1";
	private static final String MEDICATION_WRONG_CODE = "med-cod";
	private static final String HOST = "http://localhost:8080/";
	private static final String URL_DRONE_REGISTER = HOST + UrlConstants.DRONES;
	private static final String URL_DRONE_LOAD = HOST + UrlConstants.LOAD_DRONE;
	private static final String CONTROLER_TEST = "Controller: ";
	@MockBean
	DronesService dronesService;
	@Autowired
	MockMvc mockMvc;
	
	DroneDto droneDto = new DroneDto(DRONE_NUMBER, ModelType.Middleweight);
	DroneDto droneDtoMissingFields = new DroneDto(null, null);
	DroneDtoWrongEnum droneDtoWrongFields = new DroneDtoWrongEnum(DRONE_NUMBER, "WrongModel");
	DroneMedication droneMedication = new DroneMedication(DRONE_NUMBER, MEDICATION_CODE);
	DroneMedication droneMedicationWrongFields = new DroneMedication(new String(new char[1000]), MEDICATION_WRONG_CODE);
	DroneMedication droneMedicationMissingFields = new DroneMedication(null, null);
	String[] errorMessagesDroneMissingFields = {DronesValidationErrorMessages.MISSING_DRONE_NUMBER,
			DronesValidationErrorMessages.MISSING_DRONE_MODEL};
	String errorMessagesDroneWrongFields = DronesExceptionsController.JSON_TYPE_MISMATCH_MESSAGE;
	String[] errorMessagesDroneMedicationWrongFields = {
			DronesValidationErrorMessages.DRON_NUMBER_WRONG_LENGTH,
			DronesValidationErrorMessages.WRONG_MEDICATION_CODE
	};
	String[] errorMessagesDroneMedicationMissingFields = {
			DronesValidationErrorMessages.MISSING_DRONE_NUMBER,
			DronesValidationErrorMessages.MISSING_MEDICATION_CODE
	};
	
	@Autowired
	ObjectMapper mapper;

	@Test
	@DisplayName(CONTROLER_TEST + TestDisplayNames.REGISTER_DRONE_NORMAL)
	void registerDrone_correct() throws Exception {
		when(dronesService.registerDrone(droneDto)).thenReturn(droneDto);
		String jsonDroneDto = mapper.writeValueAsString(droneDto);
		String actualJSON = mockMvc.perform(post(URL_DRONE_REGISTER).contentType(MediaType.APPLICATION_JSON)
				.content(jsonDroneDto)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(jsonDroneDto, actualJSON);
	}
	
	@Test
	@DisplayName(CONTROLER_TEST + TestDisplayNames.LOAD_DRONE_NORMAL)
	void loadDrone_correct() throws Exception {
		when(dronesService.loadDrone(droneMedication)).thenReturn(droneMedication);
		String jsonDroneMed = mapper.writeValueAsString(droneMedication);
		String actualJSON = mockMvc.perform(post(URL_DRONE_LOAD).contentType(MediaType.APPLICATION_JSON)
				.content(jsonDroneMed)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(jsonDroneMed, actualJSON);
	}
	@Test
	@DisplayName(CONTROLER_TEST + TestDisplayNames.REGISTER_DRONE_MISSING_FIELDS)
	void registerDrone_missingFields_exception() throws Exception {
		String jsonDrone = mapper.writeValueAsString(droneDtoMissingFields);
		String response = mockMvc.perform(post(URL_DRONE_REGISTER).contentType(MediaType.APPLICATION_JSON)
				.content(jsonDrone)).andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertErrorMessages(response, errorMessagesDroneMissingFields);
	}
	
	private void assertErrorMessages(String response, String[] expectedMessages) {
		String[] actualMessages = response.split(";");
		Arrays.sort(actualMessages);
		Arrays.sort(expectedMessages);
		assertArrayEquals(expectedMessages, actualMessages);
		
	}
	@Test
	@DisplayName(CONTROLER_TEST + TestDisplayNames.REGISTER_DRONE_VALIDATION_VIOLATION)
	void registerDrone_wrongFields_exception() throws Exception {
		String jsonData = mapper.writeValueAsString(droneDtoWrongFields);
		String response = mockMvc.perform(post(URL_DRONE_REGISTER).contentType(MediaType.APPLICATION_JSON)
				.content(jsonData)).andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertEquals(response, errorMessagesDroneWrongFields);
			
	}

	@Test
	@DisplayName(CONTROLER_TEST + TestDisplayNames.REGISTER_DRONE_ALREADY_EXISTS)
	void registerDrone_alredyExists_exception() throws Exception {
		when(dronesService.registerDrone(droneDto)).thenThrow(new DroneAlreadyExistException());
		String jsonDroneDto = mapper.writeValueAsString(droneDto);
		String response = mockMvc.perform(post(URL_DRONE_REGISTER).contentType(MediaType.APPLICATION_JSON)
				.content(jsonDroneDto)).andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertEquals(DRONE_ALREADY_EXISTS, response);
	}
	@Test
	@DisplayName(CONTROLER_TEST + TestDisplayNames.LOAD_DRONE_NOT_FOUND)
	void loadDrone_notFoundDrone_exception() throws Exception {
		serviceExceptionRequest(new DroneNotFoundException(), 404, DRONE_NOT_FOUND);
	}
	@Test
	@DisplayName(CONTROLER_TEST + TestDisplayNames.LOAD_DRONE_MEDICATION_NOT_FOUND)
	void loadDrone_notFoundDataMedication_exception() throws Exception {
		serviceExceptionRequest(new MedicationNotFoundException(), 404, MEDICATION_NOT_FOUND);
	}
	@Test
	@DisplayName(CONTROLER_TEST + TestDisplayNames.LOAD_DRONE_LOW_BATTERY_CAPCITY)
	void loadDrone_lowBatteryCapacity_exception() throws Exception {
		serviceExceptionRequest(new LowBatteryCapacityException(), 400, LOW_BATTERY_CAPACITY);
	}
	@Test
	@DisplayName(CONTROLER_TEST + TestDisplayNames.LOAD_DRONE_NOT_MATCHING_STATE)
	void loadDrone_notMatchingState_exception() throws Exception {
		serviceExceptionRequest(new IllegalDroneStateException(), 400, NOT_IDLE_STATE);
	}
	@Test
	@DisplayName(CONTROLER_TEST + TestDisplayNames.LOAD_DRONE_NOT_MATCHING_WEIGHT)
	void loadDrone_notMatchingWeight_exception() throws Exception {
		serviceExceptionRequest(new IllegalMedicationWeightException(), 400, WEIGHT_LIMIT_VIOLATION);
	}
	@Test
	@DisplayName(CONTROLER_TEST + TestDisplayNames.LOAD_DRONE_WRONG_FIELDS)
	void loadDrone_wrongFields_exception() throws Exception {
		validationExceptionRequest(droneMedicationWrongFields, errorMessagesDroneMedicationWrongFields);
	}
	@Test
	@DisplayName(CONTROLER_TEST + TestDisplayNames.LOAD_DRONE_MISSING_FIELDS)
	void loadDrone_missingFields_exception() throws Exception {
		validationExceptionRequest(droneMedicationMissingFields, errorMessagesDroneMedicationMissingFields);
	}

	private void serviceExceptionRequest(RuntimeException serviceException, int statusCode, String errorMessage) throws JsonProcessingException, UnsupportedEncodingException, Exception {
		when(dronesService.loadDrone(droneMedication)).thenThrow(serviceException);
		String jsonData = mapper.writeValueAsString(droneMedication);
		String response = mockMvc.perform(post(URL_DRONE_LOAD).contentType(MediaType.APPLICATION_JSON)
				.content(jsonData)).andExpect(status().is(statusCode)).andReturn().getResponse().getContentAsString();
		assertEquals(errorMessage, response);
	}

	private void validationExceptionRequest(DroneMedication droneMedication, String[] errorMessage) throws Exception {
		String jsonData = mapper.writeValueAsString(droneMedication);
		String response = mockMvc.perform(post(URL_DRONE_LOAD).contentType(MediaType.APPLICATION_JSON)
				.content(jsonData)).andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertErrorMessages(response, errorMessage);
			
	}

}
