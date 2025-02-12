package id.walt.did.dids.resolver.local

import id.walt.crypto.keys.Key
import id.walt.did.dids.DidUtils
import id.walt.did.dids.document.DidDocument
import id.walt.did.dids.document.DidKeyDocument
import id.walt.did.utils.KeyUtils

class DidKeyResolver : LocalResolverMethod("key") {
    override suspend fun resolve(did: String): Result<DidDocument> = resolveToKey(did).fold(
        onSuccess = {
            Result.success(
                DidDocument(
                    DidKeyDocument(
                        did, DidUtils.identifierFromDid(did)!!, it.exportJWKObject()
                    ).toMap()
                )
            )
        }, onFailure = {
            Result.failure(it)
        }
    )

    override suspend fun resolveToKey(did: String): Result<Key> = DidUtils.identifierFromDid(did)?.let {
        KeyUtils.fromPublicKeyMultiBase(it)
    } ?: Result.failure(Throwable("Failed to extract identifier from: $did"))
}
