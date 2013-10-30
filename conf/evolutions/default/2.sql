
# --- !Ups

ALTER TABLE user 
  DROP COLUMN salt;

ALTER TABLE user 
  ADD salt VARCHAR(510) NOT NULL;

# --- !Downs

ALTER TABLE user 
  DROP COLUMN salt;

ALTER TABLE user 
  ADD salt VARCHAR(255) NOT NULL;