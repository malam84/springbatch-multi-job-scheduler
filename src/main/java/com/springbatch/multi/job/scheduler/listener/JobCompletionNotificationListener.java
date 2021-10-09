package com.springbatch.multi.job.scheduler.listener;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import com.springbatch.multi.job.scheduler.constant.Constant;

/**
 * 
 * @author malam84
 *
 */

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);
	
	@Override
	public void afterJob(JobExecution jobExecution) {
	
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			
			log.info("!!! JOB FINISHED! Time to remove files");
             
			Resource[] resources = null;
			ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		    String dirPath = Constant.DIR_PATH  + File.separator + Constant.CSV_INPUT_FOLDER;	
			try {
				  resources = resolver.getResources("file:" + dirPath + File.separator +"/*.csv");
				  for(Resource r: resources) {
						File file = r.getFile();
					    boolean deleted = file.delete();
					    if (!deleted) {
					         throw new UnexpectedJobExecutionException("Could not delete file " + file.getPath());
					    }
				   }
			} catch (IOException e) {
			    e.printStackTrace();
			}	
		}
	}
}