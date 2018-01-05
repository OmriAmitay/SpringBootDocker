package com.springboot.docker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.dto.MessageDTO;

/**
 * Security of the service given by the Spring Security package  
 * which unauthorized requests getting error code 403 or else.
 * There are many layers of security that can be put but I used only the minimum.  
 */

@SpringBootApplication
@RestController
public class SpringbootDockerApplication extends SpringBootServletInitializer {

	final static Logger LOG = Logger.getLogger(SpringbootDockerApplication.class);

	@Value("${address}")
	private String address;
	
	@Value("${remoteAddress}")
	private String remoteAddress;
	
	@Value("${port}")
	private String port;

	@RequestMapping("/healthcheck")
	@ResponseBody
	public String healthcheck() {
		LOG.info("/healthcheck response from address="+address);
		return "healthcheck from address : " + address + "\n";
	}

	@RequestMapping(value = "/message/", method = RequestMethod.POST)
	@ResponseBody
	public String postMethod(@RequestBody MessageDTO messageDTO) {
		try {
			LOG.info("recieved POST request (/message method) on server="+ address + " extract message=" + messageDTO.getMessage() + " from body");
			return forwardMsg(messageDTO);
		} catch (Exception e) {
			LOG.error("failed to send message",e);
		}
		return "POST Method failed";
	}
	
	@RequestMapping(value = "/forward/{message}", method = RequestMethod.GET)
	@ResponseBody
	public String messageGetMethod(@PathVariable("message") String message) {
		try {
			String msg = "recieved GET request (/forward method) on server=" + address + " message=" + message;
			LOG.info(msg);
			return msg;
		} catch (Exception e) {
			LOG.error("failed to get message",e);
		}
		return "forward method failed get address";
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(SpringbootDockerApplication.class);
	}

	private String forwardMsg(MessageDTO messageDTO) throws Exception {

		String url = "http://" + remoteAddress + ":" + port + "/forward/" + messageDTO.getMessage();
		LOG.info("Forward Message (GET) URL=" + url);
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);
		HttpResponse response = client.execute(request);
		LOG.info("Request response code: " + response.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		String message = result.toString();
		LOG.info("forwardMsg() parse respose=" + message);
		return message;
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringbootDockerApplication.class, args);
	}
	
}
