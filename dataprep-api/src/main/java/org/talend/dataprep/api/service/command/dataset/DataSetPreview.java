package org.talend.dataprep.api.service.command.dataset;

import static org.talend.dataprep.api.service.command.common.Defaults.emptyStream;
import static org.talend.dataprep.api.service.command.common.Defaults.pipeStream;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.function.BiFunction;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.talend.daikon.exception.ExceptionContext;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.service.PreparationAPI;
import org.talend.dataprep.api.service.command.common.GenericCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;

@Component
@Scope("request")
public class DataSetPreview extends GenericCommand<InputStream> {

    public DataSetPreview(HttpClient client, String dataSetId, boolean metadata, boolean columns, String sheetName) {
        super(PreparationAPI.TRANSFORM_GROUP, client);
        execute(() -> onExecute(dataSetId, metadata, columns, sheetName));
        onError((e) -> new TDPException(APIErrorCodes.UNABLE_TO_RETRIEVE_DATASET_CONTENT, e,
                ExceptionContext.build().put("id", dataSetId)));
        on(HttpStatus.ACCEPTED, HttpStatus.NO_CONTENT).then(emptyStream());
        on(HttpStatus.OK).then(pipeStream());
        // Move permanently/temporarily behaviors
        BiFunction<HttpRequestBase, HttpResponse, InputStream> move = (req, res) -> {
            Exception cause = new Exception(res.getStatusLine().getStatusCode() + ":" //
                    + res.getStatusLine().getReasonPhrase());
            throw new TDPException(APIErrorCodes.DATASET_REDIRECT, cause, ExceptionContext.build().put("id", dataSetId));
        };
        on(HttpStatus.MOVED_PERMANENTLY, HttpStatus.FOUND).then(move);
    }

    private HttpRequestBase onExecute(String dataSetId, boolean metadata, boolean columns, String sheetName) {
        try {
            StringBuilder url = new StringBuilder(
                    datasetServiceUrl + "/datasets/" + dataSetId + "/preview/?metadata=" + metadata + "&columns=" + columns);
            if (StringUtils.isNotEmpty(sheetName)) {
                // yup this sheet name can contains weird characters space, great french accents or even chinese
                // characters
                url.append("&sheetName=").append(URLEncoder.encode(sheetName, "UTF-8"));
            }
            return new HttpGet(url.toString());
        } catch (UnsupportedEncodingException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

}
