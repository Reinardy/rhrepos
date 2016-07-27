package com.sc.retail.csl.notification;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.apache.camel.Exchange;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.ApnsServiceBuilder;
import com.sc.retail.exception.ApplicationException;


public class OTPProcessor {
	@Autowired
	private JaxWsProxyFactoryBean theProxy;
	private final Logger logger = LoggerFactory.getLogger(OTPProcessor.class);
	Properties prop = new Properties();
	
	public OTPProcessor() throws Exception {
		OTPProperties properties = new OTPProperties();
		properties.setLocation("data.properties");
		prop = properties.getProperties();
	}

	public void register(Exchange exchange) throws ApplicationException{
		long start = System.currentTimeMillis();
		Map<String, Object> map = exchange.getIn().getHeaders();
		for (Map.Entry<String, Object> entry : map.entrySet())
		{
		    logger.info(entry.getKey() + " / " + entry.getValue());
		}
		try {
			String custid = exchange.getIn().getHeader("custid").toString();
			String deviceid = exchange.getIn().getHeader("deviceid").toString();
			prop.setProperty(custid, deviceid);
			prop.store(new FileOutputStream("src/main/resources/data.properties"), null);
			Map<String, Object> responseMap = new HashMap<String,Object>();
			responseMap.put("statusCode", 200);
			exchange.getOut().setBody(responseMap);
			String message = "ALERT: You're adding a device id "+  deviceid.substring(0,6) + "*** and custid " + custid.substring(0,4) +"*** , call +6599999999 if not done by you. ";		
	        logger.info("Sending an iOS push notification..." + message);
	        String token = prop.getProperty(custid);
	        callAPNS(token, message);
		}catch(Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			Map<String, Object> responseMap = new HashMap<String,Object>();
			responseMap.put("statuscode", 404 );
			exchange.getOut().setBody(responseMap);
		}
		logger.info("End otp service " + (System.currentTimeMillis() - start) + " ms");	
	}
		
	
	public void validate(Exchange exchange) throws ApplicationException{
		long start = System.currentTimeMillis();
		try {
			String passcode = exchange.getIn().getHeader("passcode").toString();
			String hashcode = exchange.getIn().getHeader("hashcode").toString();
			String passcodedata = "passcode"+hashcode.split(":")[1];
			logger.info("Passcode data = " + passcodedata);
			String pass = prop.getProperty(passcodedata);
			boolean response = false;
			if(pass.equals(passcode)) {
				response = true;
			} else response = false;
			Map<String, Object> responseMap = new HashMap<String,Object>();
			responseMap.put("statusCode", 200);
			responseMap.put("isValid",String.valueOf(response));
			exchange.getOut().setBody(responseMap);
			
		}catch(Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			Map<String, Object> responseMap = new HashMap<String,Object>();
			responseMap.put("statusCode", 404);
			exchange.getOut().setBody(responseMap);
		}
		logger.info("End otp service " + (System.currentTimeMillis() - start) + " ms");
		
	}
		
	/*
	 * Phone Services
	 * 
	 */

	public void otp(Exchange exchange) throws ApplicationException{
		long start = System.currentTimeMillis();
		try {
			String custid = exchange.getIn().getHeader("custid").toString();
			Random generator = new Random();
			generator.setSeed(System.currentTimeMillis());
			int num = generator.nextInt(899999) + 100000;
			String passcode = String.valueOf(num);
			String message = "Use ".concat(passcode).concat(" (iBanking OTP) to complete your iBanking transaction. Sent on ").concat(new Date().toLocaleString());		
	        logger.info("Sending an iOS push notification..." + passcode);
	        String token = prop.getProperty(custid);
	        callAPNS(token, message);
	        
	        String hashcode = exchange.getExchangeId()+ ":" + String.valueOf(start);
			Map<String, String> response = new HashMap<String,String>();
			response.put("otp", passcode);
			response.put("hashcode", hashcode);
			exchange.getOut().setBody(response);
			prop.setProperty("passcode".concat(String.valueOf(start)), passcode);
			prop.setProperty("hashcode".concat(String.valueOf(start)), hashcode);
			prop.store(new FileOutputStream("src/main/resources/data.properties"), null);
		} catch(Exception e) {
			logger.error(e.getMessage());
			Map<String, String> responseMap = new HashMap<String,String>();
			responseMap.put("statusCode", "ERROR " + e.getMessage());
			exchange.getOut().setBody(responseMap);
		}
		logger.info("End otp service " + (System.currentTimeMillis() - start) + " ms");	}

	private void callAPNS(String token, String message) {
		String type = "dev";

		logger.info("The target token is "+token);

		ApnsServiceBuilder serviceBuilder = APNS.newService();

		if (type.equals("prod")) {
		    logger.info("using prod API");
		    String certPath = OTPProcessor.class.getResource("privateKeyPOC.p12").getPath();
		    serviceBuilder.withCert(certPath, "Passw0rd")
		            .withProductionDestination();
		} else if (type.equals("dev")) {
		    logger.info("using dev API " );
		   // String certPath = PushNotification.class.getResource("privateKeyPOC.p12").getPath();
		    String certPath = "C:\\Apps\\Deploy\\apns\\src\\main\\resources\\privateKeyPOC.p12";
		    serviceBuilder.withCert(certPath, "Passw0rd").withSandboxDestination();
		} else {
		    logger.info("unknown API type "+type);
		    return;
		}

		ApnsService service = serviceBuilder.build();
		String payload = APNS.newPayload().customField("content-available", 1)
		        .alertBody(message)
		        .alertTitle("Standard Chartered")
		        .sound("")
		        .customField("custom", "OTP SC").build();

		logger.info("payload: "+payload);
		service.push(token, payload);

		logger.info("The message has been hopefully sent...");
	}

}
