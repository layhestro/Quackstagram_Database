-- Create the database
CREATE DATABASE quackstagram;
USE quackstagram;

-- Users table
CREATE TABLE Users (
    username VARCHAR(50) PRIMARY KEY,
    bio TEXT,
    passwordHash VARCHAR(255) NOT NULL,
    salt VARCHAR(50) NOT NULL,
    postsCount INT DEFAULT 0,
    followersCount INT DEFAULT 0,
    followingCount INT DEFAULT 0
);

-- Pictures table
CREATE TABLE Pictures (
    imageId VARCHAR(100) PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    imagePath VARCHAR(255) NOT NULL,
    caption TEXT,
    likesCount INT DEFAULT 0,
    timestamp DATETIME NOT NULL,
    FOREIGN KEY (username) REFERENCES Users(username) ON DELETE CASCADE
);

-- Follows table
CREATE TABLE Follows (
    follower VARCHAR(50) NOT NULL,
    followed VARCHAR(50) NOT NULL,
    timestamp DATETIME NOT NULL,
    PRIMARY KEY (follower, followed),
    FOREIGN KEY (follower) REFERENCES Users(username) ON DELETE CASCADE,
    FOREIGN KEY (followed) REFERENCES Users(username) ON DELETE CASCADE
);

-- Notifications table
CREATE TABLE Notifications (
    notificationId INT AUTO_INCREMENT PRIMARY KEY,
    receiverUsername VARCHAR(50) NOT NULL,
    senderUsername VARCHAR(50) NOT NULL,
    imageId VARCHAR(100),
    timestamp DATETIME NOT NULL,
    type ENUM('LIKE', 'COMMENT', 'FOLLOW') NOT NULL,
    FOREIGN KEY (receiverUsername) REFERENCES Users(username) ON DELETE CASCADE,
    FOREIGN KEY (senderUsername) REFERENCES Users(username) ON DELETE CASCADE,
    FOREIGN KEY (imageId) REFERENCES Pictures(imageId) ON DELETE SET NULL
);

-- Likes table
CREATE TABLE Likes (
    username VARCHAR(50) NOT NULL,
    imageId VARCHAR(100) NOT NULL,
    timestamp DATETIME NOT NULL,
    PRIMARY KEY (username, imageId),
    FOREIGN KEY (username) REFERENCES Users(username) ON DELETE CASCADE,
    FOREIGN KEY (imageId) REFERENCES Pictures(imageId) ON DELETE CASCADE
);

-- Comments table
CREATE TABLE Comments (
    commentId INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    imageId VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    timestamp DATETIME NOT NULL,
    FOREIGN KEY (username) REFERENCES Users(username) ON DELETE CASCADE,
    FOREIGN KEY (imageId) REFERENCES Pictures(imageId) ON DELETE CASCADE
);

-- FollowerHistory table
CREATE TABLE FollowerHistory (
    historyId INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    followerCount INT NOT NULL,
    timestamp DATETIME NOT NULL,
    FOREIGN KEY (username) REFERENCES Users(username) ON DELETE CASCADE
);

