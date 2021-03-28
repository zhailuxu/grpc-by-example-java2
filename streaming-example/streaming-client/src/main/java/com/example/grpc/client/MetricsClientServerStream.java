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

import java.util.stream.Stream;

import com.example.server.streaming.MetricsServiceGrpc;
import com.example.server.streaming.StreamingExample;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

/**
 * Created by rayt on 5/16/16.
 */
public class MetricsClientServerStream {
    public static void main(String[] args) throws InterruptedException {
        //获取客户端桩对象
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();
        MetricsServiceGrpc.MetricsServiceStub stub = MetricsServiceGrpc.newStub(channel);

        //发起rpc请求，设置StreamObserver用于监听服务器返回结果
        stub.collectServerStream(StreamingExample.Metric.newBuilder().setMetric(1L).build(), new StreamObserver<StreamingExample.Average>() {
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

        for (; ; ) {
            Stream.of(1L).map(l -> StreamingExample.Metric.newBuilder().setMetric(l).build())
                    .forEach(metric -> {
                        //   collect.onNext(metric);
                        //  System.out.println(metric);
                    });
            Thread.sleep(1000);
        }

        // collect.onCompleted();

        // channel.shutdown().awaitTermination(50, TimeUnit.SECONDS);
    }
}
