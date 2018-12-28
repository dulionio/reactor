package com.dulion.reactive;

import static java.util.Arrays.asList;
import static reactor.core.publisher.Flux.fromIterable;
import static reactor.core.publisher.Flux.just;

import org.junit.Test;
import org.reactivestreams.Publisher;

import reactor.core.publisher.Flux;

public class ReactTest {

    @Test
    public void testName() throws Exception {
        Flux<String> first = just("one", "two", "three", "four", "five");

        first.flatMap(this::enhance).subscribe(System.out::println);
    }

    public Publisher<String> enhance(String step) {
        return fromIterable(asList(step, "Aye", "Bee", "Cee", "Dee", "Eee", "Eff", "Gee"));
    }

}
