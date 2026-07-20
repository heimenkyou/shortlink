-- 先对用户访问次数自增，返回当前窗口内的访问总次数
local current = redis.call('INCR', KEYS[1])

-- 只有第一次访问时才设置过期时间，避免后续请求不断刷新窗口
if current == 1 then
    redis.call('EXPIRE', KEYS[1], tonumber(ARGV[1]))
end

-- 返回当前访问次数，Java 侧据此判断是否超限
return current
