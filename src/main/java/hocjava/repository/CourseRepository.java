package hocjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import hocjava.dto.CourseContactStats;
import hocjava.entity.Course;
import hocjava.exception.ResourceNotFoundException;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
	default Course getInfo(Integer id) {
        return findById(id).orElseThrow(() -> 
            new ResourceNotFoundException("Không tìm thấy khóa học"));
    }
	
	// Lấy danh sách khóa học theo tên
	List<Course> findByCourseNameContaining(String name);
	
	// Lấy danh sách khóa học khác với courseID
	List<Course> findByIdNot(Integer id);
	
	
	boolean existsByCourseNameAndIdNot(String courseName, Integer id);

	/*
	 * Lấy thông tin tổng hợp của khóa học
	 */
	@Query(value = """
			    SELECT course_name, COUNT(con.id) as contact_count
			    FROM courses c
			    LEFT JOIN contacts con ON c.id = con.course_id 
			    GROUP BY c.id, c.course_name
			    ORDER BY contact_count DESC
			""", nativeQuery = true)
	List<CourseContactStats> getCourseContactStats();

}