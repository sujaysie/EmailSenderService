INSERT INTO outbox_messages (id, event_id, payload, sent, retry_count, created_at)
VALUES
    (1001, 'evt-dummy-1001', '{"eventId":"evt-dummy-1001","templateName":"welcome-email"}', false, 0, CURRENT_TIMESTAMP),
    (1002, 'evt-dummy-1002', '{"eventId":"evt-dummy-1002","templateName":"password-reset"}', true, 1, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
