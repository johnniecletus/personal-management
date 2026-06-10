CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    user_name VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL
);

CREATE TABLE tasklists (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    CONSTRAINT fk_lists_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
);

CREATE TABLE tasklistitems (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_list_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    amount DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    completed BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_tasklistitems_tasklist
        FOREIGN KEY (task_list_id)
        REFERENCES tasklists(id)
);