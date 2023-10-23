package com.sociogram.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetStatsDto {
    @NotNull
    private String start;
    @NotNull
    private String end;
    private List<String> uris;
    private Boolean unique;
}
