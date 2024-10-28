package com.kkumteul.domain.code.dto;

import com.kkumteul.domain.code.Code;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CodeDto {

    private Long codeId;
    private String codeName;

    public static CodeDto fromEntity(Code code) {
        return new CodeDto(
                code.getId().getCodeId(),
                code.getCodeName()
        );
    }
}
