CREATE TABLE expense
(
    id       VARCHAR(255) NOT NULL UNIQUE ,
    user_id  VARCHAR(255),
    amount   DECIMAL,
    sender   VARCHAR(255),
    receiver VARCHAR(255),
    date     VARCHAR(255),
    created  TIMESTAMP,
    CONSTRAINT pk_expense PRIMARY KEY (id)
);

CREATE SEQUENCE expense_seq START WITH 1 INCREMENT BY 10;
ALTER SEQUENCE expense_seq OWNER TO rajsubhod;
ALTER SEQUENCE expense_seq OWNED BY expense.id;