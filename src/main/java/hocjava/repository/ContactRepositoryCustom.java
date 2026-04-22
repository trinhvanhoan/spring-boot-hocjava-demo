package hocjava.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import hocjava.entity.Contact;

public interface ContactRepositoryCustom {
	Page<Contact> filterContacts(Contact.ContactStatus status, Integer courseId, String keyword, Pageable pageable);
}
