package com.example.grpc.client;

/**
 * @author zhailuxu <zhailuxu@kuaishou.com>
 * Created on 2021-03-28
 */

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import com.example.server.streaming.StreamingExample;

import io.grpc.stub.StreamObserver;
import reactor.core.publisher.BaseSubscriber;

/**
 * Bridge the StreamObserver from gRPC to the Publisher from the reactive world.
 */
public class StreamObserverPublisher implements Publisher<StreamingExample.Average>, StreamObserver<StreamingExample.Average> {

    private Subscriber<? super StreamingExample.Average> subscriber;

    @Override
    public void onNext(StreamingExample.Average l) {
        subscriber.onNext(l);
    }

    @Override
    public void onError(Throwable throwable) {
        subscriber.onError(throwable);
    }

    @Override
    public void onCompleted() {
        subscriber.onComplete();
    }

    @Override
    public void subscribe(Subscriber<? super StreamingExample.Average> subscriber) {
        this.subscriber = subscriber;
        this.subscriber.onSubscribe(new BaseSubscriber() {
        });
    }
}