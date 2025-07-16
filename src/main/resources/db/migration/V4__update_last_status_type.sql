-- 1 = UP, 0 = DOWN
ALTER TABLE projects
 ADD COLUMN status_code SMALLINT;

-- Update the new column based on the old values
UPDATE projects
SET status_code = CASE
    WHEN last_status = 'UP' THEN 1
    WHEN last_status = 'DOWN' THEN 0
    ELSE 1 -- Default to UP if any other value exists
END;

-- Drop the old column
ALTER TABLE projects DROP COLUMN last_status;

-- Rename the new column
ALTER TABLE projects RENAME COLUMN status_code TO last_status;
ALTER TABLE projects ALTER COLUMN last_status SET NOT NULL;