package hocjava.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hocjava.dto.AdminDashboardDTO;
import hocjava.dto.AdminStatsDTO;
import hocjava.dto.AdminStatsMonthDTO;
import hocjava.dto.ChartDTO;
import hocjava.repository.ContactRepository;
import hocjava.repository.CourseRepository;
import hocjava.repository.UserRepository;

@Service
public class StatsService {
	
	@Autowired
	private ContactRepository contactRepository;

	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private UserRepository userRepository;

	public AdminStatsDTO getStatsGeneral() {
		var dto = new AdminStatsDTO();
		
		var contactSummary = contactRepository.getContactSummary();
		var countContact = contactRepository.countContactsLast6Months();
		
		dto.setContactSummary(contactSummary);
		dto.setTotalCourse(courseRepository.count());
		dto.setTrendingJson(ChartDTO.create("line", countContact.stream().map(c -> c.getMonth()).toList())
				.addDataset("Số yêu cầu", countContact.stream().map(c -> c.getCount()).toList()).toJson());
		
		dto.setStatusSummaryJson(ChartDTO.create("doughnut", List.of("Mới", "Đang xử lý", "Hoàn thành"))
				.addDataset("Số yêu cầu", List.of(contactSummary.getPendingCount(), contactSummary.getProcessingCount(), contactSummary.getDoneCount())).toJson());

		return dto;
	}
	
	public AdminStatsMonthDTO getStatsMonth() {
		var dto = new AdminStatsMonthDTO();
		var stats = courseRepository.getCourseContactStats();
		
		dto.setContacts(contactRepository.getTop10LastContactThisMonth());
		dto.setUserStats(userRepository.getAdminPerformanceThisMonth());
		dto.setTotalProcessByMonth(contactRepository.getTotalProcessThisMonth());
		dto.setCourseSummaryJson(ChartDTO.create("bar", stats.stream().map(c->c.getCourseName()).toList())
				.addDataset("Số lượng đăng ký", stats.stream().map(c->c.getContactCount() + 14).toList()).toJson());
		
		System.out.println(dto.getCourseSummaryJson());
		
		return dto;
	}
}
