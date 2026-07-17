package org.traveldna.plugin.sdk

import kotlin.jvm.JvmInline

private val NAMESPACED_ID = Regex("[a-z][a-z0-9]*(?:\\.[a-z][a-z0-9]*(?:-[a-z0-9]+)*)+")

private fun requireNamespacedId(value: String, field: String) {
    require(value.length in 3..128) { "$field length must be within [3, 128]" }
    require(NAMESPACED_ID.matches(value)) {
        "$field must use lowercase dot-separated segments and internal hyphens only"
    }
}

@JvmInline
value class PluginId(val value: String) {
    init { requireNamespacedId(value, "plugin id") }
    override fun toString(): String = value
}

@JvmInline
value class CapabilityId(val value: String) {
    init { requireNamespacedId(value, "capability id") }
    override fun toString(): String = value
}

/** Identifies a runtime environment, not a Kotlin source set. */
@JvmInline
value class PlatformId(val value: String) {
    init { requireNamespacedId(value, "platform id") }
    override fun toString(): String = value
}

data class LicenseNotice(
    val component: String,
    val spdxExpression: String,
    val notice: String? = null,
) {
    init {
        require(component.isNotBlank() && component.length <= 128) {
            "license component must be non-blank and at most 128 characters"
        }
        require(spdxExpression.isNotBlank() && spdxExpression.length <= 128) {
            "SPDX expression must be non-blank and at most 128 characters"
        }
        require(notice == null || (notice.isNotBlank() && notice.length <= 2_048)) {
            "license notice must be null or non-blank and at most 2048 characters"
        }
    }
}

/** Immutable snapshot of the metadata used to select and diagnose one plugin. */
class PluginDescriptor(
    val id: PluginId,
    val implementationVersion: String,
    val contractVersion: Int,
    capabilities: Set<CapabilityId>,
    supportedPlatforms: Set<PlatformId>,
    licenseNotices: List<LicenseNotice> = emptyList(),
) {
    val capabilities: Set<CapabilityId> = capabilities.toSet()
    val supportedPlatforms: Set<PlatformId> = supportedPlatforms.toSet()
    val licenseNotices: List<LicenseNotice> = licenseNotices.toList()

    init {
        require(implementationVersion.isNotBlank()) { "implementation version must not be blank" }
        require(implementationVersion.length <= 64) { "implementation version is too long" }
        require(contractVersion > 0) { "contract version must be positive" }
        require(this.capabilities.isNotEmpty()) { "a plugin must declare at least one capability" }
        require(this.capabilities.size <= MaxCapabilities) {
            "a plugin may declare at most $MaxCapabilities capabilities"
        }
        require(this.supportedPlatforms.isNotEmpty()) { "a plugin must declare at least one platform" }
        require(this.supportedPlatforms.size <= MaxPlatforms) {
            "a plugin may declare at most $MaxPlatforms runtime platforms"
        }
        require(this.licenseNotices.size <= MaxLicenseNotices) {
            "a plugin may declare at most $MaxLicenseNotices license notices"
        }
    }

    override fun equals(other: Any?): Boolean =
        other is PluginDescriptor &&
            id == other.id &&
            implementationVersion == other.implementationVersion &&
            contractVersion == other.contractVersion &&
            capabilities == other.capabilities &&
            supportedPlatforms == other.supportedPlatforms &&
            licenseNotices == other.licenseNotices

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + implementationVersion.hashCode()
        result = 31 * result + contractVersion
        result = 31 * result + capabilities.hashCode()
        result = 31 * result + supportedPlatforms.hashCode()
        result = 31 * result + licenseNotices.hashCode()
        return result
    }

    override fun toString(): String =
        "PluginDescriptor(id=$id, implementationVersion=$implementationVersion, " +
            "contractVersion=$contractVersion, capabilities=$capabilities, " +
            "supportedPlatforms=$supportedPlatforms, licenseNotices=$licenseNotices)"

    companion object {
        const val MaxCapabilities: Int = 64
        const val MaxPlatforms: Int = 16
        const val MaxLicenseNotices: Int = 64
    }
}

interface TravelDnaPlugin {
    val descriptor: PluginDescriptor
}

object KnownPlatforms {
    val Jvm = PlatformId("runtime.jvm")
    val Android = PlatformId("mobile.android")
    val Ios = PlatformId("mobile.ios")
    val LinuxX64 = PlatformId("native.linux-x64")
}
