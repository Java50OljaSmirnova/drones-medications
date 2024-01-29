package telran.drones.exceptions;

import telran.drones.api.ServiceExceptionMessages;

@SuppressWarnings("serial")
public class MedicationNotFound extends NotFoundException {

	public MedicationNotFound(String message) {
		
		super(ServiceExceptionMessages.MEDICATION_NOT_FOUND);
		
	}

}
