-- =========================================================
-- UNIQ Placement Console — Initial Schema
-- =========================================================

-- ===================== ENUMS =====================

CREATE TYPE user_role AS ENUM ('Admin', 'Collection User', 'Share Partner', 'Custom User');
CREATE TYPE active_status AS ENUM ('Active', 'Inactive');
CREATE TYPE candidate_status AS ENUM ('Registered', 'Training', 'Interview Ready', 'Attending Interviews', 'Placed', 'Collection Running', 'Fully Paid', 'Closed');
CREATE TYPE eligibility AS ENUM ('Eligible', 'Hold', 'Not Eligible');
CREATE TYPE training_mode AS ENUM ('Online', 'Offline', 'Hybrid');
CREATE TYPE placement_status AS ENUM ('Active', 'Completed', 'Cancelled');
CREATE TYPE payment_type AS ENUM ('Document Fee', 'Placement Installment', 'Refund', 'Adjustment');
CREATE TYPE payment_mode AS ENUM ('UPI', 'QR', 'Bank Transfer', 'Cash', 'Other');
CREATE TYPE document_type AS ENUM ('OFFER_LETTER', 'PAYMENT_RECEIPT', 'OTHER');
CREATE TYPE gender_type AS ENUM ('Male', 'Female', 'Other');
CREATE TYPE due_period AS ENUM ('30 Days', '45 Days', '60 Days', '90 Days', 'Custom');
CREATE TYPE referral_type AS ENUM ('Direct', 'Consultant', 'Employee Referral', 'Other');
CREATE TYPE ledger_type AS ENUM ('Company', 'Team');
CREATE TYPE settlement_direction AS ENUM ('Paid to Partner', 'Returned to Company');
CREATE TYPE access_level AS ENUM ('Full access', 'Collection Entry', 'Share View Only', 'Entry + View', 'View Only');

-- ===================== TABLES =====================

-- Branches
CREATE TABLE branches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    code VARCHAR(30) NOT NULL UNIQUE,
    location VARCHAR(150),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Teams
CREATE TABLE teams (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id UUID REFERENCES branches(id),
    name VARCHAR(100) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Users
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name VARCHAR(150) NOT NULL,
    username VARCHAR(80) NOT NULL UNIQUE,
    email VARCHAR(150),
    mobile_number VARCHAR(15),
    password_hash TEXT NOT NULL,
    role user_role NOT NULL,
    access access_level NOT NULL DEFAULT 'Full access',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    permissions JSONB DEFAULT '{}',
    last_login_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- User-Teams (many-to-many)
CREATE TABLE user_teams (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    team_id UUID NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, team_id)
);

-- Batches
CREATE TABLE batches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id UUID REFERENCES branches(id),
    team_id UUID REFERENCES teams(id),
    name VARCHAR(100) NOT NULL,
    course_name VARCHAR(150),
    training_mode training_mode NOT NULL,
    trainer_name VARCHAR(150),
    start_date DATE,
    end_date DATE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Candidates
CREATE TABLE candidates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_code VARCHAR(30) NOT NULL UNIQUE,
    batch_id UUID NOT NULL REFERENCES batches(id),
    assigned_team_id UUID REFERENCES teams(id),
    branch_id UUID REFERENCES branches(id),
    candidate_name VARCHAR(150) NOT NULL,
    mobile_number VARCHAR(15) NOT NULL UNIQUE,
    alternate_mobile VARCHAR(15),
    email VARCHAR(150),
    joining_date DATE NOT NULL,
    gender gender_type,
    qualification VARCHAR(100),
    degree VARCHAR(100),
    department VARCHAR(100),
    passing_year SMALLINT,
    college_name VARCHAR(200),
    current_location VARCHAR(150),
    course VARCHAR(100),
    batch_type training_mode,
    branch VARCHAR(100),
    trainer VARCHAR(150),
    status candidate_status NOT NULL DEFAULT 'Registered',
    eligibility eligibility DEFAULT 'Eligible',
    remarks TEXT,
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Placements
CREATE TABLE placements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id UUID NOT NULL REFERENCES candidates(id),
    company_name VARCHAR(200) NOT NULL,
    job_role VARCHAR(150),
    company_location VARCHAR(150),
    placement_date DATE NOT NULL,
    joining_date DATE NOT NULL,
    due_period_days INTEGER NOT NULL,
    due_period_label due_period,
    auto_calculated_due_date DATE NOT NULL,
    final_applied_due_date DATE NOT NULL,
    annual_ctc NUMERIC(14,2) NOT NULL,
    committed_percentage NUMERIC(5,2) NOT NULL,
    committed_amount NUMERIC(14,2),
    payment_terms TEXT,
    referral_type referral_type,
    share_team_id UUID REFERENCES teams(id),
    status placement_status NOT NULL DEFAULT 'Active',
    offer_letter_url TEXT,
    remarks TEXT,
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE(candidate_id)
);

-- Placement Due Date Overrides
CREATE TABLE placement_due_date_overrides (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    placement_id UUID NOT NULL REFERENCES placements(id),
    auto_calculated_due_date DATE NOT NULL,
    previous_applied_due_date DATE NOT NULL,
    new_applied_due_date DATE NOT NULL,
    override_reason TEXT NOT NULL,
    changed_by UUID REFERENCES users(id),
    changed_at TIMESTAMPTZ DEFAULT NOW()
);

-- Account Holders
CREATE TABLE account_holders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(150) NOT NULL,
    display_name VARCHAR(150) NOT NULL,
    linked_ledger_type ledger_type NOT NULL,
    linked_team_id UUID REFERENCES teams(id),
    bank VARCHAR(150),
    last4 VARCHAR(4),
    upi VARCHAR(150),
    payment_type VARCHAR(50),
    status active_status NOT NULL DEFAULT 'Active',
    remarks TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Documents
CREATE TABLE documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id UUID REFERENCES candidates(id),
    placement_id UUID REFERENCES placements(id),
    document_type document_type NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    storage_key TEXT NOT NULL,
    mime_type VARCHAR(100),
    file_size BIGINT,
    uploaded_by UUID REFERENCES users(id),
    uploaded_at TIMESTAMPTZ DEFAULT NOW()
);

-- Payments
CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payment_code VARCHAR(30) NOT NULL UNIQUE,
    candidate_id UUID NOT NULL REFERENCES candidates(id),
    placement_id UUID REFERENCES placements(id),
    payment_type payment_type NOT NULL,
    amount NUMERIC(14,2) NOT NULL,
    payment_date DATE NOT NULL,
    payment_mode payment_mode NOT NULL,
    account_name VARCHAR(150),
    account_holder_id UUID REFERENCES account_holders(id),
    reference_number VARCHAR(150),
    remarks TEXT,
    received_by UUID REFERENCES users(id),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Share Allocations
CREATE TABLE share_allocations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payment_id UUID NOT NULL UNIQUE REFERENCES payments(id),
    partner VARCHAR(150) NOT NULL,
    applied_percent NUMERIC(5,2) NOT NULL,
    amount NUMERIC(14,2) NOT NULL,
    allocation_date DATE NOT NULL,
    remarks TEXT,
    allocated_by UUID REFERENCES users(id),
    allocated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Settlements
CREATE TABLE settlements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    partner VARCHAR(150) NOT NULL,
    amount NUMERIC(14,2) NOT NULL,
    settlement_date DATE NOT NULL,
    account VARCHAR(150),
    direction settlement_direction NOT NULL DEFAULT 'Paid to Partner',
    remarks TEXT,
    entered_by UUID REFERENCES users(id),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Audit Logs
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    user_name VARCHAR(150),
    entity_type VARCHAR(80) NOT NULL,
    entity_id UUID,
    action VARCHAR(80) NOT NULL,
    old_values JSONB,
    new_values JSONB,
    ip_address INET,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ===================== INDEXES =====================

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_candidates_code ON candidates(candidate_code);
CREATE INDEX idx_candidates_mobile ON candidates(mobile_number);
CREATE INDEX idx_candidates_team ON candidates(assigned_team_id);
CREATE INDEX idx_candidates_status ON candidates(status);
CREATE INDEX idx_placements_candidate ON placements(candidate_id);
CREATE INDEX idx_payments_candidate ON payments(candidate_id);
CREATE INDEX idx_payments_date ON payments(payment_date);
CREATE INDEX idx_payments_account_holder ON payments(account_holder_id);
CREATE INDEX idx_share_allocations_payment ON share_allocations(payment_id);
CREATE INDEX idx_settlements_partner ON settlements(partner);
CREATE INDEX idx_settlements_date ON settlements(settlement_date);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_created ON audit_logs(created_at);

-- ===================== SEED DATA =====================

-- Default branch
INSERT INTO branches (id, name, code, location) VALUES
    ('00000000-0000-0000-0000-000000000001', 'Main Branch', 'MAIN', 'Head Office');

-- Default team
INSERT INTO teams (id, branch_id, name) VALUES
    ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'Default Team');

-- Admin user (password: admin123)
INSERT INTO users (id, full_name, username, mobile_number, password_hash, role, access, is_active, permissions) VALUES
    ('00000000-0000-0000-0000-000000000001', 'System Admin', 'admin', '9999999999',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     'Admin', 'Full access', TRUE, '{}');

-- Link admin to default team
INSERT INTO user_teams (user_id, team_id) VALUES
    ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001');

-- Default account holder
INSERT INTO account_holders (id, name, display_name, linked_ledger_type, status) VALUES
    ('00000000-0000-0000-0000-000000000001', 'Company Account', 'Company Account', 'Company', 'Active');
