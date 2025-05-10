-- triggers.sql for Quackstagram Database
-- Contains 1 procedure, 1 function, and 2 triggers

-- Enable stored procedures and functions
DELIMITER $$

-- 1. STORED PROCEDURE: Create notification and update notification counters
-- This procedure handles the creation of notifications and updates relevant counters
CREATE PROCEDURE create_notification(
    IN p_receiver VARCHAR(50),
    IN p_sender VARCHAR(50),
    IN p_image_id VARCHAR(100),
    IN p_type ENUM('LIKE', 'COMMENT', 'FOLLOW')
)
BEGIN
    -- Don't create notification if user is interacting with their own content
    IF p_receiver <> p_sender THEN
        -- Insert the notification
        INSERT INTO Notifications (receiverUsername, senderUsername, imageId, timestamp, type)
        VALUES (p_receiver, p_sender, p_image_id, NOW(), p_type);
        
        -- We could add additional logic here like:
        -- - Tracking unread notification counts
        -- - Logging notification events
        -- - Updating activity scores
        
        -- For demo purposes, let's log the notification
        INSERT INTO SystemLogs (event_type, description, timestamp)
        VALUES ('NOTIFICATION', 
                CONCAT('User ', p_sender, ' ', LOWER(p_type), 'd ', 
                      CASE 
                          WHEN p_type = 'FOLLOW' THEN CONCAT('user ', p_receiver)
                          ELSE CONCAT('content from ', p_receiver)
                      END),
                NOW());
    END IF;
END$$

-- 2. FUNCTION: Calculate engagement score for a post
-- This function calculates an engagement score based on likes, comments and recency
CREATE FUNCTION calculate_engagement_score(
    p_image_id VARCHAR(100)
) RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
    DECLARE likes_count INT;
    DECLARE comments_count INT;
    DECLARE days_old INT;
    DECLARE score DECIMAL(10,2);
    
    -- Get like count
    SELECT likesCount INTO likes_count
    FROM Pictures
    WHERE imageId = p_image_id;
    
    -- Get comment count
    SELECT COUNT(*) INTO comments_count
    FROM Comments
    WHERE imageId = p_image_id;
    
    -- Calculate days since posting
    SELECT DATEDIFF(CURRENT_DATE, timestamp) INTO days_old
    FROM Pictures
    WHERE imageId = p_image_id;
    
    -- Prevent division by zero and add small base value
    SET days_old = GREATEST(days_old, 1);
    
    -- Calculate score: (likes*1.0 + comments*1.5) / days_old^0.5
    -- This weights comments a bit higher than likes and reduces score as post ages
    SET score = (likes_count*1.0 + comments_count*1.5) / SQRT(days_old);
    
    RETURN score;
END$$

-- 3. TRIGGER: After a like is added
-- This trigger updates the picture's like count and creates a notification
CREATE TRIGGER after_like_insert
AFTER INSERT ON Likes
FOR EACH ROW
BEGIN
    -- Update the like count in the Pictures table
    UPDATE Pictures
    SET likesCount = likesCount + 1
    WHERE imageId = NEW.imageId;
    
    -- Get the username of the picture owner
    DECLARE picture_owner VARCHAR(50);
    SELECT username INTO picture_owner
    FROM Pictures
    WHERE imageId = NEW.imageId;
    
    -- Create notification using the stored procedure
    CALL create_notification(picture_owner, NEW.username, NEW.imageId, 'LIKE');
    
    -- Calculate new engagement score and update if we had such a column
    -- This demonstrates calling the function from the trigger
    DECLARE new_score DECIMAL(10,2);
    SET new_score = calculate_engagement_score(NEW.imageId);
    
    -- For demonstration purposes, we'll log the new engagement score
    INSERT INTO SystemLogs (event_type, description, timestamp)
    VALUES ('ENGAGEMENT_UPDATE', 
            CONCAT('Post ', NEW.imageId, ' has new engagement score: ', new_score),
            NOW());
END$$

-- 4. TRIGGER: After a follow relationship is created
-- This trigger updates follower/following counts and creates a notification
CREATE TRIGGER after_follow_insert
AFTER INSERT ON Follows
FOR EACH ROW
BEGIN
    -- Update follower count for the followed user
    UPDATE Users
    SET followersCount = followersCount + 1
    WHERE username = NEW.followed;
    
    -- Update following count for the follower user
    UPDATE Users
    SET followingCount = followingCount + 1
    WHERE username = NEW.follower;
    
    -- Create a follow notification
    CALL create_notification(NEW.followed, NEW.follower, NULL, 'FOLLOW');
    
    -- Also update the FollowerHistory table to track follower growth over time
    INSERT INTO FollowerHistory (username, followerCount, timestamp)
    VALUES (NEW.followed, 
            (SELECT followersCount FROM Users WHERE username = NEW.followed),
            NOW());
END$$

-- Make sure we have a system logs table for demonstration purposes
CREATE TABLE IF NOT EXISTS SystemLogs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    timestamp DATETIME NOT NULL,
    INDEX idx_system_logs_type (event_type),
    INDEX idx_system_logs_time (timestamp)
);

-- Reset delimiter
DELIMITER ;

-- Example usage (commented out):
/*
-- Insert a like
INSERT INTO Likes (username, imageId, timestamp) 
VALUES ('alice', 'bob_1', NOW());

-- Create a follow relationship
INSERT INTO Follows (follower, followed, timestamp) 
VALUES ('alice', 'charlie', NOW());
*/