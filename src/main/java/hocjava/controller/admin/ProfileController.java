package hocjava.controller.admin;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hocjava.dto.UserChangePasswordDTO;
import hocjava.dto.UserProfileDTO;
import hocjava.exception.BindingResultException;
import hocjava.service.UserService;
import hocjava.util.MapperUtil;
import hocjava.util.MessageUtil;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ProfileController {
	private final UserService userService;
	@GetMapping("/admin/profile")
	public String profilePage(Model model, @AuthenticationPrincipal UserDetails user) {
		var userInfo = userService.getInfoByUsername(user.getUsername());
		var dto = MapperUtil.clone(userInfo, UserProfileDTO.class);
		
		model.addAttribute("user", userInfo);
		model.addAttribute("dto", dto);
		
		return "views/admin/profile";
	}
	
	@PostMapping("/admin/profile")
	public String saveProfile(@ModelAttribute @Validated UserProfileDTO dto, BindingResult result, Model model, RedirectAttributes ra, @AuthenticationPrincipal UserDetails user) {
		try {
			if (result.hasErrors()) throw new BindingResultException("Dữ liệu không đúng");
			
			var userInfo = userService.updateProfile(dto);
			MessageUtil.success(ra, "Cập nhật hồ sơ cá nhân thành công!");
			
			model.addAttribute("user", userInfo);
			model.addAttribute("dto", dto);
			return "redirect:/admin/profile";
		}
		catch(Exception e) {
			MessageUtil.error(model, e instanceof BindingResultException ? result : e.getMessage());
			var userInfo = userService.getInfoByUsername(user.getUsername());
			
			model.addAttribute("user", userInfo);
			model.addAttribute("dto", dto);
			return "views/admin/profile";
		}
	}
	
	@GetMapping("/admin/profile/change-password")
	public String changePasswordModal(Model model, @AuthenticationPrincipal UserDetails user) {
		var userInfo = userService.getInfoByUsername(user.getUsername());
		
		var dto = new UserChangePasswordDTO();
		dto.setId(userInfo.getId());
		
		model.addAttribute("dto", dto);
		return "views/admin/_modal-change-password";
	}
	
	@PostMapping("/admin/profile/change-password")
	public String changePassword(@ModelAttribute @Validated UserChangePasswordDTO dto, BindingResult result, Model model, RedirectAttributes ra, @AuthenticationPrincipal UserDetails user) {
		try {
			if (result.hasErrors()) throw new BindingResultException("Dữ liệu không đúng");
			
			var userInfo = userService.changePassword(dto);
			
			MessageUtil.success(ra, "Đổi mật khẩu thành công!");
			
			model.addAttribute("user", userInfo);
			model.addAttribute("dto", dto);
			return "redirect:/admin/profile";
		}
		catch(Exception e) {
			MessageUtil.error(model, e instanceof BindingResultException ? result : e.getMessage());
			
			model.addAttribute("dto", dto);
			return "views/admin/_modal-change-password";
		}
	}
}
