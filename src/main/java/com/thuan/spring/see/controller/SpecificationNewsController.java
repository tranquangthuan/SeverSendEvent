package com.thuan.spring.see.controller;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@CrossOrigin
@RestController()
@RequestMapping("/specification")
public class SpecificationNewsController {

	public Map<Integer, SseEmitter> emittersMap = new HashMap<Integer, SseEmitter>();

	@GetMapping(value = "/subcrible/{userId}")
	public SseEmitter subcrible(@PathVariable(name = "userId") Integer userId) {
		SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
		try {
			sseEmitter.send(SseEmitter.event().name("INIT"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		emittersMap.put(userId, sseEmitter);
		sseEmitter.onCompletion(() -> emittersMap.remove(userId));
		sseEmitter.onTimeout(() -> emittersMap.remove(userId));
		sseEmitter.onError((e) -> emittersMap.remove(userId));

		return sseEmitter;
	}

	@PostMapping(value = "/news")
	public void postNews(@RequestParam String title, @RequestParam String content, @RequestParam Integer userId) {
		SseEmitter sseEmitter = emittersMap.get(userId);
		if (sseEmitter != null) {
			String data = new JSONObject().put("title", title).put("content", content).toString();
			try {
				sseEmitter.send(SseEmitter.event().name("lastestNews").data(data));
			} catch (Exception e) {
				emittersMap.remove(userId);
			}
		}

	}

}
