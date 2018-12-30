package com.example.demo;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Sspi;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsSecurityContext;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;
import waffle.windows.auth.impl.WindowsSecurityContextImpl;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.security.Principal;

@RestController
public class SecurityController {

    @RequestMapping(value = "/principal", method = RequestMethod.GET)
    public String httpservlet(Principal principal) {
        System.out.println(principal.getName());
        return principal.getName();

    }

    @RequestMapping(value = "/authetication", method = RequestMethod.GET)
    public String httpservlet(Authentication authentication) {
        System.out.println(authentication.getName());
        return authentication.getName();

    }

    @RequestMapping(value = "/httpservlet", method = RequestMethod.GET)
    public String currentUserNameSimple(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        System.out.println(principal.getName());
//        System.out.println(request.getRemoteUser());
//        System.out.println(request.getRemoteHost());
//        System.out.println(request.getRequestURI());
//        System.out.println(request.getRequestURL());
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    @RequestMapping(value = "/advap", method = RequestMethod.GET)
    public String advap() {
        StringBuilder sb = new StringBuilder();
        for(Advapi32Util.Account account: Advapi32Util.getCurrentUserGroups()) {
            System.out.println(account.fqn);
            sb.append(account.fqn).append("\n");
        }

        return sb.toString();
    }

    @RequestMapping(value = "/waffle", method = RequestMethod.GET)
    public String waffle(HttpServletRequest request) {

        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        // initialize a security context on the client
        IWindowsSecurityContext clientContext = WindowsSecurityContextImpl.getCurrent( "Negotiate", ipAddress );

        // create an auth provider and a security context for the client
        // on the server
        WindowsAuthProviderImpl provider = new WindowsAuthProviderImpl();
        IWindowsSecurityContext serverContext = null;

        // now you would send the byte[] token to the server and the server will
        // response with another byte[] token, which the client needs to answer again
        do {

            // Step 2: If you have already build an initial security context for the client
            // on the server, send a token back to the client, which the client needs to
            // accept and send back to the server again (a handshake)
            if (serverContext != null) {
                byte[] tokenForTheClientOnTheServer = serverContext.getToken();
                Sspi.SecBufferDesc continueToken = new Sspi.SecBufferDesc(Sspi.SECBUFFER_TOKEN, tokenForTheClientOnTheServer);
                clientContext.initialize(clientContext.getHandle(), continueToken, "localhost");
            }

            // Step 1: accept the token on the server and build a security context
            // representing the client on the server
            byte[] tokenForTheServerOnTheClient = clientContext.getToken();
            serverContext = provider.acceptSecurityToken("server-connection", tokenForTheServerOnTheClient, "Negotiate");

        } while (clientContext.isContinue());

        // at the end of this handshake, we know on the server side who the
        // client is, only by exchanging byte[] arrays
        System.out.println(serverContext.getIdentity().getFqn());

        return serverContext.getIdentity().getFqn();
    }


    @RequestMapping(value = "/doGet", method = RequestMethod.GET)
    public String doGet() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(user.getUsername());
        return System.getProperty("user.name");
    }





}
