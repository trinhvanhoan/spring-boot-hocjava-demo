package hocjava.dto;

import hocjava.entity.Contact;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactUpdateDTO {
    private Integer id;

    @NotNull(message = "Vui lòng chọn trạng thái")
    private Contact.ContactStatus status;

    private String adminNote;
}
