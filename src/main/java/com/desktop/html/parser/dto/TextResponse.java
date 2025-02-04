package com.desktop.html.parser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TextResponse {
    private String imgText;
    private String firstText;
    private List<String> secondText;
}
