# Lightroom for Muzei backend
To avoid massive data usage per-client due to the absence of a Lightroom search API,
searching of libraries is done in a cloud function, saving data over the wire to the client.

### Technologies
* Google Cloud function, written in Kotlin and reusing the Lightroom SDK code
* Google Cloud API Gateway, allowing for authorization atop the function

### Deploying
* Gateway config `./gradlew deployGateway`
* Search function `./gradlew :search:deploy`