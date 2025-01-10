package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true) // 允许链式调用的setter方法
public class BaseDetail {
    private String osArch;    // 操作系统架构，例如 x86 或 x64
    private String osName;    // 操作系统名称，例如 Windows、Linux
    private String osVersion; // 操作系统版本号
    private int osBit;        // 操作系统位数，通常为 32 或 64
    private String cpuName;   // CPU名称，例如 Intel Core i7
    private int cpuCore;      // CPU核心数量
    private double memory;     // 可用内存量，单位为GB
    private double disk;       // 可用磁盘空间，单位为GB
    private String ip;         // 主机的IP地址
}
