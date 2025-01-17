package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dto.Account;
import com.example.entity.vo.request.RenameClientVO;
import com.example.entity.vo.request.RenameNodeVO;
import com.example.entity.vo.request.RuntimeDetailVO;
import com.example.entity.vo.response.ClientDetailsVO;
import com.example.entity.vo.response.ClientPreviewVO;
import com.example.entity.vo.response.ClientSimpleVO;
import com.example.entity.vo.response.RuntimeHistoryVO;
import com.example.service.AccountService;
import com.example.service.ClientService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monitor")
public class MonitorController {

    @Resource
    ClientService service;

    @Resource
    AccountService accountService;

    @GetMapping("/list")
    public RestBean<List<ClientPreviewVO>> listAllClient(@RequestAttribute(Const.ATTR_USER_ID) int userId,
                                                         @RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        List<ClientPreviewVO> clients = service.listClients();
        if (this.isAdminAccount(userRole)) {
            return RestBean.success(clients);
        }else {
            // 可访问客户端的id集合
            List<Integer> list = this.accountAccessClients(userId);
            return RestBean.success(clients.stream()
                    .filter(vo -> list.contains(vo.getId()))
                    .toList());
        }
    }

    @PostMapping("/rename")
    public RestBean<Void> renameClient(@RequestBody @Valid RenameClientVO vo,
                                       @RequestAttribute(Const.ATTR_USER_ID) int userId,
                                       @RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        if(this.permissionCheck(userId,userRole,vo.getId())) {
            service.renameClient(vo);
            return RestBean.success();
        }else {
           return RestBean.noPermission();
        }

    }

    @PostMapping("/node")
    public RestBean<Void> renameNode(@RequestBody @Valid RenameNodeVO vo,
                                     @RequestAttribute(Const.ATTR_USER_ID) int userId,
                                     @RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        if(this.permissionCheck(userId,userRole,vo.getId())) {
            service.renameNode(vo);
            return RestBean.success();
        }else {
           return RestBean.noPermission();
        }


    }

    @GetMapping("/details")
    public RestBean<ClientDetailsVO> details(int clientId,
                                             @RequestAttribute(Const.ATTR_USER_ID) int userId,
                                             @RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        if(this.permissionCheck(userId,userRole,clientId)) {
            return RestBean.success(service.clientDetails(clientId));
        }else {
           return RestBean.noPermission();
        }

    }

    @GetMapping("/runtime-history")
    public RestBean<RuntimeHistoryVO> runtimeDetailsHistory(int clientId,
                                                            @RequestAttribute(Const.ATTR_USER_ID) int userId,
                                                         @RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
       if(this.permissionCheck(userId,userRole,clientId)) {
            return RestBean.success(service.clientRuntimeDetailsHistory(clientId));
        }else {
           return RestBean.noPermission();
        }
    }

    @GetMapping("/runtime-now")
    public RestBean<RuntimeDetailVO> runtimeDetailsNow(int clientId,
                                                       @RequestAttribute(Const.ATTR_USER_ID) int userId,
                                                         @RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        if(this.permissionCheck(userId,userRole,clientId)) {
            return RestBean.success(service.clientRuntimeDetailsNow(clientId));
        }else {
           return RestBean.noPermission();
        }
    }

    @GetMapping("/register")
    public RestBean<String> registerToken(@RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        if (this.isAdminAccount(userRole))
            return RestBean.success(service.registerToken());
        else
            return RestBean.noPermission();
    }

    @GetMapping("/delete")
    public RestBean<String> deleteClient(int clientId,
                                         @RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        if (this.isAdminAccount(userRole)) {
            service.deleteClient(clientId);
            return RestBean.success();
        }else {
            return RestBean.noPermission();
        }
    }

    @GetMapping("/simple-list")
    public RestBean<List<ClientSimpleVO>> simpleList(@RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        if (this.isAdminAccount(userRole))
            return RestBean.success(service.listSimpleList());
        else
            return RestBean.noPermission();
    }

    private List<Integer> accountAccessClients(int uid) {
        Account account = accountService.getById(uid);
        return account.getClientList();
    }

    private Boolean isAdminAccount(String role) {
        role = role.substring(5);
        return Const.ROLE_ADMIN.equals(role);
    }

    private Boolean permissionCheck(int uid, String role, int clientId) {
        if (this.isAdminAccount(role)) return  true;
        return this.accountAccessClients(uid).contains(clientId);
    }
}
