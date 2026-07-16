# Changelog

## 2026-07-16

### Fixed
- `KingdomPowerBatchService.calculateRanks()`: 전일 순위 맵의 키를 `Kingdom`
  엔티티(equals/hashCode 미구현으로 조회 시 항상 miss)에서 `Long`(kingdomId)로
  변경 — 이전에는 `rankChange`가 항상 0으로 계산되던 버그.
- 보유 종목 등록(`HoldingService.saveHoldings`)
  - 수량이 0 이하인 경우 `IllegalArgumentException`
  - 동일 유저·종목 재등록 시 unique 제약 위반 대신 기존 레코드 수량을 갱신(upsert)
  - 클래스에 `@Transactional` 명시
  - 존재하지 않는 유저/종목은 `EntityNotFoundException`으로 명확히 실패
    (지연 프록시(`getReferenceById`) 대신 `findById().orElseThrow` 사용)
  - `GlobalExceptionHandler` 추가: `EntityNotFoundException` → 404,
    `IllegalArgumentException` → 400

### Changed
- 배치 스케줄 주기를 `batch.kingdom-power.cron` 프로퍼티로 외부화
  - 상용(기본, `application.yml`): 매일 자정 1회
  - 개발(`application-dev.yml`, `dev` 프로필): 2초마다

### Added
- `KingdomPowerCalculatorTest`: 전투력 계산 공식 및 예외 케이스에 대한 assertion 기반 유닛 테스트
- `KingdomPowerBatchServiceTest`: 순위 역전 시 `rankChange` 부호 검증,
  배치 재실행 시 스냅샷 멱등성(왕국당 1건 유지) 검증
- `UserStockHoldingQueryRepositoryTest`: 기존에 assertion 없이 결과만 출력하던
  테스트를 실제 검증(보유수량 합계, 참여자수, kingdomId 매핑)이 있는 테스트로 재작성
- `docs/architecture.md`: 지원자 대상 설계 개요 문서 (기존 CLAUDE.md는
  AI 작업 컨텍스트 전용으로 유지, README에서는 이 문서로 링크)
- README에 Prototype/MVP 상태 배너 및 알려진 한계 안내 추가