-- V2 시드 데이터가 명시적 id로 삽입되어 각 테이블의 시퀀스가 갱신되지 않았음.
-- 이 상태로는 애플리케이션이 새 행을 삽입할 때 시드 데이터와 PK가 충돌한다.
-- 각 테이블의 시퀀스를 현재 최대 id 기준으로 재설정한다.
SELECT setval(pg_get_serial_sequence('stock', 'id'), COALESCE((SELECT MAX(id) FROM stock), 1));
SELECT setval(pg_get_serial_sequence('kingdom', 'id'), COALESCE((SELECT MAX(id) FROM kingdom), 1));
SELECT setval(pg_get_serial_sequence('users', 'id'), COALESCE((SELECT MAX(id) FROM users), 1));
SELECT setval(pg_get_serial_sequence('user_stock_holding', 'id'), COALESCE((SELECT MAX(id) FROM user_stock_holding), 1));
SELECT setval(pg_get_serial_sequence('kingdom_power_snapshot', 'id'), COALESCE((SELECT MAX(id) FROM kingdom_power_snapshot), 1));