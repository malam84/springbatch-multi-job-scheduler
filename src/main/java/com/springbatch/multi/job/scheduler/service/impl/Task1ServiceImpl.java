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

import com.springbatch.multi.job.scheduler.model.Task1;
import com.springbatch.multi.job.scheduler.service.Task1Service;

/**
 * 
 * @author malam84
 *
 */

@Service
public class Task1ServiceImpl implements Task1Service {

	private JdbcTemplate jdbcTemplate;
	private static final Logger log = LoggerFactory.getLogger(Task1ServiceImpl.class);

	@Value("${quary.insert.task1}")
	private String queryInsertTask1;

	@Autowired
	public Task1ServiceImpl(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public void insertRecordTask1(List<Task1> task1List) throws IOException {

		log.info("Method !!! insertTaskRecord ()");

		jdbcTemplate.batchUpdate(queryInsertTask1, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement prepStmt, int i) throws SQLException {
				Task1 task1 = task1List.get(i);
				prepStmt.setString(1, task1.getTaskId());
				prepStmt.setString(2, task1.getTaskName());
			}

			@Override
			public int getBatchSize() {
				return task1List.size();
			}
		});
		log.info("Task records Insert!");
	}
}
