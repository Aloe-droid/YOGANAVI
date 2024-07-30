package com.yoga.backend.members.service;

import com.yoga.backend.common.entity.Users;
import com.yoga.backend.members.dto.RegisterDto;
import com.yoga.backend.members.dto.UpdateDto;
import java.util.Set;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

public interface UsersService {

    Users registerUser(RegisterDto registerDto);

    @Transactional
    void recoverAccount(Users user);

    boolean checkNickname(String nickname);

    boolean checkUser(String email);

    void sendSimpleMessage(String registerDto, String message, String s);

    String sendPasswordResetToken(String email);

    boolean validateResetToken(String email, String token);

    String resetPassword(String email, String newPassword);

    Users getUserByUserId(int userId);

    Users updateUser(UpdateDto updateDto, int userId);

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    Set<String> getUserHashtags(int userId);

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    void updateUserHashtags(int userId, Set<String> newHashtags);

    void requestDeleteUser(int userId);

    @Transactional(isolation = Isolation.SERIALIZABLE)
    void processDeletedUsers();
}