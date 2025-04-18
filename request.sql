SELECT followed, COUNT(*) AS follower_count 
FROM Follows 
GROUP BY followed 
HAVING COUNT(*) > X;

SELECT username, COUNT(*) AS post_count 
FROM Pictures 
GROUP BY username;

SELECT c.* 
FROM Comments c
JOIN Pictures p ON c.imageId = p.imageId
WHERE p.username = 'specific_username';

SELECT * FROM Pictures 
ORDER BY likesCount DESC 
LIMIT X;

SELECT username, COUNT(*) AS liked_posts 
FROM Likes 
GROUP BY username;

SELECT username FROM Users
WHERE username NOT IN (SELECT DISTINCT username FROM Pictures);

SELECT f1.follower, f1.followed
FROM Follows f1
JOIN Follows f2 ON f1.follower = f2.followed AND f1.followed = f2.follower;

SELECT username, COUNT(*) AS post_count
FROM Pictures
GROUP BY username
ORDER BY post_count DESC
LIMIT 1;

SELECT followed, COUNT(*) AS follower_count
FROM Follows
GROUP BY followed
ORDER BY follower_count DESC
LIMIT X;

SELECT p.imageId, p.caption
FROM Pictures p
WHERE (SELECT COUNT(DISTINCT username) FROM Likes WHERE imageId = p.imageId) = 
      (SELECT COUNT(*) FROM Users);

SELECT username, 
       (COUNT(DISTINCT p.imageId) + 
        COUNT(DISTINCT c.commentId) + 
        COUNT(DISTINCT l.imageId)) AS activity_score
FROM Users u
LEFT JOIN Pictures p ON u.username = p.username
LEFT JOIN Comments c ON u.username = c.username
LEFT JOIN Likes l ON u.username = l.username
GROUP BY username
ORDER BY activity_score DESC
LIMIT 1;

SELECT username, AVG(likesCount) AS avg_likes
FROM Pictures
GROUP BY username;

SELECT p.imageId, p.caption
FROM Pictures p
WHERE (SELECT COUNT(*) FROM Comments WHERE imageId = p.imageId) > p.likesCount;

SELECT l.username
FROM Users l
WHERE NOT EXISTS (
    SELECT p.imageId FROM Pictures p
    WHERE p.username = 'specific_user'
    AND NOT EXISTS (
        SELECT * FROM Likes
        WHERE username = l.username AND imageId = p.imageId
    )
);

SELECT username, imageId, caption, likesCount
FROM (
    SELECT *, RANK() OVER (PARTITION BY username ORDER BY likesCount DESC) as rank
    FROM Pictures
) ranked
WHERE rank = 1;

SELECT u.username,
       (SELECT COUNT(*) FROM Follows WHERE followed = u.username) / 
       NULLIF((SELECT COUNT(*) FROM Follows WHERE follower = u.username), 0) AS ratio
FROM Users u
ORDER BY ratio DESC
LIMIT 1;

SELECT MONTH(timestamp) AS month, YEAR(timestamp) AS year, COUNT(*) AS post_count
FROM Pictures
GROUP BY month, year
ORDER BY post_count DESC
LIMIT 1;

SELECT u.username
FROM Users u
WHERE NOT EXISTS (
    SELECT * FROM Likes l
    JOIN Pictures p ON l.imageId = p.imageId
    WHERE p.username = 'specific_user' AND l.username = u.username
)
AND NOT EXISTS (
    SELECT * FROM Comments c
    JOIN Pictures p ON c.imageId = p.imageId
    WHERE p.username = 'specific_user' AND c.username = u.username
)
AND u.username != 'specific_user';

SELECT username, 
       (SELECT followerCount FROM FollowerHistory 
        WHERE username = fh.username AND timestamp = CURRENT_DATE()) - 
       (SELECT followerCount FROM FollowerHistory 
        WHERE username = fh.username AND timestamp = DATE_SUB(CURRENT_DATE(), INTERVAL X DAY)) 
       AS follower_increase
FROM FollowerHistory fh
GROUP BY username
ORDER BY follower_increase DESC
LIMIT 1;

SELECT followed, COUNT(*) * 100.0 / (SELECT COUNT(*) FROM Users) AS percentage
FROM Follows
GROUP BY followed
HAVING percentage > X;