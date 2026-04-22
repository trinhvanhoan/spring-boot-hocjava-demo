package hocjava.service;

import hocjava.repository.ContactRepository;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hocjava.dto.CourseDTO;

import hocjava.entity.Course;
import hocjava.exception.TransferDeleteException;
import hocjava.exception.ResourceNotFoundException;
import hocjava.repository.CourseRepository;
import hocjava.util.MapperUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseService {
	private final ContactRepository contactRepository;
	private final CourseRepository courseRepository;

	public Course getInfo(Integer id) {
		return courseRepository.getInfo(id);
	}

	public List<Course> getAllCourses() {
		return courseRepository.findAll();
	}

	public List<Course> getAllCoursesNotId(Integer id) {
		return courseRepository.findByIdNot(id);
	}

	public Course create(CourseDTO dto) {
		dto.setCourseName(dto.getCourseName().trim());

		if (courseRepository.existsByCourseNameAndIdNot(dto.getCourseName(), dto.getId())) {
			throw new RuntimeException("Đã tồn tại tên khóa học này. Vui lòng nhập tên khác.");
		}

		try {
			var course = MapperUtil.clone(dto, Course.class);
			course = courseRepository.save(course);
			return course;
		} catch (Exception ex) {
			throw new RuntimeException("Lỗi tạo khóa học");
		}
	}

	public Course update(CourseDTO dto) {
		dto.setCourseName(dto.getCourseName().trim());

		if (courseRepository.existsByCourseNameAndIdNot(dto.getCourseName(), dto.getId())) {
			throw new RuntimeException("Đã tồn tại tên khóa học này. Vui lòng nhập tên khác.");
		}

		var course = getInfo(dto.getId());

		try {
			MapperUtil.copy(dto, course);

			course = courseRepository.save(course);
			return course;
		} catch (Exception ex) {
			throw new RuntimeException("Lỗi cập nhật thông tin khóa học");
		}
	}

	public Course delete(Integer id) {
		var course = getInfo(id);

		if (contactRepository.existsByCourse(course)) {
			throw new TransferDeleteException(
					"Khóa học [%s] đã có một số thông tin liên hệ. Cần chuyển giao trước khi xóa!"
							.formatted(course.getCourseName()));
		}

		try {
			courseRepository.delete(course);
			return course;
		} catch (Exception ex) {
			throw new RuntimeException("Lỗi xóa khóa học");
		}
	}
	
	@Transactional(rollbackFor = Exception.class)
	public Course transferAndDelete(Integer oldCourseId, Integer newCourseId) {
		if (oldCourseId.equals(newCourseId)) {
			throw new RuntimeException("Khóa học cần chuyển giao trước khi xóa phải khác với khóa học bị xóa!");
		}
		var oldCourse = getInfo(oldCourseId);
		var newCourse = getInfo(newCourseId);
		
		try {
			contactRepository.transferUpdatedByCourse(oldCourse, newCourse);
			courseRepository.delete(oldCourse);
			return oldCourse;
		} catch (Exception ex) {
			throw new RuntimeException("Lỗi xóa khóa học");
		}
	}
}
