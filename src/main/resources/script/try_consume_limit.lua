-- KEYS[1] : limit key
-- ARGV[1] : amount
-- ARGV[2] : limit
-- ARGV[3] : ttlSeconds

local current = redis.call("GET", KEYS[1])

if not current then
    current = 0
else
    current = tonumber(current)
end

local amount = tonumber(ARGV[1])
local limit = tonumber(ARGV[2])
local ttl = tonumber(ARGV[3])

-- 한도 초과
if current + amount > limit then
    return 0
end

-- 증가
redis.call("INCRBY", KEYS[1], amount)

-- 최초 생성 시 TTL 설정
if current == 0 then
    redis.call("EXPIRE", KEYS[1], ttl)
end

return 1