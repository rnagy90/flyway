--
-- Copyright 2010-2017 Boxfuse GmbH
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--         http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

-- Create the Table (Class) for patch metadata persist
CREATE CLASS ${table};

-- Because the OrientDB do not support the property creation with the Class creation
-- we need to add them one by one
-- SQL mappings: 
--   MANDATORY + NOTNULL -> NOT NULL
--   DEFAULT sysdate() -> DEFAULT <current date>
--   READONLY -> replace an update preventing constraint

CREATE PROPERTY ${table}.installed_rank INTEGER (MANDATORY TRUE, NOTNULL TRUE);
CREATE PROPERTY ${table}.version STRING (MANDATORY TRUE, NOTNULL TRUE);
CREATE PROPERTY ${table}.description STRING (MANDATORY TRUE, NOTNULL TRUE);
CREATE PROPERTY ${table}.type STRING (MANDATORY TRUE, NOTNULL TRUE);
CREATE PROPERTY ${table}.script STRING (MANDATORY TRUE, NOTNULL TRUE);
CREATE PROPERTY ${table}.checksum INTEGER;
CREATE PROPERTY ${table}.installed_by STRING (MANDATORY TRUE, NOTNULL TRUE);
CREATE PROPERTY ${table}.installed_on DATE (DEFAULT sysdate(), READONLY TRUE);
CREATE PROPERTY ${table}.execution_time INTEGER (MANDATORY TRUE, NOTNULL TRUE);
CREATE PROPERTY ${table}.success BOOLEAN (MANDATORY TRUE, NOTNULL TRUE);

-- The property doesn't have such a UNIQUE constrain, but can be achieved by creating a unique index.
-- We only need the lightweight index type, because there is not any range query for this field
CREATE INDEX ${table}_uq_v_idx ON ${table} (version) UNIQUE_HASH_INDEX STRING;

-- Same here (for boolean values there will be only true/false filter)
CREATE INDEX ${table}_s_idx ON ${table} (success) NOTUNIQUE_HASH_INDEX BOOLEAN;

-- General index, capable for range queries
CREATE INDEX ${table}_ir_idx ON ${table} (installed_rank) UNIQUE INTEGER;
