package hocjava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminStatsDTO {
	private ContactSummary contactSummary; 
	private Long totalCourse;
	private String trendingJson;
	private String statusSummaryJson;
}
