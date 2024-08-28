package com.ericsson.nms.security.caas.taf.test.operators;

import com.ericsson.cifwk.taf.data.Host;

public interface CaasWebOperator {

    public boolean authenticateAuthorise(
            Host host, String url, String configFileLocationOnServer, String clientCert, String clientPrivateKey, String aaqRequestParams, String responseSyntax);
}
