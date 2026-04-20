-- =============================================
-- HireConnect Database Initialization for Docker
-- This runs automatically when MySQL container starts
-- =============================================

-- Create all required databases
CREATE DATABASE IF NOT EXISTS hireconnectdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS hireconnect_auth CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS hireconnect_profile CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS hireconnect_applications CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS hireconnect_interview CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS hireconnect_notifications CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS hireconnect_analytics CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS hireconnect_subscription CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Grant permissions
GRANT ALL PRIVILEGES ON hireconnectdb.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON hireconnect_auth.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON hireconnect_profile.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON hireconnect_applications.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON hireconnect_interview.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON hireconnect_notifications.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON hireconnect_analytics.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON hireconnect_subscription.* TO 'root'@'%';

FLUSH PRIVILEGES;