package com.exe201.ilink.controller;

import com.exe201.ilink.model.exception.ResponseBuilder;
import com.exe201.ilink.model.payload.request.AccountProfile;
import com.exe201.ilink.model.payload.request.ChangePasswordRequest;
import com.exe201.ilink.service.AccountService;
import com.exe201.ilink.service.AuthenService;
import com.exe201.ilink.service.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("account")
@RequiredArgsConstructor
@Tag(name = "Account", description = "Method for account settings required access token to gain access")
public class AccountController {

    private final AuthenService authService;
    private final CloudinaryService cloudinaryService;
    private final AccountService accountService;

    @Operation(
        summary = "View user profile",
        description = "View current user information after logging into the system. Passwords, tokens, etc., " +
            "will not be displayed",
        tags = {"Account"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account information retrieve successfully",
            content = @Content(
                examples = @ExampleObject(value = """
                        {
                           "http_status": 200,
                           "time_stamp": "06/02/2024 17:25:41",
                           "message": "Successfully retrieved user information",
                           "data": {
                             "userId": "a9126139-3c11-47ba-8493-cf7e480c3645",
                             "first_name": "James",
                             "last_name": "Bond",
                             "email": "Sniper6969@gmail.com",
                             "address": "123 Main St, Springfield",
                             "gender": null,
                             "phone": "0877643231",
                             "profile_pic": null,
                             "auth_provider": null,
                             "role_name": "USER"
                           }
                         }
                    """))),
        @ApiResponse(responseCode = "401", description = "No JWT token found in the request header"),
    })
    @GetMapping("/profile")
    public ResponseEntity<Object> getCurrentAccountInfo(HttpServletRequest request) {
        return ResponseBuilder.responseBuilderWithData(HttpStatus.OK,
            "Successfully retrieved user information", accountService.getCurrentAccountInfo(request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Object> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, HttpServletRequest request) {
        accountService.changePassword(changePasswordRequest, request);
        return ResponseBuilder.responseBuilder(HttpStatus.OK, "Password changed successfully");
    }

    @Operation(
        summary = "Update user profile picture",
        description = "Update current user profile picture after logging into the system.",
        tags = {"Account"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User profile picture updated successfully"),
        @ApiResponse(responseCode = "500", description = "Failed to update user profile picture"),
    })
    @PostMapping(value = "/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Object> updateUserProfilePicture(@NotNull @RequestParam(value = "id") UUID id,
                                                           @RequestParam(value = "profile_pic", required = false) MultipartFile profilePicture) throws IOException {

        String imageURLMain = cloudinaryService.uploadFile(profilePicture);
        accountService.updateAccountProfilePicture(id, imageURLMain);
        return ResponseBuilder.responseBuilder(HttpStatus.OK, "User profile picture updated successfully");
    }

    @Operation(
        summary = "Update user profile information",
        description = "Update current user information after logging into the system.",
        tags = {"Account"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account information update successfully"),
        @ApiResponse(responseCode = "500", description = "No JWT token found in the request header"),
    })
    @PutMapping(value = "/profile")
    public ResponseEntity<Object> updateProfile(@NotNull @RequestParam(value = "id") UUID id,
                                                @RequestBody @Valid AccountProfile accountProfile) {
        accountService.updateAccountInfo(id, accountProfile);
        return ResponseBuilder.responseBuilder(HttpStatus.OK, "Account profile information updated successfully");
    }


}
