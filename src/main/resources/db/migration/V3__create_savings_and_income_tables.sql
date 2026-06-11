CREATE TABLE currencies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    created_at DATETIME(6) NOT NULL
);

CREATE TABLE savingsclusters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    CONSTRAINT uk_savingsclusters_user_name
        UNIQUE (user_id, name),
    CONSTRAINT fk_savingsclusters_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
);

CREATE TABLE savingsclusteritems (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    cluster_id BIGINT NOT NULL,
    percentage INT NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    CONSTRAINT uk_savingsclusteritems_cluster_name
        UNIQUE (cluster_id, name),
    CONSTRAINT chk_savingsclusteritems_percentage
        CHECK (percentage >= 0 AND percentage <= 100),
    CONSTRAINT fk_savingsclusteritems_cluster
        FOREIGN KEY (cluster_id)
        REFERENCES savingsclusters(id)
);

CREATE TABLE incomes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    cluster_id BIGINT NOT NULL,
    currency_id BIGINT NOT NULL,
    amount DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    received_at DATETIME(6) NOT NULL,
    description VARCHAR(255) NOT NULL,
    created_at DATETIME(6) NOT NULL,

    CONSTRAINT fk_incomes_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),
    CONSTRAINT fk_incomes_cluster
        FOREIGN KEY (cluster_id)
        REFERENCES savingsclusters(id),
    CONSTRAINT fk_incomes_currency
        FOREIGN KEY (currency_id)
        REFERENCES currencies(id)
);

CREATE TABLE savingshistories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    income_id BIGINT NOT NULL,
    currency_id BIGINT NOT NULL,
    cluster_id BIGINT NOT NULL,
    cluster_item_id BIGINT NULL,
    savings_name VARCHAR(255) NOT NULL,
    percentage INT NOT NULL,
    calculated_amount DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    created_at DATETIME(6) NOT NULL,

    CONSTRAINT chk_savingshistories_percentage
        CHECK (percentage >= 0 AND percentage <= 100),
    CONSTRAINT fk_savingshistories_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),
    CONSTRAINT fk_savingshistories_income
        FOREIGN KEY (income_id)
        REFERENCES incomes(id),
    CONSTRAINT fk_savingshistories_currency
        FOREIGN KEY (currency_id)
        REFERENCES currencies(id),
    CONSTRAINT fk_savingshistories_cluster
        FOREIGN KEY (cluster_id)
        REFERENCES savingsclusters(id),
    CONSTRAINT fk_savingshistories_cluster_item
        FOREIGN KEY (cluster_item_id)
        REFERENCES savingsclusteritems(id)
);

CREATE TABLE monthlyoverviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    currency_id BIGINT NOT NULL,
    month_start DATE NOT NULL,
    total_income_amount DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    total_savings_amount DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    CONSTRAINT uk_monthlyoverviews_user_currency_month_start
        UNIQUE (user_id, currency_id, month_start),
    CONSTRAINT fk_monthlyoverviews_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),
    CONSTRAINT fk_monthlyoverviews_currency
        FOREIGN KEY (currency_id)
        REFERENCES currencies(id)
);
