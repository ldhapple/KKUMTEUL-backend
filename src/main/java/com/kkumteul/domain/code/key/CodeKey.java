package com.kkumteul.domain.code.key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Embeddable
public class CodeKey implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "group_code_id")
    private Long groupCodeId;

    @Column(name = "code_id")
    private Long codeId;

    public CodeKey(Long groupCodeId, Long codeId) {
        this.groupCodeId = groupCodeId;
        this.codeId = codeId;
    }
}
