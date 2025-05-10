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