package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Client;
import com.example.entity.dto.ClientDetail;
import com.example.entity.vo.request.ClientDetailVO;
import com.example.entity.vo.request.RenameClientVO;
import com.example.entity.vo.request.RenameNodeVO;
import com.example.entity.vo.request.RuntimeDetailVO;
import com.example.entity.vo.response.ClientDetailsVO;
import com.example.entity.vo.response.ClientPreviewVO;
import com.example.entity.vo.response.RuntimeHistoryVO;
import com.example.mapper.ClientDetailMapper;
import com.example.mapper.ClientMapper;
import com.example.service.ClientService;
import com.example.utils.InfluxDbUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ClientServiceImpl extends ServiceImpl<ClientMapper, Client> implements ClientService {

    @Resource
    ClientDetailMapper clientDetailMapper;

    @Resource
    InfluxDbUtils influx;

    private String registerToken = this.generateNewToken();

    private final Map<Integer,Client> ClientIdCache = new ConcurrentHashMap<>();
    private final Map<String,Client> ClientTokenCache = new ConcurrentHashMap<>();

    @PostConstruct // 在依赖注入后执行
    private void initClientCache() {
        // 将数据库中所有数据遍历然后分别添加到不同的缓存中去
        this.list().forEach(this::addClientCache);
    }

    @Override
    public String registerToken() {
        return registerToken;
    }

    @Override
    public Boolean verifyAndRegister(String token) {
        if (registerToken.equals(token)) {
            int id = RandomClientId();
            Client client = new Client(id,"未注册主机",token,"cn","未命名节点",new Date());
            if (save(client)) {
                registerToken = this.generateNewToken();
                return true;
            }
        }
        return false;
    }


    @Override
    public Client findClientById(int id) {
        return ClientIdCache.get(id);
    }

    @Override
    public Client findClientByToken(String token) {
        return ClientTokenCache.get(token);
    }

    @Override
    public void updateClientDetail(ClientDetailVO vo, Client client) {
        ClientDetail detail = new ClientDetail();
        BeanUtils.copyProperties(vo,detail);
        detail.setId(client.getId());
        if (Objects.nonNull(clientDetailMapper.selectById(client.getId()))) {
            clientDetailMapper.updateById(detail);
        }else {
            clientDetailMapper.insert(detail);
        }

    }

    private Map<Integer,RuntimeDetailVO> currentRuntime = new ConcurrentHashMap<>();

    @Override
    public void updateRuntimeDetail(RuntimeDetailVO vo, Client client) {
        currentRuntime.put(client.getId(),vo);
        influx.writeRuntimeData(client.getId(),vo);
    }

    @Override
    public List<ClientPreviewVO> listAllClient() {
        return ClientIdCache.values().stream().map(client -> {
            ClientPreviewVO vo = client.asViewObject(ClientPreviewVO.class);
            BeanUtils.copyProperties(clientDetailMapper.selectById(client.getId()),vo);
            RuntimeDetailVO runtime = currentRuntime.get(client.getId());
            if ( isOnline(runtime)) {
                BeanUtils.copyProperties(runtime,vo);
                vo.setOnline(true);
            }
            return vo;
        }).toList();
    }

    @Override
    public void renameClient(RenameClientVO vo) {
        this.update(Wrappers.<Client>update().eq("id",vo.getId()).set("name",vo.getName()));
        this.initClientCache();
    }

    @Override
    public void renameNode(RenameNodeVO vo) {
        this.update(Wrappers.<Client>update().eq("id",vo.getId())
                .set("node",vo.getNode()).set("location",vo.getLocation()));
        this.initClientCache();
    }

    @Override
    public RuntimeHistoryVO clientRuntimeHistory(int clientId) {
        RuntimeHistoryVO vo = influx.readRuntimeData(clientId);
        ClientDetail detail = clientDetailMapper.selectById(clientId);
        BeanUtils.copyProperties(detail,vo);
        return vo;
    }

    @Override
    public RuntimeDetailVO clientRuntimeNow(int clientId) {
            return currentRuntime.get(clientId);
    }

    @Override
    public ClientDetailsVO details(int clientId) {
      ClientDetailsVO vo =  ClientIdCache.get(clientId).asViewObject(ClientDetailsVO.class);
      BeanUtils.copyProperties(clientDetailMapper.selectById(clientId),vo);
      vo.setOnline(this.isOnline(currentRuntime.get(clientId)));
      return vo;
    }

    private Boolean isOnline(RuntimeDetailVO runtime) {
       return   runtime != null &&  System.currentTimeMillis() -  runtime.getTimestamp() < 60 *1000;
    }

    private void addClientCache(Client client) {
        ClientTokenCache.put(client.getToken(), client);
        ClientIdCache.put(client.getId(), client);

    }

    private int RandomClientId() {
        return new Random().nextInt(90000000) + 10000000;
    }


    private String generateNewToken() {
        String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(24);
        for (int i = 0; i < 24; i++)
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        System.out.println(sb);
        return sb.toString();
    }
}
