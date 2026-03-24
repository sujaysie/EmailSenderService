INSERT INTO templates (id, name, description, category, deleted, created_at, updated_at, active_version)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'welcome-email', 'Dummy welcome template', 'ONBOARDING', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
    ('22222222-2222-2222-2222-222222222222', 'password-reset', 'Dummy password reset template', 'SECURITY', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO template_versions (id, template_id, version_number, subject, html_body, text_body, checksum, created_at)
VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 1, 'Welcome, {{firstName}}!', '<h1>Welcome {{firstName}}</h1><p>Thanks for joining us.</p>', 'Welcome {{firstName}}. Thanks for joining us.', 'dummy-checksum-welcome-v1', CURRENT_TIMESTAMP),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-2222-2222-2222-222222222222', 1, 'Reset your password', '<p>Hi {{firstName}}, reset your password using code {{code}}.</p>', 'Hi {{firstName}}, reset your password using code {{code}}.', 'dummy-checksum-reset-v1', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

UPDATE templates
SET active_version = 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'
WHERE id = '11111111-1111-1111-1111-111111111111' AND active_version IS NULL;

UPDATE templates
SET active_version = 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'
WHERE id = '22222222-2222-2222-2222-222222222222' AND active_version IS NULL;
