package com.kkumteul.domain.code;

import com.kkumteul.domain.code.key.CodeKey;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.Data;

@Data
@Entity
public class Code {

    @EmbeddedId
    private CodeKey id;

    @Column(name = "code_name")
    private String codeName;

    @Column(name = "order_no")
    private int orderNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("groupCodeId")
    @JoinColumn(name = "group_code_id")
    private GroupCode groupCode;
}
