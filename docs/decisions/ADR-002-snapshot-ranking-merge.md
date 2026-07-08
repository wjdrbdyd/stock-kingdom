# ADR-002: KingdomPowerSnapshot에 Ranking 병합 (YAGNI)

## 상태
확정

## 배경
초기 설계에서 "전투력 계산"과 "순위 결정"을 서로 다른 책임으로 보고
`KingdomPowerSnapshot`과 `KingdomRanking`을 별도 테이블로 분리하는 방안을 검토했다.

## 결정
`rank`, `rank_change` 컬럼을 `KingdomPowerSnapshot`에 병합한다.
`KingdomRanking` 테이블은 만들지 않는다.

## 이유
분리의 실익이 없다.

- 순위 계산은 power 기준 단순 정렬이라 별도 로직이 없다.
- 배치 처리 순서 의존성(스냅샷 전체 계산 완료 후 순위 산출)만 존재한다.
- 이 의존성은 테이블 분리가 아닌 배치 코드 내 처리 순서로 충분히 해결 가능하다.
- 테이블을 분리하면 JOIN 비용만 늘고 얻는 것이 없다.

## 트레이드오프
- 추후 순위 계산 로직이 복잡해지면(예: 섹터별 별도 순위, 기간별 순위) 다시 분리가
  필요할 수 있다. 그때 `V(N)__split_ranking.sql`로 마이그레이션하면 된다.
- 현재는 YAGNI 원칙을 따른다.
