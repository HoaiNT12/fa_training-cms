package fa.training.cms.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.javafaker.Faker;
import fa.training.cms.entity.Post;
import fa.training.cms.entity.User;
import fa.training.cms.service.enums.Status;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
@StepScope
public class GenerateMockPostDataTasklet implements Tasklet {
    private static final int NUMBER_OF_RECORDS = 1000;
    private static final String FILE_NAME = "mock_posts.json"; // Tên file output
    private final String folderPath; // Được inject từ JobParameters

    // Constructor để inject folderPath
    public GenerateMockPostDataTasklet(
            @Value("${job.postDel.folderPath}") String folderPath
    ) {
        this.folderPath = folderPath;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        if (folderPath == null || folderPath.isBlank()) {
            throw new IllegalArgumentException("Job parameter 'folderPath' is required!");
        }
        // Đảm bảo thư mục đã được tạo (do createJobFolderStep đã làm)
        Path outputDirectory = Path.of(folderPath);
        if (!Files.exists(outputDirectory)) {
            // Thực tế Step 1 đã đảm bảo cái này, nhưng thêm check để an toàn hơn
            Files.createDirectories(outputDirectory);
        }
        File outputFile = outputDirectory.resolve(FILE_NAME).toFile();
        Faker faker = new Faker();
        Random random = new Random();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        List<Post> posts = new ArrayList<>();
        Status[] statuses = Status.values();
        for (int i = 1; i <= NUMBER_OF_RECORDS; i++) {
            LocalDateTime createdDate = faker.date().past(365, TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC).toLocalDateTime();
            LocalDateTime lastModifiedDate = createdDate.plusDays(random.nextInt(30));
            Long authorId = (long) (random.nextInt(5) + 1); // Giả sử có 5 user
            Post post = Post.builder()
                    .title(faker.book().title())
                    .content(faker.lorem().paragraph(5))
                    .status(statuses[random.nextInt(statuses.length)])
                    .user(User.builder().id(authorId).build())

                    .build();
            posts.add(post);
        }
        try {
            objectMapper.writeValue(outputFile, posts);
            System.out.println("Successfully generated " + NUMBER_OF_RECORDS + " mock posts to " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Error generating mock posts to file: " + outputFile.getAbsolutePath(), e);
        }
        return RepeatStatus.FINISHED;
    }
}
