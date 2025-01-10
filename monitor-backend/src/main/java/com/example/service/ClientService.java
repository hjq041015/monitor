package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.RestBean;
import com.example.entity.dto.Client;
import com.example.entity.vo.request.ClientDetailVO;

public interface ClientService extends IService<Client> {
    Boolean verifyAndRegister(String token);
    String registerToken();
    Client findClientById(int id);
    Client findClientByToken(String token);
    RestBean<Void> updateClientDetail(ClientDetailVO vo, Client client);



}
