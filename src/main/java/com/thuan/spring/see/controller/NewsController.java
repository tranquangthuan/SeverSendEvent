package com.thuan.spring.see.controller;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@CrossOrigin
@RestController
public class NewsController {

	public List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

	@GetMapping(value = "/subcrible")
	public SseEmitter subcrible() {
		SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
		try {
			sseEmitter.send(SseEmitter.event().name("INIT"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		emitters.add(sseEmitter);
		sseEmitter.onCompletion(() -> emitters.remove(sseEmitter));
		sseEmitter.onTimeout(() -> emitters.remove(sseEmitter));
		sseEmitter.onError((e) -> emitters.remove(sseEmitter));

		return sseEmitter;
	}

	@PostMapping(value = "/news")
	public void postNews(@RequestParam String title, @RequestParam String content) {
		String data = new JSONObject().put("title", title).put("content", content).toString();
		for (SseEmitter emitter : emitters) {
			try {
				emitter.send(SseEmitter.event().name("lastestNews").data(data));
			} catch (Exception e) {
				emitters.remove(emitter);
			}
		}
	}

}
