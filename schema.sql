-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS quackstagram;
USE quackstagram;

-- Create user if it doesn't exist (MariaDB syntax)
-- First check if the user exists to avoid errors
CREATE USER IF NOT EXISTS 'user'@'localhost' IDENTIFIED BY 'pass';
GRANT ALL PRIVILEGES ON quackstagram.* TO 'user'@'localhost';
FLUSH PRIVILEGES;

-- Simple schema.sql for Quackstagram Database
-- 3NF compliant

-- Drop existing tables if they exist
DROP TABLE IF EXISTS FollowerHistory;
DROP TABLE IF EXISTS Comments;
DROP TABLE IF EXISTS Likes;
DROP TABLE IF EXISTS Notifications;
DROP TABLE IF EXISTS Pictures;
DROP TABLE IF EXISTS Follows;
DROP TABLE IF EXISTS Users;

-- Create Users table
CREATE TABLE Users (
    username VARCHAR(50) PRIMARY KEY,
    bio TEXT,
    passwordHash VARCHAR(255) NOT NULL,
    salt VARCHAR(50) NOT NULL,
    profileImagePath VARCHAR(255) DEFAULT 'img/logos/DACS.png'
);

-- Create Pictures table
CREATE TABLE Pictures (
    imageId VARCHAR(100) PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    imagePath VARCHAR(255) NOT NULL,
    caption TEXT,
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (username) REFERENCES Users(username) ON DELETE CASCADE
);

-- Create Follows table
CREATE TABLE Follows (
    follower VARCHAR(50) NOT NULL,
    followed VARCHAR(50) NOT NULL,
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (follower, followed),
    FOREIGN KEY (follower) REFERENCES Users(username) ON DELETE CASCADE,
    FOREIGN KEY (followed) REFERENCES Users(username) ON DELETE CASCADE
);

-- Create Notifications table
CREATE TABLE Notifications (
    notificationId INT AUTO_INCREMENT PRIMARY KEY,
    receiverUsername VARCHAR(50) NOT NULL,
    senderUsername VARCHAR(50) NOT NULL,
    imageId VARCHAR(100),
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    type ENUM('LIKE', 'COMMENT', 'FOLLOW') NOT NULL,
    FOREIGN KEY (receiverUsername) REFERENCES Users(username) ON DELETE CASCADE,
    FOREIGN KEY (senderUsername) REFERENCES Users(username) ON DELETE CASCADE,
    FOREIGN KEY (imageId) REFERENCES Pictures(imageId) ON DELETE SET NULL
);

-- Create Likes table
CREATE TABLE Likes (
    username VARCHAR(50) NOT NULL,
    imageId VARCHAR(100) NOT NULL,
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (username, imageId),
    FOREIGN KEY (username) REFERENCES Users(username) ON DELETE CASCADE,
    FOREIGN KEY (imageId) REFERENCES Pictures(imageId) ON DELETE CASCADE
);

-- Create Comments table
CREATE TABLE Comments (
    commentId INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    imageId VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (username) REFERENCES Users(username) ON DELETE CASCADE,
    FOREIGN KEY (imageId) REFERENCES Pictures(imageId) ON DELETE CASCADE
);

-- Create FollowerHistory table for analytics
CREATE TABLE FollowerHistory (
    historyId INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    followerCount INT NOT NULL CHECK (followerCount >= 0),
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (username) REFERENCES Users(username) ON DELETE CASCADE
);

-- Insert test data

-- Insert test users
-- Note: Passwords are 'password123' hashed with SHA-256 and salt
INSERT INTO Users (username, bio, passwordHash, salt, profileImagePath) VALUES
('Xylo', 'Fierce warrior, not solo', 'Ywp+k/lvU0hXIvZaS3Q0C5qD9oT4wx5tpx1Js0sTJ6k=', 'J9FpEqoL4ceIqn9W/asRnw==', 'img/storage/profile/Xylo.png'),
('Lorin', 'For copyright reasons, I am not Grogu', 'p+8+HWS1eJIz6oB3/sXSZkTt3HNFLEtMuoIC1F+u7+M=', 'DSy9TX9rEBSIwOc7ZEW7+g==', 'img/storage/profile/Lorin.png'),
('Zara', 'Humanoid robot much like the rest', 'bPTzvvXgTRc0c/z9d4psEtB5rvJy9K6MpACvvOGC7a4=', '/WN3gWwpGs386ZqABD+2NA==', 'img/storage/profile/Zara.png'),
('Mystar', 'Xylo and I are not the same!', 'YDVdIu2IFASgsEC603ZQxnojmcKioXuiA+da0EAduu8=', 'O4F+CjFrKkyx8spx7X7W8A==', 'img/storage/profile/Mystar.png'),
('TestUser', 'Just a test account', 'AeXYU3HuDvGJRZZe+nzxbXfr+tPJlqXspzVybTj4OP0=', 'b3fZXcO6eRsDJx6uHKP3ZA==', 'img/logos/DACS.png');

-- Insert test pictures
INSERT INTO Pictures (imageId, username, imagePath, caption, timestamp) VALUES
('Lorin_1', 'Lorin', 'img/uploaded/Lorin_1.png', 'In the cookie jar my hand was not.', '2023-12-17 19:07:43'),
('Lorin_2', 'Lorin', 'img/uploaded/Lorin_2.png', 'Meditate I must.', '2023-12-17 19:09:35'),
('Xylo_1', 'Xylo', 'img/uploaded/Xylo_1.png', 'My tea strong as Force is.', '2023-12-17 19:22:40'),
('Xylo_2', 'Xylo', 'img/uploaded/Xylo_2.png', 'Jedi mind trick failed.', '2023-12-17 19:23:14'),
('Xylo_3', 'Xylo', 'img/uploaded/Xylo_3.png', 'Weekend vibes', '2023-12-18 14:08:00'),
('Zara_1', 'Zara', 'img/uploaded/Zara_1.png', 'Lost my map I have. Oops.', '2023-12-17 19:24:31'),
('Zara_2', 'Zara', 'img/uploaded/Zara_2.png', 'Yoga with Yoda', '2023-12-17 19:25:03'),
('Mystar_1', 'Mystar', 'img/uploaded/Mystar_1.png', 'Cookies gone?', '2023-12-17 19:26:50'),
('Mystar_2', 'Mystar', 'img/uploaded/Mystar_2.png', 'In my soup a fly is.', '2023-12-17 19:27:24');

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
('Lorin', 'Xylo', 'Lorin_1', '2023-12-17 20:00:00', 'COMMENT'),
('Lorin', 'Zara', 'Lorin_1', '2023-12-17 20:01:00', 'COMMENT'),
('Lorin', 'Mystar', 'Lorin_2', '2023-12-17 20:02:00', 'COMMENT'),
('Lorin', 'Xylo', NULL, '2023-12-17 18:30:00', 'FOLLOW'),
('Mystar', 'Xylo', NULL, '2023-12-17 18:31:00', 'FOLLOW'),
('Lorin', 'Zara', NULL, '2023-12-17 18:32:00', 'FOLLOW');

-- Insert follower history
INSERT INTO FollowerHistory (username, followerCount, timestamp) VALUES
('Lorin', 1, '2023-12-17 18:30:00'),
('Lorin', 2, '2023-12-17 18:32:00'),
('Lorin', 3, '2023-12-17 18:34:00'),
('Xylo', 1, '2023-12-17 18:33:00'),
('Xylo', 2, '2023-12-17 18:36:00'),
('Mystar', 1, '2023-12-17 18:37:00'),
('Zara', 1, '2023-12-17 18:35:00');