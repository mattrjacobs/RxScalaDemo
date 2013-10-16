package com.mattrjacobs.rxscala.javaconsumer;

import com.mattrjacobs.rxscala.Producer;
import rx.Observable;
import rx.util.functions.Action1;

public class SampleConsumer {
    public static void main(String[] args) {
        System.out.println("Consumer Started!");

        Observable<? extends String> s = Producer.stream();
        
        s.subscribe(new Action1<String>() {
            @Override
            public void call(String o) {
                System.out.println("onNext : " + o);
            }
        });
    }
}
