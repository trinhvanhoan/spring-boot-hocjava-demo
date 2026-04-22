package hocjava.service;

import java.util.List;

import org.springframework.stereotype.Service;

import hocjava.dto.UserChangePasswordDTO;
import hocjava.dto.UserEditDTO;
import hocjava.dto.UserNewDTO;
import hocjava.dto.UserProfileDTO;
import hocjava.dto.UserSetPasswordDTO;
import hocjava.entity.User;
import hocjava.exception.TransferDeleteException;
import hocjava.repository.ContactRepository;
import hocjava.repository.UserRepository;
import hocjava.util.HashUtil;
import hocjava.util.MapperUtil;
import hocjava.util.UserUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final ContactRepository contactRepository;
	
	public User getInfo(Integer id) {
		return userRepository.getInfo(id);
	}
	
	public User getInfoByUsername(String username) {
		return userRepository.getInfoByUsername(username);
	}
	
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
	
	public List<User> getAllUsersNotId(Integer id) {
		return userRepository.findByIdNot(id);
	}
	
	public User create(UserNewDTO dto) {
		try {
			var user = MapperUtil.clone(dto, User.class);
			user = userRepository.save(user);
			return user;
		}
		catch (Exception e) {
			throw new RuntimeException("Lỗi tạo người dùng");
		}
	}
	
	public User update(UserEditDTO dto) {
		try {
			var user = getInfo(dto.getId());
			MapperUtil.copy(dto, user);
			
			user = userRepository.save(user);
			return user;
		}
		catch (Exception e) {
			throw new RuntimeException("Lỗi cập nhật thông tin người dùng");
		}
	}
	
	public User delete(Integer id) {
	    String currentUsername = UserUtil.getCurrentAuthen().getName();
	    
		var user = getInfo(id);
		
		if (currentUsername.equals(user.getUsername())) {
			throw new RuntimeException("Bạn không thể tự xóa chính mình!");
		}
		
		if (contactRepository.existsByUpdatedBy(user)) {
			throw new TransferDeleteException("Người dùng [%s] đã xử lý một số thông tin liên hệ. Cần chuyển giao trước khi xóa!".formatted(user.getFullName()));
		}
		
		try {
			userRepository.delete(user);
			return user;
		}
		catch (Exception e) {
			throw new RuntimeException("Lỗi xóa người dùng");
		}
	}
	
	public User lockUser(Integer id) {
	    String currentUsername = UserUtil.getCurrentAuthen().getName();
	    
		var user = getInfo(id);
		
		if (currentUsername.equals(user.getUsername())) {
			throw new RuntimeException("Bạn không thể khóa chính mình!");
		}
		
		user.setStatus(0);
				
		try {
			userRepository.save(user);
			return user;
		}
		catch (Exception e) {
			throw new RuntimeException("Lỗi khóa tài khoản người dùng");
		}
	}
	
	public User unlockUser(Integer id) {
		var user = getInfo(id);
		user.setStatus(1);
				
		try {
			userRepository.save(user);
			return user;
		}
		catch (Exception e) {
			throw new RuntimeException("Lỗi kích hoạt tài khoản người dùng");
		}
	}
	
	public User transferAndDelete(Integer oldUserId, Integer newUserId) {
		if (oldUserId == newUserId) throw new RuntimeException("Người dùng cần chuyển giao trước khi xóa phải khác với người dùng bị xóa!");
		
		var oldUser = getInfo(oldUserId);
		
	    String currentUsername = UserUtil.getCurrentAuthen().getName();
	    
		if (currentUsername.equals(oldUser.getUsername())) {
			throw new RuntimeException("Bạn không thể tự xóa chính mình!");
		}
		
		var newUser = getInfo(newUserId);
		int result = contactRepository.transferUpdatedByUser(oldUser, newUser);
		if (result <= 0) {
			throw new RuntimeException("Có lỗi xảy ra trong việc chuyển giao liên hệ cho user mới");
		}
		
		try {
			userRepository.delete(oldUser);
			return oldUser;
		}
		catch (Exception e) {
			throw new RuntimeException("Lỗi xóa người dùng");
		}
	}
	
	public User setPassword(Integer id, UserSetPasswordDTO dto) {
		var user = getInfo(id);
		user.setPassword(HashUtil.encode(dto.getPassword()));
		
		try {
			userRepository.save(user);
			return user;
		}
		catch (Exception e) {
			throw new RuntimeException("Lỗi kích hoạt tài khoản người dùng");
		}
	}
	
	public User updateProfile(UserProfileDTO dto) {
		var userInfo = getInfo(dto.getId());
		
	    String currentUsername = UserUtil.getCurrentAuthen().getName();
	    
	    if (!currentUsername.equals(userInfo.getUsername())) throw new RuntimeException("Chỉ được phép cập nhật hồ sơ cá nhân của mình.");
	    
		try {
			MapperUtil.copy(dto, userInfo, "id");
			return userRepository.save(userInfo);
		}
		catch(Exception e) {
			throw new RuntimeException("Lỗi cập nhật thông tin tài khoản cá nhân");
		}
	}
	
	public User changePassword(UserChangePasswordDTO dto) {
		var userInfo = getInfo(dto.getId());
		
		String currentUsername = UserUtil.getCurrentAuthen().getName();
		if (!currentUsername.equals(userInfo.getUsername())) {
	    	throw new RuntimeException("Chỉ được phép đổi mật khẩu của tài khoản đăng đăng nhập!");
	    }
		
		if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
			throw new RuntimeException("Nhập lại mật khẩu không trùng với mật khẩu mới");
		}
		
		if (!HashUtil.isMatch(dto.getOldPassword(), userInfo.getPassword())) {
			throw new RuntimeException("Mật khẩu cũ không chính xác");
		}	    
	    
	    try {
	    	userInfo.setPassword(HashUtil.encode(dto.getNewPassword()));
		    return userRepository.save(userInfo);
	    }
	    catch (Exception e) {
	    	throw new RuntimeException("Lỗi đổi mật khẩu");
	    }
	}
}
