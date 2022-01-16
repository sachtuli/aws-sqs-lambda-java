# Example: AWS SQS and Lambda in JAVA.

This repository contains example code which is integrating SQS and Lambda. The code reads the SQS event from a file
`SQS_Event.json`, parses the JSON and creates a xml file dynamically. It reads the sample xml message, replaces the
attributes and create a new xml file. This example also includes creating a final xml string and sending a POST request
using Apache HTTPS.

## Deploy

1. To build the deployment package, use the `mvn package` command. For installing maven dependencies you can look
into `pom.xml`. Also, for reference purpose visit this link on AWS website
[](https://docs.aws.amazon.com/lambda/latest/dg/java-package.html)

2. This command generates a JAR file in the target directory.
3. Update your Lambda function with the new JAR created. 
