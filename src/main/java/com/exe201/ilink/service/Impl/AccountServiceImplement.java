package com.exe201.ilink.service.Impl;

import com.exe201.ilink.model.entity.Account;
import com.exe201.ilink.model.exception.ILinkException;
import com.exe201.ilink.model.payload.dto.request.AccountProfile;
import com.exe201.ilink.model.payload.dto.request.ChangePasswordRequest;
import com.exe201.ilink.repository.AccountRepository;
import com.exe201.ilink.sercurity.JwtTokenProvider;
import com.exe201.ilink.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImplement implements AccountService {

    private final AccountRepository accountRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Account getCurrentAccountInfo(HttpServletRequest request) {

        String token = extractTokenFormJWT(request);

        //Extract Account Info
        String userEmail = jwtTokenProvider.getUsername(token);
        Account account = accountRepository.findByEmail(userEmail)
                .orElse(null);

        if (account == null && !jwtTokenProvider.validateToken(token)) {
            throw new ILinkException(HttpStatus.BAD_REQUEST,"No account found with this token");
        }

        if (!jwtTokenProvider.isTokenValid(token, account.getEmail())) {
            throw new ILinkException(HttpStatus.UNAUTHORIZED,"Token is invalid or is expired");
        }

        return account;
    }

    @Override
    public void changePassword(ChangePasswordRequest changePasswordRequest, HttpServletRequest request) {
        Account account = getCurrentAccountInfo(request);

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), account.getPassword())){
            throw new ILinkException(HttpStatus.BAD_REQUEST, "Your old password is incorrect");
        }
        account.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        accountRepository.save(account);
    }

    @Override
    public void updateAccountProfilePicture(UUID id, String imageURLMain) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ILinkException(HttpStatus.BAD_REQUEST, "No account found with this id"));

        if (imageURLMain == null) {
            throw new ILinkException(HttpStatus.BAD_REQUEST, "Image URL is null");
        }

        account.setAvatar(imageURLMain);
        accountRepository.save(account);

    }

    @Override
    public void updateAccountInfo(UUID id, AccountProfile accountProfile) {

        if (accountProfile == null) {
            throw new ILinkException(HttpStatus.BAD_REQUEST, "Account profile is null");
        }

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ILinkException(HttpStatus.BAD_REQUEST, "Account not exists or not found "));

        account.setFirstName(accountProfile.getFirstName());
        account.setLastName(accountProfile.getLastName());
        account.setPhone(accountProfile.getPhone());
        account.setGender(accountProfile.getGender());
        account.setDob(accountProfile.getDob());
        accountRepository.save(account);


    }

    private String extractTokenFormJWT(HttpServletRequest request) {
        //Extract Token From Header
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token == null) {
            throw new ILinkException(HttpStatus.UNAUTHORIZED ,"No JWT found in request header");
        }
        return token;
    }
}
