INSERT INTO currencies (code, name, created_at)
SELECT 'NGN', 'Nigerian Naira', CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM currencies WHERE code = 'NGN');

INSERT INTO currencies (code, name, created_at)
SELECT 'USD', 'United States Dollar', CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM currencies WHERE code = 'USD');

INSERT INTO currencies (code, name, created_at)
SELECT 'EUR', 'Euro', CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM currencies WHERE code = 'EUR');

INSERT INTO currencies (code, name, created_at)
SELECT 'GBP', 'British Pound Sterling', CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM currencies WHERE code = 'GBP');
