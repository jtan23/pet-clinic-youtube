INSERT INTO users VALUES ('admin', '{bcrypt}$2y$10$PTsCWNUt2Vkr.X/AV4qu2um5sqo3Wd2qwzoV7TtEfJzMJcPQv89Bm', 1);
INSERT INTO users VALUES ('user',  '{bcrypt}$2y$10$z62nRHa0Ne2gxh2PHfd45.oUGJGPq9Iu8893nLGYkbyt2qv/y9I0W', 1);
INSERT INTO users VALUES ('guest', '{bcrypt}$2y$10$TfgqRMz0xswwwLJ8RWcVAOaUjQAPeHuMnmSjFZuK0O5bc4//8F6RK', 1);

INSERT INTO authorities VALUES ('admin', 'ROLE_ADMIN');
INSERT INTO authorities VALUES ('admin', 'ROLE_USER');
INSERT INTO authorities VALUES ('user', 'ROLE_USER');
INSERT INTO authorities VALUES ('guest', 'ROLE_GUEST');

INSERT INTO oauth2_registered_client VALUES (
    1,
    'api-client',
    CURRENT_TIMESTAMP(),
    '{noop}api-client',
    null,
    'API Client',
    'client_secret_basic',
    'client_credentials',
    null,
    null,
    'read,write',
    '{
        "@class":"java.util.Collections$UnmodifiableMap",
        "settings.client.jwk-set-url":null,
        "settings.client.require-authorization-consent":true,
        "settings.client.token-endpoint-authentication-signing-algorithm":null,
        "settings.client.require-proof-key":false
    }',
    '{
        "@class":"java.util.Collections$UnmodifiableMap",
        "settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000],
        "settings.token.access-token-time-to-live":["java.time.Duration",86400.000000000],
        "settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},
        "settings.token.reuse-refresh-tokens":true,
        "settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000],
        "settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"]
    }');
INSERT INTO oauth2_registered_client VALUES (
    2,
    'petclinic-client',
    CURRENT_TIMESTAMP(),
    '{bcrypt}$2y$10$2ZNMhpuW7.hHV8w2OE5TIe15qr/Ax6vN456I1q8b0JRun03fLy8Q2',
    null,
    'Petclinic Client',
    'client_secret_basic',
    'authorization_code,refresh_token',
    'http://127.0.0.1:8080/login/oauth2/code/petclinic-client,http://127.0.0.1:8080/authorized',
    null,
    'openid,profile',
    '{
        "@class":"java.util.Collections$UnmodifiableMap",
        "settings.client.require-proof-key":false,
        "settings.client.require-authorization-consent": true,
        "settings.client.jwk-set-url":"http://localhost:8085/.well-known/jwks.json",
        "settings.client.token-endpoint-authentication-signing-algorithm":["org.springframework.security.oauth2.jose.jws.MacAlgorithm","HS256"]
    }',
    '{
        "@class":"java.util.Collections$UnmodifiableMap",
        "settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000],
        "settings.token.access-token-time-to-live":["java.time.Duration",86400.000000000],
        "settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},
        "settings.token.reuse-refresh-tokens":true,
        "settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000],
        "settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"]
    }');
