package com.exe201.ilink.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "Object for exception handling")
public class ExceptionResponse {
    @Schema(description = "Http Status Code", example = "200")
    private Integer httpStatus;
    @Schema(description = "Time that the error occur", example = "09/8/2024 00:44:00")
    private String timestamp;
    @Schema(description = "Error title", example = "Invalid Email/Password")
    private String message;
    @Schema(description = "An Error detail", example = "Invalid Email/Password")
    private String error;

    @Schema(description = "List Of Error Details", example = """
        data: {
          "last_name": "Last name is mandatory",
          "password": "Password cannot be blank",
          "phone": "Please enter a valid (+84) phone number"
        }""")
    private Map<String, String> data;

}
