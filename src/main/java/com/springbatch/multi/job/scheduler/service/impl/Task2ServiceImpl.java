package com.springbatch.multi.job.scheduler.service.impl;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.springbatch.multi.job.scheduler.model.Task2;
import com.springbatch.multi.job.scheduler.service.Task2Service;

/**
 * 
 * @author malam84
 *
 */

@Service
public class Task2ServiceImpl implements Task2Service {

	private JdbcTemplate jdbcTemplate;
	private static final Logger log = LoggerFactory.getLogger(Task1ServiceImpl.class);

	@Value("${quary.insert.task2}")
	private String queryInsertTask2;

	@Autowired
	public Task2ServiceImpl(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public void inserRecordTask2(List<Task2> task2List) throws IOException {
		log.info("Method !!! insertTaskRecord ()");
		jdbcTemplate.batchUpdate(queryInsertTask2, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement prepStmt, int i) throws SQLException {
				Task2 task2 = task2List.get(i);
				prepStmt.setString(1, task2.getTaskId());
				prepStmt.setString(2, task2.getTaskName());
			}

			@Override
			public int getBatchSize() {
				return task2List.size();
			}
		});
		log.info("Task records Insert!");
	}
}