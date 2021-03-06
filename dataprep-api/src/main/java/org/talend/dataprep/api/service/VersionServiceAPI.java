// ============================================================================
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.dataprep.api.service;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.talend.dataprep.dataset.DatasetConfiguration.Service.Provider.CATALOG;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.talend.dataprep.api.service.version.DatasetVersionSupplier;
import org.talend.dataprep.api.service.version.VersionsSupplier;
import org.talend.dataprep.info.BuildDetails;
import org.talend.dataprep.info.Version;
import org.talend.dataprep.metrics.Timed;
import org.talend.dataprep.security.PublicAPI;

import io.swagger.annotations.ApiOperation;

@RestController
public class VersionServiceAPI extends APIService {

    @Value("${dataprep.display.version}")
    private String applicationVersion;

    @Autowired
    private Environment environment;

    @Autowired
    private List<VersionsSupplier> versionsSuppliers;

    /**
     * Returns all the versions of the different services (api, dataset, preparation and transformation) and the global
     * application version.
     *
     * @return an array of service versions
     */
    @RequestMapping(value = "/api/version", method = GET)
    @ApiOperation(value = "Get the version of all services (including underlying low level services)",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @PublicAPI
    public BuildDetails allVersions() {
        Stream<VersionsSupplier> supplierStream = versionsSuppliers.stream();
        final String datasetProvider = environment.getProperty("dataset.service.provider");
        if (CATALOG.name().equalsIgnoreCase(datasetProvider)) {
            supplierStream =
                    supplierStream.filter(versionsSupplier -> !(versionsSupplier instanceof DatasetVersionSupplier));
        }

        final Version[] versions = supplierStream //
                .map(VersionsSupplier::getVersions)
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .toArray(Version[]::new);

        return new BuildDetails(applicationVersion, versions);
    }
}
