package hocjava.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Locale;

import org.ocpsoft.prettytime.PrettyTime;

@Entity
@Table(name = "contacts")
@Data
public class Contact {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('pending', 'processing', 'done')")
    private ContactStatus status = ContactStatus.pending;

    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationship: Một liên hệ thuộc về một khóa học
    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private Course course;

    // Relationship: Một liên hệ được cập nhật bởi một User (Admin)
    @ManyToOne
    @JoinColumn(name = "updated_by", referencedColumnName = "id")
    private User updatedBy;
    
    public String getRelativeTime() {
    	if (this.createdAt == null) return "";
        
        PrettyTime p = new PrettyTime(Locale.of("vi"));
        java.util.Date date = java.sql.Timestamp.valueOf(this.createdAt);
        
        return p.format(date);
    }

    public enum ContactStatus {
        pending, processing, done
    }
}