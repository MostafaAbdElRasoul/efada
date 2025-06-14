package com.efada.utils;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.efada.exception.EfadaCustomException;
import com.efada.redis.RedisHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OTPUtils {
	@Autowired
	private RedisHandler redisHandler;


	@Value("${otp.randomNumber}")
	private Integer randomNumber;
	@Value("${otp.timeout}")
	private Long timeout;
	
	@Value("${application.environment:test}")
	private String applicationEnv;

	public Integer generateOTP(String key) {
		Random random = new Random();
		Integer otp = random.nextInt(randomNumber);
		
		if(applicationEnv.equals("test"))
		 otp = 12345;
		
		log.info("otp : " + otp);

		redisHandler.setWithExpiryInMin(key, otp, timeout);
		return otp;
	}

	public Boolean verifyOTP(String key, Integer code) {
        Object value = redisHandler.get(key);
        log.info("redis stored otp : " + value);
        Integer storedCode = null;
        if(value!=null && !value.equals("null"))
		 storedCode= (Integer) redisHandler.get(key);
		if (storedCode != null && storedCode.equals(code)) {
			redisHandler.delete(key);
			return true;
		} else {
			throw new EfadaCustomException("INVALID_OR_EXPIRED_OTP");	
			
		}
	}
}
