INSERT INTO users VALUES ('admin', '{bcrypt}$2y$10$PTsCWNUt2Vkr.X/AV4qu2um5sqo3Wd2qwzoV7TtEfJzMJcPQv89Bm', 1);
INSERT INTO users VALUES ('user',  '{bcrypt}$2y$10$z62nRHa0Ne2gxh2PHfd45.oUGJGPq9Iu8893nLGYkbyt2qv/y9I0W', 1);
INSERT INTO users VALUES ('guest', '{bcrypt}$2y$10$TfgqRMz0xswwwLJ8RWcVAOaUjQAPeHuMnmSjFZuK0O5bc4//8F6RK', 1);

INSERT INTO authorities VALUES ('admin', 'ROLE_ADMIN');
INSERT INTO authorities VALUES ('admin', 'ROLE_USER');
INSERT INTO authorities VALUES ('user', 'ROLE_USER');
INSERT INTO authorities VALUES ('guest', 'ROLE_GUEST');
