package com.yoga.backend.mypage.recorded.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DeleteDto {
    private List<Long> lectureIds;
}