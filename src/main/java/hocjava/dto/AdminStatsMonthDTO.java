package hocjava.dto;

import java.util.List;

import hocjava.entity.Contact;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsMonthDTO {
	private String courseSummaryJson;
	private List<AdminPerformance> userStats;
	private Long totalProcessByMonth;
	private List<Contact> contacts;
}
