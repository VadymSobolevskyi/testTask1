package com.example.demo.dto;

import com.example.demo.model.ParcelStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateParcelStatusRequest {
    private ParcelStatus status;
}
