package id.walt.oid4vc.requests

import id.walt.oid4vc.data.*
import id.walt.oid4vc.data.dif.PresentationDefinition
import kotlinx.serialization.json.Json

interface IAuthorizationRequest {
    val responseType: String
    val clientId: String
    val responseMode: ResponseMode?
    val redirectUri: String?
    val scope: Set<String>
    val state: String?
    val nonce: String?
}

data class AuthorizationRequest(
    override val responseType: String = ResponseType.getResponseTypeString(ResponseType.code),
    override val clientId: String,
    override val responseMode: ResponseMode? = null,
    override val redirectUri: String? = null,
    override val scope: Set<String> = setOf(),
    override val state: String? = null,
    val authorizationDetails: List<AuthorizationDetails>? = null,
    val walletIssuer: String? = null,
    val userHint: String? = null,
    val issuerState: String? = null,
    val requestUri: String? = null,
    val presentationDefinition: PresentationDefinition? = null,
    val presentationDefinitionUri: String? = null,
    val clientIdScheme: ClientIdScheme? = null,
    val clientMetadata: OpenIDClientMetadata? = null,
    val clientMetadataUri: String? = null,
    override val nonce: String? = null,
    val responseUri: String? = null,
    val codeChallenge: String? = null,
    val codeChallengeMethod: String? = null,
    override val customParameters: Map<String, List<String>> = mapOf()
) : HTTPDataObject(), IAuthorizationRequest {
    val isReferenceToPAR get() = requestUri != null
    override fun toHttpParameters(): Map<String, List<String>> {
        return buildMap {
            put("response_type", listOf(responseType))
            put("client_id", listOf(clientId))
            responseMode?.let { put("response_mode", listOf(it.name)) }
            redirectUri?.let { put("redirect_uri", listOf(it)) }
            if (scope.isNotEmpty())
                put("scope", listOf(scope.joinToString(" ")))
            state?.let { put("state", listOf(it)) }
            authorizationDetails?.let {
                put(
                    "authorization_details",
                    listOf(Json.encodeToString(AuthorizationDetailsListSerializer, authorizationDetails))
                )
            }
            walletIssuer?.let { put("wallet_issuer", listOf(it)) }
            userHint?.let { put("user_hint", listOf(it)) }
            issuerState?.let { put("issuer_state", listOf(it)) }
            requestUri?.let { put("request_uri", listOf(it)) }
            presentationDefinition?.let { put("presentation_definition", listOf(it.toJSONString())) }
            presentationDefinitionUri?.let { put("presentation_definition_uri", listOf(it)) }
            clientIdScheme?.let { put("client_id_scheme", listOf(it.value)) }
            clientMetadata?.let { put("client_metadata", listOf(it.toJSONString())) }
            clientMetadataUri?.let { put("client_metadata_uri", listOf(it)) }
            nonce?.let { put("nonce", listOf(it)) }
            responseUri?.let { put("response_uri", listOf(it)) }
            codeChallenge?.let { put("code_challenge", listOf(it)) }
            codeChallengeMethod?.let { put("code_challenge_method", listOf(it)) }
            putAll(customParameters)
        }
    }

    companion object : HTTPDataObjectFactory<AuthorizationRequest>() {
        private val knownKeys = setOf(
            "response_type",
            "client_id",
            "redirect_uri",
            "scope",
            "state",
            "authorization_details",
            "wallet_issuer",
            "user_hint",
            "issuer_state",
            "presentation_definition",
            "presentation_definition_uri",
            "client_id_scheme",
            "client_metadata",
            "client_metadata_uri",
            "nonce",
            "response_mode",
            "response_uri",
            "code_challenge",
            "code_challenge_method"
        )

        override fun fromHttpParameters(parameters: Map<String, List<String>>): AuthorizationRequest {
            return AuthorizationRequest(
                parameters["response_type"]!!.first(),
                parameters["client_id"]!!.first(),
                parameters["response_mode"]?.firstOrNull()?.let { ResponseMode.valueOf(it) },
                parameters["redirect_uri"]?.firstOrNull(),
                parameters["scope"]?.flatMap { it.split(" ") }?.toSet() ?: setOf(),
                parameters["state"]?.firstOrNull(),
                parameters["authorization_details"]?.flatMap {
                    Json.decodeFromString(
                        AuthorizationDetailsListSerializer,
                        it
                    )
                },
                parameters["wallet_issuer"]?.firstOrNull(),
                parameters["user_hint"]?.firstOrNull(),
                parameters["issuer_state"]?.firstOrNull(),
                parameters["request_uri"]?.firstOrNull(),
                parameters["presentation_definition"]?.firstOrNull()?.let { PresentationDefinition.fromJSONString(it) },
                parameters["presentation_definition_uri"]?.firstOrNull(),
                parameters["client_id_scheme"]?.firstOrNull()?.let { ClientIdScheme.fromValue(it) },
                parameters["client_metadata"]?.firstOrNull()?.let { OpenIDClientMetadata.fromJSONString(it) },
                parameters["client_metadata_uri"]?.firstOrNull(),
                parameters["nonce"]?.firstOrNull(),
                parameters["response_uri"]?.firstOrNull(),
                parameters["code_challenge"]?.firstOrNull(),
                parameters["code_challenge_method"]?.firstOrNull(),
                parameters.filterKeys { !knownKeys.contains(it) }
            )
        }

    }

}
