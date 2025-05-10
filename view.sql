-- Index 1: Speed up lookups for likes on specific images
-- Improves performance of the content_popularity view
CREATE INDEX idx_likes_imageId ON Likes(imageId);

-- Index 2: Optimize timestamp-based queries for all activity tables
-- Improves performance of the daily_activity view
CREATE INDEX idx_pictures_timestamp ON Pictures(timestamp);

CREATE VIEW user_engagement AS
SELECT 
    u.username,
    (SELECT COUNT(*) FROM Pictures WHERE username = u.username) AS post_count,
    (SELECT COUNT(*) FROM Likes WHERE username = u.username) AS likes_given,
    (SELECT COUNT(*) FROM Comments WHERE username = u.username) AS comments_made,
    (SELECT COUNT(*) FROM Follows WHERE follower = u.username) AS following_count
FROM 
    Users u
GROUP BY 
    u.username
HAVING 
    post_count > 0 OR likes_given > 0
ORDER BY 
    (post_count + likes_given + comments_made) DESC;

CREATE VIEW content_popularity AS
SELECT 
    p.imageId,
    p.username,
    p.caption,
    p.timestamp,
    (SELECT COUNT(*) FROM Likes l WHERE l.imageId = p.imageId) AS likes_count,
    (SELECT COUNT(*) FROM Comments c WHERE c.imageId = p.imageId) AS comments_count,
    ((SELECT COUNT(*) FROM Likes l WHERE l.imageId = p.imageId) + 
     (SELECT COUNT(*) FROM Comments c WHERE c.imageId = p.imageId)) AS engagement_score
FROM 
    Pictures p
WHERE 
    (SELECT COUNT(*) FROM Likes l WHERE l.imageId = p.imageId) > 0
ORDER BY 
    engagement_score DESC;

CREATE VIEW daily_activity AS
SELECT 
    DATE(timestamp) AS activity_date,
    COUNT(*) AS activity_count,
    'picture' AS activity_type
FROM 
    Pictures
GROUP BY 
    activity_date
UNION ALL
SELECT 
    DATE(timestamp) AS activity_date,
    COUNT(*) AS activity_count,
    'like' AS activity_type
FROM 
    Likes
GROUP BY 
    activity_date
UNION ALL
SELECT 
    DATE(timestamp) AS activity_date,
    COUNT(*) AS activity_count,
    'comment' AS activity_type
FROM 
    Comments
GROUP BY 
    activity_date
ORDER BY 
    activity_date DESC, activity_type;