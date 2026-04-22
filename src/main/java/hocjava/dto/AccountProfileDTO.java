package hocjava.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AccountProfileDTO {
    private String username;

    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @Pattern(regexp = "0\\d{9,10}", message = "Số điện thoại phải từ 10-11 số và bắt đầu bằng số 0")
    private String phone;

    private String oldPassword;

    private String newPassword;

    private String confirmPassword;
}