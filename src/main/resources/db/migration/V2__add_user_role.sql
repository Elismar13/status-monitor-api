-- Create roles table
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Insert default roles
INSERT INTO roles (name, description) VALUES
    ('ROLE_USER', 'Regular user with basic permissions'),
    ('ROLE_ADMIN', 'Administrator with full access');


-- Add role_id column to users table
ALTER TABLE users 
    ADD COLUMN role_id SERIAL NOT NULL;

-- Add foreign key constraint to user
ALTER TABLE users
    ADD CONSTRAINT fk_user_role 
        FOREIGN KEY (role_id) 
        REFERENCES roles(id) 
        ON DELETE SET NULL;

-- Update existing users to have ROLE_USER by default
UPDATE users SET role_id = (SELECT id FROM roles WHERE name = 'ROLE_USER') WHERE role_id IS NULL;

-- Make role_id required after setting default values
ALTER TABLE users 
    ALTER COLUMN role_id SET NOT NULL;