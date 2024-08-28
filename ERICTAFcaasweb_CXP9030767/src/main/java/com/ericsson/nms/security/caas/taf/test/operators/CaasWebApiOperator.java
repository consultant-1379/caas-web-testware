package com.ericsson.nms.security.caas.taf.test.operators;

import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.data.Host;

@Operator(context = Context.API)
public class CaasWebApiOperator implements CaasWebOperator {

    @Override
    public boolean authenticateAuthorise(
            Host host, String url, String configFileLocationOnServer, String clientCert, String clientPrivateKey, String aaqRequestParams, String responseSyntax) {
        return false;
    }

}
