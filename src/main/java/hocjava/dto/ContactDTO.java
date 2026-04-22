package hocjava.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ContactDTO {
	@NotBlank(message = "Vui lòng nhập họ tên")
    @Size(max = 100, message = "Họ tên không quá 100 ký tự")
    private String fullName;

    @NotBlank(message = "Vui lòng nhập email")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Vui lòng nhập số điện thoại")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại phải từ 10-11 số")
    private String phoneNumber;

    @NotNull(message = "Vui lòng chọn khóa học")
    private Integer courseId;

    @NotBlank(message = "Vui lòng nhập nội dung yêu cầu")
    @Size(min = 10, message = "Nội dung yêu cầu quá ngắn")
    private String message;
}
