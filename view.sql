-- views.sql for Quackstagram Database
-- Contains 3 analytical views and 2 supporting indexes

-- ========== INDEXES ==========

-- Index for optimizing comment retrieval by picture
CREATE INDEX IF NOT EXISTS idx_comments_imageid ON Comments(imageId);

-- Index for optimizing time-based picture queries
CREATE INDEX IF NOT EXISTS idx_pictures_timestamp ON Pictures(timestamp, username);

-- ========== ANALYTICAL VIEWS ==========

-- 1. Content Popularity View
-- Provides insights into which content is most engaging
CREATE OR REPLACE VIEW content_popularity_metrics AS
SELECT 
    p.imageId,
    p.username AS creator,
    p.caption,
    p.timestamp,
    p.likesCount,
    (SELECT COUNT(*) FROM Comments c WHERE c.imageId = p.imageId) AS comment_count,
    (p.likesCount + (SELECT COUNT(*) FROM Comments c WHERE c.imageId = p.imageId)) AS engagement_score,
    DATEDIFF(CURRENT_DATE, p.timestamp) AS days_since_posted,
    CASE 
        WHEN DATEDIFF(CURRENT_DATE, p.timestamp) = 0 THEN 'Today'
        WHEN DATEDIFF(CURRENT_DATE, p.timestamp) < 7 THEN 'This Week'
        WHEN DATEDIFF(CURRENT_DATE, p.timestamp) < 30 THEN 'This Month'
        ELSE 'Older'
    END AS time_category,
    
    -- Calculate virality metrics
    p.likesCount / (CASE 
                     WHEN DATEDIFF(CURRENT_DATE, p.timestamp) = 0 THEN 1 
                     ELSE DATEDIFF(CURRENT_DATE, p.timestamp) 
                   END) AS likes_per_day,
                   
    (SELECT COUNT(*) FROM Follows f WHERE f.followed = p.username) AS creator_followers_count
FROM 
    Pictures p
GROUP BY 
    p.imageId, p.username, p.caption, p.timestamp, p.likesCount
HAVING 
    (p.likesCount + (SELECT COUNT(*) FROM Comments c WHERE c.imageId = p.imageId)) > 5
ORDER BY 
    engagement_score DESC,
    likes_per_day DESC;

CREATE OR REPLACE VIEW system_analytics AS
WITH DailyActivity AS (
    -- Combine all activities by date and type in one pass
    SELECT 
        DATE(timestamp) AS date,
        'post' AS activity_type,
        COUNT(*) AS activity_count,
        COUNT(DISTINCT username) AS unique_users
    FROM Pictures
    GROUP BY DATE(timestamp)
    
    UNION ALL
    
    SELECT 
        DATE(timestamp) AS date,
        'like' AS activity_type,
        COUNT(*) AS activity_count,
        COUNT(DISTINCT username) AS unique_users
    FROM Likes
    GROUP BY DATE(timestamp)
    
    UNION ALL
    
    SELECT 
        DATE(timestamp) AS date,
        'comment' AS activity_type,
        COUNT(*) AS activity_count,
        COUNT(DISTINCT username) AS unique_users
    FROM Comments
    GROUP BY DATE(timestamp)
    
    UNION ALL
    
    SELECT 
        DATE(timestamp) AS date,
        'follow' AS activity_type,
        COUNT(*) AS activity_count,
        COUNT(DISTINCT username) AS unique_users
    FROM Follows
    GROUP BY DATE(timestamp)
)
SELECT 
    da.date,
    SUM(CASE WHEN da.activity_type = 'post' THEN da.activity_count ELSE 0 END) AS posts,
    SUM(CASE WHEN da.activity_type = 'like' THEN da.activity_count ELSE 0 END) AS likes,
    SUM(CASE WHEN da.activity_type = 'comment' THEN da.activity_count ELSE 0 END) AS comments,
    SUM(CASE WHEN da.activity_type = 'follow' THEN da.activity_count ELSE 0 END) AS follows,
    SUM(da.activity_count) AS total_daily_activity,
    COUNT(DISTINCT CASE WHEN da.activity_type = 'post' THEN da.unique_users END) +
    COUNT(DISTINCT CASE WHEN da.activity_type = 'like' THEN da.unique_users END) +
    COUNT(DISTINCT CASE WHEN da.activity_type = 'comment' THEN da.unique_users END) +
    COUNT(DISTINCT CASE WHEN da.activity_type = 'follow' THEN da.unique_users END) AS active_users,
    -- Safer engagement rate calculation
    CASE 
        WHEN SUM(CASE WHEN da.activity_type = 'post' THEN da.activity_count ELSE 0 END) > 0 
        THEN (SUM(CASE WHEN da.activity_type = 'like' THEN da.activity_count ELSE 0 END) +
              SUM(CASE WHEN da.activity_type = 'comment' THEN da.activity_count ELSE 0 END)) / 
             SUM(CASE WHEN da.activity_type = 'post' THEN da.activity_count ELSE 0 END)
        ELSE 0
    END AS engagement_rate
FROM 
    DailyActivity da
GROUP BY 
    da.date
ORDER BY 
    da.date DESC;
    
-- 3. User Engagement Metrics View
-- Provides insights into user behavior and engagement levels
CREATE OR REPLACE VIEW user_engagement_metrics AS
SELECT 
    u.username,
    u.postsCount,
    u.followersCount,
    u.followingCount,
    (SELECT COUNT(*) FROM Likes l WHERE l.username = u.username) AS likes_given,
    (SELECT COUNT(*) FROM Comments c WHERE c.username = u.username) AS comments_made,
    CASE 
        WHEN u.postsCount > 0 THEN 
            (SELECT COUNT(*) FROM Likes l 
             JOIN Pictures p ON l.imageId = p.imageId 
             WHERE p.username = u.username) / u.postsCount 
        ELSE 0 
    END AS avg_likes_received
FROM 
    Users u
GROUP BY 
    u.username, u.postsCount, u.followersCount, u.followingCount
HAVING 
    (likes_given + comments_made) > 0
ORDER BY 
    (likes_given + comments_made + u.postsCount) DESC;

-- Note: To measure performance of these views with and without indexes,
-- run them with EXPLAIN before and after adding the indexes.
-- Example: EXPLAIN SELECT * FROM content_popularity_metrics;