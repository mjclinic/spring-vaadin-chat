package demo.spring.vaadin.chat;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.crud.CrudI18n;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

import java.util.List;


@Route
@PWA(name = "Chat for Vaadin Flow with Spring", shortName = "Chat")
@StyleSheet("frontend://styles.css")
@Push
public class MainView extends VerticalLayout {
    private String username;
    private final UnicastProcessor<ChatMessage> unicastProcessor;
    private final Flux<ChatMessage> chatMessageFlux;
    private final PatientsService patientsService;

    public MainView(UnicastProcessor<ChatMessage> unicastProcessor, Flux<ChatMessage> chatMessageFlux, PatientsService patientsService) {
        this.unicastProcessor = unicastProcessor;
        this.chatMessageFlux = chatMessageFlux;
        this.patientsService = patientsService;

        addClassName("main-view");
        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        H1 h1 = new H1("Chatting");
        h1.getElement().getThemeList().add("dark");

        add(h1);

        enterChatRoomView();

        gridPatientsView();
    }

    private void gridPatientsView() {
        VerticalLayout listPatientsLayout = new VerticalLayout();
        setSizeFull();

        Crud<Patient> crud = new Crud<>(Patient.class,
            newGridPatient(),
            bindCrudPatientEdtor());

        crud.setMaxHeight("800px");
        crud.setWidth("100%");
        ListDataProvider<Patient> dataProvider =
                new ListDataProvider<Patient>(this.patientsService.getByNameLike("test").collectList().block());


        crud.setDataProvider(dataProvider);
        listPatientsLayout.setHorizontalComponentAlignment(Alignment.CENTER, crud);


        CrudI18n crudI18n = CrudI18n.createDefault();
        crudI18n.setEditItem("편집");
        crudI18n.setNewItem("새로입력");
        crudI18n.setDeleteItem("삭제");
        crudI18n.setSaveItem("저장");
        crudI18n.setCancel("취소");

        crud.setI18n(crudI18n);

        crud.addNewListener(patientNewEvent ->
                this.patientsService.create(patientNewEvent.getItem()).block());
                dataProvider.refreshAll();
        crud.addEditListener(patientEditEvent ->
                this.patientsService.update(patientEditEvent.getItem()).block());
                dataProvider.refreshAll();
        crud.addDeleteListener(patientDeleteEvent ->
                this.patientsService.delete(patientDeleteEvent.getItem().getId()).block());
                dataProvider.refreshAll();
        crud.addSaveListener(patientSaveEvent ->
                this.patientsService.update(patientSaveEvent.getItem()).block());
                dataProvider.refreshAll();

        listPatientsLayout.add(crud);
        add(listPatientsLayout);

    }

    private CrudEditor<Patient> bindCrudPatientEdtor() {
        TextField idTf = new TextField("ID");
        idTf.setRequiredIndicatorVisible(false);
        idTf.getElement().setAttribute("colspan", Integer.toString(4));

        TextField nameTf = new TextField("이름");
        nameTf.setRequiredIndicatorVisible(true);
        nameTf.getElement().setAttribute("colspan", Integer.toString(2));

        TextField zuminTf = new TextField("주민번호");
        zuminTf.setRequiredIndicatorVisible(false);
        zuminTf.getElement().setAttribute("colspan", Integer.toString(4));

        TextField telTf = new TextField("전화번호");
        telTf.setRequiredIndicatorVisible(true);
        telTf.getElement().setAttribute("colspan", Integer.toString(2));

        DatePicker lastdayDp = new DatePicker("마지막 내원일");
        lastdayDp.setRequiredIndicatorVisible(true);
        lastdayDp.getElement().setAttribute("colspan", Integer.toString(4));



        TextField fardayTf = new TextField("최근 방문");
        fardayTf.getElement().setAttribute("colspan", Integer.toString(4));


        FormLayout formLayout = new FormLayout(idTf,nameTf,zuminTf,telTf,lastdayDp,fardayTf);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 4));

        Binder<Patient> binder = new Binder<>(Patient.class);

        binder.bind(idTf, Patient::getId, Patient::setId);
        binder.bind(nameTf, Patient::getName, Patient::setName);
        binder.forField(zuminTf).withConverter(new StringToIntegerConverter("숫자만 가능"))
                .bind(Patient::getZumin, Patient::setZumin);

         //       bind(zuminTf, patient -> String.valueOf(patient.getZumin()), (patient, s) -> patient.setZumin(Integer.getInteger(s)));

        binder.bind(telTf, Patient::getTel, Patient::setTel);

        binder.bind(lastdayDp,Patient::getLastday, Patient::setLastday);
        binder.bind(fardayTf, Patient::getFarday, Patient::setFarday);


        return new BinderCrudEditor<>(binder, formLayout);
    }

    private Grid<Patient> newGridPatient() {
        Grid<Patient> grid = new Grid<>();

        grid.addColumn(patient -> patient.getId()).setHeader("ID");
        grid.addColumn(patient -> patient.getName()).setHeader("이름");
        grid.addColumn(patient -> patient.getZumin()).setHeader("주민번호");
        grid.addColumn(patient -> patient.getTel()).setHeader("전화번호");
        grid.addColumn(patient -> patient.getLastday()).setHeader("최종방문일");
        grid.addColumn(patient -> patient.getFarday()).setHeader("최근 방문");


        Crud.addEditColumn(grid);
        return grid;
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

   class ChatMessage {
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
