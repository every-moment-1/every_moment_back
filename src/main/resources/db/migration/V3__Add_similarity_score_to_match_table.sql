-- V3__Add_user_scores_and_similarity_to_match_table.sql
ALTER TABLE matches
ADD COLUMN user1_score INT NOT NULL DEFAULT 0,  -- user1의 점수 추가
ADD COLUMN user2_score INT NOT NULL DEFAULT 0,  -- user2의 점수 추가
ADD COLUMN similarity_score DOUBLE;  -- 유사도 점수 추가