-- 1. List all users who have more than X followers (replace X with any number)
SELECT 
    followed as username,
    COUNT(*) as follower_count
FROM 
    Follows
GROUP BY 
    followed
HAVING 
    COUNT(*) > 2;  -- Replace 2 with any value for X

-- 2. Show the total number of posts made by each user
SELECT 
    username,
    COUNT(*) as post_count
FROM 
    Pictures
GROUP BY 
    username
ORDER BY 
    post_count DESC;

-- 3. Find all comments made on a particular user's post
SELECT 
    c.commentId,
    c.username as commenter,
    p.imageId,
    c.content,
    c.timestamp
FROM 
    Comments c
JOIN 
    Pictures p ON c.imageId = p.imageId
WHERE 
    p.username = 'Lorin';  -- Replace with target username

-- 4. Display the top X most liked posts
SELECT 
    p.imageId,
    p.username,
    p.caption,
    COUNT(l.username) as like_count
FROM 
    Pictures p
LEFT JOIN 
    Likes l ON p.imageId = l.imageId
GROUP BY 
    p.imageId, p.username, p.caption
ORDER BY 
    like_count DESC
LIMIT 5;  -- Replace with desired value for X

-- 5. Count the number of posts each user has liked
SELECT 
    username,
    COUNT(*) as liked_post_count
FROM 
    Likes
GROUP BY 
    username
ORDER BY 
    liked_post_count DESC;

-- 6. List all users who haven't made a post yet
SELECT 
    u.username
FROM 
    Users u
LEFT JOIN 
    Pictures p ON u.username = p.username
WHERE 
    p.imageId IS NULL;

-- 7. List users who follow each other (mutual follows)
SELECT 
    f1.follower as user1,
    f1.followed as user2
FROM 
    Follows f1
JOIN 
    Follows f2 ON f1.follower = f2.followed AND f1.followed = f2.follower
WHERE 
    f1.follower < f1.followed;  -- To avoid duplicate pairs

-- 8. Show the user with the highest number of posts
SELECT 
    username,
    COUNT(*) as post_count
FROM 
    Pictures
GROUP BY 
    username
ORDER BY 
    post_count DESC
LIMIT 1;

-- 9. List the top X users with the most followers
SELECT 
    followed as username,
    COUNT(*) as follower_count
FROM 
    Follows
GROUP BY 
    followed
ORDER BY 
    follower_count DESC
LIMIT 3;  -- Replace with desired value for X

-- 10. Find posts that have been liked by all users
SELECT 
    p.imageId,
    p.username,
    p.caption
FROM 
    Pictures p
WHERE 
    (SELECT COUNT(DISTINCT username) FROM Likes WHERE imageId = p.imageId) = 
    (SELECT COUNT(*) FROM Users);

-- 11. Display the most active user (based on posts, comments, and likes)
SELECT 
    u.username,
    (SELECT COUNT(*) FROM Pictures WHERE username = u.username) +
    (SELECT COUNT(*) FROM Comments WHERE username = u.username) +
    (SELECT COUNT(*) FROM Likes WHERE username = u.username) as activity_count
FROM 
    Users u
ORDER BY 
    activity_count DESC
LIMIT 1;

-- 12. Find the average number of likes per post for each user
SELECT 
    p.username,
    COUNT(DISTINCT p.imageId) as post_count,
    COUNT(l.username) as total_likes,
    COUNT(l.username) * 1.0 / COUNT(DISTINCT p.imageId) as avg_likes_per_post
FROM 
    Pictures p
LEFT JOIN 
    Likes l ON p.imageId = l.imageId
GROUP BY 
    p.username
HAVING 
    post_count > 0
ORDER BY 
    avg_likes_per_post DESC;

-- 13. Show posts that have more comments than likes
SELECT 
    p.imageId,
    p.username,
    p.caption,
    COUNT(DISTINCT c.commentId) as comment_count,
    COUNT(DISTINCT l.username) as like_count
FROM 
    Pictures p
LEFT JOIN 
    Comments c ON p.imageId = c.imageId
LEFT JOIN 
    Likes l ON p.imageId = l.imageId
GROUP BY 
    p.imageId, p.username, p.caption
HAVING 
    comment_count > like_count;

-- 14. List the users who have liked every post of a specific user
SELECT 
    u.username as fan
FROM 
    Users u
WHERE 
    NOT EXISTS (
        SELECT p.imageId
        FROM Pictures p
        WHERE p.username = 'Lorin'
        AND NOT EXISTS (
            SELECT 1
            FROM Likes l
            WHERE l.imageId = p.imageId
            AND l.username = u.username
        )
    )
AND 
    EXISTS (SELECT 1 FROM Pictures WHERE username = 'Lorin');

-- 15. Display the most popular post of each user (based on likes)
WITH PostLikes AS (
    SELECT 
        p.username,
        p.imageId,
        p.caption,
        COUNT(l.username) as like_count,
        RANK() OVER (PARTITION BY p.username ORDER BY COUNT(l.username) DESC) as rank_within_user
    FROM 
        Pictures p
    LEFT JOIN 
        Likes l ON p.imageId = l.imageId
    GROUP BY 
        p.username, p.imageId, p.caption
)
SELECT 
    username,
    imageId,
    caption,
    like_count
FROM 
    PostLikes
WHERE 
    rank_within_user = 1;

-- 16. Find the user(s) with the highest ratio of followers to following
SELECT 
    u.username,
    (SELECT COUNT(*) FROM Follows WHERE followed = u.username) as follower_count,
    (SELECT COUNT(*) FROM Follows WHERE follower = u.username) as following_count,
    CASE 
        WHEN (SELECT COUNT(*) FROM Follows WHERE follower = u.username) = 0 THEN 0
        ELSE (SELECT COUNT(*) FROM Follows WHERE followed = u.username) * 1.0 / 
            (SELECT COUNT(*) FROM Follows WHERE follower = u.username)
    END as ratio
FROM 
    Users u
WHERE 
    (SELECT COUNT(*) FROM Follows WHERE followed = u.username) > 0
ORDER BY 
    ratio DESC
LIMIT 1;

-- 17. Show the month with the highest number of posts made
SELECT 
    DATE_FORMAT(timestamp, '%Y-%m') as month,
    COUNT(*) as post_count
FROM 
    Pictures
GROUP BY 
    month
ORDER BY 
    post_count DESC
LIMIT 1;

-- 18. Identify users who have not interacted with a specific user's posts
SELECT 
    u.username
FROM 
    Users u
WHERE 
    u.username != 'Lorin'  -- Replace with target username
AND NOT EXISTS (
    SELECT 1
    FROM Likes l
    JOIN Pictures p ON l.imageId = p.imageId
    WHERE p.username = 'Lorin' AND l.username = u.username
)
AND NOT EXISTS (
    SELECT 1
    FROM Comments c
    JOIN Pictures p ON c.imageId = p.imageId
    WHERE p.username = 'Lorin' AND c.username = u.username
);

-- 19. Display the user with the greatest increase in followers in the last X days
SELECT 
    username,
    MAX(followerCount) - MIN(followerCount) as follower_increase
FROM 
    FollowerHistory
WHERE 
    timestamp >= DATE_SUB(NOW(), INTERVAL 30 DAY)  -- Replace 30 with X
GROUP BY 
    username
ORDER BY 
    follower_increase DESC
LIMIT 1;

-- 20. Find users who are followed by more than X% of the platform users
SELECT 
    followed as username,
    COUNT(*) as follower_count,
    (COUNT(*) * 100.0 / (SELECT COUNT(*) FROM Users)) as percentage
FROM 
    Follows
GROUP BY 
    followed
HAVING 
    percentage > 50  -- Replace with desired percentage X
ORDER BY 
    percentage DESC;