DROP TABLE IF EXISTS users, items, bookings, comments;

CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,
                                     username VARCHAR(255) NOT NULL,
                                     email VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS items (
                                     id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,
                                     item_name VARCHAR(255) NOT NULL,
                                     description VARCHAR(255) NOT NULL,
                                     available BOOLEAN NOT NULL,
                                     owner_id BIGINT NOT NULL,
                                     FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS bookings (
                                        id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,
                                        item_id BIGINT NOT NULL,
                                        user_id BIGINT NOT NULL,
                                        status VARCHAR(10) NOT NULL,
                                        start_date TIMESTAMP NOT NULL,
                                        end_date TIMESTAMP NOT NULL,
                                        FOREIGN KEY (item_id) REFERENCES items(id),
                                        FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS comments (
                                        id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,
                                        text VARCHAR(2000) NOT NULL,
                                        item_id BIGINT NOT NULL,
                                        author_id BIGINT NOT NULL,
                                        created TIMESTAMP NOT NULL,
                                        FOREIGN KEY (item_id) REFERENCES items(id),
                                        FOREIGN KEY (author_id) REFERENCES users(id)
)