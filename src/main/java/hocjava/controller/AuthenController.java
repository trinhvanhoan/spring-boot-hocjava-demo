package hocjava.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthenController {
	@GetMapping("/login")
	public String loginPage() {
		return "views/login";
	}
}
