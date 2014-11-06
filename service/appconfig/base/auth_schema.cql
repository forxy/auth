DROP KEYSPACE IF EXISTS auth;
CREATE KEYSPACE auth WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1};

USE auth;

CREATE TABLE client (
    client_id TEXT,
    application_name TEXT,
    application_description TEXT,
    create_date timestamp,
    favorites SET<TEXT>,
    PRIMARY KEY (client_id)
);
//WITH CLUSTERING ORDER BY (create_date DESC);

CREATE INDEX create_date_idx ON token(create_date);
CREATE INDEX login_idx ON token(login);
CREATE INDEX password_idx ON token(password);
CREATE INDEX first_name_idx ON token(first_name);
CREATE INDEX last_name_idx ON token(last_name);
CREATE INDEX gender_idx ON token(gender);
CREATE INDEX birth_date_idx ON token(birth_date);