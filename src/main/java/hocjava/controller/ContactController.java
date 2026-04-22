package hocjava.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hocjava.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import hocjava.dto.ContactDTO;
import hocjava.entity.Course;
import hocjava.exception.BindingResultException;
import hocjava.service.ContactService;
import hocjava.service.CourseService;

@Controller
@RequiredArgsConstructor
public class ContactController {
	private final ContactService contactService;
	private final CourseService courseService;
	
	@GetMapping("/contact")
	public String contactPage(Model model) {
		if (!model.containsAttribute("dto")) {
			var dto = new ContactDTO();
			model.addAttribute("dto", dto);
		}
		model.addAttribute("courses", courseService.getAllCourses());
		
		return "views/contact";
	}
	
	@PostMapping("/contact")
	public String createContact(@ModelAttribute @Validated ContactDTO dto, BindingResult result, Model model, RedirectAttributes ra) {
		try {
			if (result.hasErrors()) throw new BindingResultException("Dữ liệu không đúng");
			
			contactService.createContact(dto);
			ra.addFlashAttribute("createContactSuccess", true);
			return "redirect:/contact";
		}
		catch (Exception e) {
			MessageUtil.error(model, e instanceof BindingResultException ? result : e.getMessage());
			
			model.addAttribute("dto", dto);
    		return "views/contact";
		}
	}
	
	@ModelAttribute("courses")
    public List<Course> addAttributeCourses() {
        return courseService.getAllCourses();
    }
}
