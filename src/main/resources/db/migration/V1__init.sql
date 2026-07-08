-- V1: 초기 스키마 (User, Stock, UserStockHolding, Kingdom, KingdomPowerSnapshot)

-- 서비스 사용자. 최소 필드로 시작하며, 추후 인증(소셜 로그인 등) 붙일 때 확장 예정.
CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    nickname    VARCHAR(50)  NOT NULL,
    email       VARCHAR(100) NOT NULL UNIQUE,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
);

-- 종목(순수 시장 데이터 전용). 게임 도메인 로직은 kingdom 테이블에서 담당.
-- market_cap: 전투력 정규화 공식(참여율 = 보유총액 / 시가총액)에 사용.
CREATE TABLE stock (
    id             BIGSERIAL PRIMARY KEY,
    ticker         VARCHAR(20)  NOT NULL UNIQUE,
    company_name   VARCHAR(100) NOT NULL,
    current_price  NUMERIC(15, 2) NOT NULL,
    market_cap     NUMERIC(20, 2) NOT NULL,  -- 전투력 정규화용 시가총액
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP
);

-- 사용자별 보유 종목/수량. User-Stock 다대다를 푸는 중간 엔티티.
-- (user_id, stock_id) 유니크 제약으로 동일 종목 중복 보유 방지.
CREATE TABLE user_stock_holding (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users(id),
    stock_id    BIGINT NOT NULL REFERENCES stock(id),
    quantity    BIGINT NOT NULL,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    CONSTRAINT uk_user_stock UNIQUE (user_id, stock_id)
);

-- 왕국(게임 도메인). stock과 1:1 대응하지만 의도적으로 분리.
-- 이유: stock은 시세만 다루고, kingdom은 게임 로직(랭킹, 전투력)을 담당.
-- 추후 섹터 통합전 도입 시 kingdom이 여러 stock을 포함하도록 확장 가능.
CREATE TABLE kingdom (
    id          BIGSERIAL PRIMARY KEY,
    stock_id    BIGINT NOT NULL UNIQUE REFERENCES stock(id),
    name        VARCHAR(50) NOT NULL,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
);

-- 왕국 일자별 전투력 스냅샷 + 순위. 배치(일 1회) 실행 시 생성.
-- 전투력 공식: power = 참여율 × log(참여자 수 + 1)
--   참여율 = (total_holding_quantity × current_price) / market_cap
--   log(참여자 수 + 1): 소수 유저 몰빵 왜곡 방지, 결속력 우대.
-- rank/rank_change: KingdomRanking 별도 테이블로 분리했다가 YAGNI 판단으로 병합.
CREATE TABLE kingdom_power_snapshot (
    id                       BIGSERIAL PRIMARY KEY,
    kingdom_id               BIGINT  NOT NULL REFERENCES kingdom(id),
    snapshot_date            DATE    NOT NULL,
    total_holding_quantity   BIGINT  NOT NULL,  -- 왕국 전체 보유 주식 수 합계
    participant_count        INTEGER NOT NULL,  -- 해당 날짜 기준 참여 유저 수
    power                    NUMERIC(20, 4) NOT NULL,  -- 계산된 전투력 점수
    rank                     INTEGER,           -- 전체 왕국 중 순위 (배치 완료 후 채움)
    rank_change              INTEGER,           -- 전일 대비 순위 변동 (상승이면 양수)
    created_at               TIMESTAMP,
    updated_at               TIMESTAMP,
    CONSTRAINT uk_kingdom_snapshot_date UNIQUE (kingdom_id, snapshot_date)
);