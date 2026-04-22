package hocjava.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import hocjava.dto.AdminPerformance;
import hocjava.entity.User;
import hocjava.exception.ResourceNotFoundException;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	default User getInfo(Integer id) {
        return findById(id).orElseThrow(() -> 
            new ResourceNotFoundException("Không tìm thấy thông tin người dùng"));
    }
	
	default User getInfoByUsername(String username) {
		return findByUsername(username).orElseThrow(() -> 
        	new ResourceNotFoundException("Không tìm thấy thông tin người dùng"));
	}
	
	/*
	 * Tìm kiếm người dùng theo username
	 */
	Optional<User> findByUsername(String username);
	
	// Lấy danh sách người dùng khác với id
	List<User> findByIdNot(Integer id);

	/*
	 * Kiểm tra username đã tồn tại chưa
	 */
	boolean existsByUsername(String username);

	/*
	 * Lấy danh sách user và số lượng contact đã xử lý trong tháng
	 */
	@Query(value = """
			SELECT
			  u.full_name,
			  COUNT(c.id) as processed_count
			FROM users u
			LEFT JOIN contacts c ON c.updated_by = u.id AND c.status = 'done'
			  AND MONTH(c.updated_at) = MONTH(CURRENT_DATE)
			  AND YEAR(c.updated_at) = YEAR(CURRENT_DATE)
			GROUP BY u.id, u.full_name
			ORDER BY processed_count DESC
			 	""", nativeQuery = true)
	List<AdminPerformance> getAdminPerformanceThisMonth();
}
