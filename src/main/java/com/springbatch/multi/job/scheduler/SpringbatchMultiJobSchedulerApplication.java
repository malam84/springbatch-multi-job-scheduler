package com.springbatch.multi.job.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 
 * @author malam84
 *
 */
@SpringBootApplication
@EnableScheduling
public class SpringbatchMultiJobSchedulerApplication {
	
    @Autowired
    private Job job;
    
    @Autowired
    private JobLauncher jobLauncher;
    
    private static final Logger log = LoggerFactory.getLogger(SpringbatchMultiJobSchedulerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringbatchMultiJobSchedulerApplication.class, args);
	}
	
	@Scheduled(cron = "*/60 * * * * ?") //Configure to run every 60 second
    public void perform() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
        log.debug(" Running job id detail >>>>>>>>>>>>>>>>>>>>>> " +params);
        jobLauncher.run(job, params);
    }
}