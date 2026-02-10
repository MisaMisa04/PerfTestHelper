DELIMITER $$

CREATE PROCEDURE ExpireOldJWTs()
BEGIN
    -- Обновляем только те записи, у которых:
    --   expires_at < текущего времени UTC
    --   и статус ещё не EXPIRED (чтобы не трогать уже помеченные)
    UPDATE jwt_tokens
    SET status = 'EXPIRED'
    WHERE expires_at < UTC_TIMESTAMP()
      AND status <> 'EXPIRED';
    commit;
END$$

DELIMITER ;
