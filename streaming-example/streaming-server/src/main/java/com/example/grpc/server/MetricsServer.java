/*
 * Copyright 2016 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.grpc.server;

import java.io.IOException;

import com.example.server.streaming.StreamingExample;

import io.grpc.Server;
import io.grpc.ServerBuilder;

/**
 * Created by rayt on 5/16/16.
 */
public class MetricsServer {
    public static void main(String[] args) throws InterruptedException, IOException {
        //启动服务
        MetricsServiceImpl metricsService = new MetricsServiceImpl();
        Server server = ServerBuilder.forPort(8080).addService(metricsService).build();
        server.start();

        //获取steam响应对象，不向客户端写回数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (; ; ) {
                    if (null != metricsService.responseObserverT) {
                        metricsService.responseObserverT.onNext(StreamingExample.Average.newBuilder().build());
                        System.out.println("send to client");

                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
        server.awaitTermination();
    }
}
