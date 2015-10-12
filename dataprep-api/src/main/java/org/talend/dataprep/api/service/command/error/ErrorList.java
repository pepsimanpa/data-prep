package org.talend.dataprep.api.service.command.error;

import static org.talend.dataprep.api.service.command.common.Defaults.emptyStream;
import static org.talend.dataprep.api.service.command.common.Defaults.pipeStream;

import java.io.InputStream;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.service.command.common.GenericCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.APIErrorCodes;

import com.netflix.hystrix.HystrixCommandGroupKey;

/**
 * Command used to list the supported error codes from low level api.
 */
@Component
@Scope("prototype")
public class ErrorList extends GenericCommand<InputStream> {

    /**
     * Private constructor.
     *
     * @param client the http client to use to reach the api.
     * @param type the api type.
     * @param groupKey the command group key.
     */
    private ErrorList(HttpClient client, HystrixCommandGroupKey groupKey, ServiceType type) {
        super(groupKey, client);
        execute(() -> onExecute(type));
        onError(e -> new TDPException(APIErrorCodes.UNABLE_TO_LIST_ERRORS, e));
        on(HttpStatus.NO_CONTENT, HttpStatus.ACCEPTED).then(emptyStream());
        on(HttpStatus.OK).then(pipeStream());
    }

    private HttpRequestBase onExecute(ServiceType type) {
        final String serviceUrl;
        switch (type) {
        case DATASET:
            serviceUrl = datasetServiceUrl + "/datasets/errors";
            break;
        case TRANSFORMATION:
            serviceUrl = transformationServiceUrl + "/transform/errors";
            break;
        case PREPARATION:
            serviceUrl = preparationServiceUrl + "/preparations/errors";
            break;
        default:
            throw new IllegalArgumentException("Type '" + type + "' is not supported.");
        }
        return new HttpGet(serviceUrl);
    }

    public enum ServiceType {
                             DATASET,
                             TRANSFORMATION,
                             PREPARATION
    }

}
