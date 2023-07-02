DROP TABLE IF EXISTS url;
DROP TABLE IF EXISTS hash_pool;

CREATE TABLE url
(
    hash_id             CHAR(8)  NOT NULL PRIMARY KEY,
    complete_url        TEXT     NOT NULL,
    qrcode_image_string LONGTEXT NOT NULL,
    date_created        DATETIME NOT NULL,
    last_queried        DATETIME NOT NULL,
    visits              INTEGER  NOT NULL DEFAULT 0,
    requests            INTEGER  NOT NULL DEFAULT 1
);

CREATE TABLE uuid_pool
(
    random_string CHAR(8) NOT NULL PRIMARY KEY

);