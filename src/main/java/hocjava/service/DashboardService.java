package hocjava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hocjava.dto.ChartDTO;
import hocjava.dto.AdminDashboardDTO;
import hocjava.repository.ContactRepository;
import hocjava.repository.CourseRepository;

@Service
public class DashboardService {
	@Autowired
	private ContactRepository contactRepository;

	@Autowired
	private CourseRepository courseRepository;

	public AdminDashboardDTO getAdminDashboard() {
		var dto = new AdminDashboardDTO();

		dto.setContactSummary(contactRepository.getContactSummary());
		dto.setTotalCourse(courseRepository.count());
		dto.setLastContacts(contactRepository.findTop3ByOrderByCreatedAtDesc());

		var countContact = contactRepository.countContactsLast7Days();

		dto.setChartJson(ChartDTO.create("bar", countContact.stream().map(c -> c.getDayOfWeekLabel()).toList())
				.addDataset("Lượt liên hệ", countContact.stream().map(c -> c.getCount()).toList())
				.addDataset("Đã hoàn thành", countContact.stream().map(c -> c.getCountDone()).toList()).toJson());

		return dto;
	}
	
}
