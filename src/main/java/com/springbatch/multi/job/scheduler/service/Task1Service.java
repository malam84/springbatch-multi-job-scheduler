package com.springbatch.multi.job.scheduler.service;

import java.io.IOException;
import java.util.List;

import com.springbatch.multi.job.scheduler.model.Task1;

/**
 * 
 * @author malam84
 *
 */

public interface Task1Service {
	
	public void insertRecordTask1(List<Task1> taskList) throws IOException;

}
