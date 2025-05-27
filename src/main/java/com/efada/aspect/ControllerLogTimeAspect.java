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
@ConditionalOnProperty(name = "isControllerAspectEnabled", havingValue = "true", matchIfMissing = false)
public class ControllerLogTimeAspect {

	@Autowired
	private HttpServletRequest request;
	
	private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//	@Pointcut(value = "execution(* com.efada.controller.AppUserController.*(..))")
//	@Pointcut("execution(* com.efada.controller.*.*(..))")
//	public void forControllerLog () {}
//	
//    @Pointcut("within(com.efada.base.BaseController+) && execution(* *(..))")
//	public void baseControllerMethods() {}
	
	@Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMappingMethods() {}

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void getMappingMethods() {}

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void putMappingMethods() {}

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public void patchMappingMethods() {}

    @Pointcut("@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void deleteMappingMethods() {}

    // Catch-all for custom @RequestMapping (with method=GET/POST/etc.)
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void requestMappingMethods() {}
    
    @Pointcut("postMappingMethods() || getMappingMethods() || putMappingMethods() || " +
            "patchMappingMethods() || deleteMappingMethods() || requestMappingMethods()")
    public void allMappingMethods() {}

    // Add exclusion for error controller
    @Pointcut("within(org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController)")
    public void excludeErrorController() {}
    
    @Around("allMappingMethods() && !excludeErrorController()")
	public Object logControllersTime(ProceedingJoinPoint  joinPoint) throws Throwable {
		long startTime = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder("KPI of API:");
		
		sb.append("[ Path: ").append(request.getServletPath()).append("]\tfor: ").append(joinPoint.getSignature())
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
