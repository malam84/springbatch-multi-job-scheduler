package com.springbatch.multi.job.scheduler.service;

import java.io.IOException;
import java.util.List;

import com.springbatch.multi.job.scheduler.model.Task2;

/**
 * 
 * @author malam84
 *
 */

public interface Task2Service {
	
	public void inserRecordTask2(List<Task2> task) throws IOException;

}
