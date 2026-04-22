package hocjava.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import hocjava.dto.ContactCountByDate;
import hocjava.dto.ContactCountByMonth;
import hocjava.dto.ContactSummary;
import hocjava.entity.Contact;
import hocjava.entity.Course;
import hocjava.entity.User;
import hocjava.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer>, ContactRepositoryCustom {
	/*
	 * Hàm lấy thông tin liên hệ thông qua id
	 */
	default Contact getInfo(Integer id) {
        return findById(id).orElseThrow(() -> 
            new ResourceNotFoundException("Không tìm thấy yêu cầu liên hệ"));
    }
	
	// Tổng số contact theo status
	long countByStatus(Contact.ContactStatus status);

	@Query(value = """
			SELECT
				DATE(created_at) as date,
				COUNT(*) as count,
				SUM(CASE WHEN status = 'done' THEN 1 ELSE 0 END) as count_done
			FROM contacts
			WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
			GROUP BY DATE(created_at)
			ORDER BY date ASC
			""", nativeQuery = true)
	List<ContactCountByDate> countContactsLast7Days();

	/*
	 * Lấy TOP 3 contact mới nhất
	 */
	List<Contact> findTop3ByOrderByCreatedAtDesc();
	
	/*
	 * Lấy TOP 10 contact mới nhất của tháng
	 */
	@Query(value="""
			SELECT * FROM contacts c 
			WHERE MONTH(c.created_at) = MONTH(CURRENT_DATE)
			  AND YEAR(c.created_at) = YEAR(CURRENT_DATE) 
			ORDER BY created_at DESC
			LIMIT 10
			""", nativeQuery = true)
	List<Contact> getTop10LastContactThisMonth();

	/*
	 * Thống kê tổng hợp
	 */
	@Query("""
		    SELECT
		        COUNT(c) as totalCount,
		        SUM(CASE WHEN c.status = 'pending' THEN 1 ELSE 0 END) as pendingCount,
		        SUM(CASE WHEN c.status = 'processing' THEN 1 ELSE 0 END) as processingCount,
		        SUM(CASE WHEN c.status = 'done' THEN 1 ELSE 0 END) as doneCount
		    FROM Contact c
			""")
	ContactSummary getContactSummary();
	
	/*
	 * Lấy thông tin tổng hợp 6 tháng
	 */
	@Query(value = """
			SELECT
			    DATE_FORMAT(created_at, '%m/%Y') AS month,
			    COUNT(*) AS count
			FROM contacts
			WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)
			GROUP BY month
			ORDER BY MIN(created_at) ASC
			""", nativeQuery = true)
	List<ContactCountByMonth> countContactsLast6Months();
	
	/*
	 * Lấy tổng số contact đã xử lý trong tháng
	 */
	@Query(value="""
			SELECT
			  COUNT(*)
			FROM contacts c
			WHERE c.status = 'done'
			  AND MONTH(c.updated_at) = MONTH(CURRENT_DATE)
			  AND YEAR(c.updated_at) = YEAR(CURRENT_DATE)
			""", nativeQuery = true)
	Long getTotalProcessThisMonth();
	
	/*
	 * Kiểm tra có contact được cập nhật bởi user hay không
	 */
	boolean existsByUpdatedBy(User user);
	
	boolean existsByCourse(Course course);
	
	@Modifying
    @Transactional
    @Query("UPDATE Contact c SET c.updatedBy = :newUser WHERE c.updatedBy = :oldUser")
    int transferUpdatedByUser(@Param("oldUser") User oldUser, @Param("newUser") User newUser);
	
	@Modifying
    @Transactional
    @Query("UPDATE Contact c SET c.course = :newCourse WHERE c.course = :oldCourse")
    int transferUpdatedByCourse(@Param("oldCourse") Course oldCourse, @Param("newCourse") Course newCourse);
	
	@Query("""
		    SELECT c FROM Contact c WHERE
		        (:status IS NULL OR c.status = :status)
		    AND (:courseId IS NULL OR c.course.id = :courseId)
		    AND (:keyword IS NULL OR 
		        LOWER(c.fullName) LIKE :keyword OR
		        LOWER(c.email) LIKE :keyword OR
		        LOWER(c.phoneNumber) LIKE :keyword
		    )
		""")
	Page<Contact> searchContacts(@Param("status") Contact.ContactStatus status, @Param("courseId") Integer courseId,
			@Param("keyword") String keyword, Pageable pageable);
}