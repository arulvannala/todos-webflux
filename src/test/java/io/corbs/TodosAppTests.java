package io.corbs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Delayed;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TodosAppTests {

	@Test
	public void funWithMono() {
	    Mono<String> publisher = Mono.just("Mono for 0 or 1 Events").delaySubscription(Duration.ofMillis(1000));
	    publisher.subscribe( it -> {
	        System.out.println(it);
        });
	    System.out.println("Howdy");
        pause(2000);
    }

    private void pause(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {  }
    }
}
