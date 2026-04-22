package hocjava.dto;

public interface ContactCountByMonth {
	String getMonth(); // Trả về định dạng "MM/yyyy"
    Long getCount();   // Tổng số contact trong tháng đó
}
