ALTER TABLE posts
  ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'NORMAL' AFTER deleted;

CREATE INDEX idx_posts_status ON posts(status);