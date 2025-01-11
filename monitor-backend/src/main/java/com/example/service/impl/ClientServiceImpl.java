package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.RestBean;
import com.example.entity.dto.Client;
import com.example.entity.dto.ClientDetail;
import com.example.entity.vo.request.ClientDetailVO;
import com.example.entity.vo.request.RuntimeDetailVO;
import com.example.mapper.ClientDetailMapper;
import com.example.mapper.ClientMapper;
import com.example.service.ClientService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ClientServiceImpl extends ServiceImpl<ClientMapper, Client> implements ClientService {

    @Resource
    ClientDetailMapper clientDetailMapper;


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
    public RestBean<Void> updateClientDetail(ClientDetailVO vo, Client client) {
        ClientDetail detail = new ClientDetail();
        BeanUtils.copyProperties(vo,detail);
        detail.setId(client.getId());
        if (Objects.nonNull(clientDetailMapper.selectById(client.getId()))) {
            clientDetailMapper.updateById(detail);
        }else {
            clientDetailMapper.insert(detail);
        }

        return null;
    }

    private Map<Integer,RuntimeDetailVO> currentRuntime = new ConcurrentHashMap<>();

    @Override
    public void updateRuntimeDetail(RuntimeDetailVO vo, Client client) {
        currentRuntime.put(client.getId(),vo);
        System.out.println(vo);
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
