package com.solace.samples;

/**
 *  Copyright 2016 Solace Systems, Inc. All rights reserved.
 *
 *  http://www.solacesystems.com
 *
 *  This source is distributed under the terms and conditions of
 *  any contract or license agreement between Solace Systems, Inc.
 *  ("Solace") and you or your company. If there are no licenses or
 *  contracts in place use of this source is not authorized. This
 *  source is provided as is and is not supported by Solace unless
 *  such support is provided for under an agreement signed between
 *  you and Solace.
 */

import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.CapabilityType;

public class CapabilityDetection {

    public static void main(String... args) throws JCSMPException {
        // Check command line arguments
        if (args.length < 1) {
            System.out.println("Usage: CapabilityDetection <msg_backbone_ip:port>");
            System.exit(-1);
        }
        System.out.println("CapabilityDetection initializing...");

        // Create a JCSMP Session
        final JCSMPProperties properties = new JCSMPProperties();
        properties.setProperty(JCSMPProperties.HOST, args[0]);     // host:port
        properties.setProperty(JCSMPProperties.USERNAME, args[1].split("@")[0]); // client-username
        properties.setProperty(JCSMPProperties.PASSWORD, args[2]); // client-password
        properties.setProperty(JCSMPProperties.VPN_NAME,  args[1].split("@")[1]); // message-vpn

        final JCSMPSession session =  JCSMPFactory.onlyInstance().createSession(properties);
        session.connect();

        // Detecting Various Important Capabilities
        //
        // Checks if the application can publish messages using the Persistent Delivery mode
        if (session.isCapable(CapabilityType.PUB_GUARANTEED)) {
            System.out.println("\nGuaranteed Publish Flow - Allowed");
        } else {
            System.out.println("Guaranteed Publish Flow - Not Allowed");
        }

        // Checks if the application can receive messages from Endpoints, such as Queues
        if (session.isCapable(CapabilityType.SUB_FLOW_GUARANTEED)) {
            System.out.println("Guaranteed Subscription Flow - Allowed");
        } else {
            System.out.println("Guaranteed Subscription Flow - Not Allowed");
        }

        // Checks if the application can create and delete Endpoints, such as Durable Queues
        if (session.isCapable(CapabilityType.ENDPOINT_MANAGEMENT)) {
            System.out.println("Endpoint Management - Allowed");
        } else {
            System.out.println("Temporary Endpoints - Not Allowed");
        }

        // Checks if the application can create Temporary Endpoints, such as Non-Durable Queues
        if (session.isCapable(CapabilityType.TEMP_ENDPOINT)) {
            System.out.println("Temporary Endpoints - Allowed");
        } else {
            System.out.println("Temporary Endpoints - Not Allowed");
        }

        // Checks if the application can browse messages from Queues using a QueueBrowser
        if (session.isCapable(CapabilityType.BROWSER)) {
            System.out.println("Queue Browser - Allowed");
        } else {
            System.out.println("Queue Browser - Not Allowed");
        }

        // Checks if the application can add/remove subscriptions on behalf of other clients (OBO - On-Behalf-Of Subscription Manager)
        if (session.isCapable(CapabilityType.SUBSCRIPTION_MANAGER)) {
            System.out.println("On-Behalf-Of Subscription Manager - Allowed");
        } else {
            System.out.println("On-Behalf-Of Subscription Manager - Not Allowed");
        }

        // Retrieves the SolOS version of the Solace Message Router the session is connected to
        String version = (String) session.getCapability(CapabilityType.PEER_SOFTWARE_VERSION);
        System.out.println("SolOS version = " + version);

        // Retrieves the name of the Solace Message Router. This property is useful when sending SEMP requests to a
        // router's SEMP topic, which may be constructed as ' #P2P/routername/#client/SEMP'.
        String routerName = (String) session.getCapability(CapabilityType.PEER_ROUTER_NAME);
        System.out.println("Solace Message Router Name = " + routerName);

        // Retrieves the maximum size allowed of a Direct message (in bytes), including all optional message headers and data
        int maxDirectMsgSize = (Integer) session.getCapability(CapabilityType.MAX_DIRECT_MSG_SIZE);
        System.out.println("MAX Direct Message Size = " + maxDirectMsgSize);

        // Retrieves the maximum size allowed of a Guaranteed message (in bytes), including all optional message headers and data
        int maxGuaranteedMsgSize = (Integer) session.getCapability(CapabilityType.MAX_GUARANTEED_MSG_SIZE);
        System.out.println("MAX Guaranteed Message Size = " + maxGuaranteedMsgSize);


        System.out.println("\nCompleted capability detection. Exiting.");
        session.closeSession();
    }
}