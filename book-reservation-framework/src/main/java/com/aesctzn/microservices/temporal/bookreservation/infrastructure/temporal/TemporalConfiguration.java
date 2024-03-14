package com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal;


import com.uber.m3.tally.Scope;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.RpcRetryOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkerFactoryOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLException;
import java.io.File;
import java.time.Duration;

//@Configuration
@Slf4j
public class TemporalConfiguration {



    /** WorkflowClient
     * Es una clase proporcionada por la biblioteca de cliente de Temporal que se utiliza para interactuar
     * con el servicio de Temporal.
     *
     * Esencialmente, actúa como una factoría para crear instancias de clientes de flujo de trabajo (WorkflowStub)
     */
    @Bean
    public WorkflowClient getWorkflowClient(WorkflowServiceStubs workflowServiceStubs){
        //WorkflowClientOptions workflowClientOptions = WorkflowClientOptions.getDefaultInstance();
        //WorkflowClientOptions options = WorkflowClientOptions.newBuilder()
        //        .setNamespace("exampleNamespace") // Establecer el espacio de nombres (namespace) para el cliente
        //        .setInterceptors(myCustomInterceptor) // Establecer interceptores personalizados
        //        .setMetricsScope(myMetricsScope) // Establecer el alcance de las métricas
        //        .setContextPropagators(myContextPropagator) // Establecer propagadores de contexto
        //        .setRpcRetryOptions(myRpcRetryOptions) // Establecer opciones de reintentos para RPC
        //        .setRpcTimeout(Duration.ofSeconds(30)) // Establecer tiempo de espera para RPC
        //        .setChannelShutdownTimeout(Duration.ofSeconds(5)) // Establecer tiempo de espera para el cierre del canal
        //        .build();
        WorkflowClientOptions workflowClientOptions = WorkflowClientOptions.newBuilder()
               .setNamespace("test")
               .build();
        return WorkflowClient.newInstance(workflowServiceStubs,workflowClientOptions);
    }


    /** WorkerFactory
     * Es una clase proporcionada por la biblioteca de cliente de Temporal que se utiliza para crear y gestionar instancias de workers de Temporal.
     * Son responsables de crear una instancia de un worker asociado a una cola y la instancia del worker permite registrar Workflows y actividades
     *
     * WorkerFactory simplifica la configuración y gestión de workers en una aplicación de Temporal.
     * Proporciona métodos para crear y configurar workers, así como para iniciar y detener su ejecución.
     */
    @Bean
    public WorkerFactory getWorkerFactory(WorkflowClient workflowClient){
        WorkerFactoryOptions workerFactoryOptions = WorkerFactoryOptions.getDefaultInstance();
        //WorkerFactoryOptions options = WorkerFactoryOptions.newBuilder()
        //        .setMaxWorkflowThreadCount(100) // Establecer el tamaño máximo de ejecución de workflowsconcurrentes
        //        .setWorkflowCacheSize(100) // Establecer el tamaño máximo del caché de workflows
        //        .build();
        //mirar los hilos concurrentes
        return WorkerFactory.newInstance(workflowClient,workerFactoryOptions);
    }

    /** WorkflowServiceStubs
     * Es una clase proporcionada por la biblioteca de cliente de Temporal que se utiliza para crear WorkflowClient de Temporal.
     * Estos clientes se utilizan para interactuar con el servidor de Temporal para iniciar, consultar y gestionar flujos de trabajo y actividades.
     *
     * En esencia, WorkflowServiceStubs configura la conexión con Temporal y permite crear WorkflowClient.*/

    @Bean
    public WorkflowServiceStubs getWorkflowServiceStubs(TemporalConnectionProperties temporalProperties) throws SSLException {
        WorkflowServiceStubs service;

        if (!temporalProperties.isDefaultConfiguration()) {
            WorkflowServiceStubsOptions options = mapWorkflowServiceStubsOptions(temporalProperties);
            service = WorkflowServiceStubs.newServiceStubs(options);
            log.info("Creando Conexión con Temporal Server");
        } else {
            log.info("Creando Conexión por defecto con Temporal Server");
            service = WorkflowServiceStubs.newLocalServiceStubs();
        }
        checkTemporalConnection(service);
        return service;

    }

    private WorkflowServiceStubsOptions mapWorkflowServiceStubsOptions(TemporalConnectionProperties temporalProperties) throws SSLException {


        WorkflowServiceStubsOptions.Builder builder = WorkflowServiceStubsOptions.newBuilder();

        /**El cliente de Temporal utiliza un canal de comunicación para enviar y recibir mensajes al servidor.
         * Este canal puede ser configurado para usar diferentes protocolos de comunicación, como gRPC, HTTP/1.1 o HTTP/2.
         * Por ejemplo, si estás utilizando gRPC como protocolo de comunicación, puedes configurar el canal para que utilice un canal gRPC personalizado*/
        //ManagedChannelBuilder<?> channel = ManagedChannelBuilder.forAddress(temporalProperties.getHost(), temporalProperties.getPort());
        //channel.usePlaintext();
        //builder.setChannel(channel.build());

        /**
         * Configuración GRPC, se está especificando cuánto tiempo debe mantenerse abierta la llamada RPC de long-polling
         * antes de que se cierre automáticamente si no se recibe una respuesta del servidor. Este tiempo de espera ayuda a evitar
         * que la llamada RPC se bloquee indefinidamente si el servidor no responde o si la respuesta tarda más de lo esperado
         * builder.setRpcLongPollTimeout(Duration.ofSeconds(1));*/
        //builder.setRpcLongPollTimeout(Duration.ofSeconds(1));
        //builder.setRpcTimeout(Duration.ofSeconds(30));
        //builder.setRpcQueryTimeout(Duration.ofSeconds(30));

        /** KeepAlive es un mecanismo que permite mantener abiertas las conexiones de red durante un período de tiempo determinado,
         * incluso después de que se haya completado una operación,
         * para evitar el costo de establecer una nueva conexión para cada solicitud.*/
        builder.setEnableKeepAlive(true);
        builder.setKeepAliveTimeout(Duration.ofMinutes(5));
        builder.setKeepAliveTime(Duration.ofSeconds(60));

        /**Cuando una conexión entre el cliente y el servidor de Temporal se pierde debido a problemas de red, fallos del servidor, etc.,
         * el cliente intentará reconectarse automáticamente al servidor para reanudar la comunicación**/
        builder.setGrpcReconnectFrequency(Duration.ofSeconds(30));

        /**Un SSLContext en Java representa un conjunto de claves y certificados que se utilizan para autenticar a las partes en una conexión segura,
         * así como los protocolos de seguridad y algoritmos de cifrado que se utilizarán durante la comunicación.*/
        //File certFile = new File("path/to/certificate.crt");
        //File keyFile = new File("path/to/privatekey.key");
        //SslContext sslContext = SslContextBuilder.forServer(certFile, keyFile).build();
        //builder.setSslContext(sslContext);
        //builder.setEnableHttps(true);
        builder.setEnableHttps(false);

        /**Cuando una operación RPC falla debido a un error temporal, como una conexión interrumpida, una respuesta de error del servidor,etc.,
         * el cliente de Temporal puede intentar automáticamente volver a realizar la operación varias veces antes de dar por finalizado el proceso
         * y devolver un error al usuario.*/
        RpcRetryOptions rpcRetryOptions = RpcRetryOptions.newBuilder()
                .setMaximumAttempts(3) // Número máximo de intentos de reintentos
                .setInitialInterval(Duration.ofSeconds(1)) // Intervalo inicial entre intentos
                .setMaximumInterval(Duration.ofSeconds(10)) // Intervalo máximo entre intentos
                .setExpiration(Duration.ofMinutes(5)) // Tiempo de expiración total para la operación
                .build();
        builder.setRpcRetryOptions(rpcRetryOptions);

        return builder.build();
    }

    private static boolean checkTemporalConnection(WorkflowServiceStubs workflowServiceStubs) {
        try {
            workflowServiceStubs.healthCheck();
            log.info("Conexión con Temporal establecida");
            return true;
        } catch (Exception e) {
            log.error("Error de conexión con temporal");
            return false;
        }
    }
}
