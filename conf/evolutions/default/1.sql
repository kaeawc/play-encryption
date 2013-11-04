
# --- !Ups

CREATE TABLE user (
  id        BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
  email     VARCHAR(255) NOT NULL,
  password  VARCHAR(255) NOT NULL,
  salt      VARCHAR(255) NOT NULL,
  created   DATETIME     NOT NULL
);

CREATE TABLE user_session (
  id        BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
  user      BIGINT       NOT NULL,
  token     VARCHAR(512) NOT NULL,
  series    BIGINT       NOT NULL,
  created   DATETIME     NOT NULL
);

CREATE TABLE login_attempt (
  id        BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
  user      BIGINT       NOT NULL,
  token     VARCHAR(512) NOT NULL,
  series    BIGINT       NOT NULL,
  created   DATETIME     NOT NULL
);

CREATE TABLE visit (
  id        BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
  uri       VARCHAR(255) NOT NULL,
  userAgent TEXT         NULL,
  user      BIGINT       NULL,
  created   DATETIME     NOT NULL
);

# --- !Downs

DROP TABLE user;
DROP TABLE visit;
DROP TABLE login_attempt;
DROP TABLE user_session;