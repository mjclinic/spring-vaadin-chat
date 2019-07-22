package demo.spring.vaadin.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

@SpringBootApplication
public class SpringVaadinChatApplication {

	public static void main(String[] args) {

		SpringApplication.run(SpringVaadinChatApplication.class, args);
	}

	@Bean
	UnicastProcessor<MainView.ChatMessage> unicastProcessor(){
		return UnicastProcessor.create();
	}

	@Bean
	Flux<MainView.ChatMessage> chatMessageFlux (UnicastProcessor<MainView.ChatMessage> chatMessageUnicastProcessor){
		return chatMessageUnicastProcessor.replay(30).autoConnect();
	}



}
