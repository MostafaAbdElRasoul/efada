package com.efada.aspect;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@ConditionalOnProperty(name = "isRepositoryAspectEnabled", havingValue = "true", matchIfMissing = false)
public class RepositoryLogTimeAspect {


	@Autowired
	private HttpServletRequest request;
	
	private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Pointcut(value = "execution(* com.efada.repository.*.*(..))")
	public void forRepositoryLog() {}
	

	@Around(value = "forRepositoryLog()")
	public Object logRepositoriesTime(ProceedingJoinPoint  joinPoint) throws Throwable {
		long startTime = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder("KPI of Repo :");
		
		sb.append("\tfor: ").append(joinPoint.getSignature())
				.append("\twithArgs: ").append("(").append(StringUtils.join(joinPoint.getArgs(), ",")).append(")");
		sb.append("\tstart time: ");
		sb.append(DATE_FORMATTER.format(startTime));
		
		Object returnValue = joinPoint.proceed();
		
		long endTime = System.currentTimeMillis();
		sb.append("\tend time: ");
		sb.append(DATE_FORMATTER.format(endTime));
		sb.append("\ttook: ");
		log.info(sb.append(endTime - startTime).append(" ms.").toString());
		
		return returnValue;
	}
}
