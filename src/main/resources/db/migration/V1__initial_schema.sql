-- ===================================
-- NBR Licensing & Compliance Portal Initial schema
-- =================================

-- ENUMS
create type user_role as ENUM ('APPLICANT', 'REVIEWER', 'APPROVER', 'ADMIN');
create type user_type as ENUM ('APPLICANT', 'STAFF');
create type application_status as ENUM (
    'PENDING',
    'UNDER_REVIEW',
    'REVIEWED',
    'APPROVED',
    'REJECTED',
    'AWAITING_RESUBMISSION',
    'RESUBMITTED'
);

-- USERS
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role user_role NOT NULL,
    user_type user_type NOT NULL,
    position VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- APPLICATIONS
CREATE TABLE applications (
    id BIGSERIAL PRIMARY KEY,
    entity_name VARCHAR(255) NOT NULL,
    entity_address VARCHAR(255) NOT NULL,
    tin_number BIGINT NOT NULL,
    contact_email VARCHAR(255) NOT NULL,
    contact_phone VARCHAR(50),
    status application_status NOT NULL DEFAULT 'PENDING',
    decision_reason TEXT,
    resubmission_reason TEXT,
    version INTEGER NOT NULL DEFAULT 0,
    review_cycle INTEGER NOT NULL DEFAULT 0,
    submitted_by BIGINT NOT NULL REFERENCES users(id),
    reviewed_by BIGINT REFERENCES users(id),
    assigned_approver_id BIGINT REFERENCES users(id),
    last_modified_by BIGINT REFERENCES users(id),
    submitted_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);


-- DOCUMENTS
CREATE TABLE documents (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL REFERENCES applications(id),
    uploaded_by BIGINT NOT NULL REFERENCES users(id),
    file_name VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    file_type VARCHAR(100) NOT NULL,
    file_path TEXT NOT NULL,
    document_type VARCHAR(100) NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    parent_document_id BIGINT REFERENCES documents(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);


-- AUDIT LOG
CREATE TABLE audit_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    entity_type VARCHAR(100) NOT NULL,
    entity_id BIGINT NOT NULL,
    action VARCHAR(100) NOT NULL,
    old_value JSONB,
    new_value JSONB,
    ip_address VARCHAR(45),
    timestamp TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);


-- REFRESH TOKENS
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    is_revoked BOOLEAN NOT NULL DEFAULT FALSE,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);


-- AUDIT LOG PROTECTION: TRIGGER + INSERT ONLY GRANT

-- Trigger function: raise exception on any update or delete
create or replace function prevent_audit_modification()
RETURNS trigger AS $$
begin
    raise exception
        'Modification of audit_log is forbidden. action=%, table=%',
        TG_OP, TG_TABLE_NAME
    USING ERRCODE = 'insufficient_privilege';
END;
$$ LANGUAGE plpgsql;

-- Attach trigger BEFORE UPDATE OR DELETE on audit_log
create trigger trg_protect_audit_log
    before update or delete on audit_log
    for each row
    EXECUTE function prevent_audit_modification();

-- Revoke all privileges from the app user then grant INSERT-only.
DO $$
begin
    if exists (select 1 from pg_roles where rolname='license_app') then
        REVOKE ALL ON AUDIT_LOG FROM license_app;
        GRANT INSERT ON AUDIT_LOG TO license_app;
        GRANT USAGE ON SEQUENCE AUDIT_LOG_ID_SEQ TO license_app;
    END IF;
END
$$;
