package com.tech.altoubli.museum.art.exception;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorDto {
    private String error;
    private String message;
}
