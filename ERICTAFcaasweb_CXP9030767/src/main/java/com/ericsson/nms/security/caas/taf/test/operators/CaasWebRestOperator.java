package com.ericsson.nms.security.caas.taf.test.operators;

import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.handlers.RemoteFileHandler;
import com.ericsson.cifwk.taf.handlers.implementation.SshRemoteCommandExecutor;
import com.ericsson.nms.security.caas.taf.test.cases.TafHosts;

@Operator(context = Context.REST)
public class CaasWebRestOperator implements CaasWebOperator {

    private SshRemoteCommandExecutor executor;    

    @Override
    public boolean authenticateAuthorise(
            Host host,
            String url,
            String configFileLocationOnServer,
            String clientCert,
            String clientPrivateKey,
            String aaqRequestParams,
            String responseSyntax) {

        String result = "";
        executor = new SshRemoteCommandExecutor(host);
        RemoteFileHandler remoteFileHandler = new RemoteFileHandler(host);

       
        if (remoteFileHandler.remoteFileExists(clientPrivateKey) && remoteFileHandler.remoteFileExists(clientCert)) {

            System.out.println("\n\thost IP:"+host.getIp());
            System.out.println("\n\thost name:"+host.getHostname());

            String culrOutputFile = configFileLocationOnServer + "tmp/" + "curloutput";
            String curlCommand = buildCulrCommand(aaqRequestParams, clientPrivateKey, clientCert, url, culrOutputFile);

            System.out.println("\n\tExecuting curlCommand: " + curlCommand);
            executor.simplExec(curlCommand);

            if (remoteFileHandler.remoteFileExists(culrOutputFile)) {
                result = executor.simplExec("grep success " + culrOutputFile);
                System.out.println("\n\t result:" + result + "|");

                executor.simplExec("rm -f " + culrOutputFile);
            } else {
                return false;
            }

        } else {
            System.out.println("\n\tThe required certificates/private keys do not exist on " + host.getHostname() + ". Test cannot execute.");
            return false;
        }

        return result.equals(responseSyntax);
    }

    private String buildCulrCommand(String aaqRequestParams, String clientPrivateKey, String clientCert, String url, String culrOutputFile) {

        return "curl -v -k --include --request POST --header 'Content-Type: application/x-www-form-urlencoded' "
                + "--data \"" + aaqRequestParams + "\" "
                + "--key " + clientPrivateKey + " "
                + "--cert " + clientCert + " "
                + "https://"+ TafHosts.getsecServ1().getIp() + url + " > "                         //use this against the multinodes!!!!!
                //+ "https://"+ TafHosts.getsecServ1().getOriginalIp() + url + " > "             //use this against the clouds!!!!!
                + culrOutputFile;




    }
}
