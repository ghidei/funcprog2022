DROP TABLE IF EXISTS vote;

DROP TABLE IF EXISTS person;

DROP TABLE IF EXISTS party;

CREATE TABLE person (
    firstname TEXT NOT NULL,
    lastname TEXT NOT NULL,
    national_identification_number TEXT PRIMARY KEY
);

CREATE TABLE party (party_name TEXT PRIMARY KEY);

CREATE TABLE vote (
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    national_identification_number TEXT NOT NULL,
    party_name TEXT NOT NULL,
    FOREIGN KEY (national_identification_number) REFERENCES person(national_identification_number),
    FOREIGN KEY (party_name) REFERENCES party(party_name),
    PRIMARY KEY (national_identification_number)
);