# ADR-005: 배치 재실행 전략 (당일 스냅샷 삭제 후 재계산)

## 상태
확정

## 배경
`kingdom_power_snapshot` 테이블에 `(kingdom_id, snapshot_date)` 유니크 제약이 있다.
배치를 당일 두 번 이상 실행하면 유니크 제약 위반으로 실패한다.

## 결정
배치 시작 시 오늘자 스냅샷을 `deleteBySnapshotDate(today)`로 삭제한 뒤 재계산한다.

```java
kingdomPowerSnapshotRepository.deleteBySnapshotDate(LocalDate.now());
kingdomPowerSnapshotRepository.flush(); // 삭제 선반영 후 재계산
```

## 이유
- 배치 재실행은 개발 중이나 장애 복구 시 필요하다.
- Upsert(`ON CONFLICT DO UPDATE`) 대신 삭제 후 재삽입을 선택한 이유:
  JPA 환경에서 Upsert는 네이티브 쿼리가 필요해 유지보수 비용이 올라간다.
  삭제 후 재삽입은 코드가 단순하고 의도가 명확하다.
- 하루에 한 번 배치라 동시성 이슈 가능성이 낮다.

## 트레이드오프
- 삭제와 재삽입 사이 짧은 순간 데이터가 없는 공백이 생긴다.
- 현재 실시간 조회 요건이 없고(일 배치), 해당 공백이 문제가 되는
  트래픽 수준이 아니므로 MVP에서는 허용한다.
- 실시간 조회 요건이 생기면 트랜잭션 범위 내 처리 또는 Upsert로 전환한다.
