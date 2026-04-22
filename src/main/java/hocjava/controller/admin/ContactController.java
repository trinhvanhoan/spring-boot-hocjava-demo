package hocjava.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hocjava.dto.ContactSearchDTO;
import hocjava.dto.ContactUpdateDTO;
import hocjava.entity.Contact;
import hocjava.exception.BindingResultException;
import hocjava.exception.ResourceNotFoundException;
import hocjava.service.ContactService;
import hocjava.service.CourseService;
import hocjava.util.EnumUtil;
import hocjava.util.MapperUtil;
import hocjava.util.MessageUtil;
import lombok.RequiredArgsConstructor;

@Controller("adminContactController")
@RequiredArgsConstructor
public class ContactController {
	private final ContactService contactService;
	private final CourseService courseService;

	@GetMapping("/admin/contacts")
	public String contactPage(ContactSearchDTO dto, Model model) {
		var page = contactService.searchContacts(dto);
		
		
		model.addAttribute("dto", dto);
		model.addAttribute("page", page);
		model.addAttribute("contactSummary", contactService.getContactSummary());
		model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("statuses", Contact.ContactStatus.values());
        model.addAttribute("statusLabels", EnumUtil.ContactStatusLabels());
        
		return "views/admin/contacts";
	}
	
	@GetMapping("/admin/contacts/{id}/edit")
	public String editContactModal(@PathVariable Integer id, Model model, RedirectAttributes ra) {
		try {
			var contact = contactService.getInfo(id);
			var dto = MapperUtil.clone(contact, ContactUpdateDTO.class);
			
			return showContactModelView(model, dto, contact);
		}
		catch (Exception e) {
			MessageUtil.error(ra, e.getMessage());
			return "redirect:/admin/contacts";
		} 
	}
	
	@PostMapping("/admin/contacts/save")
	public String editContactModal(@ModelAttribute ContactUpdateDTO dto, BindingResult result, Model model, RedirectAttributes ra) {
		try {
			if (result.hasErrors()) throw new BindingResultException("Dữ liệu không đúng");
			
			var contact = contactService.updateContact(dto);
			MessageUtil.success(ra, "Đã cập nhật yêu cầu liên hệ của người dùng %s".formatted(contact.getFullName()));
			
			return "redirect:/admin/contacts";
		}
		catch (ResourceNotFoundException e) {
			MessageUtil.error(ra, e.getMessage());
			return "redirect:/admin/contacts";
		}
		catch (Exception e) {
			System.out.println(e);
			
			MessageUtil.error(model, e instanceof BindingResultException ? result : e.getMessage());
			
			var contact = contactService.getInfo(dto.getId());
			return showContactModelView(model, dto, contact);
		}
	}
	
	private String showContactModelView(Model model, ContactUpdateDTO dto, Contact contact) {
		model.addAttribute("contact", contact);
		model.addAttribute("dto", dto);
		model.addAttribute("statuses", Contact.ContactStatus.values());
		model.addAttribute("statusLabels", EnumUtil.ContactStatusLabels());
			
		return "views/admin/_modal-contact";
	}
	
}
