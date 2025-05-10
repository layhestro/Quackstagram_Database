-- Quackstagram queries.sql
-- Contains 20 queries answering Cheapo Software Solutions' questions

-- 1. List all users who have more than X followers where X can be any integer value
SELECT username, followersCount 
FROM Users 
WHERE followersCount > ? -- Replace ? with parameter value
ORDER BY followersCount DESC;

-- 2. Show the total number of posts made by each user
SELECT username, COUNT(*) AS post_count 
FROM Pictures 
GROUP BY username
ORDER BY post_count DESC;

-- 3. Find all comments made on a particular user's post
SELECT c.commentId, c.username AS commenter, p.username AS post_owner, 
       p.imageId, c.content, c.timestamp
FROM Comments c
JOIN Pictures p ON c.imageId = p.imageId
WHERE p.username = ? -- Replace ? with username parameter
ORDER BY c.timestamp DESC;

-- 4. Display the top X most liked posts
SELECT imageId, username, caption, likesCount, timestamp
FROM Pictures
ORDER BY likesCount DESC
LIMIT ?; -- Replace ? with parameter value

-- 5. Count the number of posts each user has liked
SELECT l.username, COUNT(DISTINCT l.imageId) AS liked_posts_count
FROM Likes l
GROUP BY l.username
ORDER BY liked_posts_count DESC;

-- 6. List all users who haven't made a post yet
SELECT u.username, u.bio, u.followersCount, u.followingCount
FROM Users u
LEFT JOIN Pictures p ON u.username = p.username
WHERE p.imageId IS NULL
ORDER BY u.followersCount DESC;

-- 7. List users who follow each other (mutual follows)
SELECT f1.follower AS user1, f1.followed AS user2, f1.timestamp AS follow_date
FROM Follows f1
JOIN Follows f2 ON f1.follower = f2.followed AND f1.followed = f2.follower
WHERE f1.follower < f1.followed -- To avoid duplicate pairs (only list A-B, not B-A)
ORDER BY follow_date DESC;

-- 8. Show the user with the highest number of posts
SELECT username, COUNT(*) AS post_count
FROM Pictures
GROUP BY username
ORDER BY post_count DESC
LIMIT 1;

-- 9. List the top X users with the most followers
SELECT username, followersCount
FROM Users
ORDER BY followersCount DESC
LIMIT ?; -- Replace ? with parameter value

-- 10. Find posts that have been liked by all users
SELECT p.imageId, p.username, p.caption, p.likesCount
FROM Pictures p
WHERE (
    SELECT COUNT(DISTINCT username) 
    FROM Likes 
    WHERE imageId = p.imageId
) = (
    SELECT COUNT(*) 
    FROM Users
)
ORDER BY p.timestamp DESC;

-- 11. Display the most active user (based on posts, comments, and likes)
SELECT 
    username,
    (
        (SELECT COUNT(*) FROM Pictures WHERE username = u.username) + -- Posts
        (SELECT COUNT(*) FROM Comments WHERE username = u.username) + -- Comments
        (SELECT COUNT(*) FROM Likes WHERE username = u.username)      -- Likes
    ) AS activity_score
FROM Users u
ORDER BY activity_score DESC
LIMIT 1;

-- 12. Find the average number of likes per post for each user
SELECT 
    p.username,
    COUNT(p.imageId) AS total_posts,
    SUM(p.likesCount) AS total_likes,
    ROUND(AVG(p.likesCount), 2) AS avg_likes_per_post
FROM Pictures p
GROUP BY p.username
HAVING total_posts > 0
ORDER BY avg_likes_per_post DESC;

-- 13. Show posts that have more comments than likes
SELECT 
    p.imageId,
    p.username,
    p.caption,
    p.likesCount,
    (SELECT COUNT(*) FROM Comments c WHERE c.imageId = p.imageId) AS comment_count
FROM Pictures p
WHERE p.likesCount < (
    SELECT COUNT(*) 
    FROM Comments c 
    WHERE c.imageId = p.imageId
)
ORDER BY comment_count DESC;

-- 14. List the users who have liked every post of a specific user
SELECT l.username AS fan_username
FROM Users l
WHERE NOT EXISTS (
    -- Find posts by target user that haven't been liked by this user
    SELECT p.imageId 
    FROM Pictures p
    WHERE p.username = ? -- Replace ? with target username parameter
    AND NOT EXISTS (
        SELECT 1 
        FROM Likes 
        WHERE username = l.username AND imageId = p.imageId
    )
)
AND l.username != ?; -- Exclude the target user themselves

-- 15. Display the most popular post of each user (based on likes)
WITH RankedPosts AS (
    SELECT 
        imageId,
        username,
        caption,
        likesCount,
        timestamp,
        RANK() OVER (PARTITION BY username ORDER BY likesCount DESC) AS post_rank
    FROM Pictures
)
SELECT 
    imageId,
    username,
    caption,
    likesCount,
    timestamp
FROM RankedPosts
WHERE post_rank = 1
ORDER BY likesCount DESC;

-- 16. Find the user(s) with the highest ratio of followers to following
SELECT 
    username,
    followersCount,
    followingCount,
    CASE 
        WHEN followingCount = 0 THEN NULL -- Handle division by zero
        ELSE ROUND(followersCount / followingCount, 2)
    END AS follower_ratio
FROM Users
WHERE followingCount > 0 -- Avoid division by zero
ORDER BY follower_ratio DESC NULLS LAST
LIMIT 10;

-- 17. Show the month with the highest number of posts made
SELECT 
    YEAR(timestamp) AS year,
    MONTH(timestamp) AS month,
    MONTHNAME(timestamp) AS month_name,
    COUNT(*) AS post_count
FROM Pictures
GROUP BY YEAR(timestamp), MONTH(timestamp), MONTHNAME(timestamp)
ORDER BY post_count DESC
LIMIT 1;

-- 18. Identify users who have not interacted with a specific user's posts
SELECT u.username
FROM Users u
WHERE u.username != ? -- Replace ? with target username parameter
AND NOT EXISTS (
    -- Check for likes on target user's posts
    SELECT 1 FROM Likes l
    JOIN Pictures p ON l.imageId = p.imageId
    WHERE p.username = ? AND l.username = u.username
)
AND NOT EXISTS (
    -- Check for comments on target user's posts
    SELECT 1 FROM Comments c
    JOIN Pictures p ON c.imageId = p.imageId
    WHERE p.username = ? AND c.username = u.username
)
ORDER BY u.username;

-- 19. Display the user with the greatest increase in followers in the last X days
SELECT 
    username,
    (
        -- Current follower count
        SELECT followersCount FROM Users WHERE username = fh_end.username
    ) - (
        -- Follower count X days ago
        SELECT followerCount
        FROM FollowerHistory
        WHERE username = fh_end.username
        AND timestamp <= DATE_SUB(CURRENT_DATE(), INTERVAL ? DAY) -- Replace ? with days parameter
        ORDER BY timestamp DESC
        LIMIT 1
    ) AS follower_increase
FROM (
    SELECT DISTINCT username 
    FROM FollowerHistory 
    WHERE timestamp >= DATE_SUB(CURRENT_DATE(), INTERVAL ? DAY)
) fh_end
ORDER BY follower_increase DESC
LIMIT 1;

-- 20. Find users who are followed by more than X% of the platform users
SELECT 
    f.followed AS username,
    COUNT(DISTINCT f.follower) AS follower_count,
    (SELECT COUNT(*) FROM Users) AS total_users,
    ROUND(COUNT(DISTINCT f.follower) * 100 / (SELECT COUNT(*) FROM Users), 2) AS percentage
FROM Follows f
GROUP BY f.followed
HAVING percentage > ? -- Replace ? with percentage parameter
ORDER BY percentage DESC;
