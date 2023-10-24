package com.sociogram.main_service.event.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    @NotNull
    @DecimalMin(value = "-180.0", message = "lat должен быть больше -180.0")
    @DecimalMax(value = "180.0", message = "lat должен быть меньше 180.0")
    private Double lat;

    @NotNull
    @DecimalMin(value = "-180.0", message = "lon должен быть больше -180.0")
    @DecimalMax(value = "180.0", message = "lon должен быть меньше 180.0")
    private Double lon;
}
