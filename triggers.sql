DELIMITER $$

-- Procedure: Create notification
-- This procedure creates notifications for different user actions
CREATE PROCEDURE create_notification(
    IN p_receiver VARCHAR(50),
    IN p_sender VARCHAR(50),
    IN p_image_id VARCHAR(100),
    IN p_type ENUM('LIKE', 'COMMENT', 'FOLLOW')
)
BEGIN
    -- Don't create notification if user is interacting with their own content
    IF p_receiver <> p_sender THEN
        INSERT INTO Notifications (receiverUsername, senderUsername, imageId, timestamp, type)
        VALUES (p_receiver, p_sender, p_image_id, NOW(), p_type);
    END IF;
END$$

-- Function: Calculate picture engagement score
-- This function calculates a simple engagement score based on likes and comments
CREATE FUNCTION calculate_engagement_score(
    p_image_id VARCHAR(100)
) RETURNS INT
DETERMINISTIC
BEGIN
    DECLARE likes_count INT;
    DECLARE comments_count INT;
    
    -- Get like count
    SELECT COUNT(*) INTO likes_count
    FROM Likes
    WHERE imageId = p_image_id;
    
    -- Get comment count
    SELECT COUNT(*) INTO comments_count
    FROM Comments
    WHERE imageId = p_image_id;
    
    -- Calculate score (likes + comments*2)
    -- Comments weighted more heavily than likes
    RETURN likes_count + (comments_count * 2);
END$$

-- Trigger: After a like is added
-- This trigger calls the create_notification procedure
CREATE TRIGGER after_like_insert
AFTER INSERT ON Likes
FOR EACH ROW
BEGIN
    -- Get the username of the picture owner
    DECLARE picture_owner VARCHAR(50);
    SELECT username INTO picture_owner
    FROM Pictures
    WHERE imageId = NEW.imageId;
    
    -- Create notification using the stored procedure
    CALL create_notification(picture_owner, NEW.username, NEW.imageId, 'LIKE');
END$$

-- Trigger: After a follow relationship is created
-- This trigger calls both the procedure and the function
CREATE TRIGGER after_follow_insert
AFTER INSERT ON Follows
FOR EACH ROW
BEGIN
    -- Create a follow notification
    CALL create_notification(NEW.followed, NEW.follower, NULL, 'FOLLOW');
    
    -- Update follower history
    -- First calculate current follower count
    DECLARE follower_count INT;
    
    SELECT COUNT(*) INTO follower_count
    FROM Follows
    WHERE followed = NEW.followed;
    
    -- Also get the engagement score of the user's most recent picture
    -- (just as an example of using the function)
    DECLARE recent_picture VARCHAR(100);
    DECLARE engagement_score INT DEFAULT 0;
    
    -- Get most recent picture from the followed user
    SELECT imageId INTO recent_picture
    FROM Pictures
    WHERE username = NEW.followed
    ORDER BY timestamp DESC
    LIMIT 1;
    
    -- Calculate engagement score if picture exists
    IF recent_picture IS NOT NULL THEN
        SET engagement_score = calculate_engagement_score(recent_picture);
    END IF;
    
    -- Insert record into follower history with current count and timestamp
    INSERT INTO FollowerHistory (username, followerCount, timestamp)
    VALUES (NEW.followed, follower_count, NOW());
END$$

DELIMITER ;