CREATE TABLE IF NOT EXISTS notes (
  id                  BIGSERIAL PRIMARY KEY NOT NULL,
  title               varchar(255) NOT NULL,
  content             text NOT NULL,
  type                varchar(255) NOT NULL,
  reviews             integer NOT NULL DEFAULT 0,
  score               integer NOT NULL DEFAULT 100,
  next_review_date    timestamp NOT NULL,
  url                 varchar(255) NOT NULL,
  created_date        timestamp NOT NULL,
  last_modified_date  timestamp NOT NULL,
  version             integer NOT NULL
);