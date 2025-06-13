package com.efada.schduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Component
public class SchulderTasks {


    @Scheduled(cron = "${SchulderTasks.runTask.cornExpression}")
    @SchedulerLock(name = "SchulderTasks.runTask",
    	lockAtLeastFor = "${shcedularLock.lockAtLeastFor}",
    	lockAtMostFor = "${shcedularLock.lockAtMostFor}")
    public void runTask() {
        System.out.println("Running scheduled task at " + java.time.LocalDateTime.now());
        // Your logic here
    }
    
    
}
