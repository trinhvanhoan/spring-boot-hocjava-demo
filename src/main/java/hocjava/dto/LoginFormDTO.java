package hocjava.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginFormDTO(
	@NotBlank(message = "Tên đăng nhập không được để trống") 
	String username,
	
	@NotBlank(message = "Mật khẩu không được để trống") 
	String password
) {}
