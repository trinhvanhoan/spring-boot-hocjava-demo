package hocjava.dto;

import hocjava.entity.User;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEditDTO {
    private Integer id;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    private String fullName;

    @Email(message = "Email không đúng định dạng")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @Pattern(regexp = "(^$|[0-9]{10})", message = "Số điện thoại phải có 10 chữ số")
    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    @NotNull(message = "Vui lòng chọn vai trò")
    private User.Role role;

    private Integer status;
}