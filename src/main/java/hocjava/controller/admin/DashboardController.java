package hocjava.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import hocjava.service.DashboardService;

@Controller
public class DashboardController {
	@Autowired
	private DashboardService dashboardService;
	
	@GetMapping("/admin/dashboard")
	public String dashboardPage(Model model) {
		model.addAttribute("pageInfo", dashboardService.getAdminDashboard());
		
		return "views/admin/dashboard";
	}
	
	@GetMapping("/admin")
	public String dashboardPage() {
		return "redirect:/admin/dashboard";
	}
}
