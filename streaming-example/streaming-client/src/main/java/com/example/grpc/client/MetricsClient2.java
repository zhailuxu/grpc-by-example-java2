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

package com.example.grpc.client;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import com.example.server.streaming.MetricsServiceGrpc;
import com.example.server.streaming.StreamingExample;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

/**
 * Created by rayt on 5/16/16.
 */
public class MetricsClient2 {
    public static void main(String[] args) throws InterruptedException {
        //1.创建客户端桩
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();
        MetricsServiceGrpc.MetricsServiceStub stub = MetricsServiceGrpc.newStub(channel);

        //2.发起请求，并设置结果回调监听
        StreamObserver<StreamingExample.Metric> collect = stub.collectClientStream(new StreamObserver<StreamingExample.Average>() {
            @Override
            public void onNext(StreamingExample.Average value) {
                System.out.println(Thread.currentThread().getName() + "Average: " + value.getVal());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("error:" + t.getLocalizedMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("onCompleted:");

            }
        });

        //3.使用同一个链接，不断向服务端传送数据
        Stream.of(1L, 2L, 3L, 4L,5L).map(l -> StreamingExample.Metric.newBuilder().setMetric(l).build())
                .forEach(metric -> {
                    collect.onNext(metric);
                    System.out.println(metric);
                });

        Thread.sleep(3000);
        collect.onCompleted();
        channel.shutdown().awaitTermination(50, TimeUnit.SECONDS);
    }
}
