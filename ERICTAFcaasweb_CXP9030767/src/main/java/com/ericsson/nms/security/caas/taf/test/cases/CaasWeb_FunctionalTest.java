package com.ericsson.nms.security.caas.taf.test.cases;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.*;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.cifwk.taf.handlers.RemoteFileHandler;
import com.ericsson.cifwk.taf.tools.http.HttpResponse;
import com.ericsson.cifwk.taf.tools.http.HttpTool;
import com.ericsson.cifwk.taf.tools.http.HttpToolBuilder;
import com.ericsson.cifwk.taf.utils.FileFinder;
import com.ericsson.nms.security.caas.taf.test.getters.CaasWebGetter;
import com.ericsson.nms.security.caas.taf.test.operators.CaasWebOperator;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.io.File;

public class CaasWeb_FunctionalTest extends TorTestCaseHelper implements TestCase {

    @Inject
    private OperatorRegistry<CaasWebOperator> caasWebProvider;

    @Inject
    private CaasWebGetter caasWebGetter;
    private RemoteFileHandler remoteFileHandler;

    private static final String CERTS_LOCAL_FOLDER = "certs/";
    private static final String CERTS_REMOTE_FOLDER = "/ericsson/tor/data/autointegration/files/tmp/";
    private static final String caasweb_client_cert = "caasweb_client_cert.crt";
    private static final String caasweb_client_pk = "caasweb_client_cert.priv";
    private static final String caasweb_client_inval_cert = "caasweb_client_inval-cert.crt";
    private static final String caasweb_client_inval_privkey = "caasweb_client_inval_privkey.priv";
    private static final String client_crt_remote = CERTS_REMOTE_FOLDER + caasweb_client_cert;
    private static final String client_pk_remote = CERTS_REMOTE_FOLDER + caasweb_client_pk;
    private static final String client_invalid_crt_remote = CERTS_REMOTE_FOLDER + caasweb_client_inval_cert;
    private static final String client_invalid_pk_remote = CERTS_REMOTE_FOLDER + caasweb_client_inval_privkey;

    @BeforeSuite
    public void copyFiles() {
        remoteFileHandler = new RemoteFileHandler(TafHosts.getSC1());

        //workaround for file read error
        try {
            remoteFileHandler.copyLocalFileToRemote(FileFinder.findFile("caasweb_client_cert.crt").get(0),client_crt_remote);
            remoteFileHandler.copyLocalFileToRemote(FileFinder.findFile("caasweb_client_cert.priv").get(0), client_pk_remote);
            remoteFileHandler.copyLocalFileToRemote(FileFinder.findFile("caasweb_client_inval-cert.crt").get(0), client_invalid_crt_remote);
            remoteFileHandler.copyLocalFileToRemote(FileFinder.findFile("caasweb_client_inval_privkey.priv").get(0), client_invalid_pk_remote);
        } catch (Exception e) {
            System.out.println("error copying files to remote server: "+e.toString());
        }

//        if (new File(CERTS_LOCAL_FOLDER).exists()) {
//            remoteFileHandler.copyLocalFileToRemote(CERTS_LOCAL_FOLDER + caasweb_client_cert, client_crt_remote);
//            remoteFileHandler.copyLocalFileToRemote(CERTS_LOCAL_FOLDER + caasweb_client_pk, client_pk_remote);
//            remoteFileHandler.copyLocalFileToRemote(CERTS_LOCAL_FOLDER + caasweb_client_inval_cert, client_invalid_crt_remote);
//            remoteFileHandler.copyLocalFileToRemote(CERTS_LOCAL_FOLDER + caasweb_client_inval_privkey, client_invalid_pk_remote);
//        } else {
//            System.out.println("\t" + CERTS_LOCAL_FOLDER + " does not exist.");
//        }
    }

//    @AfterSuite
//    public void removeFiles() {
//        if (remoteFileHandler.remoteFileExists(CERTS_REMOTE_FOLDER)) {
//            remoteFileHandler.deleteRemoteFile(CERTS_REMOTE_FOLDER);
//        }
//    }

    /**
     * @param host_
     * @param url
     * @param configFileLocationOnServer
     * @param clientCrt
     * @param clientPrivateKey
     * @param aaqRequestParams
     * @param expected
     * @param responseSyntax
     * @DESCRIPTION Test case if Caas-web component will return AAQ response
     * string and http code 200(OK)
     * @PRE Application Server is running with REST and SSL certificates
     * @PRIORITY HIGH
     */
    @TestId(id = "TORF-10614_Func_1", title = "TORF-10614_Func_authentication1:  Verify Authentication requests with set of correct authentication parameters to CAAS.")
    @Context(context = {Context.REST})
    @DataDriven(name = "CaasWeb_FunctionalTest")
    //@DataDriven(name = "CaasWeb_FunctionalTestLocalhostTest")
    @Test(groups = {"caas-web_regression"})
    public void tORF10614_Func_Authentication1VerifyAuthenticationRequestsWithSetOfCorrectAuthenticationParametersToCAAS(
            @Input("host") String host_,
            @Input("url") String url,
            @Input("configFileLocationOnServer") String configFileLocationOnServer,
            @Input("clientCrt") String clientCrt,
            @Input("clientPrivateKey") String clientPrivateKey,
            @Input("aaqRequestParams") String aaqRequestParams,
            @Output("result") boolean expected,
            @Input("responseSyntax") String responseSyntax
    ) {

        CaasWebOperator caasWebOperator = caasWebProvider.provide(CaasWebOperator.class);
        setTestInfo("Send a https post request to the server with correct set of authentication parameters.");

        Host host = null;

        if (host_.equals("sc1")) {
            host = TafHosts.getSC1();
        } else if (host_.equals("sc2")) {
            host = TafHosts.getSC2();
        }

        if (host != null) {

            boolean res = caasWebOperator.authenticateAuthorise(
                    host,
                    url,
                    configFileLocationOnServer,
                    clientCrt,
                    clientPrivateKey,
                    aaqRequestParams,
                    responseSyntax);

//            if (expected != res) {
//                System.out.println("\n\thost: " + host.getHostname());
//                System.out.println("\n\turl" + url);
//                System.out.println("\n\tconfigFileLocationOnServer: " + configFileLocationOnServer);
//                System.out.println("\n\tclientCrt: " + clientCrt);
//                System.out.println("\n\tclientPrivateKey: " + clientPrivateKey);
//                System.out.println("\n\taaqRequestParams: " + aaqRequestParams);
//            }
            assertSame(expected, res);
        } else {
            System.out.println("\n\tThe host name " + host_ + " is inocorrect.");
            fail();
        }

        // useHttpTool(host, url);
    }

    private void useHttpTool(Host host, String url) {
        String absolutpath = "ERICTAFcaasweb_CXP9030767/src/main/resources/certs/";
        String clientPrivateKey = absolutpath + "caasweb_client_cert.pem";
        String clientCrt = absolutpath + "caasweb_client_privkey.pem";
        if (new File(clientPrivateKey).exists()
                && new File(clientCrt).exists()) {

            HttpTool tool = HttpToolBuilder.newBuilder(host)
                    .setSslKeyAndCert(clientCrt, clientPrivateKey)
                    .trustSslCertificates(true)
                    .useHttpsIfProvided(true)
                    .build();

            HttpResponse response = tool.request()
                    .queryParam("userID", "testuser")
                    .queryParam("queryType", "1.3.6.1.4.1.193.140.1.1")
                    .queryParam("aaqVersion", "0")
                    .queryParam("sessionID", "347")
                    .queryParam("authMethod", "1.3.6.1.4.1.193.140.1.4")
                    .queryParam("authenticator", "secret").post(url);

            System.out.println("\n response = " + response.getResponseCode());
        }

        /**
         * @DESCRIPTION Test case if Caas-web component will return AAQ response
         * string and http code 200(OK)
         * @PRE Application Server is running with REST and SSL certificates
         * @PRIORITY HIGH
         */
//    @TestId(id = "TORF-10614_Func_2", title = "TORF-10614_Func_authorization2:  Verify Authorisation requests with set of correct set of authorization parameters to CAAS.")
//    @Context(context = {Context.REST, Context.API})
//    @DataDriven(name = "CaasWeb_FunctionalTest")
//    @Test(groups = {"{Acceptance", "caas-web_regression}"})
//    public void tORF10614_Func_Authorization2VerifyAuthorisationRequestsWithSetOfCorrectSetOfAuthorizationParametersToCAAS(
//            @Input("host") String host_,
//            @Input("url") String url,
//            @Input("configFileLocationOnServer") String configFileLocationOnServer,
//            @Input("clientCrt") String clientCrt,
//            @Input("clientPrivateKey") String clientPrivateKey,
//            @Input("aaqRequestParams") String aaqRequestParams
//    ) {
//
//        CaasWebOperator caasWebOperator = caasWebProvider.provide(CaasWebOperator.class);
//        setTestInfo("Send a https post request to the server with correct set of authorization parameters.");
//        //TODO VERIFY:Verify AAQ authorization response string is returned.
//        Host host = DataHandler.getHostByName(host_);
//        boolean res = caasWebOperator.authenticateAuthorise(host, url, configFileLocationOnServer, clientCrt, clientPrivateKey, aaqRequestParams);
//        assertTrue(res);
//
//    }
        /**
         * @DESCRIPTION Test case if Caas-web component will return http code
         * 401 Unauthorized(not be confirmed)
         * @PRE Application Server is running with REST and SSL certificates
         * @PRIORITY HIGH
         */
//    @TestId(id = "TORF-10614_Func_3", title = "TORF-10614_Func_authentication3:  Test requests with set of invalid certificate.")
//    @Context(context = {Context.REST, Context.API})
//    @Test(groups = {"{Acceptance", "caas-web_regression}"})
//    public void tORF10614_Func_Authentication3TestRequestsWithSetOfInvalidCertificate() {
//
//        CaasWebOperator caasWebOperator = caasWebProvider.provide(CaasWebOperator.class);
//        setTestInfo("Send a https post request to the server with incorrect set of invalid certificate.");
//        //TODO VERIFY:Verify authentication http code 401 Unauthorized (not be confirmed) is returned
//        //throw new TestCaseNotImplementedException();
//    }
        /**
         * @DESCRIPTION Test case if Caas-web component will return AAQ response
         * strings and http code 200(OK)
         * @PRE Application Server is running with REST and SSL certificates
         * @PRIORITY HIGH
         */
//    @TestId(id = "TORF-10614_Func_4", title = "TORF-10614_Func_authorization4:  Verify Caasweb authentication and authorization requests when used by NE at security level 3 (simulated NE)")
//    @Context(context = {Context.REST, Context.API})
//    @Test(groups = {"{Acceptance", "caas-web_regression}"})
//    public void tORF10614_Func_Authorization4VerifyCaaswebAuthenticationAndAuthorizationRequestsWhenUsedByNEAtSecurityLevel3SimulatedNE() {
//
//        CaasWebOperator caasWebOperator = caasWebProvider.provide(CaasWebOperator.class);
//        setTestInfo("Send a https post requests to the server with correct set of authorization and authorization parameters.");
//        //TODO VERIFY:Verify AAQ authentication and authorization response strings are returned.
//        // throw new TestCaseNotImplementedException();
//    }
        /**
         * @DESCRIPTION Test case if Caas-web component will return http code
         * Unauthorized(401)
         * @PRE Application Server is running with REST and SSL certificates
         * @PRIORITY HIGH
         */
//    @TestId(id = "TORF-10614_Func_5", title = "TORF-10614_Func_authentication5:  Test valid requests for both SC1 and SC2 IPs.")
//    @Context(context = {Context.REST, Context.API})
//    @Test(groups = {"{Acceptance", "caas-web_regression}"})
//    public void tORF10614_Func_Authentication5TestValidRequestsForBothSC1AndSC2IPs() {
//
//        CaasWebOperator caasWebOperator = caasWebProvider.provide(CaasWebOperator.class);
//        setTestInfo("Send a https post request to the server with invalid client certificate");
//        //TODO VERIFY:Verify authentication http code Unauthorized(401) is returned
//        //throw new TestCaseNotImplementedException();
//    }
//    @TestId(id = "datadriven", title = "DataDriven Example")
//    @Test
//    @DataDriven(name = "caasweb_functionaltest")
//    public void shouldBePopulatedFromCsv(@Input("first") int x, @Input("second") int y, @Output("result") int expected) {
//          assertEquals(x + y, expected);
//          
//          
//          
//    }
    }
}
