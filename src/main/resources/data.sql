
INSERT INTO category (name, is_active, is_deleted, created_by, created_date, last_modified_by, last_modify_date)
VALUES
('Technology', true, false, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
('Health', true, false, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
('Travel', true, false, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
('Science', true, false, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
('Art', true, false, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP);


INSERT INTO app_user (username, password, role, is_active, is_deleted, created_by, created_date, last_modified_by, last_modify_date)
VALUES
('alice', '$2a$10$ODd6y7fZq.SeXNZi7JUNQOk/Bi3roJFd.ad8OBiamZIi4bWKoW4By', 'ADMIN', true, false, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
('bob', '$2a$10$i3u/GWZLWI7TvMx589tK0.G2o0kQrZDq7A4vGiXK6KJa4Xp2ZAYCC', 'EDITOR', true, false, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
('carol', '$2a$10$16Cbhe3DY/8t5CYIIw5qj.NtInqHlisFxwEKMpAVwjRcDoqdf4ae6', 'ADMIN', true, false, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
('dave', '$2a$10$OMwYvPQdAvjQnFbMjTQc2.81sYCdHhRjWNqTpaAlfKlMI5yRdI23y', 'EDITOR', true, false, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
('eve', '$2a$10$9g/17pdHWXiG5EokU88xvuS9b2Yzjc9GlECywRbhR5QPOXEQSORie', 'ADMIN', false, true, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP);


INSERT INTO profile (id, full_name, address, phone_number, email, is_deleted, created_by, created_date, last_modified_by, last_modify_date)
VALUES
(1, 'Alice Anderson', '123 Main St', '1234567890', 'alice@email.com', false, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
(2, 'Bob Brown', '456 Maple Ave', '9876543210', 'bob@email.com', false, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
(3, 'Carol Clark', '789 Oak Rd', '5551234567', 'carol@email.com', false, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
(4, 'Dave Davis', '321 Pine Lane', '4449876543', 'dave@email.com', false, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
(5, 'Eve Evans', '654 Elm St', '3336669999', 'eve@email.com', true, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP);

-- Insert Posts
INSERT INTO post (title, content, status, author_id, is_deleted, created_by, created_date, last_modified_by, last_modify_date)
VALUES
('First Tech Post', 'Welcome to the world of technology', 'PUBLISHED', 1, false, 'alice', CURRENT_TIMESTAMP, 'alice', CURRENT_TIMESTAMP),
('Health Tips', 'Remember to drink water.', 'PUBLISHED', 2, false, 'bob', CURRENT_TIMESTAMP, 'bob', CURRENT_TIMESTAMP),
('Travel Abroad', 'Pack light and travel far.', 'DRAFT', 3, false, 'carol', CURRENT_TIMESTAMP, 'carol', CURRENT_TIMESTAMP),
('Amazing Science Facts', 'Did you know...? Science is fun!', 'DRAFT', 4, false, 'dave', CURRENT_TIMESTAMP, 'dave', CURRENT_TIMESTAMP),
('Modern Art', 'Interpretation of modern art is unique.', 'PUBLISHED', 5, true, 'eve', CURRENT_TIMESTAMP, 'eve', CURRENT_TIMESTAMP),
('Second Tech Post', 'Latest in Tech World!', 'PUBLISHED', 1, false, 'alice', CURRENT_TIMESTAMP, 'alice', CURRENT_TIMESTAMP),
('Healthy Eating', 'Fruits and vegetables matter.', 'DRAFT', 2, false, 'bob', CURRENT_TIMESTAMP, 'bob', CURRENT_TIMESTAMP),
('Backpacking Europe', 'Guide to budget travel.', 'PUBLISHED', 3, false, 'carol', CURRENT_TIMESTAMP, 'carol', CURRENT_TIMESTAMP);

-- Insert PostCategory Links
INSERT INTO post_category (category_id, post_id, is_deleted, created_by, created_date, last_modified_by, last_modify_date)
VALUES
(1, 1, false, 'alice', CURRENT_TIMESTAMP, 'alice', CURRENT_TIMESTAMP), -- Tech + First Tech Post
(1, 6, false, 'alice', CURRENT_TIMESTAMP, 'alice', CURRENT_TIMESTAMP), -- Tech + Second Tech Post
(2, 2, false, 'bob', CURRENT_TIMESTAMP, 'bob', CURRENT_TIMESTAMP),     -- Health + Health Tips
(2, 7, false, 'bob', CURRENT_TIMESTAMP, 'bob', CURRENT_TIMESTAMP),     -- Health + Healthy Eating
(3, 3, false, 'carol', CURRENT_TIMESTAMP, 'carol', CURRENT_TIMESTAMP), -- Travel + Travel Abroad
(3, 8, false, 'carol', CURRENT_TIMESTAMP, 'carol', CURRENT_TIMESTAMP), -- Travel + Backpacking Europe
(4, 4, false, 'dave', CURRENT_TIMESTAMP, 'dave', CURRENT_TIMESTAMP),   -- Science + Amazing Science Facts
(5, 5, true, 'eve', CURRENT_TIMESTAMP, 'eve', CURRENT_TIMESTAMP),      -- Art + Modern Art (soft-deleted)
(1, 4, false, 'dave', CURRENT_TIMESTAMP, 'dave', CURRENT_TIMESTAMP),   -- Tech + Amazing Science Facts (cross-category)
(2, 8, false, 'carol', CURRENT_TIMESTAMP, 'carol', CURRENT_TIMESTAMP); -- Health + Backpacking Europe (cross-category)