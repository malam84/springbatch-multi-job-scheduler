package com.springbatch.multi.job.scheduler.config;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.springbatch.multi.job.scheduler.constant.Constant;
import com.springbatch.multi.job.scheduler.listener.JobCompletionNotificationListener;
import com.springbatch.multi.job.scheduler.model.Task1;
import com.springbatch.multi.job.scheduler.model.Task2;

/**
 * 
 * @author malam84
 *
 */

@Configuration
@EnableBatchProcessing
public class BatchJobConfig {

	private static final Logger log = LoggerFactory.getLogger(BatchJobConfig.class);

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	private FlatFileItemReader<Task1> csvFlatFileItemReader;

	@Bean("partitioner")
	@StepScope
	public Partitioner partitioner() {
		log.info("In Partitioner");

		MultiResourcePartitioner partitioner = new MultiResourcePartitioner();
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = null;

		String dirPath = Constant.DIR_PATH + File.separator + Constant.CSV_INPUT_FOLDER;

		try {
			resources = resolver.getResources("file:" + dirPath + File.separator + "/*.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
		partitioner.setResources(resources);
		partitioner.partition(10);
		return partitioner;
	}

	@Bean
	public Job runCVSReaderJob(JobCompletionNotificationListener listener) {
		return jobBuilderFactory.get("runCVSReaderJob").incrementer(new RunIdIncrementer()).listener(listener)
				.flow(masterStep()).end().build();
	}

	@Bean

	public Step step1() {
		return stepBuilderFactory.get("step1").<Task1, Task1>chunk(10)
				/*
				 * .processor(processor()) we can implement processor if we want to manipulate
				 * data such as upper case lower case to save in db
				 */
				.writer(writer()).reader(csvFlatFileItemReader).build();
	}

	@Bean
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setMaxPoolSize(10);
		taskExecutor.setCorePoolSize(10);
		taskExecutor.setQueueCapacity(10);
		taskExecutor.afterPropertiesSet();
		return taskExecutor;
	}

	@Bean
	@Qualifier("masterStep")
	public Step masterStep() {
		return stepBuilderFactory.get("masterStep").partitioner("step1", partitioner()).step(step1())
				.taskExecutor(taskExecutor()).build();
	}

	@SuppressWarnings("unchecked")
	@Bean
	@StepScope
	@Qualifier("csvFlatFileItemReader")
	@DependsOn("partitioner")
	public <T> FlatFileItemReader<T> CsvFlatFileItemReader(
			@Value("#{stepExecutionContext['fileName']}") String filename) throws MalformedURLException {

		log.info("File name >>>>  " + filename);

		String fileNameWithOutExt = FilenameUtils.getBaseName(filename);

		FlatFileItemReader<T> reader = new FlatFileItemReader<T>();
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		reader.setLinesToSkip(1);

		if (fileNameWithOutExt.equalsIgnoreCase(Constant.CSV_FILENAME_FOR_TASK1)) {
			String[] tokens = { "TASK_ID", "TASK_NAME" }; //Please define csv column header
			tokenizer.setNames(tokens);

		} else if (fileNameWithOutExt.equalsIgnoreCase(Constant.CSV_FILENAME_FOR_TASK2)) {
			String[] tokens = { "TASK_ID", "TASK_NAME" }; //Please define csv column header
			tokenizer.setNames(tokens);

		} else {
			log.info("Unknow file name >>>>  " + filename);
		}

		DefaultLineMapper<T> lineMapper = new DefaultLineMapper<>();
		lineMapper.setLineTokenizer(tokenizer);
		lineMapper.setFieldSetMapper(new BeanWrapperFieldSetMapper<T>() {
			{
			   if (fileNameWithOutExt.equalsIgnoreCase(Constant.CSV_FILENAME_FOR_TASK1)) {
					setTargetType((Class<? extends T>) Task1.class);

			   } else if (fileNameWithOutExt.equalsIgnoreCase(Constant.CSV_FILENAME_FOR_TASK2)) {
					setTargetType((Class<? extends T>) Task2.class);
			   }
		   }
		});

		reader.setLineMapper(lineMapper);
		reader.setResource(new UrlResource(filename));
		return reader;
	}

	@Bean
	public <T> MySQLDBItemWriter<T> writer() {
		return new MySQLDBItemWriter<T>();
	}
}