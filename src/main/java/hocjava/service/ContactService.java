package hocjava.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import hocjava.dto.ContactDTO;
import hocjava.dto.ContactSearchDTO;
import hocjava.dto.ContactSummary;
import hocjava.dto.ContactUpdateDTO;
import hocjava.entity.Contact;
import hocjava.entity.Course;
import hocjava.exception.ResourceNotFoundException;
import hocjava.repository.ContactRepository;
import hocjava.repository.CourseRepository;
import hocjava.repository.UserRepository;
import hocjava.util.MapperUtil;
import hocjava.util.UserUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContactService {
	private final ContactRepository contactRepository;
	private final CourseRepository courseRepository;
	private final UserRepository userRepository;
	
	public Contact getInfo(Integer id) {
		return contactRepository.getInfo(id);
	}
	
	/*
	 * Tạo contact từ khách hàng
	 */
	public Contact createContact(ContactDTO dto) {
		var course = courseRepository.getInfo(dto.getCourseId());
		
		try {
			var contact = MapperUtil.clone(dto, Contact.class);
			contact.setCourse(course);
			contact = contactRepository.save(contact);
			return contact;
		}
		catch (Exception ex) {
			throw new RuntimeException("Lỗi tạo yêu cầu liên hệ");
		}
	}
	
	public Contact updateContact(ContactUpdateDTO dto) {
		var contact = getInfo(dto.getId());
		
		try {
			MapperUtil.copy(dto, contact, "id");
			var user = userRepository.getInfoByUsername(UserUtil.getCurrentAuthen().getName());
			
			contact.setUpdatedBy(user);
			
			return contactRepository.save(contact);
		}
		catch (Exception ex) {
			throw new RuntimeException("Lỗi cập nhật thông tin yêu cầu liên hệ");
		}
	}
	
	public Page<Contact> searchContacts(ContactSearchDTO dto) {
		Pageable pageable = PageRequest.of(
                dto.getPage(),
                dto.getSize(),
                Sort.by("createdAt").descending()
        );
		String  keyword = null;
		if (dto.getKeyword() != null) {
			keyword = dto.getKeyword().trim().toLowerCase();
			
			if (!keyword.isEmpty()) {
		        keyword = "%" + keyword + "%";
		    } else {
		        keyword = null;
		    }
		}
		
		//return contactRepository.filterContacts(dto.getStatus(), dto.getCourseId(), keyword, pageable);
		return contactRepository.searchContacts(dto.getStatus(), dto.getCourseId(), keyword, pageable);
	}
	
	/**
	 * Lấy thông tin tổng hợp contact
	 * @return
	 */
	public ContactSummary getContactSummary() {
		return contactRepository.getContactSummary();
	}
}
