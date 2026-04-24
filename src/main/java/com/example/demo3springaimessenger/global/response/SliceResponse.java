package com.example.demo3springaimessenger.global.response;

import lombok.Builder;
import org.springframework.data.domain.Slice;

import java.util.List;

@Builder
public record SliceResponse<T>(
        List<T> content,      // 실제 데이터 리스트
        int pageNumber,       // 현재 페이지 번호 (0부터 시작)
        int pageSize,         // 요청한 페이지 크기
        int numberOfElements, // 현재 슬라이스에 담긴 요소 개수
        boolean hasNext      // 다음 슬라이스 존재 여부 (Slice 핵심)
) {
    public static <T> SliceResponse<T> of(Slice<T> slice) {
        return SliceResponse.<T>builder()
                .content(slice.getContent())
                .pageNumber(slice.getNumber() + 1) // 0-based index -> 1-based index
                .pageSize(slice.getSize())
                .numberOfElements(slice.getNumberOfElements())
                .hasNext(slice.hasNext())
                .build();
    }
}