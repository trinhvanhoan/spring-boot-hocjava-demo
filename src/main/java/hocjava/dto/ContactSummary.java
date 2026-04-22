package hocjava.dto;

public interface ContactSummary {
    Long getTotalCount();      // Tổng số request
    Long getPendingCount();    // Tổng số request mới (pending)
    Long getProcessingCount(); // Tổng số request đang xử lý (processing)
    Long getDoneCount();       // Tổng số request đã xong (done)
}