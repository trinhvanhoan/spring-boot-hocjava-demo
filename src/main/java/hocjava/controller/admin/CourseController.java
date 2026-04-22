package hocjava.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hocjava.dto.CourseDTO;
import hocjava.dto.CourseTransferFormDTO;
import hocjava.dto.UserTransferFormDTO;
import hocjava.exception.BindingResultException;
import hocjava.exception.TransferDeleteException;
import hocjava.exception.ResourceNotFoundException;
import hocjava.service.CourseService;
import hocjava.util.MessageUtil;
import hocjava.util.MapperUtil;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CourseController {
	private final CourseService courseService;
	
	@GetMapping("/admin/courses")
	public String userPage(Model model) {
		model.addAttribute("courses", courseService.getAllCourses());
		return "views/admin/courses";
	}
	
	@GetMapping("/admin/courses/new")
	public String createNewCourse(Model model) {
		var dto = new CourseDTO();

		model.addAttribute("dto", dto);
		return "/views/admin/_modal-course";
	}
	
	@PostMapping("/admin/course/save")
	public String saveCourse(@ModelAttribute @Validated CourseDTO dto, BindingResult result, Model model, RedirectAttributes ra) {
		try {
			if (result.hasErrors()) throw new BindingResultException("Dữ liệu không đúng");
			var isNew = dto.getId() == null;
			
			var message = isNew ? "Thêm mới" : "Cập nhật";
			
			var course = isNew ? courseService.create(dto) : courseService.update(dto);
			
			MessageUtil.success(ra, message + " khóa học [%s] thành công.".formatted(course.getCourseName()));
			return "redirect:/admin/courses";
		}
		catch (ResourceNotFoundException e) {
			MessageUtil.error(ra, e.getMessage());
			return "redirect:/admin/courses";
		}
		catch(Exception e) {
			MessageUtil.error(model, e instanceof BindingResultException ? result : e.getMessage());
			model.addAttribute("dto", dto);
			return "/views/admin/_modal-course";
		}
	}
	
	@GetMapping("/admin/courses/{id}/edit")
	public String editCourse(@PathVariable Integer id, Model model, RedirectAttributes ra) {
		try {
			var courseInfo = courseService.getInfo(id);
			var dto = MapperUtil.clone(courseInfo, CourseDTO.class);
			
			model.addAttribute("dto", dto);
			return "views/admin/_modal-course";
		}
		catch (Exception e) {
			MessageUtil.error(ra, e.getMessage());
			return "redirect:/admin/users";
		}
	}
	
	@GetMapping("/admin/courses/{id}/delete")
	public String deleteUser(@PathVariable Integer id, Model model, RedirectAttributes ra) {
		try {
			var course = courseService.delete(id);
			MessageUtil.success(ra, "Xóa khóa học [%s] thành công".formatted(course.getCourseName()));
			return "redirect:/admin/courses";
		}
		catch (TransferDeleteException ex) {
			MessageUtil.error(model, ex.getMessage());
			
			var dto = new UserTransferFormDTO();
			var course = courseService.getInfo(id);
			
			model.addAttribute("dto", dto);
			model.addAttribute("course", course);
			model.addAttribute("courses", courseService.getAllCoursesNotId(id));
			
			return "views/admin/course-delete-confirm";
		}		
		catch (Exception e) {
			MessageUtil.error(ra, e.getMessage());
			return "redirect:/admin/courses";
		}
	}
	
	@PostMapping("/admin/courses/{id}/delete")
	public String transferAndDeleteCourse(@PathVariable Integer id, @ModelAttribute @Validated CourseTransferFormDTO dto, BindingResult result, Model model, RedirectAttributes ra) {
		
		try {
			if (result.hasErrors()) throw new BindingResultException("Dữ liệu không đúng");
			
			var course = courseService.transferAndDelete(id, dto.getTransferId());
			MessageUtil.success(ra, "Xóa người dùng [%s] thành công.".formatted(course.getCourseName()));
			return "redirect:/admin/courses";
		}
		catch (ResourceNotFoundException e) {
			MessageUtil.error(ra, e.getMessage());
			return "redirect:/admin/courses";
		}
		catch (Exception e) {
			MessageUtil.error(model, e instanceof BindingResultException ? result : e.getMessage());
			
			var course = courseService.getInfo(id);
			
			model.addAttribute("dto", dto);
			model.addAttribute("course", course);
			model.addAttribute("courses", courseService.getAllCoursesNotId(id));
    		
    		return "views/admin/course-delete-confirm";
		}
	}
	
}
