package fa.training.cms.job;

import fa.training.cms.entity.Post;
import fa.training.cms.entity.User;
import fa.training.cms.service.enums.Status;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.batch.core.Job;
import org.springframework.transaction.PlatformTransactionManager;

import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;

import java.net.BindException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

@Configuration
public class PostSeedingJobConfig {
    @Autowired
    private final JobRepository jobRepository;
    @Autowired
    private final PlatformTransactionManager transactionManager;
    @Autowired
    private final JobFolderCreatorTasklet createFolderTasklet;
    @Autowired
    private final GenerateMockPostDataTasklet generateMockDataTasklet;
    @Autowired
    private final PostJsonReader postJsonReader;
    @Autowired
    private final PostContentLengthFilter postContentLengthFilter;
    @Autowired
    private final RemoveMockDataFileTasklet removeMockDataFileTasklet;
    @Autowired
    private final WriteReportTasklet writeReportTasklet;

    public PostSeedingJobConfig(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            JobFolderCreatorTasklet createFolderTasklet,
            GenerateMockPostDataTasklet generateMockDataTasklet,
            PostJsonReader postJsonReader, PostContentLengthFilter postContentLengthFilter,
            RemoveMockDataFileTasklet removeMockDataFileTasklet,
            WriteReportTasklet writeReportTasklet
    ) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.createFolderTasklet = createFolderTasklet;
        this.generateMockDataTasklet = generateMockDataTasklet;
        this.postJsonReader = postJsonReader;
        this.postContentLengthFilter = postContentLengthFilter;
        this.removeMockDataFileTasklet = removeMockDataFileTasklet;
        this.writeReportTasklet = writeReportTasklet;
    }
    // Step 1: Create storage folder for job files
    @Bean("folderCreationStep")
    public Step folderCreationStep() {
        return new StepBuilder("folderCreationStep", jobRepository)
                .tasklet(createFolderTasklet, transactionManager)
                .build();
    }
    // Step 2: Generate mock data file with 1000 post records
    @Bean("mockDataGenerationStep")
    public Step mockDataGenerationStep() {
        return new StepBuilder("mockDataGenerationStep", jobRepository)
                .tasklet(generateMockDataTasklet, transactionManager)
                .build();
    }
    // Step 3: Read mock data, filter posts by content length, and write to database
    @Bean("dataImportStep")
    public Step dataImportStep(JpaItemWriter<Post> postDatabaseWriter) {
        return new StepBuilder("dataImportStep", jobRepository)
                .<Post, Post>chunk(50, transactionManager)
                .reader(postJsonReader)
                .processor(postContentLengthFilter)
                .writer(postDatabaseWriter)
                .build();
    }
    // Step 4: Remove the mock data file after processing
    @Bean("fileRemovalStep")
    public Step fileRemovalStep() {
        return new StepBuilder("fileRemovalStep", jobRepository)
                .tasklet(removeMockDataFileTasklet, transactionManager)
                .build();
    }
    // Step 5: Generate a report file with the total number of posts in the database
    @Bean("reportGenerationStep")
    public Step reportGenerationStep() {
        return new StepBuilder("reportGenerationStep", jobRepository)
                .tasklet(writeReportTasklet, transactionManager)
                .build();
    }
    // Define the Post Seeding Job with all 5 steps in sequence
    @Bean("postSeedingJob")
    public Job postSeedingJob(
            @Qualifier("folderCreationStep") Step step1,
            @Qualifier("mockDataGenerationStep") Step step2,
            @Qualifier("dataImportStep") Step step3,
            @Qualifier("fileRemovalStep") Step step4,
            @Qualifier("reportGenerationStep") Step step5
    ) {
        return new JobBuilder("postSeedingJob", jobRepository)
                .start(step1)
                .next(step2)
                .next(step3)
                .next(step4)
                .next(step5)
                .build();
    }

    @Bean
    public JpaItemWriter<Post> postDatabaseWriter(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<Post>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
