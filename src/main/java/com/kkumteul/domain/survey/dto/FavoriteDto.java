package com.kkumteul.domain.survey.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FavoriteDto {

    private String name;
    private byte[] image;
}
