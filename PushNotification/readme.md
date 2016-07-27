3 April 2016

Account Microservices
---------------------

This account services get the data from Accounts EDMI and tranform it to REST Service.
The ultimate aim is to convert it to JSONAPI Format.

Setup Requirement:
Maven
JBOSS Fuse
Camel CXF
SOAP UI
accounts.wsdl

Configuration:
1. Setup Mock Service of Account.wsdl with SOAP UI and run the mock services under SOAP UI
2. Create stub java class from account.wsdl using wsdl2java tool (from CXF tools)
3. Create camel routes as the following:
	<camelContext xmlns="http://camel.apache.org/schema/blueprint">
  <route id="accountRoute">
    <from uri="cxfrs://bean://accountRestEndpoint"/>
    <log message="restRoute -&gt; ${header.operationName}"/>
    <choice id="Choice">
      <when id="when_status">
        <simple>${header.operationName} == "accounts"</simple>
        <bean ref="accountProcessor" method="processAccount" id="call_accountProcessor"/>
        <log message="hit account service -&gt; ${header.operationName}"/>
        <to uri="accountEDMIEndpoint"/>
      </when>
      <when id="when_Cancel">
        <simple>${header.operationName} == "balances"</simple>
        <bean ref="accountProcessor" method="processBalance" id="call_accountBalanceProcessor"/>
        <to uri="accountEDMIEndpoint"/>
      </when>
    </choice>
    <marshal>
      <json prettyPrint="true" library="Jackson"/>
    </marshal>
  </route>
</camelContext>

The routes flows as the following: 
REST Service > Mapping REST Request to SOAP Request > call EDMI Mock Service > get REST Response.

To run this sample: mvn camel:run

