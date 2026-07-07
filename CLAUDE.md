# CLAUDE.md

## 프로젝트 개요

주식 왕국 전쟁 서비스 — 같은 종목을 보유한 주주들이 "왕국" 단위로 묶여
경쟁하는 게이미피케이션 서비스. 개인 포트폴리오 프로젝트 (구직용).

목표: 개인 투자자들의 결속력을 높여 대주주와 같은 효과를 얻기 위함.

## 배경 / 왜 이 프로젝트인가

- 기존에 MyBatis→JPA 레거시 마이그레이션 프로젝트를 진행했으나, 데이터 없음 +
  권한/조건 로직 하드코딩으로 실질적으로 막혀 방향 전환
- 이 프로젝트의 목적: 단순 CRUD 이상의 "요구사항을 직접 정의하고 설계 판단하는
  경험"을 증명하는 것. 기능 개수보다 판단의 깊이가 중요.
- **원칙**: 설계/의사결정은 직접 하고, AI는 검증 대화 상대 + 보일러플레이트
  구현에만 활용. 코드를 그대로 받아쓰지 않고 이해하고 작성할 것.

## MVP 1단계 스코프

### 포함
- 종목 = 왕국 1:1 매핑
- 사용자 보유 종목/수량 수동 입력 (증권사 API 연동 아님, 목업 데이터로 시작)
- 시가총액 기반 왕국 전투력 계산 + 랭킹 조회

### 제외 (2단계 이후)
- 섹터 통합전, 연합 시스템
- 실시간 대결/보상 지급
- 증권사 API 연동 (OAuth)
- RAG 기반 뉴스 요약/질의응답 (4단계)

## 도메인 모델 (핵심 설계 결정)

- `User` — 최소 필드(nickname, email)로 시작, 인증 붙일 때 확장 예정
- `Stock` — 순수 시장 데이터(ticker, currentPrice)만 담당
- `UserStockHolding` — User-Stock 다대다를 푸는 중간 엔티티
- `Kingdom` — 게임 도메인 개념. **Stock에서 의도적으로 분리**했음
  (이유: Stock은 시세만 다루고, Kingdom은 게임 로직을 다뤄야 관심사가
  안 섞임. 추후 섹터 통합전 도입 시 Kingdom이 여러 Stock을 포함할 수
  있도록 확장 여지 확보)
- `KingdomPowerSnapshot` — 왕국의 일자별 전투력 + 순위(rank, rankChange)를
  **하나의 테이블로 병합**. 처음엔 KingdomRanking을 별도 엔티티로 분리
  했었으나, 순위 계산에 별도 로직이 없고(단순 정렬) 배치 처리 순서
  의존성만 있어 분리 실익이 없다고 판단해 병합함 (YAGNI).

## 전투력 계산 (확정)

- 원 지표: `참여 유저 수 × 유저들의 보유 주식 총합 수 × 현재 주가`
- **정규화 방식(확정)**: `power = 참여율 × log(참여자 수 + 1)`
  - 참여율 = `(유저 보유 주식 총합 × 현재가) / 종목 시가총액`
  - 이유: 회사 규모(시가총액)를 분모로 제거해 대형주가 항상 이기는 문제를 해결하고,
    log(참여자 수)로 소수 유저 몰빵 왜곡을 방지 — "결속력" 목표와 부합
  - **Stock 엔티티에 marketCap 필드 재추가 필요** (이전에 제거했다가 이 계산 때문에
    다시 필요해짐 — Stock.java, V1__init.sql 둘 다 수정 필요)
- 실시간 반영 vs 배치(일 1회): 아직 최종 결정 안 함. 현재는 배치로 가정하고
  구현 중이나, 실시간 필요 여부는 보류 상태
- `KingdomPowerCalculator` 클래스에 `UnsupportedOperationException`을 던지는
  스텁만 있음 — 위 공식으로 구현 필요 (다음 작업)

## 기술 스택

- Spring Boot, Spring Data JPA, QueryDSL (`io.github.openfeign.querydsl`)
- PostgreSQL
- Flyway (스키마 마이그레이션, `ddl-auto: validate`로 설정)
- 배치: Spring `@Scheduled` 예정 (아직 미구현)

## 진행 상태 체크리스트

- [x] 요구사항 정의서 작성 (MVP 스코프 확정)
- [x] 도메인 모델 설계 (Kingdom 분리, Snapshot/Ranking 병합 결정)
- [x] Spring 프로젝트 뼈대 생성 (엔티티, Repository, Flyway 마이그레이션)
- [x] 전투력 정규화 방식 결정 (참여율 × log(참여자 수+1) 확정)
- [x] Stock 엔티티에 marketCap 필드 재추가 (Stock.java, V1__init.sql)
- [ ] Git 저장소 초기화 (git init + 첫 커밋) — 아직 안 함, 다음에 할 것
- [ ] `KingdomPowerCalculator` 구현
- [ ] 종목 목업 데이터 시드 (시가총액 상위 20개 정도)
- [ ] QueryDSL Custom Repository (종목별 보유 합계 집계 등)
- [ ] 배치 스케줄러 구현
- [ ] Controller/API 계층 구현
- [ ] 설계 결정 문서화 (별도 `docs/decisions/` 에 ADR 형식으로 남기기 — 특히
      Kingdom/Stock 분리, Snapshot/Ranking 병합 건은 좋은 면접 소재이므로 꼭 기록)

## 코딩 스타일 / 주의사항

- Lombok 사용 (`@Getter`, `@Builder`, `@NoArgsConstructor(access = PROTECTED)`)
- 엔티티 생성자는 `private` + `@Builder`로, 정적 팩토리 대신 빌더 패턴 사용 중
- Flyway 마이그레이션 파일은 한 번 적용되면 절대 수정 금지 — 변경 필요 시
  새 버전 파일(`V2__...sql`) 추가
- `open-in-view: false`로 설정되어 있음 — 트랜잭션 경계 명시적으로 관리할 것
