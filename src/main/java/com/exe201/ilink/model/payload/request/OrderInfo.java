package com.exe201.ilink.model.payload.request;

import com.exe201.ilink.model.payload.dto.OrderProductDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfo {

    @JsonProperty("account_id")
    private UUID accountId;

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("shipped_address")
    private String address;

    @Size(max = 100)
    private String description;

    private List<OrderProductDTO> products;

    @JsonProperty("total_price")
    private int totalPrice;

    @JsonProperty("payment_date")
    private Date paymentDate;

}
