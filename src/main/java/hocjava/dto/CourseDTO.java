package hocjava.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
	private Integer id;
	
	@NotBlank(message = "Tên khóa học không được để trống")
	private String courseName;
}
