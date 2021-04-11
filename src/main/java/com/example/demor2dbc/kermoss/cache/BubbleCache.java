package com.example.demor2dbc.kermoss.cache;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import reactor.cache.CacheMono;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;

@Service
public class BubbleCache {

	private final Map<String, BubbleMessage> cache = new WeakHashMap<>();

	public Mono<BubbleMessage> getOrAddBubble(String key, Mono<BubbleMessage> bubbleMessage) {
		Function<String, Mono<Signal<? extends BubbleMessage>>> f = k -> {
			return Mono.justOrEmpty(this.cache.get(k)).map(Signal::next);
			};

		return CacheMono.lookup(f, key).onCacheMissResume(bubbleMessage)
		.andWriteWith((k, sig) -> Mono.fromRunnable(() -> {
			BubbleMessage msg = sig.get();
			if (msg != null) {
				cache.put(k, msg);
			}
		}));
	}

	public Mono<BubbleMessage> getBubble(String eventId) {
		return getOrAddBubble(eventId,Mono.<BubbleMessage>empty());
	}
}
