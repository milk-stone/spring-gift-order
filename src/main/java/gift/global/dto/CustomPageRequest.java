package gift.global.dto;

import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

public record CustomPageRequest(
        @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.") Integer page,
        @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.") Integer size,
        String sort
) {
    public CustomPageRequest {
        if (page == null) page = 0;
        if (size == null) size = 5;
    }
    public Pageable toPageable() {
        if (StringUtils.hasText(sort)) {
            String[] sortParams = sort.split(",");
            String property = sortParams[0];
            Sort.Direction direction = (sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1]))
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            return PageRequest.of(page, size, Sort.by(direction, property));
        }
        return PageRequest.of(page, size);
    }
}
