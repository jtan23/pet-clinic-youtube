CREATE TABLE users (
  username varchar(50) NOT NULL,
  password char(68) NOT NULL,
  enabled tinyint NOT NULL,
  PRIMARY KEY (username)
);

CREATE TABLE authorities (
  username varchar(50) NOT NULL,
  authority varchar(50) NOT NULL,
  UNIQUE KEY key_username_authority (username, authority),
  CONSTRAINT foreign_key_users_username FOREIGN KEY (username) REFERENCES users (username)
);
