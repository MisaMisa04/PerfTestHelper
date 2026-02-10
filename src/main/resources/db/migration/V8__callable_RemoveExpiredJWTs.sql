DELIMITER $$

CREATE PROCEDURE RemoveExpiredJWTs()
BEGIN
    delete from jwt_tokens
    where status = 'EXPIRED';
    commit;
END$$

CREATE EVENT remove_expired_jwts_event
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_TIMESTAMP
DO
BEGIN
    CALL RemoveExpiredJWTs();
END$$

DELIMITER ;

