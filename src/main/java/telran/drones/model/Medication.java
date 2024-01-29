package telran.drones.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="medications")
@Getter
@NoArgsConstructor
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public class Medication {
	@Id
	@Column(name="medication_code")
	String code;
	@Column(name="medication_name")
	String name;
	@Column(name="medication_weight")
	int weight;

}
