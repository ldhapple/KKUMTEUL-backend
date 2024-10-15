package com.kkumteul.domain.code;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class GroupCode {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_code_id")
    private Long groupCodeId;

    private String name;
}
