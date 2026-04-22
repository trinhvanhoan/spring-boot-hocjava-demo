package hocjava.dto;

import java.util.List;

import hocjava.entity.Contact;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardDTO {
	private ContactSummary contactSummary; 
	private Long totalCourse;
	private String chartJson;
	private List<Contact> lastContacts;
}