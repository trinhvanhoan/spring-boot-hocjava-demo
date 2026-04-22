package hocjava.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import hocjava.service.StatsService;

@Controller
public class StatsController {
	@Autowired
	private StatsService statsService;
	
	@GetMapping("/admin/stats")
	public String statsPage(Model model) {
		model.addAttribute("pageInfo", statsService.getStatsGeneral());
		
		return "views/admin/stats";
	}
	
	@GetMapping("/admin/stats-month")
	public String statsMonthPage(Model model) {
		model.addAttribute("pageInfo", statsService.getStatsMonth());
		
		return "views/admin/stats-month";
	}
}
