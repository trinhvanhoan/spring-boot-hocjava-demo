package hocjava.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTransferFormDTO {
	@NotNull(message = "Vui lòng chọn người dùng để chuyển giao dữ liệu")
    private Integer transferId;
}
