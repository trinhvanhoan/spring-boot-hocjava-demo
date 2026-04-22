package hocjava.dto;

import hocjava.entity.Contact;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactSearchDTO {
	private String keyword; // tìm theo tên, email, phone

    private Contact.ContactStatus status;

    private Integer courseId;

    private Integer page = 0;

    private Integer size = 10;
}
