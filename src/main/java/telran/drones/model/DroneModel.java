package telran.drones.model;

import jakarta.persistence.*;
import lombok.*;
import telran.drones.dto.ModelType;

@Entity
@Table(name="drone-models")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DroneModel {
	@Id
	@Enumerated(EnumType.STRING)
	ModelType modelName;
	int weight;

}
