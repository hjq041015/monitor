package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.BaseData;
import com.example.entity.dto.Account;
import com.example.entity.vo.request.ChangePasswordVO;
import com.example.entity.vo.request.ConfirmResetVO;
import com.example.entity.vo.request.EmailResetVO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends IService<Account>, UserDetailsService {
    Account findAccountByNameOrEmail(String text);
    String registerEmailVerifyCode(String type, String email, String address);
    String resetEmailAccountPassword(EmailResetVO info);
    String resetConfirm(ConfirmResetVO info);
    Boolean changePassword(int id, String oldPassword, String newPassword);
}
