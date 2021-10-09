package com.springbatch.multi.job.scheduler.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.springbatch.multi.job.scheduler.model.Task1;
import com.springbatch.multi.job.scheduler.model.Task2;
import com.springbatch.multi.job.scheduler.service.impl.Task1ServiceImpl;
import com.springbatch.multi.job.scheduler.service.impl.Task2ServiceImpl;

/**
 * 
 * @author malam84
 *
 */

@Component
public class MySQLDBItemWriter<T> implements ItemWriter<T> {

	private static final Logger log = LoggerFactory.getLogger(MySQLDBItemWriter.class);

	@Autowired
	private Task1ServiceImpl task1ServiceImpl;

	@Autowired
	private Task2ServiceImpl task2ServiceImpl;

	@Override
	@SuppressWarnings("unchecked")
	public void write(List<? extends T> tasklist) throws Exception {

		if (tasklist.get(0) instanceof Task1) {
			List<Task1> task1List = (List<Task1>) tasklist;
			task1ServiceImpl.insertRecordTask1(task1List);
		
		} else if (tasklist.get(0) instanceof Task2) {
			List<Task2> task2List = (List<Task2>) tasklist;
			task2ServiceImpl.inserRecordTask2(task2List);
		
		} else {
			log.info("Unknow generic object");
		}
	}

}