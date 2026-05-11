ALTER TABLE applications RENAME COLUMN resubmission_reason TO reason;
ALTER TABLE applications DROP COLUMN decision_reason;

ALTER TABLE applications ADD CONSTRAINT fk_applications_submitted_by FOREIGN KEY (submitted_by) REFERENCES users(id);

ALTER TABLE applications ADD CONSTRAINT fk_applications_reviewed_by FOREIGN KEY (reviewed_by) REFERENCES users(id);

ALTER TABLE applications ADD CONSTRAINT fk_applications_assigned_approver FOREIGN KEY (assigned_approver_id) REFERENCES users(id);

ALTER TABLE applications ADD CONSTRAINT fk_applications_last_modified_by FOREIGN KEY (last_modified_by) REFERENCES users(id);

ALTER TABLE documents ADD CONSTRAINT fk_documents_application FOREIGN KEY (application_id) REFERENCES applications(id);

ALTER TABLE documents ADD CONSTRAINT fk_documents_uploaded_by FOREIGN KEY (uploaded_by) REFERENCES users(id);

ALTER TABLE documents ADD CONSTRAINT fk_documents_parent FOREIGN KEY (parent_document_id) REFERENCES documents(id);

ALTER TABLE audit_log ADD CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE refresh_tokens ADD CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;