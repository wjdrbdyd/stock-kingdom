# stock-kingdom

주식 왕국 전쟁 서비스 - MVP 1단계 뼈대

## 구조

- `user` - 사용자
- `stock` - 종목(순수 시장 데이터)
- `holding` - 사용자 보유 내역
- `kingdom` - 왕국, 전투력/랭킹 스냅샷 (게임 도메인)
- `common` - 공통(BaseTimeEntity, JPA Auditing 설정)

## 아직 안 한 것 (직접 채워야 할 부분)

1. `KingdomPowerCalculator` - 전투력 정규화 방식 미확정 (요구사항정의서 참고)
2. QueryDSL Custom Repository (예: 종목별 보유 합계 집계)
3. 일 배치 스케줄러 (`@Scheduled`) - 전투력 계산 + 순위 산출
4. 목업 데이터 시드 (종목 시가총액 상위 20개)
5. Controller/API 계층 (아직 없음)

## 실행 전 준비

- PostgreSQL 로컬 실행, `stock_kingdom` 데이터베이스 생성
- `application.yml`의 DB 계정 정보 본인 환경에 맞게 수정
- 테이블은 Flyway가 자동 생성함 (`src/main/resources/db/migration/V1__init.sql`)
  - 애플리케이션 최초 실행 시 자동으로 마이그레이션 적용됨
  - 이후 엔티티 구조가 바뀌면 `V2__xxx.sql` 식으로 새 마이그레이션 파일 추가 (기존 V1 파일은 절대 수정하지 말 것 - Flyway가 체크섬으로 변경 감지함)
