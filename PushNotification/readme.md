APNS Push Notification

Services Catalog
1. Register (POST)
	request header 
	a. custid
	b. deviceid
	response 
	a. statuscode = 200
2. OTP (GET)
	request header
	a. custid
	response
	a. hashcode
	b. passocde through APNS
3. Validate (POST)
	request header 
	a. hashcode
	b. passcode
	response
	a. statuscode = 200
	b. 
Flow
1. Device register their device with custid 
2. Register service will store the device id pair with custid
3. OTP service required request header "custid" in order to send the OTP to APNS
4. OTP service will send a hashcode to the response
5. Device need to do validate after the receive a message OTP from APNS notification

July 2016
