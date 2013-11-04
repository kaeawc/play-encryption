
# --- !Ups

ALTER TABLE user_session 
  DROP COLUMN series;
ALTER TABLE login_attempt 
  DROP COLUMN series;

# --- !Downs

ALTER TABLE user_session 
  ADD series BIGINT NOT NULL;
ALTER TABLE login_attempt 
  ADD series BIGINT NOT NULL;