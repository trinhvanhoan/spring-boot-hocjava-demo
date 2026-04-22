-- 1. Tạo Database với bảng mã hỗ trợ tiếng Việt đầy đủ
CREATE DATABASE IF NOT EXISTS hocjava_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE hocjava_db;

-- 2. Tạo bảng users (danh sách thành viên quản trị)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'USER') DEFAULT 'ADMIN',
    status INT DEFAULT(1), -- 1: Active, 0: Inactive
    
    -- Các thông tin bổ sung
    full_name VARCHAR(100) NULL,
    email VARCHAR(100) NULL,
    phone VARCHAR(100) NULL
);

-- 3. Tạo bảng quản lý danh mục khóa học (Để đồng bộ với Dropdown trong Admin)
CREATE TABLE IF NOT EXISTS courses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(100) NOT NULL UNIQUE
);

-- 4. Tạo bảng chính: contacts
CREATE TABLE IF NOT EXISTS contacts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    
    -- Thông tin khách hàng (Dựa trên Validate form)
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15) NOT NULL, -- Validate 10-11 số
    
    -- Nội dung yêu cầu
    course_id INT, -- Khóa ngoại liên kết bảng courses
    message TEXT NOT NULL,
    
    -- Quản lý Admin (Dành cho Dashboard)
    status ENUM('pending', 'processing', 'done') DEFAULT 'pending',
    admin_note TEXT NULL, -- Ghi chú nội bộ của Admin
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    updated_by INT,
    
    -- Ràng buộc
    CONSTRAINT fk_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE SET NULL,
    CONSTRAINT fk_users FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 5. Tạo danh sách người quản trị
INSERT INTO users (username, password, role, status, full_name, email, phone) VALUES
('admin', '$2a$10$6EpE8oD.y6jsS9nB/J02R.A27xoUuv5GV6o8ucHyzP44aWo7jxH3a', 'ADMIN', 1, 'Administrator', 'admin@gmail.com', '0900000001'), -- password: admin
('hoantv', '$2a$10$hJKvKTRJEx2SSLfdaG8xSu2BxLdfMmR6rjw2oA.yYrWP63S3yEF2q', 'USER', 1, 'Trịnh Văn Hoan', 'hoantv@gmail.com', '0900000002'),
('binhnv', '$2a$10$eduxt0SNs/AAMvTCcVuzduMulUIQNtXjo3sQXyztt0K/HHpDccaqe', 'USER', 1, 'Nguyễn Văn Bình', 'binhnv@gmail.com', '0900000003'),
('anlv', '$2a$10$4Fwbi4GDEepePtRiI.ConuXZwDF1HF.CAGRL.xIhOTg3nZ2yr9.HG', 'USER', 1, 'Lê Văn An', 'anlv@gmail.com', '0900000004'),
('quantt', '$2a$10$9AKJBFkNjai0mkvSDYNWReOF3dIBdkxwzYTQxftswAjgPuLzeDX0G', 'USER', 1, 'Trần Trọng Quân', 'quantt@gmail.com', '0900000005'),
('daipt', '$2a$10$TjPXIN4keJ28qxukeYYpveRVxWupoqKEiNmXWNq0JF2LE8YYHpkH2', 'USER', 1, 'Phan Trọng Đại', 'quantt@gmail.com', '0900000005');

-- 5. Tạo danh mục các khóa học
INSERT INTO courses (course_name) VALUES
('Java Core'),
('Spring Boot'),
('Hibernate'),
('MySQL Database'),
('Frontend HTML CSS'),
('JavaScript'),
('ReactJS'),
('Python cơ bản'),
('Data Structures & Algorithms'),
('Docker & DevOps');

-- 6. Tạo dữ liệu mẫu cho bảng contacts
INSERT INTO contacts 
(full_name, email, phone_number, course_id, message, status, admin_note, updated_by)
VALUES
('Nguyen Van A1', 'a1@gmail.com', '0980000001', 1, 'Tôi muốn học Java', 'pending', NULL, NULL),
('Nguyen Van A2', 'a2@gmail.com', '0980000002', 2, 'Tư vấn Spring Boot', 'processing', 'Đã gọi tư vấn', 1),
('Nguyen Van A3', 'a3@gmail.com', '0980000003', 3, 'Hỏi về Hibernate', 'done', 'Đã đăng ký', 2),
('Nguyen Van A4', 'a4@gmail.com', '0980000004', 4, 'Học MySQL', 'pending', NULL, NULL),
('Nguyen Van A5', 'a5@gmail.com', '0980000005', 5, 'Frontend cơ bản', 'processing', 'Đang xử lý', 3),

('Nguyen Van A6', 'a6@gmail.com', '0980000006', 6, 'JS nâng cao', 'done', 'Hoàn tất', 2),
('Nguyen Van A7', 'a7@gmail.com', '0980000007', 7, 'ReactJS', 'pending', NULL, NULL),
('Nguyen Van A8', 'a8@gmail.com', '0980000008', 8, 'Python', 'processing', 'Đã gửi mail', 4),
('Nguyen Van A9', 'a9@gmail.com', '0980000009', 9, 'DSA', 'done', 'OK', 5),
('Nguyen Van A10', 'a10@gmail.com', '0980000010', 10, 'DevOps', 'pending', NULL, NULL),

('Tran Thi B1', 'b1@gmail.com', '0970000001', 1, 'Java cho người mới', 'processing', 'Đang gọi', 1),
('Tran Thi B2', 'b2@gmail.com', '0970000002', 2, 'Spring Boot API', 'done', 'Đã xong', 2),
('Tran Thi B3', 'b3@gmail.com', '0970000003', 3, 'Hibernate ORM', 'pending', NULL, NULL),
('Tran Thi B4', 'b4@gmail.com', '0970000004', 4, 'SQL nâng cao', 'processing', 'Chưa phản hồi', 3),
('Tran Thi B5', 'b5@gmail.com', '0970000005', 5, 'HTML CSS', 'done', 'OK', 4),

('Tran Thi B6', 'b6@gmail.com', '0970000006', 6, 'JS cơ bản', 'pending', NULL, NULL),
('Tran Thi B7', 'b7@gmail.com', '0970000007', 7, 'React project', 'processing', 'Đang học', 2),
('Tran Thi B8', 'b8@gmail.com', '0970000008', 8, 'Python AI', 'done', 'Hoàn tất', 1),
('Tran Thi B9', 'b9@gmail.com', '0970000009', 9, 'Thuật toán', 'pending', NULL, NULL),
('Tran Thi B10', 'b10@gmail.com', '0970000010', 10, 'Docker', 'processing', 'Đang xử lý', 5),

('Le Van C1', 'c1@gmail.com', '0960000001', 1, 'Java nâng cao', 'done', 'OK', 3),
('Le Van C2', 'c2@gmail.com', '0960000002', 2, 'Spring Security', 'pending', NULL, NULL),
('Le Van C3', 'c3@gmail.com', '0960000003', 3, 'Hibernate cache', 'processing', 'Chưa xong', 4),
('Le Van C4', 'c4@gmail.com', '0960000004', 4, 'MySQL tối ưu', 'done', 'Hoàn tất', 2),
('Le Van C5', 'c5@gmail.com', '0960000005', 5, 'UI/UX', 'pending', NULL, NULL),

('Le Van C6', 'c6@gmail.com', '0960000006', 6, 'JS async', 'processing', 'Đang gọi', 1),
('Le Van C7', 'c7@gmail.com', '0960000007', 7, 'React hooks', 'done', 'OK', 2),
('Le Van C8', 'c8@gmail.com', '0960000008', 8, 'Python web', 'pending', NULL, NULL),
('Le Van C9', 'c9@gmail.com', '0960000009', 9, 'Thuật toán nâng cao', 'processing', 'Đang học', 3),
('Le Van C10', 'c10@gmail.com', '0960000010', 10, 'CI/CD', 'done', 'Xong', 4);
