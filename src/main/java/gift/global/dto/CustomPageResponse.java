package gift.global.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record CustomPageResponse<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        int totalPages,
        long totalElements,
        boolean isFirst,
        boolean isLast
) {
    public static <T> CustomPageResponse<T> from(Page<T> page) {
        return new CustomPageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isFirst(),
                page.isLast()
        );
    }
}
