package com.study.datajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberDTO {
    private Long id;
    private String userName;
    private String teamName;
}
