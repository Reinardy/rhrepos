package com.sc.retail.csl.notification;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


public class OTPService {


    @GET
    @Path("/v1/otp")
    @Produces(MediaType.APPLICATION_JSON)
    public void otp(){
    }
    
    @POST
    @Path("/v1/validate")
    @Produces(MediaType.APPLICATION_JSON)
    public void validate(){
    }
 
    @POST
    @Path("/v1/register")
    @Produces(MediaType.APPLICATION_JSON)
    public void register(){
    }
 
    
 }
