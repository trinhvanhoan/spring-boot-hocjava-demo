package hocjava.util;

import java.util.Map;

import hocjava.entity.Contact;
import hocjava.entity.User;

public class EnumUtil {
	public static Map<Contact.ContactStatus, String> ContactStatusLabels() {
		return Map.of(
				Contact.ContactStatus.pending, "Mới",
				Contact.ContactStatus.processing, "Đang xử lý",
				Contact.ContactStatus.done, "Hoàn thành"
		);
	}
	
	public static Map<User.Role, String> UserRoleLabels() {
		return Map.of(
				User.Role.USER, "Người dùng",
				User.Role.ADMIN, "Quản trị hệ thống"
		);
	}
}
