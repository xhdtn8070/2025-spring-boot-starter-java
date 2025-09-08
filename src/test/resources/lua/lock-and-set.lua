-- KEYS[1]: 락을 걸 키
-- 이 스크립트는 ARGV를 통해 값을 받지 않습니다.
if redis.call("exists", KEYS[1]) == 0 then
  redis.call("set", KEYS[1], "1", "EX", "3") -- "1"이라는 고정된 플레이스홀더 값을 3초 TTL로 설정
  return "OK"
else
  return "LOCKED"
end