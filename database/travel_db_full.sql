-- Sri Lanka Travel Agency - MySQL 8 schema and optional demo content
-- Existing projects can keep spring.jpa.hibernate.ddl-auto=update.
-- This script never drops a table or deletes existing data.

CREATE DATABASE IF NOT EXISTS travel_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE travel_db;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL,
  password VARCHAR(255) NOT NULL,
  phone VARCHAR(30),
  role ENUM('ADMIN', 'CUSTOMER') NOT NULL DEFAULT 'CUSTOMER',
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS destinations (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  location VARCHAR(150) NOT NULL,
  description TEXT,
  image_url VARCHAR(500),
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS packages (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(150) NOT NULL,
  description TEXT,
  price DECIMAL(12,2) NOT NULL,
  duration_days INT NOT NULL,
  image_url VARCHAR(500),
  category VARCHAR(50) NOT NULL,
  status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
  destination_id BIGINT NOT NULL,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  KEY idx_packages_destination (destination_id),
  KEY idx_packages_status_category (status, category),
  CONSTRAINT fk_packages_destination
    FOREIGN KEY (destination_id) REFERENCES destinations(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS hotels (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(150) NOT NULL,
  location VARCHAR(150) NOT NULL,
  description TEXT,
  price_per_night DECIMAL(12,2) NOT NULL,
  rating DECIMAL(2,1),
  star_rating INT NOT NULL DEFAULT 4,
  image_url VARCHAR(500),
  website_url VARCHAR(500),
  available BIT NOT NULL DEFAULT b'1',
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  KEY idx_hotels_location (location),
  KEY idx_hotels_star_available (star_rating, available),
  CONSTRAINT chk_hotels_star_rating CHECK (star_rating BETWEEN 3 AND 5),
  CONSTRAINT chk_hotels_guest_rating CHECK (rating IS NULL OR rating BETWEEN 0 AND 5)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS experiences (
  id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(150) NOT NULL,
  category VARCHAR(60) NOT NULL,
  location VARCHAR(150) NOT NULL,
  short_description VARCHAR(500) NOT NULL,
  description TEXT,
  duration_hours INT NOT NULL DEFAULT 4,
  image_url VARCHAR(500),
  featured BIT NOT NULL DEFAULT b'0',
  active BIT NOT NULL DEFAULT b'1',
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  KEY idx_experiences_category (category),
  KEY idx_experiences_active_featured (active, featured),
  CONSTRAINT chk_experience_duration CHECK (duration_hours BETWEEN 1 AND 168)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS trip_inquiries (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL,
  phone VARCHAR(30),
  country VARCHAR(100),
  travel_month VARCHAR(30),
  guests INT NOT NULL,
  interests VARCHAR(500),
  message TEXT,
  status ENUM('NEW', 'CONTACTED', 'PLANNING', 'CLOSED') NOT NULL DEFAULT 'NEW',
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  KEY idx_inquiries_status_created (status, created_at),
  CONSTRAINT chk_inquiry_guests CHECK (guests BETWEEN 1 AND 50)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS bookings (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  package_id BIGINT NOT NULL,
  travel_date DATE NOT NULL,
  number_of_guests INT NOT NULL,
  total_amount DECIMAL(12,2) NOT NULL,
  payment_status ENUM('PENDING', 'PAID', 'FAILED', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
  booking_status ENUM('PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  KEY idx_bookings_user (user_id),
  KEY idx_bookings_package (package_id),
  KEY idx_bookings_status (booking_status, payment_status),
  CONSTRAINT fk_bookings_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_bookings_package FOREIGN KEY (package_id) REFERENCES packages(id),
  CONSTRAINT chk_booking_guests CHECK (number_of_guests >= 1)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- Safe migration for an existing database created earlier by Hibernate.
-- No existing rows are deleted. Older tables may have created_at without a
-- database default, and older hotel tables may not contain the two new fields.
-- ---------------------------------------------------------------------------

ALTER TABLE users
  MODIFY COLUMN created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6);

ALTER TABLE destinations
  MODIFY COLUMN created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6);

ALTER TABLE packages
  MODIFY COLUMN created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6);

ALTER TABLE hotels
  MODIFY COLUMN created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6);

ALTER TABLE bookings
  MODIFY COLUMN created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6);

ALTER TABLE experiences
  MODIFY COLUMN created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6);

ALTER TABLE trip_inquiries
  MODIFY COLUMN created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6);

-- Add star_rating only when the column is not already present.
SET @add_star_rating_sql = IF(
  (SELECT COUNT(*)
     FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'hotels'
      AND COLUMN_NAME = 'star_rating') = 0,
  'ALTER TABLE hotels ADD COLUMN star_rating INT NOT NULL DEFAULT 4 AFTER rating',
  'SELECT 1'
);
PREPARE add_star_rating_statement FROM @add_star_rating_sql;
EXECUTE add_star_rating_statement;
DEALLOCATE PREPARE add_star_rating_statement;

-- Add website_url only when the column is not already present.
SET @add_website_url_sql = IF(
  (SELECT COUNT(*)
     FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'hotels'
      AND COLUMN_NAME = 'website_url') = 0,
  'ALTER TABLE hotels ADD COLUMN website_url VARCHAR(500) NULL AFTER image_url',
  'SELECT 1'
);
PREPARE add_website_url_statement FROM @add_website_url_sql;
EXECUTE add_website_url_statement;
DEALLOCATE PREPARE add_website_url_statement;

-- Copyright-safe destination images are served by Wikimedia Commons redirects.
INSERT INTO destinations (name, location, description, image_url, created_at)
SELECT 'Ella', 'Badulla District',
       'Misty tea hills, scenic railway journeys, waterfalls and the iconic Nine Arches Bridge.',
       'https://commons.wikimedia.org/wiki/Special:Redirect/file/Nine%20Arches%20Bridge%20in%20Ella.jpg?width=1600',
       CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM destinations WHERE LOWER(name) = 'ella');

INSERT INTO destinations (name, location, description, image_url, created_at)
SELECT 'Sigiriya', 'Matale District',
       'An ancient rock fortress rising above forests and gardens in Sri Lanka''s Cultural Triangle.',
       'https://commons.wikimedia.org/wiki/Special:Redirect/file/Sigiriya%20Sri%20Lanka.jpg?width=1600',
       CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM destinations WHERE LOWER(name) = 'sigiriya');

INSERT INTO destinations (name, location, description, image_url, created_at)
SELECT 'Galle', 'Southern Province',
       'A coastal heritage city known for its UNESCO-listed fort, lighthouse, cafes and Indian Ocean sunsets.',
       'https://commons.wikimedia.org/wiki/Special:Redirect/file/Galle%20Dutch%20Fort%2C%20Sri%20Lanka.jpg?width=1600',
       CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM destinations WHERE LOWER(name) = 'galle');

INSERT INTO destinations (name, location, description, image_url, created_at)
SELECT 'Mirissa', 'Matara District',
       'A relaxed southern beach destination with warm seas, palm-lined bays and whale-watching journeys.',
       'https://commons.wikimedia.org/wiki/Special:Redirect/file/Mirissa%20Beach%20Sri%20Lanka.jpg?width=1600',
       CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM destinations WHERE LOWER(name) = 'mirissa');

SET @ella_id = (SELECT id FROM destinations WHERE LOWER(name) = 'ella' ORDER BY id LIMIT 1);
SET @sigiriya_id = (SELECT id FROM destinations WHERE LOWER(name) = 'sigiriya' ORDER BY id LIMIT 1);
SET @galle_id = (SELECT id FROM destinations WHERE LOWER(name) = 'galle' ORDER BY id LIMIT 1);

INSERT INTO packages (name, description, price, duration_days, image_url, category, status, destination_id, created_at)
SELECT 'Highland Rails & Tea Trails',
       'A four-day Ella escape featuring tea country, scenic walks and the famous highland railway landscape.',
       89000.00, 4,
       'https://commons.wikimedia.org/wiki/Special:Redirect/file/Nine%20Arches%20Bridge%20in%20Ella.jpg?width=1600',
       'Adventure', 'ACTIVE', @ella_id, CURRENT_TIMESTAMP(6)
WHERE @ella_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM packages WHERE name = 'Highland Rails & Tea Trails');

INSERT INTO packages (name, description, price, duration_days, image_url, category, status, destination_id, created_at)
SELECT 'Cultural Triangle Discovery',
       'A three-day journey through Sigiriya, ancient kingdoms and Sri Lanka''s living cultural heritage.',
       76000.00, 3,
       'https://commons.wikimedia.org/wiki/Special:Redirect/file/Sigiriya%20Sri%20Lanka.jpg?width=1600',
       'Cultural', 'ACTIVE', @sigiriya_id, CURRENT_TIMESTAMP(6)
WHERE @sigiriya_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM packages WHERE name = 'Cultural Triangle Discovery');

INSERT INTO packages (name, description, price, duration_days, image_url, category, status, destination_id, created_at)
SELECT 'Southern Coast & Heritage',
       'A five-day coastal journey combining Galle Fort, relaxed beaches, local food and ocean sunsets.',
       118000.00, 5,
       'https://commons.wikimedia.org/wiki/Special:Redirect/file/Galle%20Dutch%20Fort%2C%20Sri%20Lanka.jpg?width=1600',
       'Beach', 'ACTIVE', @galle_id, CURRENT_TIMESTAMP(6)
WHERE @galle_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM packages WHERE name = 'Southern Coast & Heritage');

-- The nightly values below are DEMO/INDICATIVE rates only.
-- Live rates vary by date, room type, occupancy, tax and promotion.
INSERT INTO hotels (name, location, description, price_per_night, rating, star_rating, image_url, website_url, available, created_at)
SELECT 'Heritance Kandalama', 'Dambulla',
       'An iconic nature-integrated resort overlooking Kandalama Lake near Sigiriya and Dambulla.',
       85000.00, 4.8, 5, NULL,
       'https://www.heritancehotels.com/kandalama/', b'1', CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM hotels WHERE name = 'Heritance Kandalama');

INSERT INTO hotels (name, location, description, price_per_night, rating, star_rating, image_url, website_url, available, created_at)
SELECT 'Jetwing Lighthouse', 'Galle',
       'A landmark coastal hotel on a hillock overlooking the Indian Ocean near historic Galle.',
       72000.00, 4.7, 5, NULL,
       'https://www.jetwinghotels.com/jetwinglighthouse/', b'1', CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM hotels WHERE name = 'Jetwing Lighthouse');

INSERT INTO hotels (name, location, description, price_per_night, rating, star_rating, image_url, website_url, available, created_at)
SELECT '98 Acres Resort & Spa', 'Ella',
       'A scenic boutique retreat among tea hills with mountain views, nature experiences and an infinity pool.',
       65000.00, 4.8, 4, NULL,
       'https://www.resort98acres.com/', b'1', CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM hotels WHERE name = '98 Acres Resort & Spa');

-- Experience images intentionally remain NULL: the React app provides bundled,
-- original fallbacks. Administrators can attach an owned/licensed image URL later.
INSERT INTO experiences (title, category, location, short_description, description, duration_hours, image_url, featured, active, created_at)
SELECT 'Rails Through Tea Country', 'Adventure', 'Kandy to Ella',
       'Ride one of Asia''s most scenic railways through cloud forest and emerald tea estates.',
       'A slow-travel day of mountain views, village stations, local snacks and highland stories.',
       8, NULL, b'1', b'1', CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM experiences WHERE title = 'Rails Through Tea Country');

INSERT INTO experiences (title, category, location, short_description, description, duration_hours, image_url, featured, active, created_at)
SELECT 'Sigiriya at First Light', 'Culture', 'Sigiriya',
       'Climb the ancient rock fortress before the day warms and hear the stories behind its gardens.',
       'A privately guided heritage experience with time for village flavours after the climb.',
       5, NULL, b'1', b'1', CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM experiences WHERE title = 'Sigiriya at First Light');

INSERT INTO experiences (title, category, location, short_description, description, duration_hours, image_url, featured, active, created_at)
SELECT 'Wild Elephant Country', 'Wildlife', 'Minneriya',
       'Follow quiet forest tracks with an ethical naturalist guide in search of wild elephants.',
       'A small-group safari focused on patient observation, animal welfare and local ecology.',
       4, NULL, b'1', b'1', CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM experiences WHERE title = 'Wild Elephant Country');

INSERT INTO experiences (title, category, location, short_description, description, duration_hours, image_url, featured, active, created_at)
SELECT 'Galle Fort Afterglow', 'Culture', 'Galle',
       'Walk coral-stone ramparts, hidden lanes and lighthouse viewpoints as the Indian Ocean turns gold.',
       'A relaxed evening with heritage architecture, independent makers and coastal flavours.',
       3, NULL, b'0', b'1', CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM experiences WHERE title = 'Galle Fort Afterglow');

INSERT INTO experiences (title, category, location, short_description, description, duration_hours, image_url, featured, active, created_at)
SELECT 'Barefoot Southern Coast', 'Beach', 'Mirissa',
       'A salt-air day of hidden coves, palm shade and unhurried southern hospitality.',
       'Designed for travellers who want a flexible beach day with trusted local recommendations.',
       6, NULL, b'0', b'1', CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM experiences WHERE title = 'Barefoot Southern Coast');

INSERT INTO experiences (title, category, location, short_description, description, duration_hours, image_url, featured, active, created_at)
SELECT 'Tea, Table & Tradition', 'Food', 'Nuwara Eliya',
       'Meet growers, taste single-origin teas and share a seasonal hill-country table.',
       'An intimate food and tea experience that connects landscape, craft and community.',
       4, NULL, b'0', b'1', CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM experiences WHERE title = 'Tea, Table & Tradition');

-- Admin users are intentionally not inserted here because passwords must be BCrypt hashes.
-- Create the first admin securely with ADMIN_NAME, ADMIN_EMAIL and ADMIN_PASSWORD environment variables.

SELECT 'travel_db schema and optional demo content are ready' AS result;
