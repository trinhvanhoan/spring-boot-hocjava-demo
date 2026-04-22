package hocjava.controller.admin;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hocjava.dto.UserEditDTO;
import hocjava.dto.UserNewDTO;
import hocjava.dto.UserSetPasswordDTO;
import hocjava.dto.UserTransferFormDTO;
import hocjava.exception.BindingResultException;
import hocjava.exception.ResourceNotFoundException;
import hocjava.exception.TransferDeleteException;
import hocjava.service.UserService;
import hocjava.util.MessageUtil;
import hocjava.util.MapperUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	
	@GetMapping("/admin/users")
	public String userPage(Model model) {
		model.addAttribute("users", userService.getAllUsers());
		return "views/admin/users";
	}
	
	@GetMapping("/admin/users/new")
	public String modalFormUser(Model model) {
		var dto = new UserNewDTO();
		model.addAttribute("dto", dto);
		return "views/admin/_modal-user-new";
	}
	
	@PostMapping("/admin/users/new")
	public String createUser(@ModelAttribute @Validated UserNewDTO dto, BindingResult result, Model model, RedirectAttributes ra) {
		try {
			if (result.hasErrors()) throw new BindingResultException("Dữ liệu không đúng");
			
			var user = userService.create(dto);
			if (user == null) throw new RuntimeException("Lỗi tạo người dùng");
			
			MessageUtil.success(ra, "Tạo người dùng [%s] thành công".formatted(user.getFullName()));
			return "redirect:/admin/users";
		}
		catch (Exception e) {			
			MessageUtil.error(model, e instanceof BindingResultException ? result : e.getMessage());
			
			model.addAttribute("dto", dto);
			
			return "views/admin/_modal-user-new";
		}
	}
	
	@GetMapping("/admin/users/{id}/delete")
	public String deleteUser(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
		try {
			var user = userService.delete(id);
			MessageUtil.success(ra, "Xóa người dùng [%s] thành công".formatted(user.getFullName()));
			return "redirect:/admin/users";
		}
		catch (TransferDeleteException ex) {
			MessageUtil.error(model, ex.getMessage());
			
			var dto = new UserTransferFormDTO();
			var user = userService.getInfo(id);
			
			model.addAttribute("dto", dto);
			model.addAttribute("user", user);
			model.addAttribute("users", userService.getAllUsersNotId(id));
			
			return "views/admin/user-delete-confirm";
		}
		catch (Exception e) {
			MessageUtil.error(ra, e.getMessage());
			return "redirect:/admin/users";
		}
	}
	
	@PostMapping("/admin/users/{id}/delete")
	public String transferAndDeleteUser(@PathVariable("id") Integer id, @ModelAttribute @Validated UserTransferFormDTO dto, BindingResult result, Model model, RedirectAttributes ra) {
		
		try {
			if (result.hasErrors()) throw new BindingResultException("Dữ liệu không đúng");
			
			var user = userService.transferAndDelete(id, dto.getTransferId());
			MessageUtil.success(ra, "Xóa người dùng [%s] thành công.".formatted(user.getFullName()));
			return "redirect:/admin/users";
		}
		catch (ResourceNotFoundException e) {
			MessageUtil.error(ra, e.getMessage());
			return "redirect:/admin/users";
		}
		catch (Exception e) {
			MessageUtil.error(model, e instanceof BindingResultException ? result : e.getMessage());
			
			var user = userService.getInfo(id);
			
			model.addAttribute("dto", dto);
			model.addAttribute("user", user);
			model.addAttribute("users", userService.getAllUsersNotId(id));
    		
    		return "views/admin/user-delete-confirm";
		}
	}
	
	@GetMapping("/admin/users/{id}/lock")
	public String lockUser(@PathVariable("id") Integer id, RedirectAttributes ra) {
		try {
			var user = userService.lockUser(id);
			MessageUtil.success(ra, "Khóa tài khoản người dùng [%s] thành công".formatted(user.getFullName()));
			return "redirect:/admin/users";
		}
		catch (Exception e) {
			MessageUtil.error(ra, e.getMessage());
			return "redirect:/admin/users";
		}
	}
	
	@GetMapping("/admin/users/{id}/unlock")
	public String unlockUser(@PathVariable("id") Integer id, RedirectAttributes ra) {
		try {
			var user = userService.unlockUser(id);
			MessageUtil.success(ra, "Kích hoạt tài khoản người dùng [%s] thành công".formatted(user.getFullName()));
			return "redirect:/admin/users";
		}
		catch (Exception e) {
			MessageUtil.error(ra, e.getMessage());
			return "redirect:/admin/users";
		}
	}
	
	@GetMapping("/admin/users/{id}/edit")
	public String modalFormUser(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
		try {
			var userInfo = userService.getInfo(id);
			UserEditDTO dto = MapperUtil.clone(userInfo, UserEditDTO.class);
			
			model.addAttribute("dto", dto);
			model.addAttribute("userInfo", userInfo);
			return "views/admin/_modal-user-edit";
		}
		catch (Exception e) {
			MessageUtil.error(ra, e.getMessage());
			return "redirect:/admin/users";
		}
	}
	
	@PostMapping("/admin/users/update")
	public String updateUser(@ModelAttribute @Validated UserEditDTO dto, BindingResult result, Model model, RedirectAttributes ra) {
		try {
			if (result.hasErrors()) throw new BindingResultException("Dữ liệu không đúng");
			
			var user = userService.update(dto);
			if (user == null) throw new RuntimeException("Lỗi cập nhật thông tin người dùng");
			MessageUtil.success(ra, "Cập nhật thông tin người dùng [%s] thành công".formatted(user.getFullName()));
			return "redirect:/admin/users";
		}
		catch (ResourceNotFoundException e) {
			MessageUtil.error(ra, e.getMessage());
			return "redirect:/admin/users";
		}
		catch (Exception e) {
			MessageUtil.error(model, e instanceof BindingResultException ? result : e.getMessage());
			
			model.addAttribute("dto", dto);
			model.addAttribute("userInfo", userService.getInfo(dto.getId()));
			return "views/admin/_modal-user-edit";
		}
	}
	
	@GetMapping("/admin/users/{id}/set-password")
	public String setPasswordModal(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
		try {
			var user = userService.getInfo(id);
			var dto = new UserSetPasswordDTO();
			
			model.addAttribute("dto", dto);
			model.addAttribute("user", user);
			
			return "views/admin/_modal-user-set-password";
		}
		catch (Exception e) {
			MessageUtil.error(ra, e.getMessage());
			return "redirect:/admin/users";
		}
	}
	
	@PostMapping("/admin/users/{id}/set-password")
	public String setPassword(@PathVariable("id") Integer id, @ModelAttribute @Validated UserSetPasswordDTO dto, BindingResult result, Model model, RedirectAttributes ra) {
		try {
			if (result.hasErrors()) throw new BindingResultException("Dữ liệu chưa đúng");
			
			var user = userService.setPassword(id, dto);
			MessageUtil.success(ra, "Thiết lập mật khẩu cho người dùng [%s] thành công.".formatted(user.getFullName()));
			return "redirect:/admin/users";
		}
		catch (ResourceNotFoundException e) {
			MessageUtil.error(ra, e.getMessage());
			return "redirect:/admin/users";
		}
		catch (Exception e) {
			MessageUtil.error(model, e instanceof BindingResultException ? result : e.getMessage());
			
			var user = userService.getInfo(id);
			model.addAttribute("user", user);
			
			model.addAttribute("dto", dto);
    		return "views/admin/_modal-user-set-password";
		}
	}
}
