SET timezone = 'UTC';

-- Table: pages
CREATE TABLE pages (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL
);

-- Full-text search index for pages content
CREATE INDEX idx_pages_content_fts ON pages USING gin(to_tsvector('english', content));

-- Table: user_ranks
CREATE TABLE user_ranks (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    post_add BOOLEAN NOT NULL DEFAULT FALSE,
    post_delete BOOLEAN NOT NULL DEFAULT FALSE,
    post_edit BOOLEAN NOT NULL DEFAULT FALSE,
    post_category_add BOOLEAN NOT NULL DEFAULT FALSE,
    post_category_delete BOOLEAN NOT NULL DEFAULT FALSE,
    post_category_edit BOOLEAN NOT NULL DEFAULT FALSE,
    post_comment_add BOOLEAN NOT NULL DEFAULT FALSE,
    post_comment_delete BOOLEAN NOT NULL DEFAULT FALSE,
    post_comment_edit BOOLEAN NOT NULL DEFAULT FALSE,
    user_add BOOLEAN NOT NULL DEFAULT FALSE,
    user_delete BOOLEAN NOT NULL DEFAULT FALSE,
    user_edit BOOLEAN NOT NULL DEFAULT FALSE,
    user_group_add BOOLEAN NOT NULL DEFAULT FALSE,
    user_group_delete BOOLEAN NOT NULL DEFAULT FALSE,
    user_group_edit BOOLEAN NOT NULL DEFAULT FALSE,
    user_account_add BOOLEAN NOT NULL DEFAULT FALSE,
    user_account_delete BOOLEAN NOT NULL DEFAULT FALSE,
    user_account_edit BOOLEAN NOT NULL DEFAULT FALSE,
    user_rank_add BOOLEAN NOT NULL DEFAULT FALSE,
    user_rank_delete BOOLEAN NOT NULL DEFAULT FALSE,
    user_rank_edit BOOLEAN NOT NULL DEFAULT FALSE
);

-- Table: users
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    salt VARCHAR(255) NOT NULL,
    rank_id BIGINT NOT NULL,
    email VARCHAR(255) UNIQUE DEFAULT NULL,
    banned BOOLEAN NOT NULL DEFAULT FALSE,
    activated BOOLEAN NOT NULL DEFAULT FALSE,
    activation_key VARCHAR(255) DEFAULT NULL,
    last_ip VARCHAR(45) DEFAULT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (rank_id) REFERENCES user_ranks(id) ON UPDATE CASCADE
);

-- Table: post_categories
CREATE TABLE post_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    parent_id BIGINT DEFAULT NULL,
    FOREIGN KEY (parent_id) REFERENCES post_categories(id) ON DELETE SET NULL ON UPDATE CASCADE
);

-- Table: posts
CREATE TABLE posts (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    user_id BIGINT,
    category_id BIGINT,
    published BOOLEAN NOT NULL DEFAULT FALSE,
    commentable BOOLEAN NOT NULL DEFAULT FALSE,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_edit TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (category_id) REFERENCES post_categories(id) ON DELETE SET NULL ON UPDATE CASCADE
);

-- Full-text search index for posts content
CREATE INDEX idx_posts_content_fts ON posts USING gin(to_tsvector('english', content));

-- Table: post_comments
CREATE TABLE post_comments (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    post_id BIGINT NOT NULL,
    user_id BIGINT,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Full-text search index for post_comments content
CREATE INDEX idx_post_comments_content_fts ON post_comments USING gin(to_tsvector('english', content));

-- Table: user_accounts
CREATE TABLE user_accounts (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Table: user_accounts_meta
CREATE TABLE user_accounts_meta (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, account_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (account_id) REFERENCES user_accounts(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Table: user_groups
CREATE TABLE user_groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Table: user_groups_meta
CREATE TABLE user_groups_meta (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, group_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (group_id) REFERENCES user_groups(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Table: user_payments
CREATE TABLE user_payments (
    id BIGSERIAL PRIMARY KEY,
    target_id BIGINT,
    source_id BIGINT,
    user_id BIGINT,
    comment VARCHAR(255) NOT NULL,
    date DATE NOT NULL DEFAULT CURRENT_DATE,
    amount DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_edit TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (source_id) REFERENCES user_accounts(id) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (target_id) REFERENCES user_accounts(id) ON DELETE SET NULL ON UPDATE CASCADE
);

-- Insert initial data for user_ranks
INSERT INTO user_ranks (id, name, post_add, post_delete, post_edit, post_category_add, post_category_delete, post_category_edit, post_comment_add, post_comment_delete, post_comment_edit, user_add, user_delete, user_edit, user_group_add, user_group_delete, user_group_edit, user_account_add, user_account_delete, user_account_edit, user_rank_add, user_rank_delete, user_rank_edit) VALUES
(1, 'User', FALSE, FALSE, FALSE, FALSE, FALSE, FALSE, TRUE, FALSE, FALSE, FALSE, FALSE, FALSE, FALSE, FALSE, FALSE, FALSE, FALSE, FALSE, FALSE, FALSE, FALSE),
(2, 'Moderator', FALSE, FALSE, FALSE, FALSE, FALSE, FALSE, TRUE, TRUE, TRUE, FALSE, FALSE, FALSE, FALSE, FALSE, FALSE, FALSE, FALSE, FALSE, FALSE, FALSE, FALSE),
(3, 'Administrator', TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE);

-- Insert initial admin user
INSERT INTO users (id, username, password, salt, rank_id, activated, banned, activation_key) VALUES
(1, 'admin', '3HWqMv8tiSEbBcsUfxqBx7kY4vw+cSvG7OQXp9uzM0w=', '0l/SGC6gqKwYWjw7sm2IrwzIcAjq/QkO9xXcG/LC56c=', 3, TRUE, FALSE, NULL),
(2, 'moderator', '4IV82pA2Q1BmNaIS+de+1yqS1UQSwSE21xU6kkSda6c=', '52Mdt2qexvXu99Gm+wB0iv8V3n5leK5XR1zGLNBeDQg=', 2, TRUE, FALSE, NULL),
(3, 'user', 'KD+VrVb86w2Z7Ei8UjsHbbZ/awEnReWeC8t0656EqMM=', 'e3/FXXV1XuCaSHXxtwR1K0Lc5N/Al7rTJ6Kkpx1S2Uk=', 1, TRUE, FALSE, NULL),
(4, 'bannedUser', 'tE04ap7GWryC2vCAzJpcFilsSGPozJsrAjRyZaXqeXM=', 'QLxT3YTXJuEFeaG71Shryr5BUkW+i4vocjmpKXT8h4k=', 1, TRUE, TRUE, NULL),
(5, 'notActivatedUser', 'xA28JSHhTwUxoBfOS0RCTPfnMzkvzB5i724zzo25iYI=', 'BVCeBwQYMx/7sZ8tIiR9wCvQ/plNQzTwG0neblWWu3k=', 1, FALSE, FALSE, '21b8efb5-8eb0-4530-9d9a-cdbc33ba7164');

-- Update sequences to continue from current max values
SELECT setval('user_ranks_id_seq', 3);
SELECT setval('users_id_seq', 5);

-- Create triggers for automatic last_edit updates
CREATE OR REPLACE FUNCTION update_last_edit()
RETURNS TRIGGER AS $$
BEGIN
    NEW.last_edit = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER posts_update_last_edit
    BEFORE UPDATE ON posts
    FOR EACH ROW
    EXECUTE FUNCTION update_last_edit();

CREATE TRIGGER user_payments_update_last_edit
    BEFORE UPDATE ON user_payments
    FOR EACH ROW
    EXECUTE FUNCTION update_last_edit();
