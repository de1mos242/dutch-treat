package com.google.auth.oauth2

import java.net.URI

class ServiceAccountCredentialsCustomImpl : ServiceAccountCredentials {

    constructor(
        clientId: String,
        clientEmail: String,
        privateKey: String,
        privateKeyId: String,
        tokenServerUri: URI,
        projectId: String) :
        super(clientId, clientEmail, privateKeyFromPkcs8(privateKey) , privateKeyId, null, OAuth2Utils.HTTP_TRANSPORT_FACTORY, tokenServerUri, null, projectId)


}