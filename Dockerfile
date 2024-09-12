FROM rabbitmq:3.13.7-management

RUN rabbitmq-plugins enable --offline rabbitmq_stomp rabbitmq_web_stomp