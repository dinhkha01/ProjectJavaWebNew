

--Bảng RecruitmentPosition (Vị trí tuyển dụng)
INSERT INTO recruitment_position (id, name, description, minSalary, maxSalary, minExperience, expiredDate) VALUES
                                                                                                               (1, 'Backend Developer', 'Phát triển ứng dụng server-side', 1500.00, 3000.00, 2, '2025-12-31'),
                                                                                                               (2, 'Frontend Developer', 'Xây dựng giao diện người dùng', 1200.00, 2500.00, 1, '2025-11-30'),
                                                                                                               (3, 'Fullstack Engineer', 'Phát triển cả frontend và backend', 2000.00, 4000.00, 3, '2025-10-31'),
                                                                                                               (4, 'Java Developer', 'Chuyên sâu về Java và Spring Boot', 1800.00, 3500.00, 2, '2025-09-30'),
                                                                                                               (5, 'JavaScript Expert', 'Chuyên gia React/Vue.js', 1700.00, 3200.00, 3, '2025-08-31');

-- Bảng recruitment_position_technology (Liên kết vị trí - công nghệ)
INSERT INTO recruitment_position_technology (recruitmentPositionId, technologyId) VALUES
                                                                                      (1, 5),  -- Backend Developer -> Java
                                                                                      (1, 6),  -- Backend Developer -> C++
                                                                                      (2, 3),  -- Frontend Developer -> JavaScript
                                                                                      (3, 3),  -- Fullstack Engineer -> JavaScript
                                                                                      (3, 5),  -- Fullstack Engineer -> Java
                                                                                      (4, 5),  -- Java Developer -> Java
                                                                                      (5, 3);  -- JavaScript Expert -> JavaScript

--Bảng Application (Đơn ứng tuyển)
INSERT INTO application (candidateId, recruitmentPositionId, cvUrl, progress) VALUES
                                                                                  (1, 1, 'https://firebasestorage.googleapis.com/v0/b/khaa-4a9da.appspot.com/o/cvs%2F1733145300358-Diagram%20-%20Rikkei%20Job%20New.pdf?alt=media&token=40a9f0e1-0641-4994-b929-f1c1f4c159b1', 'pending'),
                                                                                  (1, 3, 'https://firebasestorage.googleapis.com/v0/b/khaa-4a9da.appspot.com/o/cvs%2F1733145300358-Diagram%20-%20Rikkei%20Job%20New.pdf?alt=media&token=40a9f0e1-0641-4994-b929-f1c1f4c159b1', 'handling'),
                                                                                  (3, 2, 'https://firebasestorage.googleapis.com/v0/b/khaa-4a9da.appspot.com/o/cvs%2F1733145300358-Diagram%20-%20Rikkei%20Job%20New.pdf?alt=media&token=40a9f0e1-0641-4994-b929-f1c1f4c159b1', 'interviewing'),
                                                                                  (3, 4, 'https://firebasestorage.googleapis.com/v0/b/khaa-4a9da.appspot.com/o/cvs%2F1733145300358-Diagram%20-%20Rikkei%20Job%20New.pdf?alt=media&token=40a9f0e1-0641-4994-b929-f1c1f4c159b1', 'pending'),
                                                                                  (3, 5, 'https://firebasestorage.googleapis.com/v0/b/khaa-4a9da.appspot.com/o/cvs%2F1733145300358-Diagram%20-%20Rikkei%20Job%20New.pdf?alt=media&token=40a9f0e1-0641-4994-b929-f1c1f4c159b1', 'done');