ALTER TABLE message
    DROP CONSTRAINT message_dialog_id_fkey;
ALTER TABLE message
    ADD FOREIGN KEY (dialog_id) REFERENCES dialog (id);