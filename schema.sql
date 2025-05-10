-- schema.sql for Quackstagram Database
-- Complete schema with all improvements incorporated

-- Drop existing tables if they exist
DROP TABLE IF EXISTS FollowerHistory;
DROP TABLE IF EXISTS SystemLogs;
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
    profileImagePath VARCHAR(255) DEFAULT 'img/logos/DACS.png',
    postsCount INT DEFAULT 0 CHECK (postsCount >= 0),
    followersCount INT DEFAULT 0 CHECK (followersCount >= 0),
    followingCount INT DEFAULT 0 CHECK (followingCount >= 0)
);

-- Create Pictures table
CREATE TABLE Pictures (
    imageId VARCHAR(100) PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    imagePath VARCHAR(255) NOT NULL,
    caption TEXT,
    likesCount INT DEFAULT 0 CHECK (likesCount >= 0),
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (username) REFERENCES Users(username) ON DELETE CASCADE
);

-- Create index on Pictures timestamp (for views and analytics)
CREATE INDEX idx_pictures_timestamp ON Pictures(timestamp, username);

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

-- Create index on Notifications for performance
CREATE INDEX idx_notifications_receiver_timestamp ON Notifications(receiverUsername, timestamp);

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

-- Create index on Comments imageId (for views and content analysis)
CREATE INDEX idx_comments_imageid ON Comments(imageId);

-- Create FollowerHistory table for analytics
CREATE TABLE FollowerHistory (
    historyId INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    followerCount INT NOT NULL CHECK (followerCount >= 0),
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (username) REFERENCES Users(username) ON DELETE CASCADE
);

-- Create SystemLogs table for trigger logging
CREATE TABLE SystemLogs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_system_logs_type (event_type),
    INDEX idx_system_logs_time (timestamp)
);