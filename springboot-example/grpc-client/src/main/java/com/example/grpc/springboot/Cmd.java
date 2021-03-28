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

package com.example.grpc.springboot;

import com.example.echo.EchoOuterClass;
import com.example.echo.EchoServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.autoconfigure.grpc.client.GrpcChannelFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by rayt on 5/18/16.
 */
@Component
public class Cmd {
  @Autowired
  public Cmd(ApplicationArguments args, @Qualifier("discoveryClientChannelFactory") GrpcChannelFactory channelFactory,
             DiscoveryClient discoveryClient) {

    discoveryClient.getServices();
    Channel channel = channelFactory.createChannel("EchoService");

    int i = 0;
    while (true) {
      EchoServiceGrpc.EchoServiceBlockingStub stub = EchoServiceGrpc.newBlockingStub(channel);
      EchoOuterClass.Echo response = stub.echo(EchoOuterClass.Echo.newBuilder().setMessage("Hello " + i).build());
      System.out.println("sent message #" + i);
      System.out.println("got response: " + response);
      i++;
    }
  }
}
