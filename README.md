# 주식왕국 전쟁 서비스

같은 종목을 보유한 주주들이 **왕국** 단위로 묶여 경쟁하는 게이미피케이션 서비스.

개인 투자자들이 왕국 단위로 결속해 대주주에 준하는 영향력을 갖는 것을 목표로 한다.

---

## 프로젝트 배경

기존 HTS/MTS는 단순 거래 기능에 머물러 있다. 같은 종목 주주들이 자신이 얼마나
강한 왕국에 속해 있는지 확인하고, 더 많은 참여자를 모아 왕국을 키워나가는 경험을
통해 개인 투자자의 결속력을 높이는 것이 이 서비스의 목적이다.

단순 CRUD를 넘어 **요구사항을 직접 정의하고 설계 판단을 내리는 경험**을 쌓기 위해
시작한 개인 포트폴리오 프로젝트다.

---

## 핵심 설계 결정

### 1. Stock과 Kingdom을 분리한 이유
`Stock`은 시세 데이터(외부 API/배치로 갱신), `Kingdom`은 게임 도메인 로직을 담당한다.
지금은 1:1이지만, 추후 섹터 통합전에서 Kingdom이 여러 Stock을 포함하는 구조로 확장될 수 있어 의도적으로 분리했다.
→ [ADR-001 상세보기](docs/decisions/ADR-001-kingdom-stock-separation.md)

### 2. KingdomRanking 테이블을 만들지 않은 이유
순위 계산은 전투력 기준 단순 정렬이라 별도 로직이 없다. 배치 처리 순서 의존성만 존재해
테이블 분리의 실익이 없다고 판단, `rank`/`rank_change` 컬럼을 `KingdomPowerSnapshot`에 병합했다 (YAGNI).
→ [ADR-002 상세보기](docs/decisions/ADR-002-snapshot-ranking-merge.md)

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

→ [ADR-003 상세보기](docs/decisions/ADR-003-power-formula.md)

---

## 기술 스택

| 분류 | 기술 |
|---|---|
| Backend | Spring Boot, Spring Data JPA, QueryDSL |
| DB | PostgreSQL |
| 마이그레이션 | Flyway (`ddl-auto: validate`) |
| 배치 | Spring `@Scheduled` (일 1회 전투력 재계산) |

---

## 패키지 구조

```
src/main/java/com/stockkingdom
├── user        # 사용자
├── stock       # 종목 (순수 시장 데이터)
├── holding     # 사용자 보유 내역
├── kingdom     # 왕국, 전투력/랭킹 스냅샷 (게임 도메인)
└── common      # BaseTimeEntity, JPA Auditing
```

---

## API 명세

### 보유 종목 등록
```
POST /holdings
```
```json
{
  "userId": 1,
  "stockId": 3,
  "quantity": 100
}
```

### 왕국 랭킹 조회
```
GET /kingdoms/ranking
```
```json
[
  {
    "kingdomId": 1,
    "kingdomName": "삼성전자 왕국",
    "ticker": "005930",
    "power": 138.3000,
    "rank": 1,
    "rankChange": 0
  }
]
```
> 당일 배치 미실행 시 빈 배열 반환 (프론트에서 "집계 전" 처리)

### 왕국 상세 조회
```
GET /kingdoms/{kingdomId}
```
```json
{
  "kingdomId": 1,
  "kingdomName": "삼성전자 왕국",
  "ticker": "005930",
  "participantCount": 5,
  "totalHoldingQuantity": 460,
  "power": 138.3000,
  "rank": 1
}
```

---

## 실행 방법

**사전 준비**
- PostgreSQL 실행 후 `stock_kingdom` 데이터베이스 생성
- `application.yml` DB 계정 정보 수정

**실행**
```bash
./gradlew bootRun
```
테이블은 Flyway가 최초 실행 시 자동 생성한다.

**목업 데이터**
`src/main/resources/db/migration/V2__seed.sql` — 시가총액 상위 종목 및 샘플 보유 데이터 포함

**전투력 배치 수동 실행**
```
POST /admin/batch/power
```
(배치는 매일 자정 자동 실행, 위 엔드포인트로 수동 트리거 가능)

---

## 설계 결정 문서 (ADR)

| ADR | 제목 |
|---|---|
| [ADR-001](docs/decisions/ADR-001-kingdom-stock-separation.md) | Kingdom과 Stock 분리 |
| [ADR-002](docs/decisions/ADR-002-snapshot-ranking-merge.md) | KingdomPowerSnapshot에 Ranking 병합 |
| [ADR-003](docs/decisions/ADR-003-power-formula.md) | 전투력 공식 설계 |
| [ADR-004](docs/decisions/ADR-004-cohesion-distribution-deferred.md) | 결속력 분산도 MVP 제외 |
| [ADR-005](docs/decisions/ADR-005-batch-rerun-strategy.md) | 배치 재실행 전략 |
