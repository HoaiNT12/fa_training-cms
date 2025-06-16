package fa.training.cms.service.dto;

import fa.training.cms.entity.Category;
import fa.training.cms.service.enums.Status;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private Status status = Status.PUBLISHED;
    private List<CategoryDto> categoryIds = new ArrayList<>();
}

