# 아키텍처 & 설계 결정 개요

이 문서는 주식왕국 서비스의 도메인 모델과 핵심 설계 판단을 정리합니다.
개별 결정의 배경/대안 비교는 [ADR 목록](decisions/)에 상세히 남겨두었습니다.

## 도메인 모델

- `User` — 최소 필드(nickname, email)로 시작. 인증 붙일 때 확장 예정.
- `Stock` — 순수 시장 데이터(ticker, currentPrice, marketCap)만 담당.
- `UserStockHolding` — User-Stock 다대다를 푸는 중간 엔티티. (user_id, stock_id) unique.
- `Kingdom` — 게임 도메인 개념. Stock에서 의도적으로 분리 (ADR-001).
- `KingdomPowerSnapshot` — 왕국의 일자별 전투력 + 순위(rank, rankChange)를
  하나의 테이블로 병합 (ADR-002).

## 핵심 설계 결정

### 1. Stock과 Kingdom을 분리한 이유
`Stock`은 시세 데이터(외부 API/배치로 갱신), `Kingdom`은 게임 도메인 로직을 담당한다.
지금은 1:1이지만, 추후 섹터 통합전에서 Kingdom이 여러 Stock을 포함하는 구조로
확장될 수 있어 의도적으로 분리했다.
→ [ADR-001 상세보기](decisions/ADR-001-kingdom-stock-separation.md)

### 2. KingdomRanking 테이블을 만들지 않은 이유
순위 계산은 전투력 기준 단순 정렬이라 별도 로직이 없다. 배치 처리 순서 의존성만
존재해 테이블 분리의 실익이 없다고 판단, `rank`/`rank_change` 컬럼을
`KingdomPowerSnapshot`에 병합했다 (YAGNI).
→ [ADR-002 상세보기](decisions/ADR-002-snapshot-ranking-merge.md)

### 3. 전투력 공식 설계
```
power = 참여율 × log(참여자수 + 1) × 1,000,000
참여율 = (총보유주식수 × 현재가) / 시가총액
```
시총을 분모로 두어 대형주/소형주가 동등한 비율로 경쟁하게 하고,
`log(참여자수 + 1)`로 소수 유저 몰빵 왜곡을 방지한다.

| 후보 공식 | 기각 이유 |
|---|---|
| 참여자수 × 총보유량 × 현재가 | 대형주가 구조적으로 유리 |
| log(총보유금액) × log(참여자수) | 총보유금액 절대값이 점수를 지배, 소형주 역전 현상 |
| 참여율 × log(참여자수 + 1) × 승수 | **채택** — 시총 정규화로 공정성 확보 |

→ [ADR-003 상세보기](decisions/ADR-003-power-formula.md)

### 4. 배치 재실행 전략
당일 스냅샷이 이미 있으면 삭제 후 재계산 — 배치를 여러 번 돌려도 왕국당
스냅샷이 1건으로 유지되도록 함(멱등성). `KingdomPowerBatchServiceTest`에서
재실행 시나리오를 검증한다.
→ [ADR-005 상세보기](decisions/ADR-005-batch-rerun-strategy.md)

## 배치 스케줄

`KingdomPowerBatchScheduler`는 `batch.kingdom-power.cron` 프로퍼티로 주기를
프로필별로 분리한다.

| 프로필 | 주기 | 설정 파일 |
|---|---|---|
| 상용(기본) | 매일 자정 1회 | `application.yml` |
| `dev` | 2초마다 (빠른 동작 확인용) | `application-dev.yml` |

## 시드 데이터와 시퀀스

`V2__seed_mock_data.sql`은 종목/왕국/유저 등을 명시적 PK로 삽입한다. PostgreSQL의
`BIGSERIAL` 시퀀스는 명시적 insert로는 자동 갱신되지 않으므로, 시드 이후 신규
insert 시 시드 데이터와 PK가 충돌할 수 있다. `V3__fix_sequences_after_seed.sql`에서
`setval`로 각 테이블 시퀀스를 시드 데이터의 max(id) 기준으로 재설정해 해결했다.
(Flyway 적용 파일은 수정하지 않는다는 원칙에 따라 V2를 고치는 대신 V3를 추가함.)

## 알려진 한계 (Prototype/MVP 단계)

- 인증/인가 없음 — `userId`를 요청 바디로 직접 받음
- 보유 종목 등록은 목업 데이터 기반 수동 입력 (증권사 API 연동 아님)
- 실시간 반영 vs 배치(일 1회) 여부 최종 미확정, 현재는 배치로 가정
- 섹터 통합전, 연합 시스템, RAG 기반 뉴스 질의응답은 2단계 이후 스코프