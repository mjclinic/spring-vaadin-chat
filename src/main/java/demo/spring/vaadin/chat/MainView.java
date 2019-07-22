package demo.spring.vaadin.chat;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;


@Route
@PWA(name = "Chat for Vaadin Flow with Spring", shortName = "Chat")
@StyleSheet("frontend://styles.css")
@Push
public class MainView extends VerticalLayout {
    private String username;
    private final UnicastProcessor<ChatMessage> unicastProcessor;
    private final Flux<ChatMessage> chatMessageFlux;

    public MainView(UnicastProcessor<ChatMessage> unicastProcessor, Flux<ChatMessage> chatMessageFlux) {
        this.unicastProcessor = unicastProcessor;
        this.chatMessageFlux = chatMessageFlux;

        addClassName("main-view");
        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        H1 h1 = new H1("Chatting");
        h1.getElement().getThemeList().add("dark");

        add(h1);

        enterChatRoomView();
    }

    private void enterChatRoomView() {
        HorizontalLayout enterChatLayout = new HorizontalLayout();
        TextField usernameFld = new TextField();
        usernameFld.setPlaceholder("이름을 입력하세요");
        Button enterChatBtn = new Button("채팅방 입장~~");
        enterChatBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        enterChatLayout.add(usernameFld, enterChatBtn);

        enterChatBtn.addClickListener(buttonClickEvent -> {
            username = usernameFld.getValue();
            remove(enterChatLayout);
            showChatRoomView();
        });
        add(enterChatLayout);

    }

    private void showChatRoomView() {
        MessageListDivView messageListDiv = new MessageListDivView();

        HorizontalLayout imputmessageLayout = new HorizontalLayout();
        TextField enterMessageTfl = new TextField();
        Button sendBtn = new Button("메세지 보내기");
        sendBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        imputmessageLayout.add(enterMessageTfl, sendBtn);
        imputmessageLayout.setWidth("100%");
        imputmessageLayout.expand(enterMessageTfl);

        add(messageListDiv,imputmessageLayout);


        sendBtn.addClickListener(buttonClickEvent -> {
            unicastProcessor.onNext(new ChatMessage(username, enterMessageTfl.getValue()));
            enterMessageTfl.clear();
            enterMessageTfl.focus();
        });

        enterMessageTfl.focus();

        expand(messageListDiv);


        chatMessageFlux.subscribe(chatMessage -> {
            getUI().ifPresent(ui -> {
                ui.access(()->
                        messageListDiv.add(
                                new Paragraph(chatMessage.getFrom() + ": "+ chatMessage.getMessage())
                        ));
            });

        });

    }

    class MessageListDivView extends Div {
        private MessageListDivView(){
            addClassName("message-list");
        }

        @Override
        public void add(Component... components) {
            super.add(components);
            components[components.length-1]
                    .getElement()
                    .callFunction("scrollIntoView");
        }
    }

    private class ChatMessage {
        private String from;
        private String message;

        ChatMessage(String from, String message){
            this.from = from;
            this.message = message;
        }

        String getFrom(){
            return from;
        }

        String getMessage(){
            return message;
        }
    }
}
