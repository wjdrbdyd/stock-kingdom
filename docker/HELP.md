docker compose up -d

docker compose ps
docker compose exec postgres psql -U jpamgr -d jpamgr -c "SELECT 1;"

docker compose ps

docker compose stop      # 중지 (데이터 유지)
docker compose start     # 다시 시작
docker compose down      # 컨테이너 삭제 (volume은 유지됨, 데이터 안전)
docker compose down -v   # volume까지 삭제 (데이터 삭제됨, 주의!)