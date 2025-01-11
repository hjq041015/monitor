package com.example.utils;

import com.example.entity.dto.RuntimeData;
import com.example.entity.vo.request.RuntimeDetailVO;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class InfluxDbUtils {

    private InfluxDBClient client;
    private final String bucket = "monitor";
    private final String org = "hjq";

    @Value("${spring.influx.url}")
    String url;
    @Value("${spring.influx.user}")
    String user;
    @Value("${spring.influx.password}")
    String password;

    @PostConstruct
    public void init() {
        client = InfluxDBClientFactory.create(url,user,password.toCharArray());
    }



    public void writeRuntimeData(int clientId, RuntimeDetailVO vo){
        RuntimeData data = new RuntimeData();
        BeanUtils.copyProperties(vo,data);
        data.setClientId(clientId);
        data.setTimestamp(new Date(vo.getTimestamp()).toInstant());
        WriteApiBlocking writeApi = client.getWriteApiBlocking();
        writeApi.writeMeasurement(bucket, org, WritePrecision.NS, data);
    }
}
