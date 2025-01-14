package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.Client;
import com.example.entity.vo.request.ClientDetailVO;
import com.example.entity.vo.request.RenameClientVO;
import com.example.entity.vo.request.RenameNodeVO;
import com.example.entity.vo.request.RuntimeDetailVO;
import com.example.entity.vo.response.ClientDetailsVO;
import com.example.entity.vo.response.ClientPreviewVO;
import com.example.entity.vo.response.RuntimeHistoryVO;

import java.util.List;

public interface ClientService extends IService<Client> {
    Boolean verifyAndRegister(String token);
    String registerToken();
    Client findClientById(int id);
    Client findClientByToken(String token);
    void updateClientDetail(ClientDetailVO vo, Client client);
    void updateRuntimeDetail(RuntimeDetailVO vo, Client client);
    List<ClientPreviewVO> listAllClient();
    void  renameClient(RenameClientVO vo);
    ClientDetailsVO details(int clientId);
    void renameNode(RenameNodeVO vo);
    RuntimeHistoryVO clientRuntimeHistory(int clientId);
    RuntimeDetailVO clientRuntimeNow(int clientId);




}
