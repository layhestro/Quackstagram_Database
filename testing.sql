-- testing_data.sql for Quackstagram Database
-- Contains dummy data to be inserted after schema creation

-- Insert test users
-- Note: Passwords are 'password123' hashed with SHA-256 and salt
INSERT INTO Users (username, bio, passwordHash, salt, profileImagePath, postsCount, followersCount, followingCount) VALUES
('Xylo', 'Fierce warrior, not solo', 'Ywp+k/lvU0hXIvZaS3Q0C5qD9oT4wx5tpx1Js0sTJ6k=', 'J9FpEqoL4ceIqn9W/asRnw==', 'img/storage/profile/Xylo.png', 3, 2, 2),
('Lorin', 'For copyright reasons, I am not Grogu', 'p+8+HWS1eJIz6oB3/sXSZkTt3HNFLEtMuoIC1F+u7+M=', 'DSy9TX9rEBSIwOc7ZEW7+g==', 'img/storage/profile/Lorin.png', 2, 3, 1),
('Zara', 'Humanoid robot much like the rest', 'bPTzvvXgTRc0c/z9d4psEtB5rvJy9K6MpACvvOGC7a4=', '/WN3gWwpGs386ZqABD+2NA==', 'img/storage/profile/Zara.png', 2, 1, 2),
('Mystar', 'Xylo and I are not the same!', 'YDVdIu2IFASgsEC603ZQxnojmcKioXuiA+da0EAduu8=', 'O4F+CjFrKkyx8spx7X7W8A==', 'img/storage/profile/Mystar.png', 2, 1, 3),
('TestUser', 'Just a test account', 'AeXYU3HuDvGJRZZe+nzxbXfr+tPJlqXspzVybTj4OP0=', 'b3fZXcO6eRsDJx6uHKP3ZA==', 'img/logos/DACS.png', 0, 0, 2);

-- Insert test pictures
INSERT INTO Pictures (imageId, username, imagePath, caption, likesCount, timestamp) VALUES
('Lorin_1', 'Lorin', 'img/uploaded/Lorin_1.png', 'In the cookie jar my hand was not.', 5, '2023-12-17 19:07:43'),
('Lorin_2', 'Lorin', 'img/uploaded/Lorin_2.png', 'Meditate I must.', 3, '2023-12-17 19:09:35'),
('Xylo_1', 'Xylo', 'img/uploaded/Xylo_1.png', 'My tea strong as Force is.', 2, '2023-12-17 19:22:40'),
('Xylo_2', 'Xylo', 'img/uploaded/Xylo_2.png', 'Jedi mind trick failed.', 4, '2023-12-17 19:23:14'),
('Xylo_3', 'Xylo', 'img/uploaded/Xylo_3.png', 'Weekend vibes', 1, '2023-12-18 14:08:00'),
('Zara_1', 'Zara', 'img/uploaded/Zara_1.png', 'Lost my map I have. Oops.', 3, '2023-12-17 19:24:31'),
('Zara_2', 'Zara', 'img/uploaded/Zara_2.png', 'Yoga with Yoda', 1, '2023-12-17 19:25:03'),
('Mystar_1', 'Mystar', 'img/uploaded/Mystar_1.png', 'Cookies gone?', 3, '2023-12-17 19:26:50'),
('Mystar_2', 'Mystar', 'img/uploaded/Mystar_2.png', 'In my soup a fly is.', 2, '2023-12-17 19:27:24');

-- Insert follow relationships
INSERT INTO Follows (follower, followed, timestamp) VALUES
('Xylo', 'Lorin', '2023-12-17 18:30:00'),
('Xylo', 'Mystar', '2023-12-17 18:31:00'),
('Zara', 'Lorin', '2023-12-17 18:32:00'),
('Zara', 'Xylo', '2023-12-17 18:33:00'),
('Mystar', 'Lorin', '2023-12-17 18:34:00'),
('Mystar', 'Zara', '2023-12-17 18:35:00'),
('Mystar', 'Xylo', '2023-12-17 18:36:00'),
('Lorin', 'Mystar', '2023-12-17 18:37:00'),
('TestUser', 'Xylo', '2023-12-17 18:38:00'),
('TestUser', 'Lorin', '2023-12-17 18:39:00');

-- Insert likes
INSERT INTO Likes (username, imageId, timestamp) VALUES
('Xylo', 'Lorin_1', '2023-12-17 19:40:00'),
('Xylo', 'Lorin_2', '2023-12-17 19:41:00'),
('Zara', 'Lorin_1', '2023-12-17 19:42:00'),
('Mystar', 'Lorin_1', '2023-12-17 19:43:00'),
('Lorin', 'Xylo_1', '2023-12-17 19:44:00'),
('Lorin', 'Xylo_2', '2023-12-17 19:45:00'),
('Mystar', 'Xylo_2', '2023-12-17 19:46:00'),
('Zara', 'Xylo_2', '2023-12-17 19:47:00'),
('Lorin', 'Mystar_1', '2023-12-17 19:48:00'),
('Xylo', 'Mystar_1', '2023-12-17 19:49:00'),
('Zara', 'Mystar_1', '2023-12-17 19:50:00'),
('Lorin', 'Mystar_2', '2023-12-17 19:51:00'),
('Xylo', 'Mystar_2', '2023-12-17 19:52:00'),
('Lorin', 'Zara_1', '2023-12-17 19:53:00'),
('Mystar', 'Zara_1', '2023-12-17 19:54:00'),
('Xylo', 'Zara_1', '2023-12-17 19:55:00'),
('Lorin', 'Zara_2', '2023-12-17 19:56:00'),
('TestUser', 'Xylo_3', '2023-12-18 15:00:00');

-- Insert comments
INSERT INTO Comments (username, imageId, content, timestamp) VALUES
('Xylo', 'Lorin_1', 'Very wise, you are.', '2023-12-17 20:00:00'),
('Zara', 'Lorin_1', 'LOL reminds me of someone!', '2023-12-17 20:01:00'),
('Mystar', 'Lorin_2', 'Teach me your ways', '2023-12-17 20:02:00'),
('Lorin', 'Xylo_1', 'Recipe, can you share?', '2023-12-17 20:03:00'),
('Mystar', 'Xylo_2', 'These are not the droids you are looking for', '2023-12-17 20:04:00'),
('Xylo', 'Zara_1', 'Use the GPS, you should', '2023-12-17 20:05:00'),
('Lorin', 'Mystar_1', 'Not me, it was', '2023-12-17 20:06:00'),
('Zara', 'Mystar_2', 'Extra protein!', '2023-12-17 20:07:00');

-- Insert notifications
-- Note: In a real database, these would be created by triggers when likes/comments/follows are added
INSERT INTO Notifications (receiverUsername, senderUsername, imageId, timestamp, type) VALUES
('Lorin', 'Xylo', 'Lorin_1', '2023-12-17 19:40:00', 'LIKE'),
('Lorin', 'Xylo', 'Lorin_2', '2023-12-17 19:41:00', 'LIKE'),
('Lorin', 'Zara', 'Lorin_1', '2023-12-17 19:42:00', 'LIKE'),
('Lorin', 'Mystar', 'Lorin_1', '2023-12-17 19:43:00', 'LIKE'),
('Xylo', 'Lorin', 'Xylo_1', '2023-12-17 19:44:00', 'LIKE'),
('Xylo', 'Lorin', 'Xylo_2', '2023-12-17 19:45:00', 'LIKE'),
('Xylo', 'Mystar', 'Xylo_2', '2023-12-17 19:46:00', 'LIKE'),
('Xylo', 'Zara', 'Xylo_2', '2023-12-17 19:47:00', 'LIKE'),
('Mystar', 'Lorin', 'Mystar_1', '2023-12-17 19:48:00', 'LIKE'),
('Mystar', 'Xylo', 'Mystar_1', '2023-12-17 19:49:00', 'LIKE'),
('Mystar', 'Zara', 'Mystar_1', '2023-12-17 19:50:00', 'LIKE'),
('Mystar', 'Lorin', 'Mystar_2', '2023-12-17 19:51:00', 'LIKE'),
('Mystar', 'Xylo', 'Mystar_2', '2023-12-17 19:52:00', 'LIKE'),
('Zara', 'Lorin', 'Zara_1', '2023-12-17 19:53:00', 'LIKE'),
('Zara', 'Mystar', 'Zara_1', '2023-12-17 19:54:00', 'LIKE'),
('Zara', 'Xylo', 'Zara_1', '2023-12-17 19:55:00', 'LIKE'),
('Zara', 'Lorin', 'Zara_2', '2023-12-17 19:56:00', 'LIKE'),
('Lorin', 'Xylo', null, '2023-12-17 18:30:00', 'FOLLOW'),
('Mystar', 'Xylo', null, '2023-12-17 18:31:00', 'FOLLOW'),
('Lorin', 'Zara', null, '2023-12-17 18:32:00', 'FOLLOW'),
('Xylo', 'Zara', null, '2023-12-17 18:33:00', 'FOLLOW'),
('Lorin', 'Mystar', null, '2023-12-17 18:34:00', 'FOLLOW'),
('Zara', 'Mystar', null, '2023-12-17 18:35:00', 'FOLLOW'),
('Xylo', 'Mystar', null, '2023-12-17 18:36:00', 'FOLLOW'),
('Mystar', 'Lorin', null, '2023-12-17 18:37:00', 'FOLLOW'),
('Lorin', 'Xylo', 'Lorin_1', '2023-12-17 20:00:00', 'COMMENT'),
('Lorin', 'Zara', 'Lorin_1', '2023-12-17 20:01:00', 'COMMENT'),
('Lorin', 'Mystar', 'Lorin_2', '2023-12-17 20:02:00', 'COMMENT'),
('Xylo', 'Lorin', 'Xylo_1', '2023-12-17 20:03:00', 'COMMENT'),
('Xylo', 'Mystar', 'Xylo_2', '2023-12-17 20:04:00', 'COMMENT'),
('Zara', 'Xylo', 'Zara_1', '2023-12-17 20:05:00', 'COMMENT'),
('Mystar', 'Lorin', 'Mystar_1', '2023-12-17 20:06:00', 'COMMENT'),
('Mystar', 'Zara', 'Mystar_2', '2023-12-17 20:07:00', 'COMMENT');

-- Insert some follower history data for analytics
INSERT INTO FollowerHistory (username, followerCount, timestamp) VALUES
('Lorin', 1, '2023-12-17 18:30:00'),
('Lorin', 2, '2023-12-17 18:32:00'),
('Lorin', 3, '2023-12-17 18:34:00'),
('Xylo', 1, '2023-12-17 18:33:00'),
('Xylo', 2, '2023-12-17 18:36:00'),
('Mystar', 1, '2023-12-17 18:37:00'),
('Zara', 1, '2023-12-17 18:35:00');

-- Insert some system logs
INSERT INTO SystemLogs (event_type, description, timestamp) VALUES
('USER_REGISTRATION', 'User Xylo registered', '2023-12-17 18:00:00'),
('USER_REGISTRATION', 'User Lorin registered', '2023-12-17 18:01:00'),
('USER_REGISTRATION', 'User Zara registered', '2023-12-17 18:02:00'),
('USER_REGISTRATION', 'User Mystar registered', '2023-12-17 18:03:00'),
('USER_REGISTRATION', 'User TestUser registered', '2023-12-17 18:04:00'),
('CONTENT_UPLOAD', 'User Lorin uploaded new content: Lorin_1', '2023-12-17 19:07:43'),
('CONTENT_UPLOAD', 'User Lorin uploaded new content: Lorin_2', '2023-12-17 19:09:35'),
('ENGAGEMENT_UPDATE', 'Post Lorin_1 has new engagement score: 6.25', '2023-12-17 20:01:00'),
('ERROR', 'Failed login attempt for username: nonexistent', '2023-12-17 21:00:00');