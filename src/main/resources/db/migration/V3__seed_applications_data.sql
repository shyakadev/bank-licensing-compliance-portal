-- PENDING Application
INSERT INTO applications (entity_name, entity_address, tin_number, contact_email, contact_phone, status, submitted_by, version)
VALUES (
    'EcoBank Ltd', 'Kigali Heights', '1222122', 'info@eco.rw', '+250781', 'PENDING',
    (SELECT id FROM users WHERE username = 'mary'),
    0
);

-- UNDER_REVIEW Application
INSERT INTO applications (entity_name, entity_address, tin_number, contact_email, contact_phone, status, submitted_by, reviewed_by, version)
VALUES (
    'Innovate Fin', 'Norrsken Kigali', '1332222', 'dev@inn.rw', '+250782', 'UNDER_REVIEW',
    (SELECT id FROM users WHERE username = 'mary'),
    (SELECT id FROM users WHERE username = 'eric'),
    0
);

-- REVIEWED Application
INSERT INTO applications (entity_name, entity_address, tin_number, contact_email, contact_phone, status, submitted_by, reviewed_by, assigned_approver_id, review_cycle, version)
VALUES (
    'Legacy Trust', 'Downtown', '1423233', 'ops@legacy.rw', '+250783', 'REVIEWED',
    (SELECT id FROM users WHERE username = 'mary'),
    (SELECT id FROM users WHERE username = 'eric'),
    (SELECT id FROM users WHERE username = 'rebecca'),
    1, 0
);

-- AWAITING_RESUBMISSION Application
INSERT INTO applications (entity_name, entity_address, tin_number, contact_email, contact_phone, status, submitted_by, reviewed_by, assigned_approver_id, resubmission_reason, review_cycle, version)
VALUES (
    'Alpha Digital', 'K-Free Zone', '12502342', 'admin@alpha.rw', '+250784', 'AWAITING_RESUBMISSION',
    (SELECT id FROM users WHERE username = 'mary'),
    (SELECT id FROM users WHERE username = 'eric'),
    (SELECT id FROM users WHERE username = 'rebecca'),
    'Missing tax clearance certificate.',
    1, 0
);

-- 3. Seed Document for the last Application
INSERT INTO documents (application_id, uploaded_by, file_name, file_size, file_type, file_path, document_type, version)
VALUES (
    (SELECT id FROM applications WHERE entity_name = 'Alpha Digital'),
    (SELECT id FROM users WHERE username = 'mary'),
    'tax_clearance_v1.pdf', 512000, 'application/pdf', '/storage/alpha/tax_v1.pdf', 'TAX_DOCUMENT', 1
);