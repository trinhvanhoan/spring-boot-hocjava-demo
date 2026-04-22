package hocjava.repository;

import org.springframework.stereotype.Repository;

import hocjava.entity.Contact;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Repository
public class ContactRepositoryCustomImpl implements ContactRepositoryCustom {
	@PersistenceContext
    private EntityManager entityManager;
	
	@Override
    public Page<Contact> filterContacts(Contact.ContactStatus status, Integer courseId, String keyword, Pageable pageable) {
        // Xây dựng phần điều kiện (WHERE clause) dùng chung cho cả query data và query count
        StringBuilder whereClause = new StringBuilder(" WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        if (status != null) {
            whereClause.append(" AND c.status = :status");
            params.put("status", status);
        }
        if (courseId != null) {
            whereClause.append(" AND c.course.id = :courseId");
            params.put("courseId", courseId);
        }
        
        // Xử lý Keyword
        if (keyword != null && !keyword.trim().isEmpty()) {
            whereClause.append("""
            		AND (LOWER(c.fullName) LIKE :keyword
            		OR LOWER(c.email) LIKE :keyword
            		OR LOWER(c.phoneNumber) LIKE :keyword)
            		""");
            params.put("keyword", "%" + keyword.trim().toLowerCase() + "%");
        }

        // Truy vấn lấy dữ liệu (Data Query)
        StringBuilder dataSql = new StringBuilder("SELECT c FROM Contact c").append(whereClause);
        
        // Thêm sắp xếp (Ordering) từ pageable nếu có
        if (pageable.getSort().isSorted()) {
        	dataSql.append(" ORDER BY ");
        	String orderBy = pageable.getSort().stream()
                    .map(order -> "c." + order.getProperty() + " " + order.getDirection().name())
                    .collect(Collectors.joining(", "));
        	
                dataSql.append(orderBy);
        }

        TypedQuery<Contact> dataQuery = entityManager.createQuery(dataSql.toString(), Contact.class);
        params.forEach(dataQuery::setParameter);

        // Thực  hiện việc phân trang
        dataQuery.setFirstResult((int) pageable.getOffset());
        dataQuery.setMaxResults(pageable.getPageSize());

        List<Contact> contacts = dataQuery.getResultList();

        // Truy vấn lấy tổng số bản ghi (Count Query)
        StringBuilder countSql = new StringBuilder("SELECT COUNT(c) FROM Contact c").append(whereClause);
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql.toString(), Long.class);
        
        params.forEach(countQuery::setParameter);
        
        Long totalElements = countQuery.getSingleResult();

        // Trả về đối tượng Page
        return new PageImpl<>(contacts, pageable, totalElements);
    }
}
